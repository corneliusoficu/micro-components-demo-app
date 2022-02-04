package nl.vu.dynamicplugins.stockslist;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

import nl.vu.dynamicplugins.stockslist.model.UserStock;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MongoDBHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBHandler.class);

    private static final String HOST = "localhost";
    private static final int PORT = 27017;

    private static final String DATABASE = "mc-db";
    private static final String SHARES_COLLECTION = "shares";

    private static MongoDBHandler mongoDBHandler = null;

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoDBHandler() {
        mongoClient = new MongoClient(HOST, PORT);
        database = mongoClient.getDatabase(DATABASE);
    }

    public static MongoDBHandler getConnection() {
        if(mongoDBHandler == null) {
            mongoDBHandler = new MongoDBHandler();
        }
        return mongoDBHandler;
    }

    public List<Document> getUserStocks(String email) {
        MongoCollection<Document> stocksCollection = database.getCollection(SHARES_COLLECTION);
        FindIterable<Document> itemsIterable = stocksCollection.find(new Document("email", email));
        List<Document> documents = new ArrayList<>();
        for(Document document : itemsIterable) {
            documents.add(document);
        }
        return documents;
    }

    public void storeUserStocks(String email, List<UserStock> newUserStocks) {
        MongoCollection<Document> stocksCollection = database.getCollection(SHARES_COLLECTION);

        for(UserStock newUserStock: newUserStocks) {
            Document updatedDocument = stocksCollection.findOneAndUpdate(
                new Document("ticker", newUserStock.getTicker().toLowerCase()), 
                new Document("$inc", new Document("noShares", newUserStock.getShares())));

            if(updatedDocument != null) {
                continue;
            }

            Document document = new Document();
            document.put("ticker", newUserStock.getTicker().toLowerCase());
            document.put("email", email);
            document.put("name", newUserStock.getName() != null ? newUserStock.getName() : newUserStock.getTicker());
            document.put("noShares", newUserStock.getShares());
            document.put("market", newUserStock.getMarket() != null ? newUserStock.getMarket() : "NASDAQ");
            stocksCollection.insertOne(document);
        }
    }
}
