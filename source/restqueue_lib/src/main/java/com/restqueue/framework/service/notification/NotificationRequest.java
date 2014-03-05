package com.restqueue.framework.service.notification;

import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.channelstate.ChannelState;

/**
 * User: Nik Tomkinson
 * Date: 02/01/2014
 * Time: 23:40
 */
public class NotificationRequest {
    private MessageListenerNotification messageListenerNotification;
    private EntryWrapper entryWrapper;
    private ChannelState channelState;

    public NotificationRequest(MessageListenerNotification messageListenerNotification, EntryWrapper entryWrapper, ChannelState channelState) {
        this.messageListenerNotification = messageListenerNotification;
        this.entryWrapper = entryWrapper;
        this.channelState = channelState;
    }

    public MessageListenerNotification getMessageListenerNotification() {
        return messageListenerNotification;
    }

    public EntryWrapper getEntryWrapper() {
        return entryWrapper;
    }

    public ChannelState getChannelState() {
        return channelState;
    }
}
