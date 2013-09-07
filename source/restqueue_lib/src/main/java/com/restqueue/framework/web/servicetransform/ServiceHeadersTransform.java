package com.restqueue.framework.web.servicetransform;

import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.service.transport.ServiceHeaders;

import javax.ws.rs.core.HttpHeaders;
import java.util.List;

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
 * Time: 7:42:25 PM
 */
public class ServiceHeadersTransform {
    public static ServiceHeaders serviceHeadersFromHttpHeaders(HttpHeaders httpHeaders) {
        final ServiceHeaders.ServiceHeadersBuilder serviceHeaderBuilder = new ServiceHeaders.ServiceHeadersBuilder();

        for (CustomHeaders customHeaders : CustomHeaders.values()) {
            final List<String> headerValues = httpHeaders.getRequestHeader(customHeaders.getName());
            if (headerValues != null) {
                for (String value : headerValues) {
                    serviceHeaderBuilder.addHeader(customHeaders, value);
                }
            }
        }
        return serviceHeaderBuilder.build();
    }
}
