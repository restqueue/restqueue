package com.restqueue.framework.common.utils;

import com.restqueue.common.utils.PriorityUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;


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
 * Date: Feb 2, 2011
 * Time: 10:55:34 PM
 */
public class PriorityUtilsUnitTest {
    private Map<String, Integer> priorityMap = new HashMap<String, Integer>();

    @Before
    public void setupPriorityMap(){
        priorityMap.put("Medium",60);
        priorityMap.put("UltraLow",0);
        priorityMap.put("High",80);
        priorityMap.put("Low",50);

        priorityMap= PriorityUtils.sortByValue(priorityMap);
    
    }

    @Test
    public void utilCanCorrectlyIdentifyThePriorityGroupFromMapAndValueInBand(){

        assertEquals("Trying 79","Medium",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(79,priorityMap));
        assertEquals("Trying 70","Medium",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(70,priorityMap));
        assertEquals("Trying 60","Medium",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(60,priorityMap));
        assertEquals("Trying 59","Low",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(59,priorityMap));
        assertEquals("Trying 50","Low",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(50,priorityMap));
        assertEquals("Trying 49","UltraLow",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(49,priorityMap));
        assertEquals("Trying 40","UltraLow",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(40,priorityMap));
        assertEquals("Trying 30","UltraLow",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(30,priorityMap));
        assertEquals("Trying 20","UltraLow",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(20,priorityMap));
        assertEquals("Trying 10","UltraLow",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(10,priorityMap));
        assertEquals("Trying 0","UltraLow",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(0,priorityMap));
    }

    @Test
    public void utilCanCorrectlyIdentifyThePriorityGroupFromMapAndValueAboveBand(){
        assertEquals("Trying 100","High",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(100,priorityMap));
        assertEquals("Trying 90","High",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(90,priorityMap));
        assertEquals("Trying 80","High",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(80,priorityMap));
    }

    @Test
    public void utilCanCorrectlyIdentifyThePriorityGroupFromMapAndNegativeValue(){
        assertEquals("Trying -1","UltraLow",PriorityUtils.getPriorityGroupFromPriorityValueAndMap(-1,priorityMap));
    }
}
