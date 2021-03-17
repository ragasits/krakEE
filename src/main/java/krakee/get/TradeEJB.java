package krakee.get;

import com.mongodb.MongoInterruptedException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Variable;
import com.mongodb.client.result.InsertManyResult;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import krakee.ConfigEJB;
import krakee.MyException;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

/**
 * Download and process Trade values
 *
 * @author rgt
 */
@Stateless
public class TradeEJB {

    static final Logger LOGGER = Logger.getLogger(TradeEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;

    private int pairTradeSize = 0;
    private int missingCount = 0;
    private int deleteCount = 0;

    /**
     * get Last limit size trade pairs
     *
     * @param limit
     * @return
     */
    public List<TradePairDTO> getLasts(int limit) {
        return config.getTradePairColl()
                .find()
                .sort(Sorts.descending("timeDate"))
                .limit(limit)
                .into(new ArrayList<>());
    }

    /**
     * Get list of the trade years
     *
     * @return
     */
    public List<String> getYear() {
        MongoCursor<Document> cursor = config.getTradePairColl()
                .aggregate(
                        Arrays.asList(
                                Aggregates.project(
                                        Projections.fields(Projections.excludeId(),
                                                Projections.computed("year", new Document("$year", "$timeDate")))),
                                Aggregates.group("year", Accumulators.sum("year", 1))
                        //Aggregates.sort(Sorts.descending("count")),
                        //Aggregates.match(Filters.ne("count", 1)),
                        //Aggregates.limit(100)
                        ), Document.class
                )
                .allowDiskUse(Boolean.TRUE)
                .iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            System.out.println(doc);
        }

        return null;
    }

