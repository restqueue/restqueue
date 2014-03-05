package com.restqueue.framework.client.common.serializer;

import com.restqueue.common.utils.ArrayUtils;
import com.restqueue.framework.client.common.summaryfields.EndPoint;
import com.restqueue.framework.client.entrywrappers.EntrySummary;
import com.restqueue.framework.service.notification.MessageListenerAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to render various things into HTML for the administration web pages.<BR/><BR/>
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
 * Date: Jan 24, 2011
 * Time: 7:03:21 PM
 */
public class HTMLSerializer {
    @SuppressWarnings("unchecked")
    public String serialize(Object object, final String... arguments) {
        if(object == null && arguments[0].equalsIgnoreCase("otherChannelsList")){
            return "empty";
        }
        if(object == null && arguments[0].equalsIgnoreCase("shutdownConfirmation")){
            return serializeShutdownConfirmationPage();
        }
        if (object instanceof List) {
            if (arguments.length > 0 && arguments[0].equals("snapshotList")) {
                return serializeSnapshotList((List<Object>) object);
            }
            return serializeList((List<Object>) object, arguments);
        }
        if (object instanceof Map) {
            return serializeMap((Map<String, Object>)object);
        }
        final StringBuilder stringBuilder = new StringBuilder().append("<html><head><title>Message</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /></head><body");

        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        addWhiteFontParagraphTag(stringBuilder);
        stringBuilder.append(nullSafeObjectToString(object));
        endParagraphTag(stringBuilder);
        stringBuilder.append("</body></html>");

        return stringBuilder.toString();
    }

    private String nullSafeObjectToString(Object object) {
        return object==null?"":object.toString();
    }

    private String serializeShutdownConfirmationPage(){
        final StringBuilder stringBuilder = new StringBuilder("<html><head><title>Registered Consumers</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body");
        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        stringBuilder.append("<FORM style=\"display: inline\" METHOD=POST ACTION=\"/control/1.0/stopserver\">" +
                        "<BUTTON name=\"shutdownserver\" value=\"Shut Down Server\" type=\"submit\">Shut Down Server</BUTTON></FORM>");

        return stringBuilder.append("</body></html>").toString();
    }

    public String serializeList(List<Object> objectList, final String... arguments) {
        if(objectList.size()==0){
            return serializeStringList(objectList);
        }
        if(objectList.get(0) instanceof EntrySummary){
            return serializeEntrySummaryList(objectList, arguments);
        }
        if(objectList.get(0) instanceof EndPoint){
            return serializeEndPointList(objectList);
        }
        if(objectList.get(0) instanceof String){
            return serializeStringList(objectList);
        }
        if(objectList.get(0) instanceof MessageListenerAddress){
            return serializeConsumerAddressList(objectList);
        }
        return "";
    }

