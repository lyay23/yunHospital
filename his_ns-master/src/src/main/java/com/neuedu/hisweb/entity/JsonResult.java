package com.neuedu.hisweb.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * *
 * @param <T>
 */
public class JsonResult<T> {
    //true 则本次请求成功，返回的数据在data中 false本次请求失败，错误信息记录在errMsg中
    private Boolean result;
    private String errMsg;
    private T data;
    private String token;

    public JsonResult() {
    }


   public JsonResult(T data) {
        this.result = true;
        this.data = data;
    }

    public JsonResult(Boolean rs) {
        this.result = rs;

    }
    public JsonResult(T data,String token){
        this.result=true;
        this.data=data;
        this.token=token;
    }

    public JsonResult(String errMsg) {
        this.result = false;
        this.errMsg = errMsg;
    }


    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
