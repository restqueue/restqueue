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
 * Time: 7:26:36 PM
 */
public class StockQuery {
    private String stockCode;
    private int stockAvailable;
    private long stockQueryUid;

    public StockQuery(String stockCode, int stockAvailable, long stockQueryUid) {
        this.stockCode = stockCode;
        this.stockAvailable = stockAvailable;
        this.stockQueryUid = stockQueryUid;
    }

    public String getStockCode() {
        return stockCode;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public long getStockQueryUid() {
        return stockQueryUid;
    }

    @Override
    public String toString() {
        return "StockQuery{" +
                "stockCode='" + stockCode + '\'' +
                ", stockAvailable=" + stockAvailable +
                ", stockQueryUid=" + stockQueryUid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockQuery that = (StockQuery) o;

        if (stockQueryUid != that.stockQueryUid) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (stockQueryUid ^ (stockQueryUid >>> 32));
    }
}
