package nl.vu.dynamicplugins.stocksgraph.services;


import nl.vu.dynamicplugins.core.base.services.BaseEndpoint;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Component(service = StocksGraphService.class, immediate = true, property = //
        { //
                "service.exported.interfaces=*", //
                "service.exported.configs=org.apache.cxf.rs", //
                "org.apache.cxf.rs.address=/stocks-graph", "cxf.bus.prop.skip.default.json.provider.registration=true" } //
)
public class StocksGraphService extends BaseEndpoint {

    private final static Logger LOGGER = LoggerFactory.getLogger(StocksGraphService.class);

    @GET
    @Produces({"text/javascript"})
    @Path("app-stocks-graph.js")
    @Override
    public Response view() {
        LOGGER.info("Requesting view for stocks-graph!");
        InputStream viewFileInputStream = getViewFile(getClass().getClassLoader());
        return buildInputStreamResponseResponse(viewFileInputStream);
    }

    @Override
    public Response health() {
        LOGGER.info("Requesting health for stocks-graph");
        return super.health();
    }
}