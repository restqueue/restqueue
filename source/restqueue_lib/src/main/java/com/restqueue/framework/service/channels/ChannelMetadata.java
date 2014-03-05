package com.restqueue.framework.service.channels;

import com.restqueue.framework.client.common.summaryfields.EndPoint;

/**
 * User: Nik Tomkinson
 * Date: 04/01/2014
 * Time: 20:15
 */
public class ChannelMetadata {
    private Class channelClass;
    private EndPoint channelEndPoint;

    public ChannelMetadata() {

    }

    public ChannelMetadata(Class channelClass, String url) {
        this.channelClass = channelClass;
        final String[] split = url.split("/");
        String name=split[split.length-1].substring(0,1).toUpperCase()+split[split.length-1].substring(1);
        this.channelEndPoint=new EndPoint.EndPointBuilder().setUrl(url).
                setDescription(name).
                setShortCode(name.toUpperCase()).
                build();
    }

    public Class getChannelClass() {
        return channelClass;
    }

    public EndPoint getChannelEndPoint() {
        return channelEndPoint;
    }
}
