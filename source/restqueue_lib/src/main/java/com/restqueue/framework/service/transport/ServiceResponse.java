package com.restqueue.framework.service.transport;

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
 * Time: 9:08:41 AM
 */
public class ServiceResponse {
    private ServiceHeaders serviceHeaders=new ServiceHeaders.ServiceHeadersBuilder().build();
    private Object body;
    private int returnCode;
    private String mediaType;

    private ServiceResponse() {
    }

    public ServiceHeaders getServiceHeaders() {
        return serviceHeaders;
    }

    public Object getBody() {
        return body;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getMediaType() {
        return mediaType;
    }

    public static class ServiceResponseBuilder{
        private ServiceResponse serviceResponse=new ServiceResponse();

        public ServiceResponseBuilder setServiceHeaders(ServiceHeaders serviceHeaders){
            serviceResponse.serviceHeaders=serviceHeaders;
            return this;
        }

        public ServiceResponseBuilder setBody(Object body){
            serviceResponse.body=body;
            return this;
        }

        public ServiceResponseBuilder setReturnCode(int returnCode){
            serviceResponse.returnCode=returnCode;
            return this;
        }

        public ServiceResponseBuilder setMediaType(String mediaType){
            serviceResponse.mediaType=mediaType;
            return this;
        }

        public ServiceResponse build(){
            return serviceResponse;
        }
    }
}
