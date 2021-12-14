package krakee;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import krakee.calc.CandleDTO;
import krakee.deep.DeepDTO;
import krakee.input.InputDTO;
import krakee.input.InputRowDTO;
import krakee.input.InputStatDTO;
import krakee.get.TradePairDTO;
import krakee.learn.LearnDTO;
import krakee.profit.ProfitDTO;
import org.bson.Document;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Global configuration, database settings,
 *
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
    private MongoCollection<TradePairDTO> tradePairColl;
    private MongoCollection<CandleDTO> candleColl;
    private MongoCollection<ProfitDTO> profitColl;
    private MongoCollection<LearnDTO> learnColl;
    private MongoCollection<DeepDTO> deepColl;
    private MongoCollection<InputDTO> inputColl;
    private MongoCollection<InputRowDTO> inputRowColl;
    private MongoCollection<InputStatDTO> inputStatColl;

    /**
     * Initiate: - Set proxy - MongoDB Create collections and missing indexes
     */
    @PostConstruct
    public void init() {
        //Set proxy
        if (this.proxyEnabled) {
            System.setProperty("https.proxyHost", this.proxyHostname);
            System.setProperty("https.proxyPort", this.proxyPort);
        }

        //Set Mongodb 
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        this.client = MongoClients.create();
        this.database = this.client.getDatabase("krakEE").withCodecRegistry(pojoCodecRegistry);

        this.tradePairColl = this.database.getCollection("tradepair", TradePairDTO.class);
        if (!this.isIndex(tradePairColl, "last_-1")) {
            this.tradePairColl.createIndex(Indexes.descending("last"));
        }
        if (!this.isIndex(tradePairColl, "timeDate_1")) {
            this.tradePairColl.createIndex(Indexes.ascending("timeDate"));
        }

        this.candleColl = this.database.getCollection("candle", CandleDTO.class);
        if (!this.isIndex(candleColl, "startDate_1")) {
            this.candleColl.createIndex(Indexes.ascending("startDate"), new IndexOptions().unique(true));
        }
        if (!this.isIndex(candleColl, "startDate_-1")) {
            this.candleColl.createIndex(Indexes.descending("startDate"), new IndexOptions().unique(true));
        }
        if (!this.isIndex(candleColl, "calcCandle_1")) {
            this.candleColl.createIndex(Indexes.ascending("calcCandle"));
        }

        this.profitColl = this.database.getCollection("profit", ProfitDTO.class);
        if (!this.isIndex(profitColl, "testNum_1")) {
            this.profitColl.createIndex(Indexes.ascending("testNum"), new IndexOptions().unique(true));
        }

        this.learnColl = this.database.getCollection("learn", LearnDTO.class);
        this.deepColl = this.database.getCollection("deep", DeepDTO.class);

        this.inputColl = this.database.getCollection("input", InputDTO.class);
        if (!this.isIndex(inputColl, "deepName_1")) {
            this.inputColl.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("deepName"),
                            Indexes.ascending("candle.startDate")
                    )
            );
        }

        this.inputRowColl = this.database.getCollection("inputRow", InputRowDTO.class);

        this.inputStatColl = this.database.getCollection("inputStat", InputStatDTO.class);
        if (!this.isIndex(inputColl, "learnName_1_inputType_1")) {
            this.inputStatColl.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("learnName"),
                            Indexes.ascending("inputType")
                    )
            );
        }

    }

    /**
     * Is the index exists?
     *
     * @param collection
     * @param indexName
     * @return
     */
    private boolean isIndex(MongoCollection collection, String indexName) {
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

    public MongoCollection<TradePairDTO> getTradePairColl() {
        return tradePairColl;
    }

    public MongoCollection<CandleDTO> getCandleColl() {
        return candleColl;
    }

    public MongoCollection<ProfitDTO> getProfitColl() {
        return profitColl;
    }

    public MongoCollection<LearnDTO> getLearnColl() {
        return learnColl;
    }

    public MongoCollection<DeepDTO> getDeepColl() {
        return deepColl;
    }

    public MongoCollection<InputDTO> getInputColl() {
        return inputColl;
    }

    public MongoCollection<InputRowDTO> getInputRowColl() {
        return inputRowColl;
    }

    public MongoCollection<InputStatDTO> getInputStatColl() {
        return inputStatColl;
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
