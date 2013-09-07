package com.restqueue.app.analysis;


import com.restqueue.common.utils.FileUtils;

import java.io.*;
import java.util.*;


public class FileStructureAnalysis {

    public static HashSet<String> getFiles(String startingDirectory, String endFileName, String[] excludeBranchMatches)
            throws java.io.IOException {

        for (String excludeBranchMatch : excludeBranchMatches) {
            //if the current path matches any excluded branches - do not return any files
            if(startingDirectory.toLowerCase().endsWith(excludeBranchMatch)){
                return new HashSet<String>();
            }
        }

        //Local Variables
        String tmpFullFile;
        File startDir = new File(startingDirectory);
        File tmpFile;
        String[] thisDirContents;
        HashSet<String> filesFound = new HashSet<String>();

        //Check that this is a valid directory
        if (!startDir.isDirectory()) {
            throw new java.io.IOException(startingDirectory + " was not a valid directory");
        }

        //Get the contents of the current directory
        thisDirContents = startDir.list();

        if (thisDirContents != null) {
            //Now loop through , apply filter , or adding them to list of sub dirs
            for (String thisDirContent : thisDirContents) {

                //Get Handle to (full) file (inc path)
                tmpFullFile = FileUtils.combineFileAndDirectory(thisDirContent,
                        startingDirectory);

                tmpFile = new File(tmpFullFile);

                //Add to Output if file
                if (tmpFile.isFile()) {
                    //Add if file 
                    if ((endFileName == null) ||
                            (tmpFullFile.endsWith(endFileName))) {
                        filesFound.add(tmpFullFile);
                    }
                }
            }
        }

        return filesFound;

    }

    public static HashSet<String> getDirs(String startingDirectory, String[] excludeBranchMatches) throws java.io.IOException {

        for (String excludeBranchMatch : excludeBranchMatches) {
            //if the current path matches any excluded branches - do not dig deeper
            if(startingDirectory.toLowerCase().endsWith(excludeBranchMatch)){
                return new HashSet<String>();
            }
        }


//        final String excludeMatch = "src" + File.separator + "test";

        //Local Variables
        String tmpFullFile;
        String tmpSubDir;
        File startDir = new File(startingDirectory);
        File tmpFile;
        String[] thisDirContents;
        HashSet<String> dirsFound = new HashSet<String>();
        HashSet<String> subDirFilesFound;

        //Check that this is a valid directory
        if (!startDir.isDirectory()) {
            throw new java.io.IOException(startingDirectory + " was not a valid directory");
        }

        //Add the current directory to the output list
        dirsFound.add(startingDirectory);

        //Get the contents of the current directory
        thisDirContents = startDir.list();

        if (thisDirContents != null) {
            //Now loop through , apply filter , or adding them to list of sub dirs
            for (String thisDirContent : thisDirContents) {

                //Get Handle to (full) file (inc path)
                tmpFullFile = FileUtils.combineFileAndDirectory(thisDirContent, startingDirectory);

                tmpFile = new File(tmpFullFile);

                //We're only interested in directories
                if (tmpFile.isDirectory()) {

                    //Add this to the directory list
                    dirsFound.add(tmpFullFile);

                    //Now Do Recursive Call (to this method)if Directory
                    tmpSubDir = FileUtils.combineFileAndDirectory(thisDirContent, startingDirectory);
                    subDirFilesFound = FileStructureAnalysis.getDirs(tmpSubDir, excludeBranchMatches);
                    dirsFound.addAll(subDirFilesFound);
                }
            }
        }
        return dirsFound;
    }
}


