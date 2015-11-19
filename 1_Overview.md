# Overview #

## What is RESTQueue? ##

RESTQueue is a Java open source framework that enables you to very simply and quickly create a REST-based messaging server to support your messaging application or system. The framework uses the principal of convention over configuration so that you can set up a number of messaging channels incredibly quickly but also configure them to your exact requirements.

It features simple REST APIs (using http) for the message channels (so that using it with non-Java systems is simple) but it is also supported by Java libraries to make working with your messages in a Java project even easier.

A simple example of how RESTQueue can be used is for a situation when you have a number of people (or software components) that allocate themselves tasks from a queue, process these tasks and then remove the tasks when complete.

## How do I use it? ##

You can use the REST APIs to send, retrieve, update and delete the messages. The message content is simply a serialized version of any message of your choice. This would typically be a domain object that you already have a Java class defined for. There are also Java client libraries supplied so that you can write client applications in Java to interact with the messaging channels as your message-based system requires.

## The server ##

The server (or servers, as there can be many configured to run on the same physical machine) is the place where the channels are handled and the messaging data is stored. This is run from the command line.

## Types of channel ##

There are a number of different types of channels you can set up using the framework:

  * Queue
  * Stack
  * Pool
  * Sequencer

The Queue is a basic first-in-first-out queue.

The Stack is a last-in-first-out stack.

The Pool is randomised so that the message order does not matter.

The Sequencer enforces a strict order based on a next message id.

The Pool is good for applications with more than one message consumer as it will reduce contention between the consumers trying to reserve or assign the message to themselves.

The Sequencer can be used so that the entry order does not matter but the exit order is tightly controlled. This is good for situations where there are multiple message creators sending messages into the channel (as they are concurrent the entry order cannot be guaranteed) but the application dictates that the order that the messages are taken from the queue is critical.

There are sub-types that can be applied to any of these types and compliment the functionality to make the channels work exactly as you need. They are: Unreserved, Distinct and Prioritised. Unreserved will always just show messages in the channel that have not been reserved (or assigned) to a message consumer. Distinct will refuse duplicate messages. Prioritised will always show the messages in a descending order of priority.

## Messaging roles ##

### Message Creator ###
This role relates to the person (or software component) that is generating the content of the messages and posting them into the channel.

### Message Consumer ###

The Message Consumer is the person (or software component) that is interested in the content of the message and will get the new messages from the channel and decide what to do with them. They will most likely be the ones assigning messages to themselves and finally deleting the messages from the channels.

### Message Listener ###

Message Listeners are subtly different from Message Consumers in that they can be informed of new messages, but are not expected to do anything with them. It may be the case that the Message Listeners are also Message Consumers but not necessarily.

### Channel Administrator ###

The Channel Administrator will be the one setting up the channel in the first place and tuning the settings of the channel to make sure it runs at it should.