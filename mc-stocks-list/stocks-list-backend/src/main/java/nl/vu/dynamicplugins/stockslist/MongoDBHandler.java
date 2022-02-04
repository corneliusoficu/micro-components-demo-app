package nl.vu.dynamicplugins.stockslist;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nl.vu.dynamicplugins.stockslist.model.UserStock;
import org.bson.Document;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MongoDBHandler {
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

        List<Document> documents = newUserStocks.stream()
                .map(s -> {
                    Document document = new Document();
                    document.put("email", email);
                    document.put("ticker", s.getTicker());
                    document.put("name", s.getName() != null ? s.getName() : s.getTicker());
                    document.put("noShares", s.getShares());
                    document.put("market", s.getMarket() != null ? s.getMarket() : "NASDAQ");
                    return document;
                })
                .collect(Collectors.toList());

        stocksCollection.insertMany(documents);
    }
}
