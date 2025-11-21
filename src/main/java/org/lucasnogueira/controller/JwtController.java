package org.lucasnogueira.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.lucasnogueira.service.JwtService;

@Path("/jwt")
@ApplicationScoped
public class JwtController {

    @Inject
    JwtService jwtService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getJwt() {
        String jwt = jwtService.generateJwt();
        return Response.ok("Bearer " + jwt).build();
    }

}
