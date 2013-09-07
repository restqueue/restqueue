package com.restqueue.framework.web.servicetransform;

import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.service.transport.ServiceResponse;

import javax.ws.rs.core.Response;
import java.util.List;
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
 * Date: Jan 17, 2011
 * Time: 7:44:26 PM
 */
public class ServiceResponseTransform {
    public static Response httpResponseFromServiceResponse(ServiceResponse serviceResponse){
        final Response.ResponseBuilder builder = Response.status(serviceResponse.getReturnCode()).entity(serviceResponse.getBody());
        for(Map.Entry<CustomHeaders, List<String>> entry:serviceResponse.getServiceHeaders().getHeaderMap().entrySet()){
            for(String value:entry.getValue()){
                builder.header(entry.getKey().getName(),value);
            }
        }
        builder.type(serviceResponse.getMediaType());

        return builder.build();
    }
}
