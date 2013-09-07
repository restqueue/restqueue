package com.restqueue.framework.service.transport;

import java.util.HashMap;
import java.util.Map;

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
 * Date: Jan 18, 2011
 * Time: 9:08:26 AM
 */
public class ServiceRequest {
    private ServiceHeaders serviceHeaders=new ServiceHeaders.ServiceHeadersBuilder().build();
    private String mediaTypeRequested;
    private String body;
    private Map<String,String> parameters =new HashMap<String,String>();

    private ServiceRequest() {
    }

    public String getMediaTypeRequested() {
        return mediaTypeRequested;
    }

    public String getBody() {
        return body;
    }

    public ServiceHeaders getServiceHeaders() {
        return serviceHeaders;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public static class ServiceRequestBuilder{
        private ServiceRequest serviceRequest=new ServiceRequest();

        public ServiceRequestBuilder setServiceHeaders(ServiceHeaders serviceHeaders){
            serviceRequest.serviceHeaders=serviceHeaders;
            return this;
        }

        public ServiceRequestBuilder setMediaTypeRequested(String mediaTypeRequested){
            serviceRequest.mediaTypeRequested=mediaTypeRequested;
            return this;
        }

        public ServiceRequestBuilder setBody(String body){
            serviceRequest.body=body;
            return this;
        }

        public ServiceRequestBuilder addParameter(String name, String value){
            serviceRequest.parameters.put(name, value);
            return this;
        }

        public ServiceRequest build(){
            return serviceRequest;
        }
    }
}
