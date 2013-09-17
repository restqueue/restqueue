package com.restqueue.framework.client.results;

import com.restqueue.framework.client.exception.HttpResponseErrorBean;

/**
 * These are the common methods for all of the Result objects to implement.<BR/><BR/>
 *
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
 * Date: Jan 15, 2011
 * Time: 5:33:46 PM
 */
public interface Result {
    public HttpResponseErrorBean getException();

    public void setResponseCode(int responseCode);

    public int getResponseCode();

    public boolean isSuccess();
}
