package krakee.get;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.bson.Document;

/**
 *
 * @author rgt
 */
@Singleton
@Startup
public class ConfigEJB {
    
    private final String krakenURL = "https://api.kraken.com/0/public/Trades?pair=XBTEUR&since=";
    private final boolean proxyEnabled = false;
    private final String proxyHostname = "pac.mytrium.com";
    private final String proxyPort = "8080";
    private final boolean runTrade = false;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> tradePairColl;
    

    @PostConstruct
    public void init() {
        this.client = MongoClients.create();
        this.database = this.client.getDatabase("krakEE");
        this.tradePairColl = this.database.getCollection("tradepair");
    }
    
    @PreDestroy
    public void close(){
        this.client.close();
    }

    public MongoCollection<Document> getTradePairColl() {
        return tradePairColl;
    }

    public String getKrakenURL() {
        return krakenURL;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public String getProxyHostname() {
        return proxyHostname;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public boolean isRunTrade() {
        return runTrade;
    }
    
    
    
    

}
