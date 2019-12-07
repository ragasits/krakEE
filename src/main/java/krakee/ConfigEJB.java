package krakee;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.WebTarget;
import org.bson.Document;

/**
 *
 * @author rgt
 */
@Singleton
@Startup
public class ConfigEJB {

    private final String krakenURL = "https://api.kraken.com/0/public";
    private final boolean proxyEnabled = false;
    private final String proxyHostname = "pac.mytrium.com";
    private final Integer proxyPort = 8080;
    private final int defaultTimerDuration = 10; //in sec

    private boolean runTrade = true;
    private boolean runCandle = false;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> tradePairColl;
    private MongoCollection<Document> candleColl;
    private WebTarget webTarget = null;

    /**
     * Initiate MongoDB Create collections and missing indexes
     */
    @PostConstruct
    public void init() {
        //Set Mongodb 
        this.client = MongoClients.create();
        this.database = this.client.getDatabase("krakEE");

        this.tradePairColl = this.database.getCollection("tradepair");
        if (!this.isIndex(tradePairColl, "last_-1")) {
            this.tradePairColl.createIndex(Indexes.descending("last"));
        }
        if (!this.isIndex(tradePairColl, "timeDate_1")) {
            this.tradePairColl.createIndex(Indexes.ascending("timeDate"));
        }

        this.candleColl = this.database.getCollection("candle");
        if (!this.isIndex(candleColl, "startDate_1")) {
            this.candleColl.createIndex(Indexes.ascending("startDate"), new IndexOptions().unique(true));
        }
        if (!this.isIndex(candleColl, "calcCandle_1")) {
            this.candleColl.createIndex(Indexes.ascending("calcCandle"));
        }
    }

    /**
     * Is the index exists?
     *
     * @param collection
     * @param indexName
     * @return
     */
    private boolean isIndex(MongoCollection<Document> collection, String indexName) {
        MongoCursor<Document> indexes = collection.listIndexes().iterator();
        while (indexes.hasNext()) {
            Document index = indexes.next();
            if (indexName.equals(index.getString("name"))) {
                return true;
            }
        }
        return false;
    }

    @PreDestroy
    public void close() {
        this.client.close();
    }

    @Override
    public String toString() {
        return "ConfigEJB{" + "krakenURL=" + krakenURL + ", proxyEnabled=" + proxyEnabled + ", proxyHostname=" + proxyHostname + ", proxyPort=" + proxyPort + ", defaultTimerDuration=" + defaultTimerDuration + ", runTrade=" + runTrade + ", runCandle=" + runCandle + '}';
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

    /**
     * Timer duration in sec!
     *
     * @return
     */
    public int getDefaultTimerDuration() {
        return defaultTimerDuration;
    }
}
