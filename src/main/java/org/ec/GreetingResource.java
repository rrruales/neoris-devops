package org.ec;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/DevOps")
public class GreetingResource {
    private Logger log = LoggerFactory.getLogger(GreetingResource.class);
    private static final String ERROR = "ERROR";

    @Inject
    JsonWebToken jwt;
    @Context
    HttpServerRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response hello(PojoIN requestPojo) {
        log.info("PROCESO INICIAL");
        jwt.getClaimNames();
        if (validateJWT(request.getHeader("X-JWT-KWY"))) {
            PojoOut response = new PojoOut();
            response.message = "Hello " + requestPojo.to + " your message will be send";
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String handleGetError() {
        return ERROR;
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String handlePutError() {
        return ERROR;
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String handleDeleteError() {
        return ERROR;
    }

    @Transactional
    public boolean validateJWT(String token) {
        log.info("PROCESO VALIDACION DE TOKEN");
        try {
            JwtDB jwtDB = JwtDB.getJwt(token);
            if (jwtDB != null) {
                JwtDB.deleteJwt(token);
                return true;
            }
        } catch (Exception er) {
            log.error("Validacion JWT", er);
        }
        return false;
    }
}
