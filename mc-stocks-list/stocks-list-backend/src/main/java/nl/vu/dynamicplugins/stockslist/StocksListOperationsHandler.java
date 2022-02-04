package nl.vu.dynamicplugins.stockslist;

import nl.vu.dynamicplugins.stockslist.model.UserStock;
import org.apache.cxf.jaxrs.client.WebClient;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class StocksListOperationsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StocksListOperationsHandler.class);

    private static final String POLYGON_API_KEY = "WLRMH113Y_lhuyusaKZD8i3VgqLzaaiv";
    private static final String POLYGON_STOCK_ENDPOINT = "https://api.polygon.io/v2/aggs/ticker/%s/prev?adjusted=true&apiKey=%s";

    MongoDBHandler mongoDBHandler;
    WebClient webClient;

    public StocksListOperationsHandler() {
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
            try {
                userStock.setShareValue(getShareValueForTicker(userStock.getTicker()));
            } catch (IOException e) {
                LOGGER.error("Got Exception when getting price quote", e);
                userStock.setShareValue(0.0);
            }
            return userStock; })
        .collect(Collectors.toList());
    }

    private double getShareValueForTicker(String ticker) throws IOException {
        return getRandomNumber(30, 90);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public List<UserStock> storeUserStocks(String email, List<UserStock> stocksToBeSaved) {
        mongoDBHandler.storeUserStocks(email, stocksToBeSaved);
        return getUserOwnedStocks(email);
    }
}
