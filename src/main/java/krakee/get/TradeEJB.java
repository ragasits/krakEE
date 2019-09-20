package krakee.get;

import com.mongodb.client.model.Sorts;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.bson.Document;

/**
 *
 * @author rgt
 */
@Stateless
public class TradeEJB {

    static final Logger LOGGER = Logger.getLogger(TimerEjb.class.getCanonicalName());

    @EJB
    ConfigEJB configEJB;

    /**
     * Get, convert, store trades from Kraken
     *
     */
    public void callKrakenTrade() {

        //Get last value from Mongo
        String last = "0";
        Document document = configEJB.getTradePairColl().find()
                .sort(Sorts.descending("last"))
                .first();
        if (document != null) {
            last = new TradePairDTO(document).getLast();
        }
        System.out.println("Last: " + last);

        JsonObject tradeJson = this.getRestTrade(last);
        List<TradePairDTO> pairList = this.convertToDTO(tradeJson);
        insertToMongo(pairList);

        LOGGER.log(Level.INFO, "TimerEjb Schedule Fired .... " + pairList.size() + " " + pairList.get(0).getLastDate());
    }

    /**
     * Insert TradePairs to Mongo
     */
    private void insertToMongo(List<TradePairDTO> pairList) {

        for (TradePairDTO dto : pairList) {
            configEJB.getTradePairColl().insertOne(dto.getTradepair());
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

    /**
     * REST client, get data from Kraken
     *
     * @return
     */
    private JsonObject getRestTrade(String last) {
        JsonObject tradeO;
        HttpURLConnection conn;

        try {
            URL url = new URL(configEJB.getKrakenURL() + last);
            if (configEJB.isProxyEnabled()) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("pac.mytrium.com", 8080));
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }
            }

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            JsonReader reader = Json.createReader(in);
            tradeO = reader.readObject();

            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(TimerEjb.class
                    .getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(TimerEjb.class
                    .getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return tradeO;

    }
}
