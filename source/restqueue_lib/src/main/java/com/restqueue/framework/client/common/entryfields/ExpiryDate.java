package com.restqueue.framework.client.common.entryfields;

import com.restqueue.common.utils.DateUtils;
import com.restqueue.common.utils.StringUtils;
import com.restqueue.framework.service.exception.ChannelStoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
 * Date: 21/08/2013
 * Time: 11:05
 */
public class ExpiryDate {
    private static final String YEAR="y";
    private static final String MONTH="M";
    private static final String WEEK="w";
    private static final String DAY="d";
    private static final String HOUR="h";
    private static final String MINUTE="m";
    private static final String SECOND="s";

    private final Map<String,Integer> partsMap = new HashMap<String, Integer>();

    private ExpiryDate() {}

    public static ExpiryDate fromDelayHeader(String delayHeader){
        final ExpiryDate expiryDate = new ExpiryDate();

        expiryDate.partsMap.put(YEAR,0);
        expiryDate.partsMap.put(MONTH,0);
        expiryDate.partsMap.put(WEEK,0);
        expiryDate.partsMap.put(DAY,0);
        expiryDate.partsMap.put(HOUR,0);
        expiryDate.partsMap.put(MINUTE,0);
        expiryDate.partsMap.put(SECOND,0);

        if(delayHeader==null){
            return expiryDate;
        }
        delayHeader=delayHeader.trim();

        try {
            if(StringUtils.allCharactersAreNumeric(delayHeader)){
                //assume seconds
                expiryDate.partsMap.put(SECOND,Integer.valueOf(delayHeader));
            }
            else{
                final String[] splitByColon = delayHeader.split("[:]");
                for (String part : splitByColon) {
                    part=part.trim();
                    for (String partKey : expiryDate.partsMap.keySet()) {
                        if(part.endsWith(partKey)){
                            expiryDate.partsMap.put(partKey,Integer.valueOf(part.replace(partKey,"")));
                            break;
                        }
                    }
                }
            }
        }
        catch (NumberFormatException e) {
            throw new ChannelStoreException("Request has a malformed delay header - it MUST be in the form '1y:2M:3w:4d:5h:6m:7s' for " +
                    "1 year, 2 Months, 3 weeks, 4 days, 5 hours, 6 minutes and 7 seconds (some of these parts can be missing) or '1234567' for 1,234,567 seconds",
                    ChannelStoreException.ExceptionType.INVALID_ENTRY_DATA_PROVIDED);
        }

        return expiryDate;
    }

    public String toExpiryDateHeader(String createdDateHeader){
        final Date createdDate;
        try {
            createdDate = new SimpleDateFormat(DateUtils.HTTP_DATE_FORMAT).parse(createdDateHeader);
        }
        catch (ParseException e) {
            throw new ChannelStoreException("Request has a malformed created date header.",
                    ChannelStoreException.ExceptionType.INVALID_ENTRY_DATA_PROVIDED);
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdDate);
        //add offset
        calendar.add(Calendar.YEAR,partsMap.get(YEAR));
        calendar.add(Calendar.MONTH,partsMap.get(MONTH));
        calendar.add(Calendar.WEEK_OF_YEAR,partsMap.get(WEEK));
        calendar.add(Calendar.DAY_OF_YEAR,partsMap.get(DAY));
        calendar.add(Calendar.HOUR,partsMap.get(HOUR));
        calendar.add(Calendar.MINUTE,partsMap.get(MINUTE));
        calendar.add(Calendar.SECOND,partsMap.get(SECOND));

        return new SimpleDateFormat(DateUtils.HTTP_DATE_FORMAT).format(calendar.getTime());
    }
}
