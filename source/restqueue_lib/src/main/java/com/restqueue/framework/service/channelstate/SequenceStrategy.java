package com.restqueue.framework.service.channelstate;

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
 * Date: Mar 4, 2011
 * Time: 6:54:35 PM
 */
public enum SequenceStrategy {
    /**
     * SINGLE will provide the messages one at a time as long as they match the channel's nextMessageSequence value and
     * they are available on the channel.
     *
     * GROUPED will provide as many messages that are available on the channel as possible in a sequence starting from
     * the channel's nextMessageSequence value.
     */
    SINGLE, GROUPED
}
