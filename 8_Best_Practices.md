# Best Practices #

## Domain Objects ##

The domain objects are the objects that represent the message body and are specific to your application. For example a coffee order, a customer support issue or an item of clothing for dry cleaning could be domain objects that populate the channels.

### Implement equals and hashcode ###

In order for the framework to correctly determine whether a message sender has added a duplicate message to the channel or whether to allow a message update to be made, you need to make sure that the domain object contains valid and meaningful implementations of the equals and hashcode methods.