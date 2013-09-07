package com.restqueue.app.analysis;


import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
 * Time: 20:45
 */
public class SourceScanner {
    protected static final Logger log = Logger.getLogger(SourceScanner.class);
    private List<Candidate> candidates = new ArrayList<Candidate>();

    public List<Candidate> scan(String startFromDirectory, String[] excludeBranchMatches) throws IOException {
        final HashSet<String> filePaths =
                FileStructureAnalysis.getDirs(startFromDirectory, excludeBranchMatches);
        for (String filePath : filePaths) {
            final HashSet<String> files = FileStructureAnalysis.getFiles(filePath, ".java", excludeBranchMatches);
            for (String file : files) {
                final Candidate candidate = Candidate.fromFile(new File(file));
                if (candidate != null) {
                    candidates.add(candidate);
                }
            }
        }

        log.info("----- Identified Channel Candidates -----");

        for (Candidate candidate : candidates) {
            log.info("----- "+candidate.getName()+" -----");
        }

        return candidates;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }
}
