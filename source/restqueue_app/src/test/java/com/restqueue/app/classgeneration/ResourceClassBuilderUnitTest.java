package com.restqueue.app.classgeneration;

import com.restqueue.app.analysis.Candidate;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

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
 * Date: 06/04/2013
 * Time: 01:01
 */
@Ignore
public class ResourceClassBuilderUnitTest {
    @Test
    public void canBuildResourceClassFromSrcMainCandidate() {
        final ResourceClassBuilder resourceClassBuilder = new ResourceClassBuilder();

        final File file = new File("src/main/java/com/restqueue/gen/web/ComplaintStackResource.java");
        if(file.exists()){
            file.delete();
        }

        final Candidate candidate = Candidate.fromFile(
                new File("/Users/ntomkinson/ij_workspace/creche/rubytrial/src/main/java/com/restqueue/candidates/DistinctUnreservedDelayedComplaintStack.java"));
        resourceClassBuilder.buildFromCandidate(candidate);
    }

    @Test
    public void canBuildResourceClassFromSrcTestCandidate() {
        final ResourceClassBuilder resourceClassBuilder = new ResourceClassBuilder();

        final File file = new File("src/main/java/com/restqueue/gen/web/TestComplaintStackResource.java");
        if(file.exists()){
            file.delete();
        }

        final Candidate candidate = Candidate.fromFile(
                new File("/Users/ntomkinson/ij_workspace/creche/rubytrial/src/test/java/com/restqueue/candidates/DistinctUnreservedDelayedTestComplaintStack.java"));
        resourceClassBuilder.buildFromCandidate(candidate);
    }

}
