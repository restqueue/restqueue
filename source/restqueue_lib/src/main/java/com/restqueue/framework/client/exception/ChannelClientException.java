package com.restqueue.framework.client.exception;

/**
 * This exception is used to identify to Java client code what has happened when something goes wrong with an operation
 * when using one of the provided Java client libraries.<BR/><BR/>
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
 * Date: Jan 31, 2011
 * Time: 7:36:55 PM
 */
public class ChannelClientException extends RuntimeException{
    public enum ExceptionType {UNKNOWN, TRANSPORT_PROTOCOL, CHARACTER_ENCODING, CONNECTION, BAD_STATE, MISSING_DATA}
    private ExceptionType exceptionType;
    public ChannelClientException(String s, ExceptionType exceptionType) {
        super(s);
        this.exceptionType=exceptionType;
    }

    public ChannelClientException(String s, Throwable throwable, ExceptionType exceptionType) {
        super(s, throwable);
        this.exceptionType=exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }
}
