package com.restqueue.framework.service.resourcedelegate;

import com.restqueue.common.arguments.ArgumentMetaData;
import com.restqueue.common.arguments.ServerArguments;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.service.notification.MessageListenerNotification;
import com.restqueue.framework.service.notification.MessageListenerNotificationRepository;
import com.restqueue.framework.service.server.AbstractServer;
import com.restqueue.framework.service.transport.ServiceHeaders;
import com.restqueue.framework.service.transport.ServiceRequest;
import com.restqueue.framework.service.transport.ServiceResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
    * Copyright 2010-2013 Nik Tomkinson

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 * Date: May 31, 2011
 * Time: 10:42:22 PM
 */
public class ChannelResourceDelegateTest {
    @BeforeClass
    public static void setupClass() {
        final List<ArgumentMetaData> allowedArguments=new ArrayList<ArgumentMetaData>();
        allowedArguments.add(new ArgumentMetaData(AbstractServer.SPECIFIED_PORT_SWITCH,"Port", ArgumentMetaData.ArgumentMetaDataType.INTEGER, AbstractServer.PORT));
        allowedArguments.add(new ArgumentMetaData(AbstractServer.NO_CACHE_SWITCH,"No Cache", ArgumentMetaData.ArgumentMetaDataType.BOOLEAN, false));
        allowedArguments.add(new ArgumentMetaData(AbstractServer.SPECIFIED_PERSISTENCE_SWITCH, "Persistence", ArgumentMetaData.ArgumentMetaDataType.STRING, "Normal"));
        allowedArguments.add(new ArgumentMetaData(AbstractServer.SPECIFIED_PERSISTENCE_FREQUENCY_SWITCH, "Persistence Frequency in seconds",
                ArgumentMetaData.ArgumentMetaDataType.INTEGER, 30000));

        ServerArguments.createInstance(allowedArguments, new String[]{});
    }

    @Test
    public void delegateShouldRegisterListenerCorrectly(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotificationRepository.getOrCreateNotificationInstance(ChannelResourceDelegateTest.class);
        final ChannelResourceDelegate channelResourceDelegate = new ChannelResourceDelegate(null, null, messageListenerNotification);

        final String fullRegistrationUrl = "/channel/messagelisteners";

        final ServiceRequest.ServiceRequestBuilder builder = new ServiceRequest.ServiceRequestBuilder();
        builder.setMediaTypeRequested(MediaType.APPLICATION_XML);

        final ServiceHeaders.ServiceHeadersBuilder headersBuilder = new ServiceHeaders.ServiceHeadersBuilder();

        final ReturnAddress emailReturnAddress = new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.com");

        headersBuilder.addHeader(CustomHeaders.RETURN_ADDRESSES, emailReturnAddress.format());

        builder.setServiceHeaders(headersBuilder.build());

        builder.addParameter("fullRegistrationUrl", fullRegistrationUrl+"/listener1");
        builder.addParameter("registrationUrl", fullRegistrationUrl);
        builder.addParameter("registrationPoint", "ALL");
        builder.addParameter("messageListenerId", "listener1");

        final ServiceRequest serviceRequest = builder.build();


        final ServiceResponse response = channelResourceDelegate.registerMessageListener(serviceRequest);

        System.out.println("response = " + response.getBody());

        assertEquals(200, response.getReturnCode());
    }

    @Test
    public void delegateShouldUnRegisterListenerCorrectly(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotificationRepository.getOrCreateNotificationInstance(ChannelResourceDelegateTest.class);
        final ChannelResourceDelegate channelResourceDelegate = new ChannelResourceDelegate(null, null, messageListenerNotification);

        final String fullRegistrationUrl = "/channel/messagelisteners";

        final ServiceRequest.ServiceRequestBuilder builder = new ServiceRequest.ServiceRequestBuilder();
        builder.setMediaTypeRequested(MediaType.APPLICATION_XML);

        final ServiceHeaders.ServiceHeadersBuilder headersBuilder = new ServiceHeaders.ServiceHeadersBuilder();

        final ReturnAddress emailReturnAddress = new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.com");

        headersBuilder.addHeader(CustomHeaders.RETURN_ADDRESSES, emailReturnAddress.format());

        builder.setServiceHeaders(headersBuilder.build());

        builder.addParameter("fullRegistrationUrl", fullRegistrationUrl+"/listener1");
        builder.addParameter("registrationUrl", fullRegistrationUrl);
        builder.addParameter("registrationPoint", "ALL");
        builder.addParameter("messageListenerId", "listener1");

        final ServiceRequest serviceRequest = builder.build();

        channelResourceDelegate.registerMessageListener(serviceRequest);

        final ServiceRequest.ServiceRequestBuilder builderForUnregistration = new ServiceRequest.ServiceRequestBuilder();
        builderForUnregistration.setMediaTypeRequested(MediaType.APPLICATION_XML);

        builder.addParameter("fullRegistrationUrl", fullRegistrationUrl+"/listener1");
        builder.addParameter("registrationUrl", fullRegistrationUrl);
        builder.addParameter("messageListenerId", "listener1");

        final ServiceRequest serviceRequestForUnregistration = builder.build();

        final ServiceResponse response = channelResourceDelegate.unRegisterMessageListener(serviceRequestForUnregistration);

        System.out.println("response = " + response.getBody());

        assertEquals(200, response.getReturnCode());
    }

