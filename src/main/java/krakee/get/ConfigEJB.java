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
    private final Integer proxyPort = 8080;
    private boolean runTrade = false;
    private boolean runCandle = true;
    
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> tradePairColl;
    private MongoCollection<Document> candleColl;
    ;

    @PostConstruct
    public void init() {
        this.client = MongoClients.create();
        this.database = this.client.getDatabase("krakEE");
        this.tradePairColl = this.database.getCollection("tradepair");
        this.candleColl = this.database.getCollection("candle");
    }
    
    @PreDestroy
    public void close(){
        this.client.close();
    }

    public MongoCollection<Document> getTradePairColl() {
        return tradePairColl;
    }

    public MongoCollection<Document> getCandleColl() {
        return candleColl;
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

    public Integer getProxyPort() {
        return proxyPort;
    }

    public boolean isRunTrade() {
        return runTrade;
    }

    public void setRunTrade(boolean runTrade) {
        this.runTrade = runTrade;
    }

    public boolean isRunCandle() {
        return runCandle;
    }

    public void setRunCandle(boolean runCandle) {
        this.runCandle = runCandle;
    }
    
    
    
    
}