    public String serializeConsumerAddressList(List<Object> objectList) {
        final StringBuilder stringBuilder = new StringBuilder("<html><head><title>Registered Consumers</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body");
        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        stringBuilder.append("<TABLE border=\"0px\" cellpadding=\"4\" cellspacing=\"1\" bgcolor=\"#999999\">");
        stringBuilder.append("<TR><TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Consumer ID</font></TH>" +
                "<TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Type</font></TH>" +
                "<TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Address</font></TH>" +
                "</TR>");

        for (Object object : objectList) {

            final MessageListenerAddress value = (MessageListenerAddress) object;

            stringBuilder.append("<TR><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            stringBuilder.append(value.getListenerId());
            stringBuilder.append("</TD><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            stringBuilder.append(value.getReturnAddress().getType());
            stringBuilder.append("</TD><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            stringBuilder.append(value.getReturnAddress().getAddress());
            stringBuilder.append("</TD></TR>");
        }

        stringBuilder.append("</TABLE>");

        return stringBuilder.append("</body></html>").toString();
    }

    private String serializeEndPointList(List<Object> objectList) {
        final StringBuilder stringBuilder = new StringBuilder("<html><head><title>Channel Summary</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body");

        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        stringBuilder.append("<TABLE border=\"0px\" cellpadding=\"4\" cellspacing=\"1\" bgcolor=\"#999999\">");
        stringBuilder.append("<TR><TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Links</font></TH>" +
                "</TR>");

        for (Object object : objectList) {
            final EndPoint endPoint = (EndPoint) object;
            if ("GET".equals(endPoint.getHttpMethod())) {
                stringBuilder.append("<TR><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
                stringBuilder.append("<a href='").append(endPoint.getUrl()).append("'>");
                stringBuilder.append(endPoint.getDescription());
                stringBuilder.append("</a>");
                stringBuilder.append("</TD></TR>");
            }
        }

        stringBuilder.append("</TABLE>");

        return stringBuilder.append("</body></html>").toString();
    }

    public String serializeEntrySummaryList(List<Object> objectList, final String... arguments) {
        final StringBuilder stringBuilder = new StringBuilder("<html><head><title>Current contents</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body");
        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        stringBuilder.append("<TABLE border=\"0px\" cellpadding=\"4\" cellspacing=\"1\" bgcolor=\"#999999\">");
        stringBuilder.append("<TR><TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Message Detail</font></TH>" +
                "<TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Assigned To</font></TH></TR>");

        for (Object object : objectList) {

            //if EntrySummary then assume contents listing

            final EntrySummary entrySummary = (EntrySummary) object;
            stringBuilder.append("<TR><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            stringBuilder.append(entrySummary.getContent().toString());
            stringBuilder.append("</font></TD><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            stringBuilder.append(entrySummary.getMessageConsumerId() == null ? "Not Yet Assigned" : entrySummary.getMessageConsumerId());
            stringBuilder.append("</font></TD></TR>");
        }

        stringBuilder.append("</TABLE>");
        if(arguments.length==0 || (arguments.length>0 && !"snapshotContents".equals(arguments[0]))){
            stringBuilder.append("<FORM style=\"display: inline\" METHOD=POST ACTION=\"purge\">" +
                "<BUTTON name=\"submit\" value=\"submit\" type=\"submit\">Purge Channel Contents</BUTTON></FORM>");
            stringBuilder.append("<FORM style=\"display: inline\" METHOD=POST ACTION=\"snapshots\">" +
                "<BUTTON name=\"submit\" value=\"submit\" type=\"submit\">Take Snapshot</BUTTON></FORM>");
        }
        return stringBuilder.append("</body></html>").toString();
    }

    @SuppressWarnings("unchecked")
    public String serializeSnapshotList(List<Object> objectList) {
        final StringBuilder stringBuilder = new StringBuilder("<html><head><title>Snapshot List</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body");
        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        stringBuilder.append("<TABLE border=\"0px\" cellpadding=\"4\" cellspacing=\"1\" bgcolor=\"#999999\">");
        stringBuilder.append("<TR><TH colspan=2 bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Snapshots</font></TH>" +
                "</TR>");

        for (Object object : objectList) {

            final Map<String, Object> linkMap = (Map<String, Object>) object;

            stringBuilder.append("<TR><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            stringBuilder.append("<A href='");
            stringBuilder.append(linkMap.get("target"));
            stringBuilder.append("'>");
            stringBuilder.append(linkMap.get("name"));
            stringBuilder.append("</A>");
            stringBuilder.append("</font></TD><TD bgcolor=\"#FFFFFF\" align=\"center\">");
            stringBuilder.append("<FORM style=\"display: inline\" METHOD=POST ACTION=\"");
            stringBuilder.append(linkMap.get("restoreLink"));
            stringBuilder.append("\"><BUTTON name=\"submit\" value=\"submit\" type=\"submit\">Restore</BUTTON></FORM>");
            stringBuilder.append("</TD></TR>");

        }

        stringBuilder.append("</TABLE>");

        return stringBuilder.append("</body></html>").toString();
    }

    public String serializeStringList(List<Object> objectList) {
        final StringBuilder stringBuilder = new StringBuilder("<html><head><title>Values</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body");
        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        stringBuilder.append("<TABLE border=\"0px\" cellpadding=\"4\" cellspacing=\"1\" bgcolor=\"#999999\">");
        stringBuilder.append("<TR><TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Values</font></TH>" +
                "</TR>");

        for (Object object : objectList) {

            final String value = (String) object;

            stringBuilder.append("<TR><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            stringBuilder.append(value);
            stringBuilder.append("</TD></TR>");
        }

        stringBuilder.append("</TABLE>");

        return stringBuilder.append("</body></html>").toString();
    }

    public String serializeMap(Map<String, Object> map){
        final StringBuilder stringBuilder = new StringBuilder("<html><head><title>Values</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body");
        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        stringBuilder.append("<TABLE border=\"0px\" cellpadding=\"4\" cellspacing=\"1\" bgcolor=\"#999999\">");
        stringBuilder.append("<TR><TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Key</font></TH>" +
                "<TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Value</font></TH>" +
                "</TR>");

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            stringBuilder.append("<TR><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            stringBuilder.append(entry.getKey());
            stringBuilder.append("</font></TD><TD bgcolor=\"#FFFFFF\" align=\"center\"><font size=\"2\" face=\"arial\" color=\"#000000\">");
            if(entry.getValue() instanceof List){
                stringBuilder.append(ArrayUtils.listToString((List<?>) entry.getValue(), ", "));
            }
            else if(entry.getValue() instanceof Map){
                List<String> listOfInnerEntries = new ArrayList<String>();
                for (Object innerEntry : ((Map) entry.getValue()).entrySet()) {
                    listOfInnerEntries.add(innerEntry.toString());
                }
                stringBuilder.append(ArrayUtils.listToString(listOfInnerEntries, ", "));
            }
            else{
                stringBuilder.append(entry.getValue());
            }
            stringBuilder.append("</font></TD></TR>");
        }

        stringBuilder.append("</TABLE>");

        return stringBuilder.append("</body></html>").toString();
    }

    private void addBlackBackground(final StringBuilder stringBuilder){
        stringBuilder.append("background-color: #000000;");
    }

    private void addWhiteFontParagraphTag(final StringBuilder stringBuilder){
        stringBuilder.append("<p style=\"font-family:arial;color:white;\">");
    }

    private void endParagraphTag(final StringBuilder stringBuilder){
        stringBuilder.append("</p>");
    }



    private void addStyleStart(final StringBuilder stringBuilder){
        stringBuilder.append(" style=\"");
    }

    private void addStyleEnd(final StringBuilder stringBuilder) {
        stringBuilder.append("\"");
    }
}
