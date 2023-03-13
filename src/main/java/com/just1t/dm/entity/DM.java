package com.just1t.dm.entity;

/**
 * @author just1t
 * @date 2023/3/3 23:21
 * @introduce
 */
public class DM {
    private String name;
    private String url;

    public DM(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DM{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
