package com.restqueue.app.classgeneration;

import com.restqueue.app.Setup;
import com.restqueue.common.arguments.ServerArguments;
import com.restqueue.common.classgeneration.AbstractSourceBuilder;
import com.restqueue.common.classgeneration.SourceClassGenerationHelper;
import com.restqueue.common.utils.StringUtils;

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
 * Date: 14/04/2013
 * Time: 12:27
 */
public class ServerClassBuilder extends AbstractSourceBuilder {
    public ServerClassBuilder() {
        addImport("com.restqueue.framework.service.server.AbstractServer");
        addImport("java.io.IOException");
        packageName = "com.restqueue.gen.app";
        className = "Server";
    }

    public void build(){
        final StringBuilder stringBuilder = new StringBuilder();
        final SourceClassGenerationHelper sourceClassGenerationHelper = new SourceClassGenerationHelper();

        stringBuilder.append(sourceClassGenerationHelper.renderPackageDeclaration(packageName));
        stringBuilder.append(sourceClassGenerationHelper.renderImports(imports));
        stringBuilder.append(sourceClassGenerationHelper.renderClassDocumentation());
        stringBuilder.append(sourceClassGenerationHelper.renderClassExtendsDeclaration(className, "AbstractServer"));

        final String mainMethod = new SourceClassGenerationHelper.MethodBuilder("main").
                setStatic(true).setAccess("public").setMethodBody("new Server().startUpServer(arguments);").
                addMethodArgument(false, "String[]", "arguments").
                addException("IOException").
                build();
        stringBuilder.append(mainMethod);

        final String onStartMethod = new SourceClassGenerationHelper.MethodBuilder("onStart").
                setStatic(false).setAccess("public").setMethodBody("// set up any custom implementations here").build();
        stringBuilder.append(onStartMethod);

        final String onShutDownMethod = new SourceClassGenerationHelper.MethodBuilder("onShutDown").
                setStatic(false).setAccess("public").setMethodBody("// call custom shut down code here").build();
        stringBuilder.append(onShutDownMethod);

        stringBuilder.append(sourceClassGenerationHelper.renderClassEnd());

        String sourceRoot = ServerArguments.getInstance().getStringArgument(Setup.SPECIFIED_MAIN_BRANCH_ROOT_SWITCH);

        if(sourceRoot==null){
            //default source path
            sourceRoot="src" + File.separator + "main" + File.separator + "java";
        }
        else{
            sourceRoot = StringUtils.removeLeadingAndTrailingCharacters(sourceRoot, File.separator);
        }

        setTargetFileNameFromSourceRootAndClassName(sourceRoot);

        writeFile(stringBuilder.toString());
    }

}
