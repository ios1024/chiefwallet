package com.spark.otcclient.pojo;

import java.util.List;

public class CurrencyTypeBean {


    /**
     * code : 200
     * message : SUCCESS
     * data : ["CNY","USD","EUR"]
     * count : null
     * responseString : 200~SUCCESS
     * url : null
     * cid : null
     */

    private int code;
    private String message;
    private Object count;
    private String responseString;
    private Object url;
    private Object cid;
    private List<String> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getCount() {
        return count;
    }

    public void setCount(Object count) {
        this.count = count;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public Object getCid() {
        return cid;
    }

    public void setCid(Object cid) {
        this.cid = cid;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
