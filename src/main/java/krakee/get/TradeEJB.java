package krakee.get;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
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
import java.util.List;
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
import javax.ws.rs.core.Response;
import krakee.ConfigEJB;
import krakee.MyException;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

/**
 * Download and process Trade values
 * @author rgt
 */
@Stateless
public class TradeEJB {

    static final Logger LOGGER = Logger.getLogger(TradeEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;

    private int pairTradeSize = 0;
    
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
            this.pairTradeSize = pairList.size();

            //Insert TradePairs to Mongo
            /*
            for (TradePairDTO dto : pairList) {
            config.getTradePairColl().insertOne(dto);
            }
             */
            InsertManyResult insertMany = config.getTradePairColl().insertMany(pairList);
  
            LOGGER.log(Level.INFO, "Trade Fired .... " + this.pairTradeSize + " " + pairList.get(0).getLastDate());
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
        String last = ob.asJsonObject().getJsonObject("result").getString("last");

        JsonArray errors = ob.asJsonObject().getJsonArray("error");
        StringBuilder sb = new StringBuilder();
        for (JsonValue e : errors) {
            sb = sb.append(" ").append(e.toString());
        }
        String error = sb.toString().trim();
        String pair = "XXBTZEUR";

        List<TradePairDTO> tradePairList = new ArrayList<>();
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
        } catch (NoSuchAlgorithmException | KeyManagementException | ProcessingException ex) {
            throw new MyException("getRestTrade: " + ex.getMessage());
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
    public List<String> chkTradePair() {
        //Chk last<>max(time)
        MongoCursor<Document> cursor = config.getTradePairColl().aggregate(
                Arrays.asList(
                        Aggregates.group("$last", Accumulators.max("max", "$time"))
                ), Document.class
        ).cursor();

        List<String> list = new ArrayList();

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
}
