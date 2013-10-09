package com.restqueue.app;

import com.restqueue.app.analysis.Candidate;
import com.restqueue.app.analysis.SourceScanner;
import com.restqueue.app.classgeneration.ResourceClassBuilder;
import com.restqueue.app.classgeneration.ServerClassBuilder;
import com.restqueue.common.arguments.ArgumentMetaData;
import com.restqueue.common.arguments.ArgumentParseException;
import com.restqueue.common.arguments.ServerArguments;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
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
 * Time: 19:47
 */
public class Setup {
    protected static final Logger log = Logger.getLogger(Setup.class);
    private static List<ArgumentMetaData> allowedArguments=new ArrayList<ArgumentMetaData>();
    public static final String INCLUDE_TEST_BRANCH_SWITCH = "t";
    public static final String SPECIFIED_TEST_BRANCH_ROOT_SWITCH = "T";
    public static final String SPECIFIED_MAIN_BRANCH_ROOT_SWITCH = "M";

    public static void main(String[] args) {
        try {
            //validate arguments (include-test-branch, exclude-directories, etc)
            log.info("----- Validating command line arguments -----");
            allowedArguments.add(new ArgumentMetaData(INCLUDE_TEST_BRANCH_SWITCH,"Include Test Branch", ArgumentMetaData.ArgumentMetaDataType.STRING, "Exclude",
                    new String[]{"Include","Exclude"}));
            allowedArguments.add(new ArgumentMetaData(SPECIFIED_TEST_BRANCH_ROOT_SWITCH,"Specified Test Branch", ArgumentMetaData.ArgumentMetaDataType.STRING, null,null));
            allowedArguments.add(new ArgumentMetaData(SPECIFIED_MAIN_BRANCH_ROOT_SWITCH, "Specified Main Branch", ArgumentMetaData.ArgumentMetaDataType.STRING, null,null));
            ServerArguments.createInstance(allowedArguments, args);
            log.info("----- Command line arguments OK -----");

            boolean includeTestBranch = (ServerArguments.getInstance().getStringArgument(INCLUDE_TEST_BRANCH_SWITCH)).equalsIgnoreCase("Include");

            //walk source directory getting candidates
            //specifically exclude test sources (if not overridden by command line switch)
            final List<String> excludeBranchMatchesList = new ArrayList<String>();
            if (!includeTestBranch) {
                if(ServerArguments.getInstance().getStringArgument(SPECIFIED_TEST_BRANCH_ROOT_SWITCH)!=null){
                    //use specified test branch
                    excludeBranchMatchesList.add(ServerArguments.getInstance().getStringArgument(SPECIFIED_TEST_BRANCH_ROOT_SWITCH));
                }
                else{
                    //use default test branch
                    excludeBranchMatchesList.add("src" + File.separator + "test" + File.separator + "java");
                }
            }

            log.info("----- Analysing java source files -----");

            if(includeTestBranch){
                log.info("----- Including source test branch -----");
            }
            else{
                log.info("----- Excluding source test branch -----");
                for (String toIgnore : excludeBranchMatchesList) {
                    log.info("----- Excluding "+toIgnore+" -----");
                }
            }

            final SourceScanner sourceScanner = new SourceScanner();
            sourceScanner.scan(System.getProperty("user.dir"), excludeBranchMatchesList.toArray(new String[excludeBranchMatchesList.size()]));

            //create resource classes
            log.info("----- Creating REST endpoints for message channels -----");
            int resourceCreationCounter=0;
            for (Candidate candidate : sourceScanner.getCandidates()) {
                //create Resources from candidate
                new ResourceClassBuilder().buildFromCandidate(candidate);
                resourceCreationCounter++;
            }
            log.info("----- Created "+resourceCreationCounter+" REST endpoints for message channels -----");

            //create Server classes
            log.info("----- Creating Server class -----");
            new ServerClassBuilder().build();
            log.info("----- Created Server class -----");

            log.info("----- Finished -----");
        }
        catch (IOException e) {
            log.error("Exception:" + e.getMessage());
        }
        catch (ArgumentParseException argumentParseException) {
            log.error("Exception:" + argumentParseException.getMessage());
        }
        catch (Exception e){
            log.error("Unexpected exception:"+e.getMessage());
        }
    }
}
