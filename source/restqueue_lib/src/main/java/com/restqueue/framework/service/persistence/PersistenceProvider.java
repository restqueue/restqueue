package com.restqueue.framework.service.persistence;

import com.restqueue.common.arguments.ServerArguments;
import com.restqueue.framework.service.server.AbstractServer;

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
 * Date: 25/08/2013
 * Time: 09:39
 */
public class PersistenceProvider {
    public static Persistence getPersistenceImplementationBasedOnProgramArguments(){
        final Persistence persistence;

        final String persistenceType = ServerArguments.getInstance().getStringArgument(AbstractServer.SPECIFIED_PERSISTENCE_SWITCH);
        if(persistenceType!=null && persistenceType.equalsIgnoreCase("ReadOnly")){
            persistence=new ReadOnlyXmlPersistence();
        }
        else if(persistenceType!=null && persistenceType.equalsIgnoreCase("None")){
            persistence=new DoNothingPersistence();
        }
        else if(persistenceType!=null && persistenceType.equalsIgnoreCase("Polling")){
            persistence=AsynchronousPersistence.getInstance();
        }
        else{
            //default
            persistence=new XmlFilePersistence();
        }
        return persistence;
    }
}
