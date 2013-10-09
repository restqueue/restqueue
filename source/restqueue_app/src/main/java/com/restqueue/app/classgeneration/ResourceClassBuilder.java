package com.restqueue.app.classgeneration;

import com.restqueue.app.Setup;
import com.restqueue.app.analysis.Candidate;
import com.restqueue.common.arguments.ServerArguments;
import com.restqueue.common.classgeneration.AbstractSourceBuilder;
import com.restqueue.common.classgeneration.SourceClassGenerationHelper;
import com.restqueue.common.utils.StringUtils;

import java.io.File;
import java.text.MessageFormat;
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
 * Date: 06/04/2013
 * Time: 00:01
 */
public class ResourceClassBuilder extends AbstractSourceBuilder {

    private List<String> filterClassNames=new ArrayList<String>();
    private String filterChain;

    public ResourceClassBuilder() {
        addImport("com.restqueue.framework.web.resources.AbstractQueueResource");
        addImport("com.restqueue.framework.service.backingstoreduplicatesfilters.BackingStoreDuplicatesFilter");
        addImport("javax.ws.rs.Path");
    }

    public void buildFromCandidate(Candidate candidate){
        final StringBuilder stringBuilder = new StringBuilder();
        final SourceClassGenerationHelper sourceClassGenerationHelper = new SourceClassGenerationHelper();

        //build package line and class file name
        buildClassNameFromCandidate(candidate);
        buildFileName(candidate);
        stringBuilder.append(sourceClassGenerationHelper.renderPackageDeclaration(packageName));

        //build import lines
        if(candidate.isDuplicatesAllowed()){
            imports.add("com.restqueue.framework.service.backingstoreduplicatesfilters.DuplicatesAllowed");
        }
        else{
            imports.add("com.restqueue.framework.service.backingstoreduplicatesfilters.DuplicatesNotAllowed");
        }
        setupFilterChainAndImports(candidate);

        stringBuilder.append(sourceClassGenerationHelper.renderImports(imports));

        //build class doc
        stringBuilder.append(sourceClassGenerationHelper.renderClassDocumentation());

        //build path annotation line
        stringBuilder.append(sourceClassGenerationHelper.renderClassLevelAnnotationWithParameter("Path","\""+buildUrlFromCandidate(candidate)+"\""));

        //build class declaration
        stringBuilder.append(sourceClassGenerationHelper.renderClassExtendsDeclaration(className, "AbstractQueueResource"));

        //build duplicates filter method
        stringBuilder.append(sourceClassGenerationHelper.renderMethodLevelAnnotation("Override"));
        if(candidate.isDuplicatesAllowed()){
            stringBuilder.append(getDuplicatesFilterMethodTemplate("return new DuplicatesAllowed();"));
        }
        else{
            stringBuilder.append(getDuplicatesFilterMethodTemplate("return new DuplicatesNotAllowed();"));
        }

        //build filter chain method
        stringBuilder.append(sourceClassGenerationHelper.renderMethodLevelAnnotation("Override"));
        stringBuilder.append(getApplicableFilterChainMethodTemplate("return " + filterChain + ";"));

        //build url method
        stringBuilder.append(sourceClassGenerationHelper.renderMethodLevelAnnotation("Override"));
        stringBuilder.append(getImplementedResourceUrlMethodTemplate("return \""+buildUrlFromCandidate(candidate)+"\";"));

        //build end of class
        stringBuilder.append(sourceClassGenerationHelper.renderClassEnd());

        //write file
        writeFile(stringBuilder.toString());
    }

