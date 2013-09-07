package com.restqueue.framework.service.persistence;

/**
    * Copyright 2010-2013 Nik Tomkinson

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 * Date: Jan 28, 2012
 * Time: 7:36:24 PM
 */
public class XmlFilePersistence extends AbstractFilePersistence{
    private static final String XML_FILENAME_EXTENSION = ".xml";
    private static final String APPLICATION_XML = "application/xml";

    @Override
    protected final String getFilenameExtension() {
        return XML_FILENAME_EXTENSION;
    }

    @Override
    protected final String getFilenameExtensionCode() {
        return APPLICATION_XML;
    }
}
