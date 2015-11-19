# Message Consuming using the REST API #
## Getting the channel contents ##

GET the message channel contents from:

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/entries
```

using the following mandatory http header:
```
Accept:(application/xml or application/json)
```

and the following optional header:
```
If-None-Match:{the value of the eTag from the previous GET attempt}
```

As the response you should get a serialised version of the channel contents in your chosen content type and the eTag header.

This can be easily done in a browser which returns an HTML rendering of the contents.

If you want just the messages that are contained within a specific batch, use the endpoint:

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/entries/batch/{batchid}
```

or if you want just messages of a specific priority, use:

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/entries/priority/{priorityGroup}
```

alternatively if you want just messages that have not already been assigned (or reserved) by a message consumer, use:

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/entries/unreserved
```


Take a look at the wiki page http://code.google.com/p/restqueue/wiki/3_Message_Creation for more information on batching and prioritising.

## Getting the Message ##

Once you have retrieved the channel contents, you will see that the contents is a list of items and one of the pieces of information for each item is a URL location. To get the detail of the message at this location, you just need to:

GET the message detail from the value of the Location header retrieved from the selected message summary in the channel contents for example:

```
http://{serverip}:{serverport}/channels/1.0/{channelName}/entries/1234567890
```

using the following mandatory http header:
```
Accept:(application/xml or application/json)
```

For the response, you should get the serialised version of the message contents in your choice of content type. There will be various headers provided for you to use later if needed.

## Reserving the Message ##
Reserving the message is pretty much the same as updating the message, but you have to make sure that you add the header:

```
x-restqueue-message-consumer:{messageConsumerId}
```
This will identify who has the message reserved.

You need to make sure that you use the latest eTag as follows:

```
If-Match:{last eTag value for the message}
```

this stops you overwriting other people's changes.

## Updating the Message ##
To update a message you simply PUT the message back to its Location address with the changes that have been made. These can be changes to the message content or any of the existing headers (or even additional headers as required).

You need to make sure that you use the latest eTag as follows:

```
If-Match:{last eTag value for the message}
```

this stops you overwriting other people's changes.

You should receive a 200 (OK) from the server on a successful update. If your version of the message or headers is out of date and there is a risk that you would overwrite someone else's changes, then you will get a 412 (Precondition failed). You should re-GET the message and try again.

## Deleting the Message ##

To delete a message from the channel, you send a DELETE to the Location of the message. You don't need to send anything in the body and no special headers. You should get a 200 (OK) on a successful delete.