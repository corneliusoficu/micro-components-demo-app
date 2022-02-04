package nl.vu.dynamicplugins.stocksactions;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import nl.vu.dynamicplugins.stocksactions.model.StockOrder;

public class StockOrderExecutor  {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockOrderExecutor.class);

    private static final String TOPIC_STOCK_ORDER_BASE = "nl/vu/dynamicplugins/stocksactions/StockOrderExecutor/";
    private static final String TOPIC_STOCK_ORDER_BUY = TOPIC_STOCK_ORDER_BASE + "BUY";
    private static final String TOPIC_STOCK_ORDER_SELL = TOPIC_STOCK_ORDER_BASE + "SELL";

    public void executeStockOrder(EventAdmin eventAdmin, String userEmail, StockOrder stockOrder) {
        Event event = null;
        
        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("email", userEmail);
        eventProperties.put("ticker", stockOrder.getTicker());
        eventProperties.put("shares", stockOrder.getShares());
        
        switch(stockOrder.getOrderType().toUpperCase()) {
            case "BUY":
                event = new Event(TOPIC_STOCK_ORDER_BUY, eventProperties);
                break;
            case "SELL":
                event = new Event(TOPIC_STOCK_ORDER_SELL, eventProperties);
                break;
            default:
                throw new IllegalStateException(String.format("Unkown stock order type: %s", stockOrder.getOrderType().toUpperCase()));
        }
        
        eventAdmin.postEvent(event);
        LOGGER.info("Sending a stock order event of type {} using Event Admin to topic: {}", stockOrder.getOrderType().toUpperCase(), event.getTopic());
    }
}
