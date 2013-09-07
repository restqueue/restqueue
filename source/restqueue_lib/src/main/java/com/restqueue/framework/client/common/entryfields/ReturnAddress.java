package com.restqueue.framework.client.common.entryfields;


import com.restqueue.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.List;

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
 * Date: Jan 8, 2011
 * Time: 7:54:44 PM
 */
public class ReturnAddress {
    private ReturnAddressType type;
    private String address;

    private ReturnAddress() {
    }

    public ReturnAddress(ReturnAddressType type, String address) {
        this.type = type;
        this.address = address;
    }

    public ReturnAddressType getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnAddress that = (ReturnAddress) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return type + ":" + address;
    }

    public String format() {
        return type + ":" + address;
    }

    public static ReturnAddress parse(String returnAddressString){
        final ReturnAddress returnAddress = new ReturnAddress();

        if(!returnAddressString.contains(":")){
            throw new IllegalArgumentException(invalidReturnAddressMessage(returnAddressString));
        }

        try {
            returnAddress.type=ReturnAddressType.valueOf(returnAddressString.substring(0,returnAddressString.indexOf(":")));
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(invalidReturnAddressMessage(returnAddressString));
        }
        returnAddress.address=returnAddressString.substring(returnAddressString.indexOf(":")+1);

        return returnAddress;
    }

    public static ReturnAddress[] parse(List<String> returnAddressesList){
        if(returnAddressesList==null){
            return new ReturnAddress[0];
        }

        final List<ReturnAddress> returnAddresses = new ArrayList<ReturnAddress>();

        for(String returnAddressString:returnAddressesList){
            returnAddresses.add(ReturnAddress.parse(returnAddressString));
        }
        
        return returnAddresses.toArray(new ReturnAddress[returnAddresses.size()]);
    }

    private static String invalidReturnAddressMessage(final String returnAddress){
        return "Request has a malformed return address header:"+returnAddress+" it should be in the form TYPE:ADDRESS where TYPE is one of:" +
                EnumUtils.stringArrayToGrammaticallyCorrectCommaList(ReturnAddressType.values());
    }
}