    @Test
    public void delegateShouldDealWithUnRegisteringNonRegisteredListenerCorrectly(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotificationRepository.getOrCreateNotificationInstance(ChannelResourceDelegateTest.class);
        final ChannelResourceDelegate channelResourceDelegate = new ChannelResourceDelegate(null, null, messageListenerNotification);

        final String fullRegistrationUrl = "/channel/messagelisteners";

        final ServiceRequest.ServiceRequestBuilder builder = new ServiceRequest.ServiceRequestBuilder();
        builder.setMediaTypeRequested(MediaType.APPLICATION_XML);

        final ServiceHeaders.ServiceHeadersBuilder headersBuilder = new ServiceHeaders.ServiceHeadersBuilder();

        final ReturnAddress emailReturnAddress = new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.com");

        headersBuilder.addHeader(CustomHeaders.RETURN_ADDRESSES, emailReturnAddress.format());

        builder.setServiceHeaders(headersBuilder.build());

        builder.addParameter("fullRegistrationUrl", fullRegistrationUrl+"/listener1");
        builder.addParameter("registrationUrl", fullRegistrationUrl);
        builder.addParameter("registrationPoint", "ALL");
        builder.addParameter("messageListenerId", "listener1");

        final ServiceRequest serviceRequest = builder.build();

        channelResourceDelegate.registerMessageListener(serviceRequest);

        final ServiceRequest.ServiceRequestBuilder builderForUnregistration = new ServiceRequest.ServiceRequestBuilder();
        builderForUnregistration.setMediaTypeRequested(MediaType.APPLICATION_XML);

        builder.addParameter("fullRegistrationUrl", fullRegistrationUrl+"/nonExistingListener");
        builder.addParameter("registrationUrl", fullRegistrationUrl);
        builder.addParameter("messageListenerId", "nonExistingListener");

        final ServiceRequest serviceRequestForUnregistration = builder.build();

        final ServiceResponse response = channelResourceDelegate.unRegisterMessageListener(serviceRequestForUnregistration);

        System.out.println("response = " + response.getBody());

        assertEquals(200, response.getReturnCode());
    }

    @Test
    public void delegateShouldRegisterUnRegisterListenerTwiceCorrectly(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotificationRepository.getOrCreateNotificationInstance(ChannelResourceDelegateTest.class);
        final ChannelResourceDelegate channelResourceDelegate = new ChannelResourceDelegate(null, null, messageListenerNotification);

        final String fullRegistrationUrl = "/channel/messagelisteners";

        final ServiceRequest.ServiceRequestBuilder builder = new ServiceRequest.ServiceRequestBuilder();
        builder.setMediaTypeRequested(MediaType.APPLICATION_XML);

        final ServiceHeaders.ServiceHeadersBuilder headersBuilder = new ServiceHeaders.ServiceHeadersBuilder();

        final ReturnAddress emailReturnAddress = new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.com");

        headersBuilder.addHeader(CustomHeaders.RETURN_ADDRESSES, emailReturnAddress.format());

        builder.setServiceHeaders(headersBuilder.build());

        builder.addParameter("fullRegistrationUrl", fullRegistrationUrl+"/listener1");
        builder.addParameter("registrationUrl", fullRegistrationUrl);
        builder.addParameter("registrationPoint", "ALL");
        builder.addParameter("messageListenerId", "listener1");

        final ServiceRequest serviceRequest = builder.build();

        channelResourceDelegate.registerMessageListener(serviceRequest);

        final ServiceRequest.ServiceRequestBuilder builderForUnregistration = new ServiceRequest.ServiceRequestBuilder();
        builderForUnregistration.setMediaTypeRequested(MediaType.APPLICATION_XML);

        builder.addParameter("fullRegistrationUrl", fullRegistrationUrl+"/listener1");
        builder.addParameter("registrationUrl", fullRegistrationUrl);
        builder.addParameter("messageListenerId", "listener1");

        final ServiceRequest serviceRequestForUnregistration = builder.build();

        channelResourceDelegate.unRegisterMessageListener(serviceRequestForUnregistration);
        channelResourceDelegate.registerMessageListener(serviceRequest);
        channelResourceDelegate.unRegisterMessageListener(serviceRequestForUnregistration);
    }

}
