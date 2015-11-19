# Java Client Libraries #

## API Documentation ##
To get you started with using the Java client libraries, you can find the `JavaDoc` in the GIT repository at [http://code.google.com/p/restqueue/source/browse?repo=javadoc](http://code.google.com/p/restqueue/source/browse?repo=javadoc) which will be updated whenever anything significant changes.

To get the `JavaDoc`, you can either clone the GIT repository, or download it as a zip file using the 'zip' link provided.

## Message Sender Libraries ##

There is currently one library that is to be used by software that has the role of Message Sender. This is as follows:

### Basic Message Sender ###

The `BasicMessageSender` class has all the functionality that you need to send messages into the channel. Example code to use this class is shown below:

```
package your.own.package;

import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.messageproducers.BasicMessageSender;
import com.restqueue.framework.client.results.Result;
import com.restqueue.user.domainentities.Complaint;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Nik Tomkinson
 * Date: 23/07/2013
 * Time: 22:09
 */
public class Send {
    private final Map<CustomHeaders, List<String>> headerMap = new HashMap<CustomHeaders, List<String>>();
    private Object body;

    public static void main(String[] args) {
        final Send send = new Send();
        send.body = new Complaint(args[0]);

        send.prepareMessage();
        System.out.println(send.send());
    }


    public void prepareMessage() {
        final String messageCreator = "message_creator_1";
        headerMap.put(CustomHeaders.CREATOR, Collections.singletonList(messageCreator));
    }

    private Result send() {
        final BasicMessageSender basicMessageSender = new BasicMessageSender();
        basicMessageSender.setServerIpAddress("localhost");
        basicMessageSender.setServerPort(9998);
        basicMessageSender.setChannelEndpoint("/channels/1.0/complaintStack");
        basicMessageSender.setObjectBody(body);

        for (Map.Entry<CustomHeaders, List<String>> entry : headerMap.entrySet()) {
            basicMessageSender.addHeader(entry.getKey(), entry.getValue());
        }

        return basicMessageSender.sendMessage();
    }
}

```

## Message Consumer Libraries ##

### Basic Message Consumer ###

The `BasicMessageConsumer` class has all the functionality that you need to get messages from the channel, assign them and remove them. Example code to use this class is shown below:

```
package your.own.package;

import com.restqueue.framework.client.entrywrappers.EntrySummary;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.client.exception.ChannelClientException;
import com.restqueue.framework.client.messageconsumers.BasicMessageConsumer;

import java.io.IOException;
import java.util.List;

/**
 * User: Nik Tomkinson
 * Date: 24/07/2013
 * Time: 21:05
 */
public class Retrieve {
    public static void main(String[] args) throws IOException, ChannelClientException {
        final Retrieve retrieve = new Retrieve();
        retrieve.retrieve();
    }

    private void retrieve(){
        final BasicMessageConsumer basicMessageConsumer = new BasicMessageConsumer();
        basicMessageConsumer.setChannelEndpoint("/channels/1.0/complaintStack");
        basicMessageConsumer.setServerIpAddress("localhost");
        basicMessageConsumer.setServerPort(9998);

        //get the channel contents
        List<EntrySummary> entrySummaries = basicMessageConsumer.getAllMessages();

        if(entrySummaries.isEmpty()){
            System.out.println("There are no messages in the channel");
            return;
        }

        for (EntrySummary entrySummary : entrySummaries) {
            System.out.println(entrySummary);
        }

        //get the first message detail
        basicMessageConsumer.setUrlLocation(entrySummaries.get(0).getLinkUri());
        final EntryWrapper entryWrapper = basicMessageConsumer.getMessage();

        System.out.println(entryWrapper);

        //reserve the message
        basicMessageConsumer.setETag(entryWrapper.getETag());
        basicMessageConsumer.setMessageConsumerId("message_consumer_01");
        basicMessageConsumer.reserveMessage();

        //delete the message
        basicMessageConsumer.deleteMessage();
    }

}

```

### Abstract Polling Message Consumer ###

The `AbstractPollingMessageConsumer` abstract class is a polling message consumer that polls the channel at a configurable frequency and calls the subclass-implemented method `processMessages(List<EntrySummary> entrySummaryList)` when the contents have changed.

When you have the server running on the default setting to allow conditional GETs, the output looks like it runs just once (until you change the contents of the channel). Watching the server log will show you the conditional requests coming in.

```
package your.own.package;

import com.restqueue.framework.client.entrywrappers.EntrySummary;
import com.restqueue.framework.client.messageconsumers.AbstractPollingMessageConsumer;

import java.util.List;

/**
 * User: Nik Tomkinson
 * Date: 31/07/2013
 * Time: 12:06
 */
public class PollingClient extends AbstractPollingMessageConsumer {

    public static void main(String[] args) throws InterruptedException {
        final PollingClient pollingClient = new PollingClient();
        pollingClient.poll();
    }

    private void poll() throws InterruptedException {
        setChannelEndpoint("/channels/1.0/complaintStack");
        setServerPort(9998);
        setPollingFrequencyInSeconds(5);
        startPolling();
    }

    @Override
    public void processMessages(List<EntrySummary> entrySummaryList) {
        int noAssigned = 0;
        for (EntrySummary entrySummary : entrySummaryList) {
            if (entrySummary.getMessageConsumerId() != null && !entrySummary.getMessageConsumerId().isEmpty()) {
                noAssigned++;
            }
        }
        System.out.println("Number of messages:"+entrySummaryList.size()+", assigned:" + noAssigned);

    }
}


```

## Server Admin and Config Libraries ##

### Basic Channel Manager ###

The `BasicChannelManager` has all the functionality you need to perform the administration and configuration on your channels. The following code shows an example of how some of the functionality is used:

```
package your.own.package;

import com.restqueue.framework.client.channelmanagement.BasicChannelManager;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
import com.restqueue.framework.client.results.Result;
import com.restqueue.framework.service.channelstate.BatchStrategy;
import com.restqueue.framework.service.channelstate.SequenceStrategy;

/**
 * User: Nik Tomkinson
 * Date: 01/08/2013
 * Time: 09:37
 */
public class ChannelManager {
    public static void main(String[] args){
        final ChannelManager channelManager = new ChannelManager();
        channelManager.manageChannel();
    }

    private void manageChannel(){
        final BasicChannelManager basicChannelManager = new BasicChannelManager();
        basicChannelManager.setChannelEndpoint("/channels/1.0/complaintStack");
        basicChannelManager.setServerIpAddress("localhost");
        basicChannelManager.setServerPort(9998);

        //add a new priority group called 'quite_low' which will be from 10 to the next highest one minus one
        Result result = basicChannelManager.addChannelPriority("quite_low", 10);
        System.out.println(result);

        //get the new priority band
        result = basicChannelManager.getChannelPriority("quite_low");
        System.out.println(result);

        //update this new priority band from start from 5 instead of 10
        result = basicChannelManager.updateChannelPriority("quite_low",5,"10");
        System.out.println(result);

        //remove the priority band
        basicChannelManager.deleteChannelPriority("quite_low");
        System.out.println(result);

        //set the channel to drain, so that ant new messages will be rejected
        result = basicChannelManager.drain();
        System.out.println(result);

        //set the max channel size to 124
        result = basicChannelManager.updateChannelMaxSize(124);
        System.out.println(result);

        //increment the next message sequence on the channel when the current sequence is 3
        result = basicChannelManager.incrementNextMessageSequence(0);
        System.out.println(result);

        //set the batching strategy to "ARRIVAL"
        result = basicChannelManager.updateBatchStrategy(BatchStrategy.ARRIVAL);
        System.out.println(result);

        //set the sequence strategy to "GROUPED"
        result = basicChannelManager.updateSequenceStrategy(SequenceStrategy.GROUPED);
        System.out.println(result);

        //register a new message listener called 'test_listener' to listen for messages in 'batch_01' only, and get the
        //notifications appended to a file called 'my_notifications'
        result = basicChannelManager.registerMessageListener("test_listener",new ReturnAddress(ReturnAddressType.LOGFILE, "my_notifications"),"batch_01",null);
        System.out.println(result);

        //un-register a message listener called 'test' that is listening for all messages
        result = basicChannelManager.unRegisterMessageListener("test",null,null);
        System.out.println(result);
    }

}

```