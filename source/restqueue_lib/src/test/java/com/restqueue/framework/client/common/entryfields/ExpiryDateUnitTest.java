package com.restqueue.framework.client.common.entryfields;

import com.restqueue.framework.service.exception.ChannelStoreException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
 * Time: 11:53
 */
public class ExpiryDateUnitTest {

    private static final String CREATED_DATE = "Thu, 29 Nov 1973 21:33:09 GMT";

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderNoDelay() {
        final String delay="0";
        assertEquals(CREATED_DATE,ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneSecondFormattedDelay() {
        final String delay="1s";
        assertEquals("Thu, 29 Nov 1973 21:33:10 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneSecondNonFormattedDelay() {
        final String delay="1";
        assertEquals("Thu, 29 Nov 1973 21:33:10 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneMinuteFormattedDelay() {
        final String delay="1m";
        assertEquals("Thu, 29 Nov 1973 21:34:09 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneMinuteNonFormattedDelay() {
        final String delay="60";
        assertEquals("Thu, 29 Nov 1973 21:34:09 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneHourDelay() {
        final String delay="1h";
        assertEquals("Thu, 29 Nov 1973 22:33:09 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneDayDelay() {
        final String delay="1d";
        assertEquals("Fri, 30 Nov 1973 21:33:09 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneWeekDelay() {
        final String delay="1w";
        assertEquals("Thu, 06 Dec 1973 21:33:09 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneMonthDelay() {
        final String delay="1M";
        assertEquals("Sat, 29 Dec 1973 21:33:09 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeaderOneYearDelay() {
        final String delay="1y";
        assertEquals("Fri, 29 Nov 1974 21:33:09 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader(CREATED_DATE));
    }

    @Test
    public void shouldProvideCorrectExpiryDateHeaderFromCreatedDateHeader1234567Delay() {
        final String delay="1y:2M:3w:4d:5h:6m:7s";
        assertEquals("Sat, 15 Nov 2014 05:06:07 GMT",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader("Wed, 21 Aug 2013 00:00:00 BST"));
    }

    @Test
    public void shouldNotThrowAnExceptionWithInvalidHeader() {
        final String delay="fff";
        assertEquals("Wed, 21 Aug 2013 00:00:00 BST",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader("Wed, 21 Aug 2013 00:00:00 BST"));
    }

    @Test
    public void shouldNotThrowAnExceptionWithEmptyHeader() {
        final String delay="";
        assertEquals("Wed, 21 Aug 2013 00:00:00 BST",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader("Wed, 21 Aug 2013 00:00:00 BST"));
    }

    @Test
    public void shouldNotThrowAnExceptionWithNullHeader() {
        final String delay=null;
        assertEquals("Wed, 21 Aug 2013 00:00:00 BST",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader("Wed, 21 Aug 2013 00:00:00 BST"));
    }

    @Test
    public void shouldNotThrowAnExceptionWithInvalidFormatHeader() {
        final String delay="1z:3q";
        assertEquals("Wed, 21 Aug 2013 00:00:00 BST",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader("Wed, 21 Aug 2013 00:00:00 BST"));
    }

    @Test(expected = ChannelStoreException.class)
    public void shouldThrowAnExceptionWithInvalidFormatHeader() {
        final String delay="x1y:qs";
        assertEquals("",ExpiryDate.fromDelayHeader(delay).toExpiryDateHeader("Wed, 21 Aug 2013 00:00:00 BST"));
    }
}
