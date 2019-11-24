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
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import krakee.ConfigEJB;
import krakee.MyException;
import org.bson.Document;

/**
 *
 * @author rgt
 */
@Stateless
public class TradeEJB {

    static final Logger LOGGER = Logger.getLogger(TradeEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;
    
    private int pairTradeSize=0;

    /**
     * Get, convert, store trades from Kraken
     *
     */
    @Asynchronous
    public void callKrakenTrade() {

        config.setRunTrade(false);

        //Get last value from Mongo
        String last = "0";
        Document document = config.getTradePairColl().find()
                .sort(Sorts.descending("last"))
                .first();
        if (document != null) {
            last = new TradePairDTO(document).getLast();
        }

        try {
            JsonObject tradeJson = this.getRestTrade(last);
            List<TradePairDTO> pairList = this.convertToDTO(tradeJson);
            this.pairTradeSize = pairList.size();

            //Insert TradePairs to Mongo
            for (TradePairDTO dto : pairList) {
                config.getTradePairColl().insertOne(dto.getTradepair());
            }

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

    /**
     * REST client, get data from Kraken
     *
     * @return
     */
    private JsonObject getRestTrade(String last) throws MyException {
        JsonObject tradeO;
        HttpURLConnection conn;

        try {
            URL url = new URL(config.getKrakenURL() + last);
            if (config.isProxyEnabled()) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyHostname(), config.getProxyPort()));
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                if (conn.getResponseCode() != 200) {
                    throw new MyException("getRestTrade: Failed : HTTP error code : " + conn.getResponseCode());
                }
            }

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            JsonReader reader = Json.createReader(in);
            tradeO = reader.readObject();

            conn.disconnect();
        } catch (MalformedURLException ex) {
            throw new MyException("getRestTrade: " + ex.getMessage());
        } catch (IOException ex) {
            throw new MyException("getRestTrade: " + ex.getClass()+": "+ex.getMessage());
        }

        return tradeO;

    }

    public int getPairTradeSize() {
        return pairTradeSize;
    }

    @Override
    public String toString() {
        return "TradeEJB{" + "config=" + config + ", pairTradeSize=" + pairTradeSize + '}';
    }
    
    
}
