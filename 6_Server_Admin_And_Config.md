# Server administration and config #
In order to get the server running just as you want, there may be some administration to do in terms of configuring the channels and the server.

Some of the setting and functionality detailed below can be managed through the web interface at:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}
```

Take a look and see what you can do.

## Persistence ##
There are a number of methods for saving the channel contents to ensure that when the server is restarted the contents are not lost forever. These methods are outlined below:

### Normal synchronous file persistence ###
This is useful when you need to make sure that the channel contents are never lost. When using this type, the channel contents are saved whenever a message is created, updated or deleted on the channel. For very highly concurrent applications, the disk activity may become a bottleneck so another type of persistence may be more appropriate. This is the default type of persistence so you don't need to configure the server to use this. The contents are saved to disk at the following location:
```
/{userHome}/restqueue/server_{portNumber}/{channelName}/contents}
```

### Polling asynchronous file persistence ###
The Polling persistence runs asynchronously and saves the contents to the same location as the Normal persistence. This runs in a dedicated thread so there is no disk activity bottleneck and therefore it is more appropriate for highly concurrent applications.

You configure your server to run using this type of persistence using the command line argument
```
-P:Polling
```

You should also set the polling frequency to something that suits your application, although the default is set to 30 seconds. You can set the polling frequency using this command line argument:
```
-PF:{frequencyInSeconds}
```

There is a maximum frequency set to once every 5 seconds. This is to minimise the load on the server.

### Read-Only file persistence ###
If for some reason you need to start a new server and have a channel pre-populated with the contents from a previously saved state you can use this persistence type.

You configure your server to run using this type of persistence using the command line argument
```
-P:ReadOnly
```

### No persistence ###

For some applications, it is perfectly fine to maintain the channel contents in memory and also fine to lose the contents if the server is restarted. It may also be ok to manually (or programmatically) take snapshots of the channel contents instead or using the persistence functionality.

You configure your server to run using this type of persistence using the command line argument
```
-P:None
```

## Channel sizing using the REST API ##

In order to keep the memory usage of the server to a manageable level (or for other reasons) you may want to restrict the maximum number of messages that the channel will hold. The default is set to 100 and the server will not allow any more messages onto the channel above this number. In this case it will return a 409 (Conflict) response. If you want to configure the max size to something other than the default, you can set it using the REST API. This can be done by PUTting a single integer number to the following URL:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/state/maxSize
```

You will need to use the following mandatory header:
```
If-Match:{the current value}
```

You should get a 200 (OK) response on a successful update but if someone else has changed it since you last checked the value, you will get a 412 (Precondition failed). You should try again with the latest current value.

## Batching strategy ##

If you decide that batching of messages is useful to your application, you can take advantage of two alternative ways of retrieving the batched messages. These are as follows:

### Arrival strategy ###
This batching strategy allows consumers of batched messages to retrieve the messages as soon as they are posted onto the channel using the URL specified below:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/entries/batch/{batchid}
```

So that the URL is simply acting as a filter to return just the messages assigned to {batchid}.

### Complete strategy ###
This batching strategy only allows consumers of batched messages to get the messages from:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/entries/batch/{batchid}
```
when the whole batch of messages has arrived on the channel. This can be useful when you have multiple message senders working on parts of the same batch but the messages can be retrieved and subsequently processed only when the whole batch is available.

### Setting the value using the REST API ###

Arrival strategy is the default setting for the channel, but you can explicitly set the strategy by PUTting the new setting value ('ARRIVAL' or 'COMPLETE') to the following URL:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/state/batchStrategy
```

and using the mandatory header:
```
If-Match:{the eTag for the current value}
```

To get the eTag for the current value, you will have to GET the current value (from the same URL) and then look for the eTag header in the response.

## Sequencing ##

For a Sequencer channel type to work effectively, the channel must keep track of which is the next message to be made available to the message consumer using the nextMessageSequence value. When a message is assigned to a message consumer, this value is automatically rolled forward to the next message sequence. It is important that the message consumer assigns the messages in the sequence order otherwise the channel sequence will be incorrectly set. This value can be set as follows:

### Updating nextMessageSequence using the REST API ###
Send the new value as a simple integer to:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/state/nextMessageSequence
```

You will need to use the following mandatory header:
```
If-Match:{the current value}
```

## Sequence strategy ##

If you have configured your channel to be a Sequencer, then there is another option available to you which controls the way that the channel releases the messages to the message consumer. This is the sequence strategy. There are two setting as follows:

### Single ###

When this strategy setting is configured, the channel will only allow a single message to be retrieved from the channel. This message is the one with the correct sequence value.

For example, if the channel receives the messages with sequence values 2,3,1,5 and the message nextMessageSequence value is set to 1, a GET to the channel contents URL will just show the message with the sequence value of 1. When the channel nextMessageSequence is set to 2, just message 2 will show.

### Grouped ###

In contrast to Single, Group will allow retrieval of as many messages as it can given the current nextMessageSequence value. So if the channel receives the messages with sequence values 2,3,1,5 and the message nextMessageSequence value is set to 1, a GET to the channel contents URL will show messages with sequences 1,2 and 3.

