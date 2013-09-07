package com.restqueue.app.analysis;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
 * Date: 05/04/2013
 * Time: 20:34
 */
public class SourceScannerIntegrationTest {
    @Test
    public void shouldParseCandidatesCorrectly() throws IOException {

        final SourceScanner sourceScanner = new SourceScanner();
        sourceScanner.scan(System.getProperty("user.dir"),new String[0]);

        int expectedCheckSum=1+2+4+8+16+32+64+128+256+512+1024+2048;
        int actualChecksum=0;

        assertEquals(12,sourceScanner.getCandidates().size());

        for (Candidate candidate : sourceScanner.getCandidates()) {
            if(candidate.getName().equals("DistinctCupStack")){
                assertEquals(CandidateType.STACK, candidate.getCandidateType());
                assertFalse(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("Cup",candidate.getEntityName());
                actualChecksum+=1;
            }
            else if(candidate.getName().equals("GlassDelayedQueue")){
                assertEquals(CandidateType.QUEUE, candidate.getCandidateType());
                assertTrue(candidate.isDuplicatesAllowed());
                assertTrue(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("Glass",candidate.getEntityName());
                actualChecksum+=2;
            }
            else if(candidate.getName().equals("GlassQueue")){
                assertEquals(CandidateType.QUEUE, candidate.getCandidateType());
                assertTrue(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("Glass",candidate.getEntityName());
                actualChecksum+=4;
            }
            else if(candidate.getName().equals("BigPlateSequencer")){
                assertEquals(CandidateType.SEQUENCER, candidate.getCandidateType());
                assertTrue(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("BigPlate",candidate.getEntityName());
                actualChecksum+=8;
            }
            else if(candidate.getName().equals("DistinctUnreservedKnivesStack")){
                assertEquals(CandidateType.STACK, candidate.getCandidateType());
                assertFalse(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertTrue(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("Knives",candidate.getEntityName());
                actualChecksum+=16;
            }
            else if(candidate.getName().equals("UnreservedSpoonsSequencer")){
                assertEquals(CandidateType.SEQUENCER, candidate.getCandidateType());
                assertTrue(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertTrue(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("Spoons",candidate.getEntityName());
                actualChecksum+=32;
            }
            else if(candidate.getName().equals("DistinctDelayedUnreservedSaucerStack")){
                assertEquals(CandidateType.STACK, candidate.getCandidateType());
                assertFalse(candidate.isDuplicatesAllowed());
                assertTrue(candidate.isDelay());
                assertTrue(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("Saucer",candidate.getEntityName());
                actualChecksum+=64;
            }
            else if(candidate.getName().equals("PriorityDishStack")){
                assertEquals(CandidateType.STACK, candidate.getCandidateType());
                assertTrue(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertTrue(candidate.isPriority());
                assertEquals("Dish",candidate.getEntityName());
                actualChecksum+=128;
            }
            else if(candidate.getName().equals("TeaSpoonPool")){
                assertEquals(CandidateType.POOL, candidate.getCandidateType());
                assertTrue(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("TeaSpoon",candidate.getEntityName());
                actualChecksum+=256;
            }
            else if(candidate.getName().equals("PriorityMyDelayedOrdersQueue")){
                assertEquals(CandidateType.QUEUE, candidate.getCandidateType());
                assertTrue(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertTrue(candidate.isPriority());
                assertEquals("MyDelayedOrders",candidate.getEntityName());
                actualChecksum+=512;
            }
            else if(candidate.getName().equals("DistinctMyQueueTimingsPool")){
                assertEquals(CandidateType.POOL, candidate.getCandidateType());
                assertFalse(candidate.isDuplicatesAllowed());
                assertFalse(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("MyQueueTimings",candidate.getEntityName());
                actualChecksum+=1024;
            }
            else if(candidate.getName().equals("BasicMessageDelayQueue")){
                assertEquals(CandidateType.QUEUE, candidate.getCandidateType());
                assertTrue(candidate.isDuplicatesAllowed());
                assertTrue(candidate.isDelay());
                assertFalse(candidate.isUnreservedOnly());
                assertFalse(candidate.isPriority());
                assertEquals("BasicMessage",candidate.getEntityName());
                actualChecksum+=2048;
            }
        }
        assertEquals(expectedCheckSum, actualChecksum);
    }
}
