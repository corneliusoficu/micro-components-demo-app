package nl.vu.dynamicplugins.stockslist.services;


import nl.vu.dynamicplugins.core.base.services.BaseEndpoint;
import nl.vu.dynamicplugins.stockslist.SecurityHandler;
import nl.vu.dynamicplugins.stockslist.StocksListOperationsHandler;
import nl.vu.dynamicplugins.stockslist.model.UserStock;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.event.EventConstants;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

@Component(service = StocksListService.class, immediate = true, property = //
        { //
                "service.exported.interfaces=*", //
                "service.exported.configs=org.apache.cxf.rs", //
                "org.apache.cxf.rs.address=/stocks-list", 
                "cxf.bus.prop.skip.default.json.provider.registration=true",
                EventConstants.EVENT_TOPIC + "=" + "nl/vu/dynamicplugins/stocksactions/StockOrderExecutor/BUY"
        } //
)
public class StocksListService extends BaseEndpoint implements EventHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(StocksListService.class);

    private SecurityHandler securityHandler;
    private StocksListOperationsHandler stocksListOperationsHandler;

    public StocksListService() {
        securityHandler = new SecurityHandler();
        stocksListOperationsHandler = new StocksListOperationsHandler();
    }

    @GET
    @Produces({"text/javascript"})
    @Path("app-stocks-list.js")
    @Override
    public Response view() {
        LOGGER.info("Requesting view for stocks-list!");
        InputStream viewFileInputStream = getViewFile(getClass().getClassLoader());
        return buildInputStreamResponseResponse(viewFileInputStream);
    }

    @GET
    @Produces({"application/json"})
    @Path("owned-stocks")
    public Response getListOfStocksForUser(@HeaderParam("Authorization") String token) {
        if(token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String email = securityHandler.getAuthorizedUserEmailFromToken(token.substring(7));
        if(email == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        List<UserStock> userStocks = stocksListOperationsHandler.getUserOwnedStocks(email);
        return Response.ok().entity(userStocks).build();
    }

    @POST
    @Produces({"application/json"})
    @Path("owned-stocks")
    public Response saveOwnedStocksForUser(@HeaderParam("Authorization") String token, List<UserStock> stocksToBeSaved) {
        if(token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String email = securityHandler.getAuthorizedUserEmailFromToken(token.substring(7));
        if(email == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        List<UserStock> userStocks = stocksListOperationsHandler.storeUserStocks(email, stocksToBeSaved);
        return Response.ok().entity(userStocks).build();
    }

    @Override
    public Response health() {
        LOGGER.info("Requesting health for stocks-list");
        return super.health();
    }

    @Override
    public void handleEvent(Event event) {
        String email = (String) event.getProperty("email");
        String ticker = (String) event.getProperty("ticker");
        String shares = (String) event.getProperty("shares");
        LOGGER.info("Received a stock order event from event admin, email: {}, ticker: {}, shares: {}", email, ticker, shares);
    }
}