    /**
     * Get, convert, store trades from Kraken
     *
     */
    @Asynchronous
    public void callKrakenTrade() {

        config.setRunTrade(false);

        //Get last value from Mongo
        String last = "0";
        TradePairDTO lastDto = config.getTradePairColl().find()
                .sort(Sorts.descending("last"))
                .first();
        if (lastDto != null) {
            last = lastDto.getLast();
        }

        try {
            JsonObject tradeJson = this.getRestTrade(last);
            List<TradePairDTO> pairList = this.convertToDTO(tradeJson);

            if (!pairList.isEmpty()) {
                this.pairTradeSize = pairList.size();
                InsertManyResult insertMany = config.getTradePairColl().insertMany(pairList);
                LOGGER.log(Level.INFO, "Trade Fired .... {0} {1}", new Object[]{this.pairTradeSize, pairList.get(0).getLastDate()});
            } else {
                LOGGER.log(Level.INFO, "Trade Fired .... Error");
            }

            config.setRunTrade(true);
        } catch (MyException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Convert JSON to DTO
     *
     * @param ob
     * @return
     */
    private List<TradePairDTO> convertToDTO(JsonObject ob) {
        List<TradePairDTO> tradePairList = new ArrayList<>();
        String last = null;
        try {
            last = ob.asJsonObject().getJsonObject("result").getString("last");
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return tradePairList;
        }

        JsonArray errors = ob.asJsonObject().getJsonArray("error");
        StringBuilder sb = new StringBuilder();
        for (JsonValue e : errors) {
            sb = sb.append(" ").append(e.toString());
        }
        String error = sb.toString().trim();
        String pair = "XXBTZEUR";

        JsonArray jsonPairs = ob.asJsonObject().getJsonObject("result").getJsonArray(pair);
        for (JsonValue p : jsonPairs) {
            tradePairList.add(
                    new TradePairDTO(
                            new BigDecimal(p.asJsonArray().getString(0)),
                            new BigDecimal(p.asJsonArray().getString(1)),
                            p.asJsonArray().getJsonNumber(2).bigDecimalValue(),
                            p.asJsonArray().getString(3),
                            p.asJsonArray().getString(4),
                            p.asJsonArray().getString(5),
                            error, last, pair)
            );
        }
        return tradePairList;
    }

    public int getPairTradeSize() {
        return pairTradeSize;
    }

    @Override
    public String toString() {
        return "TradeEJB{" + "config=" + config + ", pairTradeSize=" + pairTradeSize + '}';
    }

    /**
     * REST client, get data from Kraken Get https cert on the fly
     *
     * @return
     */
    private JsonObject getRestTrade(String last) throws MyException {
        SSLContext sc;
        Response response = null;
        //Client sslClient;

        TrustManager[] noopTrustManager = new TrustManager[]{
            new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        try {
            sc = SSLContext.getInstance("ssl");
            sc.init(null, noopTrustManager, null);

            //"https://api.kraken.com/0/public/Trades?pair=XBTEUR&since=";
            URI apiUri = new URI(config.getKrakenURL());

            KrakenClient krakenClient = RestClientBuilder.newBuilder()
                    .baseUri(apiUri)
                    .sslContext(sc)
                    .build(KrakenClient.class);

            response = krakenClient.getTrade("XBTEUR", last);
        } catch (NoSuchAlgorithmException | KeyManagementException | ProcessingException | WebApplicationException ex) {
            throw new MyException("getRestTrade: Failed :" + ex.getMessage());
        } catch (URISyntaxException ex) {
            Logger.getLogger(TradeEJB.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (response == null) {
            throw new MyException("getRestTrade: Failed : No response");
        }

        if (response.getStatus() == 200) {
            InputStream inputStream = response.readEntity(InputStream.class);
            InputStreamReader in = new InputStreamReader(inputStream);
            JsonReader reader = Json.createReader(in);
            return reader.readObject();
        } else {
            throw new MyException("getRestTrade: Failed : HTTP error code :" + response.getStatus());
        }

    }

    /**
     * Check Trade consistency
     *
     * @return
     */
    public ArrayList<String> chkTradePair() {
        //Chk last<>max(time)
        MongoCursor<Document> cursor = config.getTradePairColl().aggregate(
                Arrays.asList(
                        Aggregates.group("$last", Accumulators.max("max", "$time"))
                ), Document.class
        ).cursor();

        ArrayList<String> list = new ArrayList();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String id = doc.getString("_id").substring(0, 14);
            String max = ((Decimal128) doc.get("max")).bigDecimalValue().toString().replace(".", "");

            if (!id.equals(max)) {
                list.add("last:" + id + " max($time):" + max);
            }
        }
        return list;
    }

    /**
     * Search and look missing trades, check duplicates
     *
     * @param oldDto
     * @param errorList
     */
    @Asynchronous
    private void chkCompareTrade(TradePairDTO oldDto, ArrayList<String> errorList) {
        ArrayList<TradePairDTO> newDtoList = null;
        try {
            newDtoList = config.getTradePairColl()
                    .find(eq("time", oldDto.getTime()))
                    .into(new ArrayList<>());
        } catch (MongoInterruptedException e) {
            System.out.println("MongoInterruptedException: " + e.getMessage());
            return;
        }

        if (newDtoList == null || newDtoList.isEmpty()) {
            //errorList.add("Missing element:" + oldDto.toString());
            //System.out.println("Missing element:" + oldDto.toString());
            //numMissing++;

        }
    }

    /**
     * Looking for missing elements
     *
     * @return
     */
    public ArrayList<String> chkCompareTrades() {
        ArrayList<String> errorList = new ArrayList();

        MongoCursor<TradePairDTO> cursor = config.getTradePairOldColl()
                .find()
                //.cursorType(CursorType.Tailable)
                //.noCursorTimeout(true)
                //.limit(2000000)
                .iterator();

        while (cursor.hasNext()) {

            TradePairDTO oldDto = cursor.next();

            this.chkCompareTrade(oldDto, errorList);
        }

        System.out.println("Done!");

        //errorList.add("Missing elements:" + numMissing);
        //errorList.add("Multiple elements:" + numMultipe);
        return errorList;
    }

    /**
     * Compare trades (aggregates + lookup)
     *
     * @return
     */
    public ArrayList<String> chkCompareTrades1() {
        ArrayList<String> errorList = new ArrayList();

        MongoCursor<Document> cursor = config.getTradePairOldColl()
                .aggregate(
                        Arrays.asList(
                                Aggregates.lookup("tradepair_1",
                                        Arrays.asList(
                                                new Variable("time", "$time"),
                                                new Variable("volume", "$volume"),
                                                new Variable("price", "$price")
                                        ),
                                        Arrays.asList(
                                                Aggregates.match(
                                                        Filters.expr(new Document("$and",
                                                                Arrays.asList(
                                                                        new Document("$eq", Arrays.asList("$time", "$$time")),
                                                                        new Document("$eq", Arrays.asList("$volume", "$$volume")),
                                                                        new Document("$eq", Arrays.asList("$price", "$$price"))
                                                                )
                                                        )))),
                                        "newtrade"),
                                //Aggregates.unwind("$newtrade"),
                                Aggregates.match(Filters.exists("newtrade.time", false)),
                                Aggregates.limit(100)
                        ), Document.class
                ).iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            System.out.println(doc.toString());
            errorList.add(doc.toString());
        }

        return errorList;

    }

    /**
     * Delete the old trade when it is the same as the new (time, volume, price)
     *
     * @param dto
     */
    @Asynchronous
    private void chkDeleteTheSameTrade(TradePairDTO dto) {
        try {
            ArrayList newList = config.getTradePairColl()
                    .find(Filters.and(Arrays.asList(
                            Filters.eq("time", dto.getTime()),
                            Filters.eq("volume", dto.getVolume()),
                            Filters.eq("price", dto.getPrice())
                    )))
                    .into(new ArrayList<>());

            if (newList == null || newList.isEmpty()) {
                //errorList.add("Missing trade: " + dto.getTime() + ": " + dto.getVolume() + ": " + dto.getPrice());
                //System.out.println("Missing trade: " + dto.getTime() + ": " + dto.getVolume() + ": " + dto.getPrice());
                missingCount++;
            } else {
                //System.out.println("Delete trade: " + dto.getTime() + ": " + dto.getVolume() + ": " + dto.getPrice());

                //Delete if element exists
                config.getTradePairOldColl().deleteMany(
                        Filters.and(Arrays.asList(
                                Filters.eq("time", dto.getTime()),
                                Filters.eq("volume", dto.getVolume()),
                                Filters.eq("price", dto.getPrice()
                                )))
                );

                deleteCount++;
            }
        } catch (MongoInterruptedException e) {
            //errorList.add(e.getMessage());
            System.out.println("MongoInterruptedException: " + e.getMessage());
        }
    }

    /**
     * Delete the old trade when it is the same (time, price) and rounded volume
     *
     * @param dto
     */
    private void chkDeleteAlmostSameTradeVolume(TradePairDTO dto) {
        try {
            ArrayList<TradePairDTO> newList = config.getTradePairColl()
                    .find(Filters.and(Arrays.asList(
                            Filters.eq("time", dto.getTime()),
                            Filters.eq("price", dto.getPrice())
                    )))
                    .into(new ArrayList<>());

            if (newList == null || newList.isEmpty()) {
                missingCount++;
            } else {
                for (TradePairDTO newDto : newList) {
                    BigDecimal bd = dto.getVolume().subtract(newDto.getVolume()).abs();

                    if (bd.equals(new BigDecimal("0.00000001"))) {
                        //System.out.println("Delete ..Time: " + dto.getTime() + " Price:" + dto.getPrice()
                        //      + " Volume1:" + dto.getVolume() + " Volume2:" + newDto.getVolume());

                        //Delete if element exists
                        config.getTradePairOldColl().deleteMany(
                                Filters.and(Arrays.asList(
                                        Filters.eq("time", dto.getTime()),
                                        Filters.eq("price", dto.getPrice()
                                        )))
                        );

                        this.deleteCount++;
                    } else {
                        //System.out.println("Missing ..Time: " + dto.getTime() + " Price:" + dto.getPrice()
                        //      + " Volume1:" + dto.getVolume() + " Volume2:" + newDto.getVolume());

                        missingCount++;
                    }
                }

            }
        } catch (MongoInterruptedException e) {
            //errorList.add(e.getMessage());
            System.out.println("MongoInterruptedException: " + e.getMessage());
        }
    }

    /**
     * Delete the old trade when it is the same (price, volume) and rounded time
     *
     * @param dto
     */
    private void chkDeleteAlmostSameTradeTime(TradePairDTO dto) {
        try {
            ArrayList<TradePairDTO> newList = config.getTradePairColl()
                    .find(Filters.and(Arrays.asList(
                            Filters.eq("price", dto.getPrice()),
                            Filters.eq("volume", dto.getVolume())
                    )))
                    .into(new ArrayList<>());

            if (newList == null || newList.isEmpty()) {
                missingCount++;
            } else {
                for (TradePairDTO newDto : newList) {
                    BigDecimal bd = dto.getTime().subtract(newDto.getTime()).abs();

                    if (bd.equals(new BigDecimal("0.0001"))) {
                        /*                        System.out.println("Delete .. Price:" + dto.getPrice()
                        + " Volume:" + dto.getVolume()
                        + " Time1:" + dto.getTime()
                        + " Time2:" + newDto.getTime());*/

                        //Delete if element exists
                        config.getTradePairOldColl().deleteMany(
                                Filters.and(Arrays.asList(
                                        Filters.eq("time", dto.getTime()),
                                        Filters.eq("price", dto.getPrice()
                                        )))
                        );
                        this.deleteCount++;

                        return;
                    } else {
                        /*                        System.out.println("Missing .. Price:" + dto.getPrice()
                        + " Volume:" + dto.getVolume()
                        + " Time1:" + dto.getTime()
                        + " Time2:" + newDto.getTime());*/

                        missingCount++;
                    }
                }

            }
        } catch (MongoInterruptedException e) {
            //errorList.add(e.getMessage());
            System.out.println("MongoInterruptedException: " + e.getMessage());
        }
    }

    /**
     * Delete the old trade when it is the same (time, volume) and rounded price
     * @param dto 
     */
    private void chkDeleteAlmostSameTradePrice(TradePairDTO dto) {
        try {
            ArrayList<TradePairDTO> newList = config.getTradePairColl()
                    .find(Filters.and(Arrays.asList(
                            Filters.eq("time", dto.getTime()),
                            Filters.eq("volume", dto.getVolume())
                    )))
                    .into(new ArrayList<>());

            if (newList == null || newList.isEmpty()) {
                missingCount++;
            } else {
                for (TradePairDTO newDto : newList) {
                    BigDecimal bd = dto.getPrice().subtract(newDto.getPrice()).abs();

                    if (bd.equals(new BigDecimal("0.00001"))) {
                        /*                        System.out.println("Delete .. Time:" + dto.getTime()
                        + " Volume:" + dto.getVolume()
                        + " Price1:" + dto.getPrice()
                        + " Price2:" + newDto.getPrice());*/
                        //Delete if element exists
                        config.getTradePairOldColl().deleteMany(
                                Filters.and(Arrays.asList(
                                        Filters.eq("time", dto.getTime()),
                                        Filters.eq("price", dto.getPrice()
                                        )))
                        );
                        this.deleteCount++;

                        return;
                    } else {
                        /*                        System.out.println("Missing .. Time:" + dto.getTime()
                        + " Volume:" + dto.getVolume()
                        + " Price1:" + dto.getPrice()
                        + " Price2:" + newDto.getPrice());*/

                        missingCount++;
                    }
                }

            }
        } catch (MongoInterruptedException e) {
            //errorList.add(e.getMessage());
            System.out.println("MongoInterruptedException: " + e.getMessage());
        }
    }

    /**
     * Compare trades and delete when it is exists
     *
     * @return
     */
    public ArrayList<String> chkCompareDeleteTrades() {
        ArrayList<String> errorList = new ArrayList();
        this.missingCount = 0;
        this.deleteCount = 0;

        MongoCursor<TradePairDTO> cursor = config.getTradePairOldColl()
                .find()
                //.skip(3000000)
                //.limit(100000)
                .iterator();

        while (cursor.hasNext()) {
            TradePairDTO dto = cursor.next();
            //chkDeleteTheSameTrade(dto);
            //chkDeleteAlmostSameTradeVolume(dto);
            //chkDeleteAlmostSameTradeTime(dto);
            chkDeleteAlmostSameTradePrice(dto);
        }

        errorList.add("Done... Missing: " + this.missingCount + " Delete: " + this.deleteCount);
        return errorList;
    }

    /**
     * Trade: Search for duplicates
     *
     * @return
     */
    public ArrayList<String> chkTradeDuplicates() {
        ArrayList<String> list = new ArrayList();

        Map<String, Object> multiIdMap = new HashMap<>();
        multiIdMap.put("time", "$time");
        multiIdMap.put("volume", "$volume");
        multiIdMap.put("price", "$price");

        MongoCursor<Document> cursor = config.getTradePairColl()
                .aggregate(
                        Arrays.asList(
                                Aggregates.group(new Document(multiIdMap), Accumulators.sum("count", 1)),
                                Aggregates.sort(Sorts.descending("count")),
                                Aggregates.match(Filters.ne("count", 1)),
                                Aggregates.limit(100)
                        ), Document.class
                )
                .allowDiskUse(Boolean.TRUE)
                .iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();

            Document id = (Document) doc.get("_id");
            BigDecimal time = ((Decimal128) id.get("time")).bigDecimalValue();
            BigDecimal volume = ((Decimal128) id.get("volume")).bigDecimalValue();
            BigDecimal price = ((Decimal128) id.get("price")).bigDecimalValue();
            Integer count = doc.getInteger("count");

            list.add("Time: " + time + " Volume: " + volume + " Price: " + price + " Count: " + count);
        }

        return list;
    }
}
