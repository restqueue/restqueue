package com.restqueue.framework.client.results;

import com.restqueue.framework.client.exception.HttpResponseErrorBean;

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
 * Time: 12:42:34 AM
 */
public class CreationResult implements Result{
    private String location;
    private String etag;
    private String body;

    private final ExecutionResult executionResult=new ExecutionResult();


    public String getEtag() {
        return this.etag;
    }

    public String getBody() {
        return this.body;
    }

    public HttpResponseErrorBean getException() {
        return executionResult.getException();
    }

    public boolean isSuccess() {
        return executionResult.isSuccess();
    }

    public String getLocation() {
        return location;
    }

    public void setEtag(String etag) {
        this.etag=etag;
    }

    public void setBody(String body) {
        this.body=body;
    }

    public void setException(HttpResponseErrorBean exception) {
        this.executionResult.setException(exception);
    }

    public void setSuccess(boolean success) {
        this.executionResult.setSuccess(success);
    }

    public void setResponseCode(int code) {
        this.executionResult.setResponseCode(code);
    }

    public int getResponseCode(){
        return this.executionResult.getResponseCode();
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "CreationResult{" +
                "location='" + location + '\'' +
                ", executionResult=" + executionResult +
                '}';
    }
}
