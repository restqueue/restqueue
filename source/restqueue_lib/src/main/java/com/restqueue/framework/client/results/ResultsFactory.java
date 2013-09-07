package com.restqueue.framework.client.results;

import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.exception.ChannelClientException;
import com.restqueue.framework.client.exception.HttpResponseErrorBean;
import com.restqueue.framework.common.utils.HttpUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
 * Time: 1:10:28 AM
 */
public class ResultsFactory {
    public CreationResult creationResultFromHttpPostResponse(HttpResponse httpResponse) {
        final CreationResult creationResult = new CreationResult();

        creationResult.setLocation(HttpUtils.nullSafeStringHeaderValueFromResponse(httpResponse, CustomHeaders.LOCATION));
        creationResult.setSuccess(Response.Status.CREATED.getStatusCode() == httpResponse.getStatusLine().getStatusCode());
        creationResult.setEtag(HttpUtils.nullSafeStringHeaderValueFromResponse(httpResponse, CustomHeaders.ETAG));
        final String stringBody = extractStringBody(getContentFromResponse(httpResponse));
        creationResult.setBody(stringBody);
        creationResult.setResponseCode(httpResponse.getStatusLine().getStatusCode());
        if (!creationResult.isSuccess() && stringBody.startsWith("{error")) {
            creationResult.setException((HttpResponseErrorBean)new Serializer().fromType(stringBody,"application/json"));
        }

        return creationResult;
    }

    public ConditionalPutResult conditionalPutResultFromHttpPutResponse(HttpResponse httpResponse) {
        final ConditionalPutResult conditionalPutResult = new ConditionalPutResult();

        conditionalPutResult.setSuccess(Response.Status.OK.getStatusCode() == httpResponse.getStatusLine().getStatusCode());
        conditionalPutResult.setEtag(HttpUtils.nullSafeStringHeaderValueFromResponse(httpResponse, CustomHeaders.ETAG));
        final String stringBody = extractStringBody(getContentFromResponse(httpResponse));
        conditionalPutResult.setBody(stringBody);
        conditionalPutResult.setResponseCode(httpResponse.getStatusLine().getStatusCode());
        if (!conditionalPutResult.isSuccess() && stringBody.startsWith("{error")) {
            conditionalPutResult.setException((HttpResponseErrorBean)new Serializer().fromType(stringBody,"application/json"));
        }

        return conditionalPutResult;
    }

    public RetrievalResult retrievalResultFromHttpGetResponse(HttpResponse httpResponse) {
        final RetrievalResult retrievalResult = new RetrievalResult();

        retrievalResult.setSuccess(Response.Status.OK.getStatusCode() == httpResponse.getStatusLine().getStatusCode());
        retrievalResult.setEtag(HttpUtils.nullSafeStringHeaderValueFromResponse(httpResponse, CustomHeaders.ETAG));
        String stringBody="";
        if(httpResponse.getEntity()!=null){
            stringBody = extractStringBody(getContentFromResponse(httpResponse));
            retrievalResult.setBody(stringBody);
        }
        retrievalResult.setResponseCode(httpResponse.getStatusLine().getStatusCode());
        for (CustomHeaders customHeaders : CustomHeaders.values()) {
            for (Header header : httpResponse.getAllHeaders()) {
                if (header.getName().equals(customHeaders.getName())) {
                    retrievalResult.addHeader(customHeaders, header.getValue());
                }
            }
        }

        if (!retrievalResult.isSuccess() && stringBody.startsWith("{error")) {
            retrievalResult.setException((HttpResponseErrorBean)new Serializer().fromType(stringBody,"application/json"));
        }

        return retrievalResult;
    }

    public ExecutionResult executionResultFromHttpDeleteResponse(HttpResponse httpResponse) {
        final ExecutionResult executionResult = new ExecutionResult();

        executionResult.setSuccess(Response.Status.OK.getStatusCode() == httpResponse.getStatusLine().getStatusCode());
        final String stringBody = extractStringBody(getContentFromResponse(httpResponse));
        executionResult.setResponseCode(httpResponse.getStatusLine().getStatusCode());
        if (!executionResult.isSuccess() && stringBody.startsWith("{error")) {
            executionResult.setException((HttpResponseErrorBean)new Serializer().fromType(stringBody,"application/json"));
        }

        return executionResult;
    }

    private InputStream getContentFromResponse(HttpResponse httpResponse) {
        try {
            return httpResponse.getEntity().getContent();
        }
        catch (IOException e) {
            throw new ChannelClientException("Unknown exception getting content from the response:",e, ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public String extractStringBody(InputStream bodyStream) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int nextChar;
        byte[] buffer = new byte[100];
        try{
        while ((nextChar = bodyStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, (char) nextChar);
        }
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        }
        catch (IOException e){
            throw new ChannelClientException("Unknown exception getting content from the response:",e, ChannelClientException.ExceptionType.UNKNOWN);
        }
        return new String(byteArrayOutputStream.toByteArray());
    }
}
