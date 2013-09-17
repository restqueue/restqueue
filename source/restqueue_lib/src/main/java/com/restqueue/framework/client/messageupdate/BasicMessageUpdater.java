package com.restqueue.framework.client.messageupdate;

import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.exception.ChannelClientException;
import com.restqueue.framework.client.results.ConditionalPutResult;
import com.restqueue.framework.client.results.ResultsFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class enables you to update a message easily. The general pattern is to set the new headers and content body that you
 * want to update the message to. You MUST set the URL Location of the message that you want to update and the eTag value
 * of the current message (as one of the headers using CustomHeaders.IF_MATCH) .<BR/><BR/>
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
 * Date: Feb 2, 2011
 * Time: 9:07:45 PM
 */
public class BasicMessageUpdater {
    private final Map<CustomHeaders, List<String>> headerMap = new HashMap<CustomHeaders, List<String>>();
    private String stringBody;
    private Object objectBody;
    private MediaType asType;
    private String urlLocation;
    private static final HttpParams params = new BasicHttpParams();
    static{
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    }

    /**
     * This method updates the message given the headers and body that you want to update have been set.
     * @return The result of the operation giving you access to the http response code and error information
     */
    public ConditionalPutResult updateMessage(){
        Object messageBody=null;
        if(stringBody==null && objectBody==null){
            throw new IllegalArgumentException("String body and Object body cannot both be null.");
        }
        else{
            if(stringBody!=null){
                messageBody=stringBody;
            }
            if(objectBody!=null){
                messageBody=objectBody;
            }
        }

        if(urlLocation==null){
            throw new IllegalArgumentException("The Channel Endpoint must be set.");
        }

        if(messageBody instanceof String && asType==null){
            throw new IllegalArgumentException("The type must be set when using a String body.");
        }

        final HttpPut httpPut = new HttpPut(urlLocation);

        try {
            if(messageBody instanceof String){
                httpPut.setEntity(new StringEntity((String)messageBody));
                httpPut.setHeader(HttpHeaders.CONTENT_TYPE, asType.toString());
            }
            else{
                httpPut.setEntity(new StringEntity(new Serializer().toType(messageBody, MediaType.APPLICATION_JSON)));
                httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            }
        } catch (UnsupportedEncodingException e) {
            httpPut.setEntity(new BasicHttpEntity());
        }

        boolean headersProvidedContainsIfMatch=false;

        for (Map.Entry<CustomHeaders, List<String>> entry : headerMap.entrySet()) {
            for (String headerValue : entry.getValue()) {
                httpPut.addHeader(entry.getKey().getName(), headerValue);
                if(CustomHeaders.IF_MATCH.getName().equals(entry.getKey().getName())){
                    headersProvidedContainsIfMatch=true;
                }
            }
        }

        if(!headersProvidedContainsIfMatch){
            throw new ChannelClientException("An If-Match header MUST be provided to update the message. " +
                    "Make sure you get the message detail first and use the ETag value from the response.",
                    ChannelClientException.ExceptionType.MISSING_DATA);
        }

        DefaultHttpClient client = new DefaultHttpClient(params);
        try {
            final HttpResponse response = client.execute(httpPut);
            return new ResultsFactory().conditionalPutResultFromHttpPutResponse(response);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.",e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.",e,
                                ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to update the message:", e, ChannelClientException.ExceptionType.UNKNOWN);
        }

    }

    /**
     * Set the object body here.
     * @param objectBody The body of the message.
     */
    public void setObjectBody(Object objectBody) {
        this.objectBody = objectBody;
    }

    /**
     * Add a header to the list of headers to update
     * @param customHeaders The type of header
     * @param values The header values
     */
    public void addHeader(CustomHeaders customHeaders, List<String> values){
        if(headerMap.containsKey(customHeaders)){
            headerMap.get(customHeaders).addAll(values);
        }
        else{
            headerMap.put(customHeaders, values);
        }
    }

    /**
     * Set the full URL of the message to operate on.
     * (eg. http://{serverip}:{serverport}/channels/1.0/{channelName}/entries/{entryid})
     */
    public void setUrlLocation(String urlLocation) {
        this.urlLocation = urlLocation;
    }

    /**
     * This is to set the body if you already have a serialized version of the body of the message. You will need to make
     * sure that the class of the message body is in the classpath of the server.
     * @param stringBody The serialized message body
     * @param asType The serialization format that the stringBody is using.
     */
    public void setStringBodyAndType(String stringBody, MediaType asType) {
        this.stringBody = stringBody;
        this.asType = asType;
    }
}
