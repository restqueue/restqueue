package com.restqueue.framework.service.server;

import com.restqueue.common.arguments.ArgumentMetaData;
import com.restqueue.common.arguments.ServerArguments;
import com.restqueue.framework.service.persistence.AsynchronousPersistence;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Date: 12/04/2013
 * Time: 19:25
 */
public abstract class AbstractServer {
    //default port
    public static int PORT = 9998;
    private static final Logger log = Logger.getLogger(AbstractServer.class);
    private static List<ArgumentMetaData> allowedArguments=new ArrayList<ArgumentMetaData>();
    public static final String SPECIFIED_PORT_SWITCH = "p";
    public static final String SPECIFIED_PERSISTENCE_SWITCH = "P";
    public static final String SPECIFIED_PERSISTENCE_FREQUENCY_SWITCH = "PF";
    public static final String NO_CACHE_SWITCH = "NC";

    private Thread persistenceThread;

    public void startUpServer(String[] arguments) throws IOException {
        allowedArguments.add(new ArgumentMetaData(SPECIFIED_PORT_SWITCH,"Port", ArgumentMetaData.ArgumentMetaDataType.INTEGER, PORT));
        allowedArguments.add(new ArgumentMetaData(NO_CACHE_SWITCH,"No Cache", ArgumentMetaData.ArgumentMetaDataType.BOOLEAN, false));
        allowedArguments.add(new ArgumentMetaData(SPECIFIED_PERSISTENCE_SWITCH, "Persistence", ArgumentMetaData.ArgumentMetaDataType.STRING, "Normal"));
        allowedArguments.add(new ArgumentMetaData(SPECIFIED_PERSISTENCE_FREQUENCY_SWITCH, "Persistence Frequency in seconds",
                ArgumentMetaData.ArgumentMetaDataType.INTEGER, 30000));
        ServerArguments.createInstance(allowedArguments, arguments);

        PORT = ServerArguments.getInstance().getIntegerArgument(SPECIFIED_PORT_SWITCH);

        //start persistence thread if needed
        if(ServerArguments.getInstance().getStringArgument(SPECIFIED_PERSISTENCE_SWITCH).equalsIgnoreCase("Polling")){
            AsynchronousPersistence persistence= AsynchronousPersistence.getInstance();
            persistence.setRefreshInterval(ServerArguments.getInstance().getIntegerArgument(SPECIFIED_PERSISTENCE_FREQUENCY_SWITCH));

            persistenceThread = new Thread(persistence);
            persistenceThread.start();
        }

        final String baseUri = "http://localhost:" + PORT + "/";
        final Map<String, String> initParameters =
                new HashMap<String, String>();

        initParameters.put("com.sun.jersey.config.property.packages", "com.restqueue.gen.web");

        log.info("Starting server using port "+PORT);
        SelectorThread threadSelector =
                GrizzlyWebContainerFactory.create(baseUri, initParameters);

        onStart();

        log.info("Server started! Type QUIT, STOP or EXIT to stop this server.");

        final BufferedReader bufferedConsoleReader = new BufferedReader(new InputStreamReader(System.in));
        String command="";
        while(!(command.equalsIgnoreCase("QUIT") ||command.equalsIgnoreCase("STOP") || command.equalsIgnoreCase("EXIT"))){
            command = bufferedConsoleReader.readLine();
        }

        log.info("Server stopping!");
        threadSelector.stopEndpoint();

        if(persistenceThread!=null){
            persistenceThread.interrupt();
        }

        onShutDown();

        System.exit(0);
    }

    public abstract void onStart();

    public abstract void onShutDown();
}
