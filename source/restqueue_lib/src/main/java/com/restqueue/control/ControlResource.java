package com.restqueue.control;

import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.common.summaryfields.EndPoint;
import com.restqueue.framework.service.channels.ChannelMetadata;
import com.restqueue.framework.service.channels.ChannelsRegistry;
import com.restqueue.framework.service.server.ServerKillSwitch;
import com.restqueue.framework.service.transport.ServiceRequest;
import com.restqueue.framework.service.transport.ServiceResponse;
import com.restqueue.framework.web.servicetransform.ServiceResponseTransform;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Nik Tomkinson
 * Date: 17/09/2013
 * Time: 20:31
 */
@Path("/control/1.0")
public class ControlResource {
    @POST
    @Path("/stopserver")
    @Consumes(MediaType.WILDCARD)
    public Response stopServer() throws URISyntaxException {
        ServerKillSwitch.getInstance().killServer();
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/allchannels")
    @Produces(MediaType.TEXT_HTML)
    public Response otherChannels(){
        return ServiceResponseTransform.httpResponseFromServiceResponse(
                getAllChannelsPage(new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(MediaType.TEXT_HTML).build()));
    }

    private ServiceResponse getAllChannelsPage(final ServiceRequest serviceRequest){
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).
                setBody(new Serializer().toType(channelRootEndpointList(), serviceRequest.getMediaTypeRequested(), "otherChannels")).build();
    }

    private List<EndPoint> channelRootEndpointList(){
        final List<EndPoint> endPointList = new ArrayList<EndPoint>();
        final List<ChannelMetadata> channelMetadataList = ChannelsRegistry.getInstance().channelsSummary();
        for (ChannelMetadata channelMetadata : channelMetadataList) {
            endPointList.add(channelMetadata.getChannelEndPoint());
        }
        return endPointList;
    }
}
