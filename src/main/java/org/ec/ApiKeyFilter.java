package org.ec;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ApiKeyFilter {
    private Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    private static final String API_KEY_HEADER = "X-Parse-REST-API-Key";
    private static final String VALID_API_KEY = "2f5ae96c-b558-4c7b-a590-a501ae1c3f6c";

    @ServerRequestFilter(preMatching = true)
    public Response preMatchingFilter(ContainerRequestContext requestContext) {
        log.info("PROCESO DE VALIDACION DE TOKEN");
        String apiKey = requestContext.getHeaderString(API_KEY_HEADER);
        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
        
        return null;
    }
}
