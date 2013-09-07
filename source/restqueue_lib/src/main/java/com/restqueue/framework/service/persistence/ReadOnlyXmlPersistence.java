package com.restqueue.framework.service.persistence;

import org.apache.log4j.Logger;

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
 * Date: 13/04/2013
 * Time: 19:25
 */
public class ReadOnlyXmlPersistence extends AbstractFilePersistence{
    private static final Logger log = Logger.getLogger(ReadOnlyXmlPersistence.class);

    private static final String XML_FILENAME_EXTENSION = ".xml";
    private static final String APPLICATION_XML = "application/xml";

    @Override
    protected String getFilenameExtension() {
        return XML_FILENAME_EXTENSION;
    }

    @Override
    protected String getFilenameExtensionCode() {
        return APPLICATION_XML;
    }
}
