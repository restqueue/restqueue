package com.restqueue.framework.client.results;

import com.restqueue.framework.client.exception.HttpResponseErrorBean;

/**
 * This class holds the details of a response for a conditional PUT (for example an update to a message). It details
 * the eTag of the response (which can be used in subsequent attempts) and the success/failure of the operation. As there
 * is a body supplied as part of the response, it is available through the method getBody().<BR/><BR/>
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
 * Date: Jan 23, 2011
 * Time: 8:23:57 PM
 */
public class ConditionalPutResult implements Result {
    private String etag;
    private String body;
    private final ExecutionResult executionResult=new ExecutionResult();

    public ConditionalPutResult() {
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

    @Override
    public String toString() {
        return "ConditionalPutResult{" +
                "etag='" + etag + '\'' +
                ", body='" + body + '\'' +
                ", executionResult=" + executionResult +
                '}';
    }
}
