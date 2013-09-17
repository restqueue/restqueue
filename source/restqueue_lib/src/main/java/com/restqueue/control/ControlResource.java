package com.restqueue.control;

import com.restqueue.framework.service.server.ServerKillSwitch;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;

/**
 * User: Nik Tomkinson
 * Date: 17/09/2013
 * Time: 20:31
 */
@Path("/control/1.0/stopserver")
public class ControlResource {
    @POST
    @Consumes(MediaType.WILDCARD)
    public Response stopServer() throws URISyntaxException {
        ServerKillSwitch.getInstance().killServer();
        return Response.status(Response.Status.OK).build();
    }
}