### Setting the value using the REST API ###

Grouped strategy is the default setting for the channel, but you can explicitly set the strategy by PUTting the new setting value ('SINGLE' or 'GROUPED') to the following URL:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/state/sequenceStrategy
```

and using the mandatory header:
```
If-Match:{the eTag for the current value}
```

To get the eTag for the current value, you will have to GET the current value (from the same URL) and then look for the eTag header in the response.

## Snapshots ##

If at any point you need to keep a permanent record of what the state of the channel was at a specific point in time, this can easily be achieved using the Snapshot functionality. When you take a snapshot of the channel, the contents, state and message listener registration information is written to disk. You can always restore from this snapshot at a later date.

### Managing Snapshots using the REST API ###

A snapshot can be taken by POSTing an empty body to the following URL:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/snapshots
```

The snapshot will be created and you can see a list of current snapshots by GETting it from the same URL. Using the mandatory header:
```
Content-Type:(application/xml or application/json)
```

To restore from a snapshot, you POST an empty body to:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/restore/{snapshotId}
```

The snapshotId is a descending timestamp of the time that the snapshot was taken (eg. 20130828230504 taken at 23:05:04 on 28th Aug 2013 this is in local timezone for convenience) and is found as part of the snapshot list retrieved as detailed above.

All of the snapshot management functionality can be done through the admin pages at:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}
```

The snapshot data is saved to disk at:
```
/{userHome/restqueue/server_{portNumber}/{channelName}/snapshots/{snapshotId}}
```

## Priority settings ##

In order to be able to group the messages on the channel by priority, you need to set up bands of priority that suit your application. The defaults work very well and are set as follows:

| **low** | **medium** | **high** |
|:--------|:-----------|:---------|
|0 to 32  |33 to 65    |66 and above|

You can set your own priority grouping if these defaults do not work for you. There can be as many groups as you need and they can be named however you like.

### Setting priority groups using the REST API ###

This is done in stages. You delete bands you don't want, update bands where the name is right but the value is wrong and you add new bands with your own values.

To add new bands or update an existing band, PUT the band value to the following URL:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/state/prioritySettings/{priority}
```

So, for example, if you wanted to add a new band and you just had the defaults set, you might PUT the value 80 (in the http body) to:
```
http://localhost:9998/channels/1.0/myMessagesQueue/state/prioritySettings/very_high
```

After this action, the groups would look like this:
| **low** | **medium** | **high** | **very\_high** |
|:--------|:-----------|:---------|:---------------|
|0 to 32  |33 to 65    |66 to 79  |80 and above    |

If you wanted to change the setting for medium to 25, you would PUT the value 25 in the body to
```
http://localhost:9998/channels/1.0/myMessagesQueue/state/prioritySettings/medium
```

and then the settings would look like:

| **low** | **medium** | **high**| **very\_high** |
|:--------|:-----------|:--------|:---------------|
|0 to 24  |25 to 65    |66 to 79 |80 and above    |

If you wanted to remove a priority group, you would send a DELETE to
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/state/prioritySettings/{priority}
```

so that (for example) if you felt that for some reason 'high' was no longer needed, send DELETE to:
```
http://localhost:9998/channels/1.0/myMessagesQueue/state/prioritySettings/high
```

after this, the settings would look like:

| **low** | **medium** | **very\_high** |
|:--------|:-----------|:---------------|
|0 to 24  |25 to 79    |80 and above    |

At any point in time, you can look at the priority groups by GETting them from the following URL:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/state/prioritySettings
```

## Channel contents caching ##

In some applications where there are thousands of messages sitting on a channel, you may want to prevent a polling message consumer from getting the whole channel contents every time that they poll the channel for new messages. This could be to save traffic on the local network or increase response time. The default behaviour is to allow a conditional GET (using a `If-None-Match` header) of the channel contents (which will return a 304 (Unmodified)). This tells the message consumer that they don't need to GET the channel contents again as it is exactly the same as their last GET. If a normal GET is executed (without a `If-None-Match` header) then the channel contents are returned whether they have changed or not.

If you want to make sure that the channel contents are returned whether the contents have changed or not (i.e. disable conditional GET), then run the server with the following command line argument:
```
-NC:true
```

## Headless mode ##

When you start your server, you can configure it to run in either a headless mode (where you start it the normal way and then stop it using the REST API by sending a POST to:
```
http://{serverip}:{serverport}/control/1.0/stopserver
```

or you can configure it to run inside a console window where you start it the usual way and stop it by typing either 'stop','exit' or 'quit' into the console window.

The default mode is headless, but you can configure it to run in the mode you want by using the following command line argument:
```
-h:{true/false}
```

where true means headless and false is running in a console window.

## Message Listening ##

It is good to keep track of who is listening for new messages on a channel, so you can do this through the REST API by GETting the results of:
```
http://{serverip}:{serverport}/channels/1.0/{channelName}/registration/messagelisteners
```

for message listeners that are listening for ALL messages,

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/registration/batch/{batchId}/messagelisteners
```

for listeners of a specific batchId, or

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/registration/priority/{priorityId}/messagelisteners
```

for listeners of a specific priority.