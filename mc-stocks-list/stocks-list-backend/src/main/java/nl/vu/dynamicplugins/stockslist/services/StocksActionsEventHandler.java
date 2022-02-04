package nl.vu.dynamicplugins.stockslist.services;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vu.dynamicplugins.stockslist.StocksListOperationsHandler;
import nl.vu.dynamicplugins.stockslist.model.UserStock;

@Component(property = {
    EventConstants.EVENT_TOPIC + "=" + "nl/vu/dynamicplugins/stocksactions/StockOrderExecutor/*",
})

public class StocksActionsEventHandler implements EventHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(StocksActionsEventHandler.class);

    StocksListOperationsHandler stocksListOperationsHandler;

    public StocksActionsEventHandler() {
        stocksListOperationsHandler = new StocksListOperationsHandler();
    }

    @Override
    public void handleEvent(Event event) {
        String email = (String) event.getProperty("email");
        String ticker = (String) event.getProperty("ticker");
        String shares = (String) event.getProperty("shares");

        UserStock userStock = new UserStock();
        userStock.setEmail(email);
        userStock.setTicker(ticker);
        userStock.setShares(Double.valueOf(shares));
        
        List<UserStock> userStocksList = new ArrayList<>();
        userStocksList.add(userStock);
        
        LOGGER.info("Received a stock order event from event admin, email: {}, ticker: {}, shares: {}", email, ticker, shares);   

        if(event.getTopic().endsWith("BUY")) {
            stocksListOperationsHandler.storeUserStocks(email, userStocksList);
        }
    }
}
