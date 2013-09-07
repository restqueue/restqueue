package com.restqueue.framework.client.common.messageheaders;

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
 * Date: Jan 15, 2011
 * Time: 6:13:17 PM
 */
public enum CustomHeaders {
    CACHE_CONTROL("Cache-Control", null),
    CREATED_DATE("x-restqueue-message-created", null),
    CREATOR("x-restqueue-message-creator", null),
    ETAG("ETag", null),
    IF_MATCH("If-Match", null),
    IF_NONE_MATCH("If-None-Match", null),
    LAST_MODIFIED("Last-Modified", null),
    LOCATION("Location", null),
    MESSAGE_BATCH_KEY("x-restqueue-message-batchkey", null),
    MESSAGE_CONSUMER_ID("x-restqueue-message-consumer", null),
    MESSAGE_DELAY("x-restqueue-message-delay", -1L),
    MESSAGE_DELAY_UNTIL("x-restqueue-message-delay-until",null),
    MESSAGE_PRIORITY("x-restqueue-message-priority", null),
    MESSAGE_SEQUENCE("x-restqueue-message-sequence", -1L),
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
