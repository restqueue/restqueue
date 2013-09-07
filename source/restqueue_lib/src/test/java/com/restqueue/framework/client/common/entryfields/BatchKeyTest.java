package com.restqueue.framework.client.common.entryfields;

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
 * Date: Feb 6, 2011
 * Time: 11:28:07 PM
 */
public class BatchKeyTest {
    @Test
    public void parseCorrectlyParsesBatchKeyString(){
        final BatchKey batchKey = BatchKey.parse("batchId:3/8");
        assertEquals("batchId",batchKey.getBatchId());
        assertEquals(3,batchKey.getBatchedMessageNumber());
        assertEquals(8,batchKey.getBatchSize());
    }

    @Test
    public void formatCorrectlyFormatsBatchKeyString(){
        final BatchKey batchKey = new BatchKey("batchId",3,8);
        assertEquals("batchId:3/8",batchKey.format());
    }
}
