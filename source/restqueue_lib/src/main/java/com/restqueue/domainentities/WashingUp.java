package com.restqueue.domainentities;

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
 * Date: Nov 25, 2010
 * Time: 7:26:01 PM
 */
public class WashingUp {
    private String itemDescription;
    private int itemOrder;

    public WashingUp(String itemDescription, int itemOrder) {
        this.itemDescription = itemDescription;
        this.itemOrder = itemOrder;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    @Override
    public String toString() {
        return "WashingUp{" +
                "itemDescription='" + itemDescription + '\'' +
                ", itemOrder=" + itemOrder +
                '}';
    }
}
