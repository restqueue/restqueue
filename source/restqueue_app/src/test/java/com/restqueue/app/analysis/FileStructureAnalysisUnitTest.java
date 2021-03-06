package com.restqueue.app.analysis;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

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
 * Date: 04/04/2013
 * Time: 19:25
 */
public class FileStructureAnalysisUnitTest {
    protected static final Logger log = Logger.getLogger(FileStructureAnalysisUnitTest.class);
    private String[] excludeBranchMatches = new String[]{
            "src" + File.separator + "test",
            "src" + File.separator + "main" + File.separator + "java/com/restqueue/utils"
    };

    @Test
    public void walksDirectoryContentsCorrectly() throws IOException {
        final HashSet<String> filePaths =
                FileStructureAnalysis.getDirs("/Users/ntomkinson/ij_workspace/creche/restqueue_app/src", excludeBranchMatches);
        for (String filePath : filePaths) {
            final HashSet<String> files = FileStructureAnalysis.getFiles(filePath, ".java", excludeBranchMatches);
            for (String file : files) {
                log.info(file);
            }
        }
    }
}
