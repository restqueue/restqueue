package com.restqueue.framework.service.channelstate;


import com.restqueue.common.utils.ArrayUtils;

import java.util.*;

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
 * Date: Feb 5, 2012
 * Time: 4:38:27 PM
 */
public class ChannelStateHtmlRenderer {
    private List<String> linksForEditing = Arrays.asList("prioritySettings", "batchStrategy", "nextMessageSequence", "maxSize", "sequenceStrategy");

    public String serializeMap(Map<String, Object> map){
        final StringBuilder stringBuilder = new StringBuilder("<html><head><title>Channel State</title>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body");
        addStyleStart(stringBuilder);
        addBlackBackground(stringBuilder);
        addStyleEnd(stringBuilder);
        stringBuilder.append(">");

        stringBuilder.append("<TABLE border=\"0px\" cellpadding=\"4\" cellspacing=\"1\" bgcolor=\"#999999\">");
        stringBuilder.append("<TR><TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Key</font></TH>" +
//                "<TH bgcolor=\"#f0f0f0\"><font size=\"3\" face=\"arial\" color=\"#000000\">Value</font></TH><TH bgcolor=\"#f0f0f0\"></TH>" +
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
            stringBuilder.append("</font></TD>");
//            if(linksForEditing.contains(entry.getKey())){
//                stringBuilder.append("<TD bgcolor=\"#FFFFFF\" align=\"center\">" +
//                        "<font size=\"2\" face=\"arial\" color=\"#000000\">" +
//                        "<a href=\"state/").append(entry.getKey()).append("\">Edit</a>" +
//                        "</font></TD>");
//            }
//            else{
//                stringBuilder.append("<TD bgcolor=\"#FFFFFF\" align=\"center\">" +
//                        "<font size=\"2\" face=\"arial\" color=\"#000000\">" +
//                        "&nbsp;</font></TD>");
//            }
            stringBuilder.append("</TR>");
        }

        stringBuilder.append("</TABLE>");

        return stringBuilder.append("</body></html>").toString();
    }

    private void addStyleStart(final StringBuilder stringBuilder){
        stringBuilder.append(" style=\"");
    }

    private void addStyleEnd(final StringBuilder stringBuilder) {
        stringBuilder.append("\"");
    }

    private void addBlackBackground(final StringBuilder stringBuilder){
        stringBuilder.append("background-color: #000000;");
    }

    private void addDropDownBox(final StringBuilder stringBuilder, final List<String> options, final String currentSelection){
        stringBuilder.append("<select>");
        for (String option : options) {
            stringBuilder.append("<option");
            if(option.equals(currentSelection)){
                stringBuilder.append(" selected");
            }
            stringBuilder.append(">").append(option).append("</option>");            
        }
        stringBuilder.append("</select>");
    }
}
