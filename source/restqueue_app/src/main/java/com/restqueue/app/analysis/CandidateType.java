package com.restqueue.app.analysis;

import java.util.ArrayList;
import java.util.List;

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
 * Date: 02/04/2013
 * Time: 20:51
 */
public enum CandidateType {
    QUEUE("Queue"),STACK("Stack"),POOL("Pool"),SEQUENCER("Sequencer");
    private final String type;

    CandidateType(String type) {
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public static List<String> allTypes(){
        final List<String> types=new ArrayList<String>();
        for (CandidateType candidateType : CandidateType.values()) {
            types.add(candidateType.getType());
        }
        return types;
    }
}
