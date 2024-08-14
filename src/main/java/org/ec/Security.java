package org.ec;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import io.smallrye.jwt.build.Jwt;

@Path("/jwt")
public class Security {
    private Logger log = LoggerFactory.getLogger(Security.class);

    @GET
    @Path("/generate")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public String jwt(@Context SecurityContext securityContext) {
        log.info("PROCESO DE JWT");
        return generateJWT(securityContext.getUserPrincipal().getName());
    }

    @Transactional
    public String generateJWT(String request){
        Instant instantExpiresAt = Instant.now().plusSeconds(10);
        String jwt =Jwt.upn(request)
        .groups("WEB")
        .expiresAt(instantExpiresAt).sign();
        JwtDB.addJwt(jwt);
        return jwt;

   }
}
