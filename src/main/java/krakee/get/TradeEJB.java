package krakee.get;

import com.mongodb.client.MongoCollection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    @EJB
    ConfigEJB configEJB;

    /**
     * Get, convert, store trades from Kraken
     * @return 
     */
    public int callKrakenTrade() {
        JsonObject tradeJson = this.getRestTrade();
        List<TradePairDTO> pairList = this.convertToDTO(tradeJson);
        insertToMongo(pairList);

        return pairList.size();
    }

    /**
     * Insert TradePairs to Mongo
     */
    private void insertToMongo(List<TradePairDTO> pairList) {
        MongoCollection<Document> collection = configEJB.getCollection();
        
        for (TradePairDTO dto : pairList) {
            collection.insertOne(dto.getTradepair());
            /*
            collection.replaceOne(
                    eq("time",dto.getTimeDate()),
                    dto.getTradepair(),
                    new UpdateOptions().upsert(true).bypassDocumentValidation(true)
            );
            */
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
        String error = sb.toString();
        String pair = "XXBTZEUR";

        List<TradePairDTO> tradePairList = new ArrayList<>();
        JsonArray jsonPairs = ob.asJsonObject().getJsonObject("result").getJsonArray(pair);
        for (JsonValue p : jsonPairs) {
            tradePairList.add(new TradePairDTO(new BigDecimal(p.asJsonArray().getString(0)),
                    new BigDecimal(p.asJsonArray().getString(1)),
                    p.asJsonArray().getJsonNumber(2).bigDecimalValue(),
                    p.asJsonArray().getString(3),
                    p.asJsonArray().getString(4),
                    p.asJsonArray().getString(5),
                    error, last, pair));
        }

        return tradePairList;
    }

    /**
     * REST client, get data from Kraken
     *
     * @return
     */
    private JsonObject getRestTrade() {
        JsonObject tradeO;

        try {
            URL url = new URL(configEJB.getKrakenURL());
            //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("pac.mytrium.com", 8080));
            //HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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
            Logger.getLogger(TimerEjb.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(TimerEjb.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return tradeO;

    }
}