    private void setupFilterChainAndImports(Candidate candidate) {
        //set base filter
        switch (candidate.getCandidateType()) {
            case POOL:
                filterClassNames.add("new ShuffledFilter()");
                imports.add("com.restqueue.framework.service.backingstorefilters.ShuffledFilter");
                break;
            case QUEUE:
                filterClassNames.add("new ArrivalOrderFilter()");
                imports.add("com.restqueue.framework.service.backingstorefilters.ArrivalOrderFilter");
                break;
            case SEQUENCER:
                filterClassNames.add("new SequencingFilter()");
                imports.add("com.restqueue.framework.service.backingstorefilters.SequencingFilter");
                break;
            case STACK:
                filterClassNames.add("new ReverseArrivalOrderFilter()");
                imports.add("com.restqueue.framework.service.backingstorefilters.ReverseArrivalOrderFilter");
                break;
            default:
                filterClassNames.add("new ArrivalOrderFilter()");
                imports.add("com.restqueue.framework.service.backingstorefilters.ArrivalOrderFilter");
        }

        if(candidate.isUnreservedOnly()){
            filterClassNames.add("new AllUnreservedFilter({0})");
            imports.add("com.restqueue.framework.service.backingstorefilters.AllUnreservedFilter");
        }
        if(candidate.isPriority()){
            filterClassNames.add("new PriorityDescendingFilter({0})");
            imports.add("com.restqueue.framework.service.backingstorefilters.PriorityDescendingFilter");
        }
        imports.add("com.restqueue.framework.service.backingstorefilters.BackingStoreFilter");

        String chainResult = "";
        for (int i = 0; i < filterClassNames.size(); i++) {
            String chain = filterClassNames.get(i);
            if(i>0){
                chainResult=MessageFormat.format(chain, chainResult);
            }
            else{
                chainResult=chain;
            }
        }
        filterChain=chainResult;
    }

    private void buildFileName(Candidate candidate){
        //default package
        packageName = "com.restqueue.gen.web";

        String sourceRoot = ServerArguments.getInstance().getStringArgument(Setup.SPECIFIED_MAIN_BRANCH_ROOT_SWITCH);

        if(sourceRoot==null){
            //default source path
            sourceRoot="src" + File.separator + "main" + File.separator + "java";
        }
        else{
            sourceRoot = StringUtils.removeLeadingAndTrailingCharacters(sourceRoot, File.separator);
        }

        String testSourceRoot = ServerArguments.getInstance().getStringArgument(Setup.SPECIFIED_TEST_BRANCH_ROOT_SWITCH);

        if(testSourceRoot==null){
            //default source path
            testSourceRoot="src" + File.separator + "test" + File.separator + "java";
        }
        else{
            testSourceRoot = StringUtils.removeLeadingAndTrailingCharacters(testSourceRoot, File.separator);
        }

        if(candidate.getFullPath().contains(sourceRoot) || (candidate.getFullPath().contains(testSourceRoot))){
            setTargetFileNameFromSourceRootAndClassName(sourceRoot);
        }
        else {
            throw new IllegalArgumentException("Specified part of file path " +
                    File.separator + sourceRoot + File.separator + " not found.. ");
        }
    }

    private void buildClassNameFromCandidate(Candidate candidate){
        className = candidate.getName()+"Resource";
    }

    private String buildUrlFromCandidate(Candidate candidate){
        buildClassNameFromCandidate(candidate);
        return ("/channels/1.0/"+ className.substring(0,1).toLowerCase() + className.substring(1)).replace("Resource","");
    }

    private String getDuplicatesFilterMethodTemplate(String methodBody){
        return new SourceClassGenerationHelper.MethodBuilder("getDuplicatesFilter").
                setReturnType("BackingStoreDuplicatesFilter").
                setAccess("protected").
                setMethodBody(methodBody).
                build();
    }

    private String getApplicableFilterChainMethodTemplate(String methodBody){
        return new SourceClassGenerationHelper.MethodBuilder("getApplicableFilterChain").
                setReturnType("BackingStoreFilter").
                setAccess("protected").
                setMethodBody(methodBody).
                build();
    }

    private String getImplementedResourceUrlMethodTemplate(String methodBody){
        return new SourceClassGenerationHelper.MethodBuilder("getImplementedResourceUrl").
                setReturnType("String").
                setAccess("protected").
                setMethodBody(methodBody).
                build();
    }

}
