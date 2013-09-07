package com.restqueue.framework.service.notification;

import com.restqueue.common.arguments.ArgumentMetaData;
import com.restqueue.common.arguments.ServerArguments;
import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.server.AbstractServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

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
 * Time: 1:50:56 AM
 */
public class MessageListenerNotificationUnitTest {
    private static final String TEST_LISTENER_ID_ONE = "testListenerId1";
    private static final String TEST_LISTENER_ID_TWO = "testListenerId2";
    private static final String TEST_URL_ONE = "testURL1";
    private static final String TEST_URL_TWO = "testURL2";

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

    @Before
    public void clearOutRegistrations(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotificationRepository.getOrCreateNotificationInstance(MessageListenerNotificationUnitTest.class);
        messageListenerNotification.registeredMessageListeners.clear();
        messageListenerNotification.messageListenerGroupRegistration.clear();
    }

    @Test
    public void listenerNotificationShouldRegisterOneListenerCorrectly(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotification.createInstance(MessageListenerNotificationUnitTest.class);

        assertTrue(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertTrue(messageListenerNotification.registeredMessageListeners.isEmpty());

        final MessageListenerAddress address = new MessageListenerAddress();
        address.setListenerId(TEST_LISTENER_ID_ONE);
        address.setReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.org"));

        messageListenerNotification.registerMessageListener(address, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);

        assertFalse(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertFalse(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNotNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNotNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE));
        assertEquals(1, messageListenerNotification.registeredMessageListeners.size());
        assertEquals(1, messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE).getListenerIds().size());

        assertEquals(TEST_LISTENER_ID_ONE, messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE).getListenerId());
        assertEquals(ReturnAddressType.EMAIL, messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE).getReturnAddress().getType());
    }

    @Test
    public void listenerNotificationShouldRegisterTwoListenersCorrectly(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotification.createInstance(MessageListenerNotificationUnitTest.class);

        assertTrue(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertTrue(messageListenerNotification.registeredMessageListeners.isEmpty());

        final MessageListenerAddress address = new MessageListenerAddress();
        address.setListenerId(TEST_LISTENER_ID_ONE);
        address.setReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.org"));

        messageListenerNotification.registerMessageListener(address, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);

        final MessageListenerAddress addressTwo = new MessageListenerAddress();
        addressTwo.setListenerId(TEST_LISTENER_ID_TWO);
        addressTwo.setReturnAddress(new ReturnAddress(ReturnAddressType.URL, "message/me/here"));

        messageListenerNotification.registerMessageListener(addressTwo, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);

        assertFalse(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertFalse(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNotNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNotNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO));
        assertEquals(2, messageListenerNotification.registeredMessageListeners.size());
        assertEquals(2, messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE).getListenerIds().size());

        assertEquals(TEST_LISTENER_ID_TWO, messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO).getListenerId());
        assertEquals(ReturnAddressType.URL, messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO).getReturnAddress().getType());
    }


    @Test
    public void listenerNotificationShouldUnRegisterTwoListenersCorrectly(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotification.createInstance(MessageListenerNotificationUnitTest.class);

        assertTrue(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertTrue(messageListenerNotification.registeredMessageListeners.isEmpty());

        final MessageListenerAddress address = new MessageListenerAddress();
        address.setListenerId(TEST_LISTENER_ID_ONE);
        address.setReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.org"));

        messageListenerNotification.registerMessageListener(address, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);

        final MessageListenerAddress addressTwo = new MessageListenerAddress();
        addressTwo.setListenerId(TEST_LISTENER_ID_TWO);
        addressTwo.setReturnAddress(new ReturnAddress(ReturnAddressType.URL, "message/me/here"));

        messageListenerNotification.registerMessageListener(addressTwo, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);

        assertFalse(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertFalse(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNotNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNotNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO));
        assertEquals(2, messageListenerNotification.registeredMessageListeners.size());
        assertEquals(2, messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE).getListenerIds().size());

        messageListenerNotification.unRegisterMessageListener(address, TEST_URL_ONE);

        assertFalse(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertFalse(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNotNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNotNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO));
        assertNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE));
        assertEquals(1, messageListenerNotification.registeredMessageListeners.size());
        assertEquals(1, messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE).getListenerIds().size());

        messageListenerNotification.unRegisterMessageListener(addressTwo, TEST_URL_ONE);

        assertTrue(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertTrue(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO));
        assertNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE));
        assertEquals(0, messageListenerNotification.registeredMessageListeners.size());
    }

    @Test
    public void listenerNotificationShouldUnRegisterOneUnusedListenerCorrectly(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotification.createInstance(MessageListenerNotificationUnitTest.class);

        assertTrue(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertTrue(messageListenerNotification.registeredMessageListeners.isEmpty());

        final MessageListenerAddress address = new MessageListenerAddress();
        address.setListenerId(TEST_LISTENER_ID_ONE);
        address.setReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.org"));

        messageListenerNotification.unRegisterMessageListener(address, TEST_URL_ONE);

        assertTrue(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertTrue(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE));
    }    

    @Test
    public void listenerNotificationShouldUnRegisterTwoListenersCorrectlyWithMultipleUse(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotification.createInstance(MessageListenerNotificationUnitTest.class);

        assertTrue(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertTrue(messageListenerNotification.registeredMessageListeners.isEmpty());

        final MessageListenerAddress address = new MessageListenerAddress();
        address.setListenerId(TEST_LISTENER_ID_ONE);
        address.setReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.org"));

        messageListenerNotification.registerMessageListener(address, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);

        final MessageListenerAddress addressTwo = new MessageListenerAddress();
        addressTwo.setListenerId(TEST_LISTENER_ID_TWO);
        addressTwo.setReturnAddress(new ReturnAddress(ReturnAddressType.URL, "message/me/here"));

        messageListenerNotification.registerMessageListener(addressTwo, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);
        messageListenerNotification.registerMessageListener(addressTwo, TEST_URL_TWO, RegistrationPoint.ALL, new Object[0]);

        assertFalse(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertFalse(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNotNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNotNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_TWO));
        assertNotNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE));
        assertNotNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO));
        assertEquals(2, messageListenerNotification.registeredMessageListeners.size());
        assertEquals(2, messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE).getListenerIds().size());
        assertEquals(1, messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_TWO).getListenerIds().size());

        messageListenerNotification.unRegisterMessageListener(address, TEST_URL_ONE);

        assertFalse(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertFalse(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNotNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNotNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO));
        assertNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE));
        assertEquals(1, messageListenerNotification.registeredMessageListeners.size());
        assertEquals(1, messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE).getListenerIds().size());

        messageListenerNotification.unRegisterMessageListener(addressTwo, TEST_URL_ONE);

        assertFalse(messageListenerNotification.messageListenerGroupRegistration.isEmpty());
        assertFalse(messageListenerNotification.registeredMessageListeners.isEmpty());
        assertNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_ONE));
        assertNotNull(messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_TWO));
        assertEquals(1, messageListenerNotification.messageListenerGroupRegistration.get(TEST_URL_TWO).getListenerIds().size());
        assertNotNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_TWO));
        assertNull(messageListenerNotification.registeredMessageListeners.get(TEST_LISTENER_ID_ONE));
        assertEquals(1, messageListenerNotification.registeredMessageListeners.size());
    }

    @Test
    public void listenerNotificationShouldCorrectlyNotifyTwoListeners(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotification.createInstance(MessageListenerNotificationUnitTest.class);

        messageListenerNotification.setMessageListenerNotifier(ReturnAddressType.EMAIL, new MockEmailMessageListenerNotifier());

        final MessageListenerAddress address = new MessageListenerAddress();
        address.setListenerId(TEST_LISTENER_ID_ONE);
        address.setReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.org"));

        messageListenerNotification.registerMessageListener(address, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);

        final MessageListenerAddress addressTwo = new MessageListenerAddress();
        addressTwo.setListenerId(TEST_LISTENER_ID_TWO);
        addressTwo.setReturnAddress(new ReturnAddress(ReturnAddressType.URL, "message/me/here"));

        messageListenerNotification.registerMessageListener(addressTwo, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);
        messageListenerNotification.registerMessageListener(addressTwo, TEST_URL_TWO, RegistrationPoint.ALL, new Object[0]);

        messageListenerNotification.notifyMessageListeners(new EntryWrapper(), new ChannelState());
        
    }

    @Test
    public void listenerNotificationShouldCorrectlyNotifyOneOfTwoListeners(){
        final MessageListenerNotification messageListenerNotification = MessageListenerNotification.createInstance(MessageListenerNotificationUnitTest.class);

        final MessageListenerAddress address = new MessageListenerAddress();
        address.setListenerId(TEST_LISTENER_ID_ONE);
        address.setReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "test@messagelistener.org"));

        messageListenerNotification.registerMessageListener(address, TEST_URL_ONE, RegistrationPoint.ALL, new Object[0]);

        final MessageListenerAddress addressTwo = new MessageListenerAddress();
        addressTwo.setListenerId(TEST_LISTENER_ID_TWO);
        addressTwo.setReturnAddress(new ReturnAddress(ReturnAddressType.URL, "message/me/here"));

        messageListenerNotification.registerMessageListener(addressTwo, TEST_URL_TWO, RegistrationPoint.SPECIFIC_BATCH, new Object[]{"BatchID_1"});

        final EntryWrapper entryWrapper = new EntryWrapper.EntryWrapperBuilder().setBatchKey(new BatchKey("BatchID_2",1,1)).build();


        final Map<ReturnAddressType, MessageListenerNotifier> listenerNotifiers = new HashMap<ReturnAddressType, MessageListenerNotifier>();

        final MockEmailMessageListenerNotifier mockEmailMessageListenerNotifier = new MockEmailMessageListenerNotifier();
        final MockUrlMessageListenerNotifier mockUrlMessageListenerNotifier = new MockUrlMessageListenerNotifier();
        listenerNotifiers.put(ReturnAddressType.EMAIL, mockEmailMessageListenerNotifier);
        listenerNotifiers.put(ReturnAddressType.URL, mockUrlMessageListenerNotifier);

        MessageListenerNotification.messageListenerNotifiers = listenerNotifiers;

        messageListenerNotification.notifyMessageListeners(entryWrapper, new ChannelState());

        assertEquals(1, mockEmailMessageListenerNotifier.getMethodCallCount());
        assertEquals(0, mockUrlMessageListenerNotifier.getMethodCallCount());

    }

    private class MockEmailMessageListenerNotifier implements MessageListenerNotifier {
        private int methodCallCount=0;
        public void notifyListener(MessageListenerAddress messageListenerAddress, final String location, final String eTag) {
            methodCallCount++;
        }

        public int getMethodCallCount() {
            return methodCallCount;
        }
    }

    private class MockUrlMessageListenerNotifier implements MessageListenerNotifier {
        private int methodCallCount=0;
        public void notifyListener(MessageListenerAddress messageListenerAddress, final String location, final String eTag) {
            methodCallCount++;
        }

        public int getMethodCallCount() {
            return methodCallCount;
        }
    }

}
