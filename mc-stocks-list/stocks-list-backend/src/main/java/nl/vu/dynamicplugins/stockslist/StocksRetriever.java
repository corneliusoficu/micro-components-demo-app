package nl.vu.dynamicplugins.stockslist;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import nl.vu.dynamicplugins.stockslist.model.PolygonResponse;
import nl.vu.dynamicplugins.stockslist.model.UserStock;
import org.apache.cxf.jaxrs.client.WebClient;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class StocksRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(StocksRetriever.class);

    private static final String POLYGON_API_KEY = "WLRMH113Y_lhuyusaKZD8i3VgqLzaaiv";
    private static final String POLYGON_STOCK_ENDPOINT = "https://api.polygon.io/v2/aggs/ticker/%s/prev?adjusted=true&apiKey=%s";

    MongoDBHandler mongoDBHandler;
    WebClient webClient;

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
        String endpoint = String.format(POLYGON_STOCK_ENDPOINT, ticker.toUpperCase(), POLYGON_API_KEY);
        MappingJsonFactory factory = new MappingJsonFactory();
        Response response = WebClient.create(endpoint).get();
        JsonParser parser = factory.createParser((InputStream)response.getEntity());
        PolygonResponse polygonResponse= parser.readValueAs(PolygonResponse.class);

        if(polygonResponse.getResults() == null || polygonResponse.getResults().isEmpty()) {
            LOGGER.warn("No stocks results could be retrieved for ticker: {}", ticker);
            LOGGER.warn(polygonResponse.toString());
            return 0.0;
        }
        return (double) polygonResponse.getResults().get(0).get("c");
    }

    public List<UserStock> storeUserStocks(String email, List<UserStock> stocksToBeSaved) {
        mongoDBHandler.storeUserStocks(email, stocksToBeSaved);
        return getUserOwnedStocks(email);
    }
}
