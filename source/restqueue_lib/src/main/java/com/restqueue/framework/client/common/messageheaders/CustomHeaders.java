package com.restqueue.framework.client.common.messageheaders;

/**
 * This class holds the http headers specific for the RESTQueue framework.<BR/><BR/>
 *
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
 * Date: Jan 15, 2011
 * Time: 6:13:17 PM
 */
public enum CustomHeaders {
    /**
     * CACHE_CONTROL is used for conditional GETs
     */
    CACHE_CONTROL("Cache-Control", null),
    /**
     * CREATED_DATE is used to identify the time and date that a message was created and put on the channel
     */
    CREATED_DATE("x-restqueue-message-created", null),
    /**
     * CREATOR is used by the message sender to identify themselves
     */
    CREATOR("x-restqueue-message-creator", null),
    /**
     * ETAG is the eTag value of the resource and is used to prevent lost updates and for conditional GETs
     */
    ETAG("ETag", null),
    /**
     * IF_MATCH is used when sending an update to a message so that updates are not lost
     */
    IF_MATCH("If-Match", null),
    /**
     * IF_NONE_MATCH is used for a conditional GET of the channel contents
     */
    IF_NONE_MATCH("If-None-Match", null),
    /**
     * LAST_MODIFIED is used to identify the last time and date at which the message was changed
     */
    LAST_MODIFIED("Last-Modified", null),
    /**
     * LOCATION is for identifying the URL of a message so that the client can easily view, update or delete the message
     */
    LOCATION("Location", null),
    /**
     * MESSAGE_BATCH_KEY is used to identify that a message is part of a batch and to identify the batch size and the
     * message number in the batch
     */
    MESSAGE_BATCH_KEY("x-restqueue-message-batchkey", null),
    /**
     * MESSAGE_CONSUMER_ID is used by a message consumer to identify themselves during assignment of a message
     */
    MESSAGE_CONSUMER_ID("x-restqueue-message-consumer", null),
    /**
     * MESSAGE_DELAY is used to set the delay on a message so that it cannot be retrieved by any message consumer until
     * the delay has expired. The value is either an integer number of seconds or in the form '1y:2M:3w:4d:5h:6m:7s' for
     * 1 year, 2 Months, 3 weeks, 4 days, 5 hours, 6 minutes and 7 seconds (some parts can be missing)
     */
    MESSAGE_DELAY("x-restqueue-message-delay", -1L),
    /**
     * MESSAGE_DELAY_UNTIL is used by the message sender to explicitly set the date after which the message becomes viewable
     * through the channel contents URL
     */
    MESSAGE_DELAY_UNTIL("x-restqueue-message-delay-until",null),
    /**
     * MESSAGE_PRIORITY is used by the message sender to identify the numeric priority of the message
     */
    MESSAGE_PRIORITY("x-restqueue-message-priority", null),
    /**
     * MESSAGE_SEQUENCE is used by the message sender to set the strict sequence number for a message when it is part of a
     * series of messages
     */
    MESSAGE_SEQUENCE("x-restqueue-message-sequence", -1L),
    /**
     * RETURN_ADDRESSES is used for the address that a message sender sets as a return (or reply-to) address or the address that a
     * message listener wants to receive notifications on
     */
    RETURN_ADDRESSES("x-restqueue-return-addresses", null);

    private String name;
    private Object nullEquivalentValue;

    private CustomHeaders(String name, Object nullEquivalentValue) {
        this.name = name;
        this.nullEquivalentValue = nullEquivalentValue;
    }

    public String getName() {
        return name;
    }

    public Object getNullEquivalentValue() {
        return nullEquivalentValue;
    }
}
