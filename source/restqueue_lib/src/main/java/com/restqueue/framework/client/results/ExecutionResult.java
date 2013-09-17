package com.restqueue.framework.client.results;

import com.restqueue.framework.client.exception.HttpResponseErrorBean;

/**
 * This represents the common fields that all http method responses contain. They are the success of the operation,
 * the http response code and the exception details<BR/><BR/>
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
 * Date: Jan 15, 2011
 * Time: 12:35:06 AM
 */
public class ExecutionResult implements Result {
    private HttpResponseErrorBean exception;
    private boolean success;
    private int responseCode;

    public ExecutionResult() {
    }

    public HttpResponseErrorBean getException() {
        return exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setException(HttpResponseErrorBean exception) {
        this.exception = exception;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "exception=" + exception +
                ", success=" + success +
                ", responseCode=" + responseCode +
                '}';
    }
}
