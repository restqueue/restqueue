# Listening for Messages using the REST API #
## Configuring yourself as a Message listener ##

To add yourself as a message listener, you need to have an address of some sort where you can receive notifications. The currently supported methods of receiving notifications are:

  * email
  * a URL
  * text message
  * summarised into a log file
  * postal
  * telephone
  * fax.

Of these, only email and log file have been implemented. The others are set as placeholders for your own custom implementations (or will be implemented in a later version of RESTQueue).

Once you have an address, you PUT a request to:

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/registration/messagelisteners/{listenerId}
```

with the custom header of:

```
x-restqueue-return-addresses:{your return address}
```
for example:
```
x-restqueue-return-addresses:EMAIL:bob@messagelisteners.org
```

The listenerId in the URL can be anything that uniquely identifies you as a message listener (but if you are also a message consumer, use your messageConsumerId for simplicity)

you can alternatively PUT this to:

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/registration/batch/{batchId}/messagelisteners/{listenerId}
```

to just get notifications about messages in the specified batch (specified by batchid)

or

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/registration/priority/{priorityId}/messagelisteners/{listenerId}
```

to just get notifications about messages of a specified priority group (eg. 'high').

You can unregister yourself for listening on any of these URLs as well. To do this just send a DELETE to any of the URLs above.