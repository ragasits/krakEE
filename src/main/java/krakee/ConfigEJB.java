/*
 * Copyright (C) 2021 Ragasits Csaba
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package krakee;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import krakee.calc.CandleDTO;
import krakee.get.TradePairDTO;
import krakee.learn.LearnDTO;
import krakee.model.ModelDTO;
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

    @Inject
    @ConfigProperty(name = "krakEE.krakenURL", defaultValue = "")
    private String krakenURL;

    @Inject
    @ConfigProperty(name = "krakEE.proxyEnabled", defaultValue = "false")
    private boolean proxyEnabled;

    @Inject
    @ConfigProperty(name = "krakEE.proxyHostname", defaultValue = "")
    private String proxyHostname;

    @Inject
    @ConfigProperty(name = "krakEE.proxyPort", defaultValue = "")
    private String proxyPort;

    @Inject
    @ConfigProperty(name = "krakEE.defaultTimerDuration", defaultValue = "10")
    private int defaultTimerDuration;

    @Inject
    @ConfigProperty(name = "krakEE.runTrade", defaultValue = "false")
    private boolean runTrade;

    @Inject
    @ConfigProperty(name = "krakEE.runCandle", defaultValue = "false")
    private boolean runCandle;

    private MongoClient client;
    private MongoCollection<TradePairDTO> tradePairColl;
    private MongoCollection<CandleDTO> candleColl;
    private MongoCollection<ProfitDTO> profitColl;
    private MongoCollection<LearnDTO> learnColl;
    private MongoCollection<ModelDTO> modelColl;

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
        MongoDatabase database = this.client.getDatabase("krakEE").withCodecRegistry(pojoCodecRegistry);

        this.tradePairColl = database.getCollection("tradepair", TradePairDTO.class);
        if (!this.isIndex(tradePairColl, "last_-1")) {
            this.tradePairColl.createIndex(Indexes.descending("last"));
        }
        if (!this.isIndex(tradePairColl, "timeDate_1")) {
            this.tradePairColl.createIndex(Indexes.ascending("timeDate"));
        }

        this.candleColl = database.getCollection("candle", CandleDTO.class);
        if (!this.isIndex(candleColl, "startDate_1")) {
            this.candleColl.createIndex(Indexes.ascending("startDate"), new IndexOptions().unique(true));
        }
        if (!this.isIndex(candleColl, "startDate_-1")) {
            this.candleColl.createIndex(Indexes.descending("startDate"), new IndexOptions().unique(true));
        }
        if (!this.isIndex(candleColl, "calcCandle_1")) {
            this.candleColl.createIndex(Indexes.ascending("calcCandle"));
        }

        this.profitColl = database.getCollection("profit", ProfitDTO.class);
        if (!this.isIndex(profitColl, "testNum_1")) {
            this.profitColl.createIndex(Indexes.ascending("testNum"), new IndexOptions().unique(true));
        }

        this.learnColl = database.getCollection("learn", LearnDTO.class);
        this.modelColl = database.getCollection("model", ModelDTO.class);
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

    public MongoCollection<ModelDTO> getModelColl() {
        return modelColl;
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
