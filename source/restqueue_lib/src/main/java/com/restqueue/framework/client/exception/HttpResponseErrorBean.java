package com.restqueue.framework.client.exception;

/**
 * This class is an object representation of the Http error description that may occur when an error happens as a result
 * of an http GET, PUT, POST or DELETE. It is used inside the Result implementations and simply wraps the http error message.<BR/><BR/>
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
 * Time: 1:21:30 AM
 */
public class HttpResponseErrorBean {
    private String message;

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "HttpResponseErrorBean{" +
                "message='" + message + '\'' +
                '}';
    }
}
