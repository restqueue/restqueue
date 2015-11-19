# Getting Started #

## Requirements ##

Running on your server, you will need Java Standard Edition installed and running. A RESTQueue server should run fine on any Unix or Windows-based machine as long as you can get to the command line and run Java programs from there it should be ok. As this framework supports your own message based requirements, you would need an existing Java project that compiles and contains the classes that define the messages that you want to send on the channels.

## Download ##

Go to the 'Source' link above, and then click 'Browse' and navigate to the 'downloads' folder to download the libraries needed to create and run the server and save them in your Java workspace:

  * The RESTQueue application (run once to generate the server and messaging channels):
`RestQueueApp-{major}.{minor}-jar-with-dependencies.jar`
  * The RESTQueue framework library (contains all you need to run the server and to write Java messaging client code easily)
`RestQueueLib-{major}.{minor}-jar-with-dependencies.jar`

To download the jar files, select them in turn from the git repository and then click on 'View raw file' in the bottom right to download them.

## Building the server ##

Make sure that you create 'Trigger' classes in your java project that use the following convention to create the channels just as you need them:

```
{A}{B}{C}{D}.java
```

Where:
  * A can be 0 or more of Distinct, Unreserved, Priority, Prioritising, Prioritizing
  * B is your message class name
  * C can also be 0 or more of Distinct, Unreserved, Priority, Prioritising, Prioritizing
  * D must be 1 of Queue, Stack, Pool or Sequencer.

Eg. `DistinctCustomerSupportQueryQueue.java` will trigger generation of a Queue that provides unique CustomerSupportQuery messages.

Eg. `UnreservedTaskPool.java` (or `TaskUnreservedPool.java`) will trigger generation of a Pool (randomly ordered list) of Task information and only show Tasks that have not already been reserved.

These Trigger classes don't need to extend anything, implement anything, use annotations or have any specific methods. They are just used to trigger the channel creation when the server is building.

Once these Trigger classes are ready and the project compiles into an executable jar, on the command line navigate to the source root (where the top level package folder is) and then run the following:

```
> java -jar {path_to_RestQueueApp_jar}/RestQueueApp-{major}.{minor}-jar-with-dependencies.jar
```

This will build your server using default options.
You can also use these optional arguments:

```
-T:{relative_path_to_test_branch_of_code}

-M:{relative_path_to_main_branch_of_code}
```

which means you can specify alternative places to look for the Trigger classes (other than the defaults src/main/java/... and src/test/java/...). This is in case your project source is not in the src/main/java/ and src/test/java/ typical folder structure.

```
-t:{Include/Exclude}
```

which enables you to tell the server generation code to include or exclude the test branch of code (src/test/java/...). The default is to exclude it.

If all is well, the server will have been created with the REST endpoints for the messaging channels as follows:

Eg. `DistinctCustomerSupportQueryQueue.java` should trigger generation of an endpoint at http://{serverip}:{serverport}/channels/1.0/distinctCustomerSupportQueryQueue

and a `Server.java` class which you need to configure as the main class in your executable jar (done in the manifest file or via your Maven pom).

You also need to make sure that when your server runs, it has access to the RESTQueue libraries. So you will need to add it to the classpath when compiling and running, or add the following to your maven pom file:

```
        <dependency>
            <groupId>RestQueue</groupId>
            <artifactId>RestQueueLib</artifactId>
            <version>{major}.{minor}</version>
        </dependency>
```

## Running the server ##

To start using the messaging channels you need to run the server. This is done from the command line as follows:

```
> java -jar {your_server_jar_name}.jar
```

You can also use these optional arguments:

```
-P:{Normal/Polling/ReadOnly/None}
```

which defines the type of channel contents persistence to use Normal (synchronous), Polling (asynchronous), ReadOnly (only reads previous state) or None (all contents reside in volatile memory only and will not survive a restart). Default is Normal for synchronous persistence.

```
-PF:{Polling persistence frequency in seconds}
```

for use with the Polling persistence. The default is 30 seconds.
```
-p:{server port number}
```

to use one other than the default 9998

```
-NC:{true/false}
```

to enable or disable channel contents caching on the client side (which is a feature that saves bandwidth by only sending the channel contents when they have changed). The default is false so that caching is enabled.