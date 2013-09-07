package com.restqueue.framework.service.transport;

import com.restqueue.framework.client.common.messageheaders.CustomHeaders;

import java.util.ArrayList;
import java.util.HashMap;
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
 * Date: Jan 18, 2011
 * Time: 9:08:56 AM
 */
public class ServiceHeaders {
    private Map<CustomHeaders, List<String>> headerMap = new HashMap<CustomHeaders, List<String>>();

    private ServiceHeaders() {
    }

    public void addHeader(CustomHeaders customHeaders, String value) {
        if (headerMap.get(customHeaders) == null) {
            headerMap.put(customHeaders, new ArrayList<String>());
        }
        headerMap.get(customHeaders).add(value);
    }

    public Map<CustomHeaders, List<String>> getHeaderMap() {
        return headerMap;
    }

    public List<String> getHeaderValueList(CustomHeaders customHeaders) {
        return headerMap.get(customHeaders);
    }

    public static class ServiceHeadersBuilder {
        private ServiceHeaders serviceHeaders = new ServiceHeaders();

        public ServiceHeadersBuilder addHeader(CustomHeaders customHeaders, Object value) {
            if (value != null) {
                serviceHeaders.addHeader(customHeaders, String.valueOf(value));
            }
            return this;
        }

        public ServiceHeaders build() {
            return serviceHeaders;
        }
    }

    public String getSingleStringHeaderValueFromHeaders(CustomHeaders header) {
        if(this.getHeaderValueList(header)==null){
            return null;
        }
        return this.getHeaderValueList(header).get(0);
    }

    public int getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders header) {
        return nullSafeIntHeaderValueFromHeaders(header, 0);
    }

    public int nullSafeIntHeaderValueFromHeaders(CustomHeaders header, int index) {
        if (this.getHeaderValueList(header) == null) {
            return 0;
        }
        return Integer.parseInt(this.getHeaderValueList(header).get(index));
    }

    public boolean hasHeaderValue(CustomHeaders customHeader){
        return (getHeaderValueList(customHeader) != null && getHeaderValueList(customHeader).size() > 0);
    }
}
