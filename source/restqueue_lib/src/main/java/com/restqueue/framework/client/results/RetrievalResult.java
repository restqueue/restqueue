package com.restqueue.framework.client.results;

import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.exception.HttpResponseErrorBean;

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
 * Date: Jan 15, 2011
 * Time: 12:39:15 AM
 */
public class RetrievalResult implements Result {
    private Map<CustomHeaders, List<String>> headersMap=new HashMap<CustomHeaders, List<String>>();
    private String etag;
    private String body;
    private final ExecutionResult executionResult=new ExecutionResult();

    public RetrievalResult() {
    }

    public String getEtag() {
        return etag;
    }

    public String getBody() {
        return body;
    }

    public HttpResponseErrorBean getException() {
        return executionResult.getException();
    }

    public void setResponseCode(int code) {
        this.executionResult.setResponseCode(code);
    }

    public int getResponseCode() {
        return executionResult.getResponseCode();
    }


    public boolean isSuccess() {
        return executionResult.isSuccess();
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setException(HttpResponseErrorBean exception) {
        this.executionResult.setException(exception);
    }

    public void setSuccess(boolean success) {
        this.executionResult.setSuccess(success);
    }

    public Map<CustomHeaders, List<String>> getHeadersMap() {
        return headersMap;
    }

    public void setHeadersMap(Map<CustomHeaders, List<String>> headersMap) {
        this.headersMap = headersMap;
    }

    public void addHeader(CustomHeaders customHeader, String value){
        if(this.headersMap.get(customHeader)==null){
            this.headersMap.put(customHeader,new ArrayList<String>());
        }
        this.headersMap.get(customHeader).add(value);
    }

    @Override
    public String toString() {
        return "RetrievalResult{" +
                "headersMap=" + headersMap +
                ", etag='" + etag + '\'' +
                ", body='" + body + '\'' +
                ", executionResult=" + executionResult +
                '}';
    }
}
