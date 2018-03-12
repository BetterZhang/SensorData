package com.jsdttec.sensordata.domain;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/03/09 下午 1:48
 * Desc   : description
 */

public class RequestParamsVo {


    /**
     * DeviceID : 123456
     * DeviceType : BATTERY
     * Version : 1.0
     * Date : 2018-03-08
     * PV : 1.0
     * TV : 48V
     * LON : 118.859808
     * LAT : 31.953038
     * DeviceData : {"RSOC":"80","Quanity":"2Kwh","PS":"0"}
     */

    private String DeviceID;
    private String DeviceType;
    private String Version;
    private String Date;
    private String PV;
    private String TV;
    private double LON;
    private double LAT;
    private DeviceDataBean DeviceData;

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String DeviceID) {
        this.DeviceID = DeviceID;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String DeviceType) {
        this.DeviceType = DeviceType;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getPV() {
        return PV;
    }

    public void setPV(String PV) {
        this.PV = PV;
    }

    public String getTV() {
        return TV;
    }

    public void setTV(String TV) {
        this.TV = TV;
    }

    public double getLON() {
        return LON;
    }

    public void setLON(double LON) {
        this.LON = LON;
    }

    public double getLAT() {
        return LAT;
    }

    public void setLAT(double LAT) {
        this.LAT = LAT;
    }

    public DeviceDataBean getDeviceData() {
        return DeviceData;
    }

    public void setDeviceData(DeviceDataBean DeviceData) {
        this.DeviceData = DeviceData;
    }

    public static class DeviceDataBean {
        /**
         * RSOC : 80
         * Quanity : 2Kwh
         * PS : 0
         */

        private String RSOC;
        private String Quanity;
        private String PS;

        public String getRSOC() {
            return RSOC;
        }

        public void setRSOC(String RSOC) {
            this.RSOC = RSOC;
        }

        public String getQuanity() {
            return Quanity;
        }

        public void setQuanity(String Quanity) {
            this.Quanity = Quanity;
        }

        public String getPS() {
            return PS;
        }

        public void setPS(String PS) {
            this.PS = PS;
        }
    }
}
