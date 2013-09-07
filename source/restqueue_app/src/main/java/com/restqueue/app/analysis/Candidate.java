package com.restqueue.app.analysis;


import com.restqueue.common.utils.CamelCaseUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Time: 20:49
 */
public class Candidate {
    private CandidateType candidateType;
    private boolean duplicatesAllowed=true;
    private boolean delay=false;
    private boolean unreservedOnly=false;
    private String name;
    private String entityName;
    private boolean priority=false;
    private String fullPath;

    private Candidate() {}

    public CandidateType getCandidateType() {
        return candidateType;
    }

    public static Candidate fromFile(File candidateFile){
        final Candidate candidate = new Candidate();
        candidate.fullPath=candidateFile.getPath();
        String fileName = candidateFile.getName();

        if(fileName.indexOf(".")>0){
            fileName=fileName.split("[.]")[0];
        }

        candidate.name=fileName;

        //split by camel case
        final String[] fileNameSplit = CamelCaseUtils.splitByCamelCase(fileName);

        //if only 1 word class name
        if(fileNameSplit.length==1){
            return null;
        }

        //parse type
        for (CandidateType candidateType : CandidateType.values()) {
            if(fileNameSplit[fileNameSplit.length-1].equals(candidateType.getType())){
                candidate.candidateType=candidateType;
                break;
            }
        }
        if(candidate.getCandidateType()==null){
            return null;
        }

        //run through to determine keyword true/false so that you can use keywords between non-keywords

        final List<String> keywords = new ArrayList<String>();
        keywords.addAll(Arrays.asList("Delayed", "Delay", "Distinct", "Unreserved", "Priority", "Prioritised", "Prioritized"));
        keywords.addAll(CandidateType.allTypes());

        int firstNonKeyword=0;
        int lastNonKeyword=0;
        for (int i = 0; i < fileNameSplit.length; i++) {
            if(!keywords.contains(fileNameSplit[i])){
                if(firstNonKeyword==0){
                    firstNonKeyword=i;
                }
                if(lastNonKeyword<i){
                    lastNonKeyword=i;
                }
            }
        }

        final StringBuilder entityNameBuilder = new StringBuilder();
        for (int i = 0; i < fileNameSplit.length; i++) {
            String part = fileNameSplit[i];
            if ((i<firstNonKeyword || i>lastNonKeyword) && part.equals("Delayed")) {
                candidate.delay = true;
            }
            else if ((i<firstNonKeyword || i>lastNonKeyword) && part.equals("Delay")) {
                candidate.delay = true;
            }
            else if ((i<firstNonKeyword || i>lastNonKeyword) && part.equals("Distinct")) {
                candidate.duplicatesAllowed = false;
            }
            else if ((i<firstNonKeyword || i>lastNonKeyword) && part.equals("Unreserved")) {
                candidate.unreservedOnly = true;
            }
            else if ((i<firstNonKeyword || i>lastNonKeyword) && part.equals("Priority")) {
                candidate.priority = true;
            }
            else if ((i<firstNonKeyword || i>lastNonKeyword) && part.equals("Prioritised")) {
                candidate.priority = true;
            }
            else if ((i<firstNonKeyword || i>lastNonKeyword) && part.equals("Prioritized")) {
                candidate.priority = true;
            }
            else {
                if (!part.equals(candidate.getCandidateType().getType())) {
                    entityNameBuilder.append(part);
                }
            }
        }


        if(entityNameBuilder.length()==0){
            return null;
        }

        candidate.entityName=entityNameBuilder.toString();

        if(keywords.contains(candidate.entityName)){
            return null;
        }

        return candidate;
    }

    public boolean isDuplicatesAllowed() {
        return duplicatesAllowed;
    }

    public boolean isDelay() {
        return delay;
    }

    public String getName() {
        return name;
    }

    public boolean isUnreservedOnly() {
        return unreservedOnly;
    }

    public String getEntityName() {
        return entityName;
    }

    public boolean isPriority() {
        return priority;
    }

    public String getFullPath() {
        return fullPath;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "candidateType=" + candidateType +
                ", duplicatesAllowed=" + duplicatesAllowed +
                ", delay=" + delay +
                ", unreservedOnly=" + unreservedOnly +
                ", name='" + name + '\'' +
                ", entityName='" + entityName + '\'' +
                ", priority=" + priority +
                ", fullPath='" + fullPath + '\'' +
                '}';
    }
}
