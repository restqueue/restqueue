package com.restqueue.framework.service.exception;

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
 * Date: Dec 6, 2010
 * Time: 9:52:13 AM
 */
public class ChannelStoreException extends RuntimeException{

    public enum ExceptionType {CHANNEL_STORE_MAX_CAPACITY, READ_ONLY_STATE_FIELD, ENTRY_NOT_FOUND, INVALID_STATE_FIELD,
        INVALID_ENTRY_DATA_PROVIDED, DUPLICATE_MESSAGE_DATA_NOT_ALLOWED, CHANNEL_CONCURRENCY, INVALID_PRIORITY, INVALID_STATE_FIELD_VALUE, FILE_SYSTEM
    }
    private ExceptionType exceptionType;
    public ChannelStoreException(String message, ExceptionType exceptionType) {
        super(message);
        this.exceptionType=exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }
}
