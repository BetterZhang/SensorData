package com.jsdttec.sensordata.domain;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/03/09 上午 10:38
 * Desc   : description
 */

public class ResponseBody {

    private String errorcode;
    private String errormsg;

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    @Override
    public String toString() {
        return "{" +
                "errorcode='" + errorcode + '\'' +
                ", errormsg='" + errormsg + '\'' +
                '}';
    }
}
