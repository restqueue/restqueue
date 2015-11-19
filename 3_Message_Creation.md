# Message Creation #

## Putting Messages on the channel using the REST API ##

For the `MyMessagesQueue.java` Trigger class...

POST the serialised version of the message contents to:

```
http://{serverip}:{serverport}/channels/1.0/myMessagesQueue/entries
```

using the following mandatory http header:
```
Content-Type:(application/xml or application/json)
```

and the following optional headers:
```
x-restqueue-message-creator:{name or id to identify the message creator}
x-restqueue-message-batchkey:{batch key for the message (see section on batching) eg. 'Batch_01:1/2'}
x-restqueue-message-delay:{amount to delay the message eg. '600' for 600 seconds or '1d:2h:10m' for 1 day, 2 hours and 10 minutes (see section on delaying)}
x-restqueue-message-delay-until:{explicit delay-until date (see section on delaying)}
x-restqueue-message-priority:{integer priority (see the section on prioritising)}
x-restqueue-message-sequence:{integer sequence number (see the section on sequencing)}
x-restqueue-return-addresses:{a return address for the sender eg. 'EMAIL:bob@messagesenders.org'}
```

Once this is sent, you should get a http response code of 201 for 'created' and a http header for the Location of the new message.

## Batching ##

To assign a message to a batch, you create a batch key using the following convention:
```
{batchid}:{message_number}/{batch_size}
```
eg. `Batch01:1/2`
which is message 1 of 2 in batch 'Batch01'

Then simply use the custom http header `x-restqueue-message-batchkey` as the following example shows:

```
x-restqueue-message-batchkey:Batch01:1/2
```

## Delaying ##

To delay a message from being shown in the channel contents you can specify a delay duration in integer number of seconds as follows:

```
x-restqueue-message-delay:3661
```
for 1 hour, 1 minute and 1 second in seconds
or alternatively the same duration as a combination of parts:
```
x-restqueue-message-delay:1h:1m:1s
```

You can use Years (y), Months (M), Weeks (w), days (d), hours (h), minutes (m) and seconds (s).

Also you can specify the delay-until date precisely (in [RFC2822](http://tools.ietf.org/html/rfc2822) format), for example:
```
x-restqueue-message-delay-until:Fri, 29 Nov 2013 21:33:09 GMT
```



## Prioritising ##

To prioritise a message you just need to provide the following custom http header:
```
x-restqueue-message-priority:80
```
for a priority of 80

The default scale is 0 to 100 but you can use any integer as it is relative to other messages. The channel administrator can set the priority settings, but the defaults are 0-33 is priorityGroup=low, 34-66 is priorityGroup=medium and 67-100 is priorityGroup=high.

## Sequencing ##

In order to make the sequencer type of channel work as it should, you need to assign a sequence id to the messages as they are POSTed to the channel. To do this you should use the following custom header:
```
x-restqueue-message-sequence:7
```
which means this is the 7th message and it should only be shown when the value for the channel next message sequence is set to 7.