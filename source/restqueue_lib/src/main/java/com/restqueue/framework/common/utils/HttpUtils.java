package com.restqueue.framework.common.utils;

import com.restqueue.common.utils.ArrayUtils;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import org.apache.http.HttpResponse;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
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
 * Date: Jan 13, 2011
 * Time: 7:43:10 PM
 */
public class HttpUtils {
    public static final String[] SUPPORTED_MEDIA_TYPES = new String[]{MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML};

    public static String nullSafeStringHeaderValueFromResponse(HttpResponse response, CustomHeaders header) {
        if (response.getFirstHeader(header.getName()) == null) {
            return "";
        }
        return response.getFirstHeader(header.getName()).getValue();
    }

    public static String nullSafeStringHeaderValueFromHeaders(HttpHeaders headers, CustomHeaders header, int index) {
        if (headers.getRequestHeader(header.getName()) == null) {
            return "";
        }
        return headers.getRequestHeader(header.getName()).get(index);
    }

    public static int nullSafeIntHeaderValueFromResponse(HttpResponse response, CustomHeaders header) {
        if (response.getFirstHeader(header.getName()) == null) {
            return 0;
        }
        return Integer.parseInt(response.getFirstHeader(header.getName()).getValue());
    }

    public static int nullSafeIntHeaderValueFromHeaders(HttpHeaders headers, CustomHeaders header, int index) {
        if (headers.getRequestHeader(header.getName()) == null) {
            return 0;
        }
        return Integer.parseInt(headers.getRequestHeader(header.getName()).get(index));
    }

    public static String getStringHeaderValueFromHeaders(HttpHeaders headers, CustomHeaders header, int index) {
        return headers.getRequestHeader(header.getName()).get(index);
    }

    public static int getSingleNullSafeIntHeaderValueFromHeaders(HttpHeaders headers, CustomHeaders header) {
        return nullSafeIntHeaderValueFromHeaders(headers, header, 0);
    }

    public static String getSingleStringHeaderValueFromHeaders(HttpHeaders headers, CustomHeaders header) {
        return headers.getRequestHeader(header.getName()).get(0);
    }

    public static List<String> getListOfStringHeaderValuesFromHeaders(HttpHeaders headers, CustomHeaders header) {
        return headers.getRequestHeader(header.getName());
    }

    public static boolean isSupportedMediaType(String mediaType){
        return Arrays.asList(SUPPORTED_MEDIA_TYPES).contains(mediaType);
    }

    public static String unsupportedMediaTypeMessage(String mediaType){
        return "Unsupported media type:" + mediaType+". Currently supported types are:"+
                    ArrayUtils.stringArrayToGrammaticallyCorrectCommaList(SUPPORTED_MEDIA_TYPES);
    }
}
