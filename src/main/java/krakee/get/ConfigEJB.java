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
    
    private final String krakenURL = "https://api.kraken.com/0/public/Trades?pair=XBTEUR";

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    

    @PostConstruct
    public void init() {
        this.client = MongoClients.create();
        this.database = this.client.getDatabase("krakEE");
        this.collection = this.database.getCollection("tradepair");
    }
    
    @PreDestroy
    public void close(){
        this.client.close();
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public String getKrakenURL() {
        return krakenURL;
    }

}
