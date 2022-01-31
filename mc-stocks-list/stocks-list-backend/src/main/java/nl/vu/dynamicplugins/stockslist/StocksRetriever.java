package nl.vu.dynamicplugins.stockslist;

import nl.vu.dynamicplugins.stockslist.model.UserStock;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

public class StocksRetriever {
    MongoDBHandler mongoDBHandler;

    public StocksRetriever() {
        mongoDBHandler = MongoDBHandler.getConnection();
    }

    public List<UserStock> getUserOwnedStocks(String email) {
        List<Document> storedDocuments = mongoDBHandler.getUserStocks(email);
        return storedDocuments.stream().map(doc -> {
            UserStock userStock = new UserStock();
            userStock.setId(doc.getObjectId("_id").toHexString());
            userStock.setEmail(doc.getString("email"));
            userStock.setMarket(doc.getString("market"));
            userStock.setName(doc.getString("name"));
            userStock.setTicker(doc.getString("ticker"));
            userStock.setShares(doc.get("noShares",  0.0));
            return userStock; })
        .collect(Collectors.toList());
    }

    public List<UserStock> storeUserStocks(String email, List<UserStock> stocksToBeSaved) {
        mongoDBHandler.storeUserStocks(email, stocksToBeSaved);
        return getUserOwnedStocks(email);
    }
}
