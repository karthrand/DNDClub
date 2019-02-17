package com.oude.dndclub.bean;

public class updateInfo {
    private String versionName;
    private Integer versionCode;
    private String info;
    private String url;

    public updateInfo() {

    }

    public updateInfo(String version, Integer code, String describe, String url, String pwd) {
        this.versionName = version;
        this.versionCode = code;
        this.info = describe;
        this.url = url;
    }

    public String getVersion() {
        return versionName;
    }

    public void setVersion(String version) {
        this.versionName = version;
    }

    public Integer getCode() {
        return versionCode;
    }

    public void setCode(Integer code) {
        this.versionCode = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
