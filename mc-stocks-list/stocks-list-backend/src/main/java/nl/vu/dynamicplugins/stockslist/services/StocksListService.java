package nl.vu.dynamicplugins.stockslist.services;


import nl.vu.dynamicplugins.core.base.services.BaseEndpoint;
import nl.vu.dynamicplugins.stockslist.SecurityHandler;
import nl.vu.dynamicplugins.stockslist.StocksRetriever;
import nl.vu.dynamicplugins.stockslist.model.UserStock;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

@Component(service = StocksListService.class, immediate = true, property = //
        { //
                "service.exported.interfaces=*", //
                "service.exported.configs=org.apache.cxf.rs", //
                "org.apache.cxf.rs.address=/stocks-list", "cxf.bus.prop.skip.default.json.provider.registration=true" } //
)
public class StocksListService extends BaseEndpoint {
    private final static Logger LOGGER = LoggerFactory.getLogger(StocksListService.class);

    private SecurityHandler securityHandler;
    private StocksRetriever stocksRetriever;

    public StocksListService() {
        securityHandler = new SecurityHandler();
        stocksRetriever = new StocksRetriever();
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

        List<UserStock> userStocks = stocksRetriever.getUserOwnedStocks(email);
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

        List<UserStock> userStocks = stocksRetriever.storeUserStocks(email, stocksToBeSaved);
        return Response.ok().entity(userStocks).build();
    }



    @Override
    public Response health() {
        LOGGER.info("Requesting health for stocks-list");
        return super.health();
    }
}