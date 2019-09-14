package krakee.get;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 * @author rgt
 */
@Stateless
public class TradeEJB {

    static final Logger LOGGER = Logger.getLogger(TradeEJB.class.getCanonicalName());

    public JsonObject getKrakenTrade() {
        JsonObject tradeO;

        try {
            URL url = new URL("https://api.kraken.com/0/public/Trades?pair=XBTEUR");
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
