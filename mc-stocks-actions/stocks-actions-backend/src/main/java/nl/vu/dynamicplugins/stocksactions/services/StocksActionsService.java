package nl.vu.dynamicplugins.stocksactions.services;


import nl.vu.dynamicplugins.core.base.services.BaseEndpoint;
import nl.vu.dynamicplugins.stocksactions.model.StockOrder;
import nl.vu.dynamicplugins.stocksactions.StockOrderExecutor;
import nl.vu.dynamicplugins.stocksactions.StockOrderExecutor;
import nl.vu.dynamicplugins.stocksactions.security.SecurityHandler;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Component(service = StocksActionsService.class, immediate = true, property = //
        { //
                "service.exported.interfaces=*", //
                "service.exported.configs=org.apache.cxf.rs", //
                "org.apache.cxf.rs.address=/stocks-actions", "cxf.bus.prop.skip.default.json.provider.registration=true" } //
)
public class StocksActionsService extends BaseEndpoint {

    private final static Logger LOGGER = LoggerFactory.getLogger(StocksActionsService.class);
    
    private SecurityHandler securityHandler = new SecurityHandler(); 
    private StockOrderExecutor stockOrderExecutor = new StockOrderExecutor();

    @Reference
    EventAdmin eventAdmin;

    @GET
    @Produces({"text/javascript"})
    @Path("app-stocks-actions.js")
    @Override
    public Response view() {
        LOGGER.info("Requesting view for stocks-actions!");
        InputStream viewFileInputStream = getViewFile(getClass().getClassLoader());
        return buildInputStreamResponseResponse(viewFileInputStream);
    }

    @POST
    @Produces({"application/json"})
    @Path("executeStockOrder")
    public Response executeStockOrder(@HeaderParam("Authorization") String token, StockOrder stockOrder) {
        if(token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String email = securityHandler.getAuthorizedUserEmailFromToken(token.substring(7));
        if(email == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        stockOrderExecutor.executeStockOrder(eventAdmin, email, stockOrder);

        return Response.ok().build();
    }

    @Override
    public Response health() {
        LOGGER.info("Requesting health for stocks-actions");
        return super.health();
    }
}