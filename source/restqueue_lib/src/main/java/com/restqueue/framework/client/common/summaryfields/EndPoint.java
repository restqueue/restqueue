package com.restqueue.framework.client.common.summaryfields;

import javax.ws.rs.core.MediaType;

/**
 * This class represents an operational end point for the framework and is generally used to render options to the web
 * administration pages.<BR/><BR/>
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
 * Date: Mar 11, 2011
 * Time: 9:51:55 PM
 */
public class EndPoint {
    public static final String GET="GET";
    public static final String PUT="PUT";
    public static final String POST="POST";
    public static final String DELETE="DELETE";

    public static final String DEFAULT_MEDIA_TYPES = MediaType.APPLICATION_JSON+";"+MediaType.APPLICATION_XML;

    private String url;
    private String httpMethod=GET;
    private String description;
    private String shortCode;
    private String accepts;
    private String produces= DEFAULT_MEDIA_TYPES;

    public String getUrl() {
        return url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getDescription() {
        return description;
    }

    public String getShortCode() {
        return shortCode;
    }

    public static class EndPointBuilder {
        private EndPoint endPoint=new EndPoint();

        public EndPointBuilder setUrl(String url) {
            endPoint.url=url;
            return this;
        }

        public EndPointBuilder setHttpMethod(String httpMethod) {
            endPoint.httpMethod=httpMethod;
            return this;
        }

        public EndPointBuilder setDescription(String description) {
            endPoint.description=description;
            return this;
        }

        public EndPointBuilder setShortCode(String shortCode) {
            endPoint.shortCode=shortCode;
            return this;
        }

        public EndPointBuilder setAccepts(String accepts) {
            endPoint.accepts=accepts;
            return this;
        }

        public EndPointBuilder setProduces(String produces) {
            endPoint.produces=produces;
            return this;
        }

        public EndPoint build() {
            return endPoint;
        }
    }
}
