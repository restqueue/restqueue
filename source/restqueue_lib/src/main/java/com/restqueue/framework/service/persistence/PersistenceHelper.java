package com.restqueue.framework.service.persistence;

import com.restqueue.common.utils.FileUtils;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.server.AbstractServer;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Nik Tomkinson
 * Date: 16/09/2013
 * Time: 19:08
 */
public class PersistenceHelper {
    private static final Logger log = Logger.getLogger(PersistenceHelper.class);
    protected static final String SNAPSHOT_ID_PLACEHOLDER = "yyyyMMddHHmmss";
    protected static final String CHANNEL_NAME_PLACEHOLDER = "$CHANNEL_NAME$";
    protected static final String SERVER_ID = "server_"+ AbstractServer.PORT;
    protected static final String BASE_FOLDER = System.getProperty("user.home") +
            File.separator +
            Persistence.FRAMEWORK_NAME +
            File.separator +
            SERVER_ID +
            File.separator;

    protected static final String CONTENTS_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            "contents" +
            File.separator;

    protected static final String STATE_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            "state" +
            File.separator;

    protected static final String MESSAGE_LISTENERS_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            Persistence.MESSAGE_LISTENERS_KEY +
            File.separator;

    protected static final String MESSAGE_LISTENER_REGISTRATION_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            Persistence.MESSAGE_LISTENER_REGISTRATION_KEY +
            File.separator;

    protected static final String SNAPSHOT_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            "snapshots" +
            File.separator;

    protected static final String CONTENTS_SNAPSHOT_FOLDER = SNAPSHOT_BASE_FOLDER +
            SNAPSHOT_ID_PLACEHOLDER +
            File.separator +
            "contents" +
            File.separator;

    protected static final String STATE_SNAPSHOT_FOLDER = SNAPSHOT_BASE_FOLDER +
            SNAPSHOT_ID_PLACEHOLDER +
            File.separator +
            "state" +
            File.separator;

    protected static final String LISTENERS_SNAPSHOT_FOLDER = SNAPSHOT_BASE_FOLDER +
            SNAPSHOT_ID_PLACEHOLDER +
            File.separator +
            "messagelisteners" +
            File.separator;

    protected static final String LISTENER_REGISTRATION_SNAPSHOT_FOLDER = SNAPSHOT_BASE_FOLDER +
            SNAPSHOT_ID_PLACEHOLDER +
            File.separator +
            "messagelistenerregistration" +
            File.separator;

    private String filenameExtension;
    private String filenameExtensionCode;

    public PersistenceHelper(String filenameExtension, String filenameExtensionCode) {
        this.filenameExtension = filenameExtension;
        this.filenameExtensionCode = filenameExtensionCode;
    }

    protected void saveChannelStateToPath(final Class associatedChannelResourceClazz, final ChannelState channelState, final String pathToUse){
        final String fileName = classBasedFileNameWithExtension(associatedChannelResourceClazz);
        if(FileUtils.saveToDisk(pathToUse, fileName, channelState.serializeToType(getFilenameExtensionCode()))){
            log.info("Channel state for "+associatedChannelResourceClazz.getCanonicalName()+" saved to disk at:"+
                    pathToUse + classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("There was an error saving to disk - the channel state has not been saved.");
        }
    }

    protected void saveChannelContentsToPath(final Class associatedChannelResourceClazz, final List<EntryWrapper> channelContents, final String pathToUse){
        final String fileName = classBasedFileNameWithExtension(associatedChannelResourceClazz);
        if(FileUtils.saveToDisk(pathToUse, fileName, new Serializer().toType(channelContents, getFilenameExtensionCode()))){
            log.info("Channel contents for "+associatedChannelResourceClazz.getCanonicalName()+" saved to disk at:"+
                    pathToUse + classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("There was an error saving to disk - the channel contents have not been saved.");
        }
    }

    @SuppressWarnings("unchecked")
    protected List<EntryWrapper> loadChannelContentsFromPath(final Class associatedChannelResourceClazz, String pathToUse){
            final List<EntryWrapper> channelContents = new ArrayList<EntryWrapper>();
        final String restoredContents = FileUtils.restoreFromDisk(pathToUse, classBasedFileNameWithExtension(associatedChannelResourceClazz));
        if (restoredContents != null) {
            channelContents.addAll((List<EntryWrapper>) new Serializer().fromType(restoredContents, getFilenameExtensionCode()));
        }

        return channelContents;
    }

    protected String simpleNameFolder(final Class associatedChannelResourceClazz){
        return associatedChannelResourceClazz.getSimpleName().replace("Resource","");
    }

    protected String classBasedFileNameWithExtension(final Class associatedChannelResourceClazz){
        return associatedChannelResourceClazz.getCanonicalName()+getFilenameExtension();
    }

    protected String fillOutPath(String templateString, final Class associatedChannelResourceClazz, String snapshotId){
        String result=templateString.replace(PersistenceHelper.CHANNEL_NAME_PLACEHOLDER, simpleNameFolder(associatedChannelResourceClazz));
        if(snapshotId!=null){
            result=result.replace(PersistenceHelper.SNAPSHOT_ID_PLACEHOLDER, snapshotId);
        }
        return result;
    }

    public String getFilenameExtension() {
        return filenameExtension;
    }

    public String getFilenameExtensionCode() {
        return filenameExtensionCode;
    }
}
