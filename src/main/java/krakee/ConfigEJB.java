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
import javax.inject.Inject;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Global configuration, database settings, 
 * @author rgt
 */
@Singleton
@Startup
public class ConfigEJB {

    //private final String krakenURL = "https://api.kraken.com/0/public";
    @Inject
    @ConfigProperty(name = "krakEE.krakenURL", defaultValue = "")
    private String krakenURL;

    //private final boolean proxyEnabled = false;
    @Inject
    @ConfigProperty(name = "krakEE.proxyEnabled", defaultValue = "false")
    private boolean proxyEnabled;

    //private final String proxyHostname = "pac.mytrium.com";
    @Inject
    @ConfigProperty(name = "krakEE.proxyHostname", defaultValue = "")
    private String proxyHostname;

    //private final Integer proxyPort = 8080;
    @Inject
    @ConfigProperty(name = "krakEE.proxyPort", defaultValue = "")
    private String proxyPort;

    //private final int defaultTimerDuration = 10; //in sec
    @Inject
    @ConfigProperty(name = "krakEE.defaultTimerDuration", defaultValue = "10")
    private int defaultTimerDuration;

    //private boolean runTrade = true;
    @Inject
    @ConfigProperty(name = "krakEE.runTrade", defaultValue = "false")
    private boolean runTrade;

    //private boolean runCandle = false;
    @Inject
    @ConfigProperty(name = "krakEE.runCandle", defaultValue = "false")
    private boolean runCandle;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> tradePairColl;
    private MongoCollection<Document> candleColl;
    private MongoCollection<Document> profitColl;
    private MongoCollection<Document> profitBestColl;

    /**
     * Initiate:
     * - Set proxy
     * - MongoDB Create collections and missing indexes
     */
    @PostConstruct
    public void init() {
        //Set proxy
        if (this.proxyEnabled) {
            System.setProperty("https.proxyHost", this.proxyHostname);
            System.setProperty("https.proxyPort", this.proxyPort);
        }

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
        if (!this.isIndex(candleColl, "startDate_-1")) {
            this.candleColl.createIndex(Indexes.descending("startDate"), new IndexOptions().unique(true));
        }        
        if (!this.isIndex(candleColl, "calcCandle_1")) {
            this.candleColl.createIndex(Indexes.ascending("calcCandle"));
        }
        
        this.profitColl = this.database.getCollection("profit");
        this.profitBestColl = this.database.getCollection("profitBest");
        
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

    public MongoCollection<Document> getProfitColl() {
        return profitColl;
    }

    public MongoCollection<Document> getProfitBestColl() {
        return profitBestColl;
    }
    
    public String getKrakenURL() {
        return krakenURL;
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
