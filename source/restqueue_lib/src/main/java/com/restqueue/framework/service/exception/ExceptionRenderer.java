package com.restqueue.framework.service.exception;

import javax.ws.rs.core.MediaType;

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
 * Date: Jan 1, 2011
 * Time: 9:33:03 PM
 */
public class ExceptionRenderer {
    public static String renderExceptionAsType(String exceptionMessage, String asType){
        final StringBuilder exceptionStringBuilder = new StringBuilder();
        if(MediaType.APPLICATION_XML.equals(asType)){
            return exceptionStringBuilder.append("<error>").append("<message>").
                    append(exceptionMessage).append("</message>").append("</error>").toString();
        } else if(MediaType.APPLICATION_JSON.equals(asType)){
            return exceptionStringBuilder.append("{").append("\"error\":{\"message\":\"").append(exceptionMessage).append("\"}}").toString();
        }
        else{
            return exceptionMessage;
        }
    }

    public static String renderExceptionAsType(Exception exception, String asType){
        return renderExceptionAsType(exception.getMessage(), asType);
    }
}
