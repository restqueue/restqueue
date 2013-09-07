package com.restqueue.framework.entryfields;

import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
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
 * Date: Jan 15, 2011
 * Time: 7:29:57 PM
 */
public class ReturnAddressTest {
    @Test
    public void returnAddressParseWorksOk(){
        assertEquals(new ReturnAddress(ReturnAddressType.EMAIL,"thisIsTheAddress@somewhere.com"),ReturnAddress.parse("EMAIL:thisIsTheAddress@somewhere.com"));
    }

    @Test
    public void returnAddressFormatWorksOk(){
        assertEquals("EMAIL:thisIsTheAddress@somewhere.com",new ReturnAddress(ReturnAddressType.EMAIL,"thisIsTheAddress@somewhere.com").format());
        assertEquals("URL:http://localhost:9998/channels/1.0/stockQueryResultsQueue",
                new ReturnAddress(ReturnAddressType.URL,"http://localhost:9998/channels/1.0/stockQueryResultsQueue").format());
    }
    
}
