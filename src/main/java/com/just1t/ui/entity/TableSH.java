package com.just1t.ui.entity;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author just1t
 * @date 2023/3/4 16:54
 * @introduce
 */
public class TableSH {
    public final SimpleBooleanProperty flog;
    public final SimpleStringProperty name;
    public final String url;

    public TableSH(SimpleBooleanProperty flog, SimpleStringProperty name, String url) {
        this.flog = flog;
        this.name = name;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public boolean isFlog() {
        return flog.get();
    }

    public SimpleBooleanProperty flogProperty() {
        return flog;
    }

    public void setFlog(boolean flog) {
        this.flog.set(flog);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public String toString() {
        return "TableSH{" +
                "flog=" + flog +
                ", name=" + name +
                ", url='" + url + '\'' +
                '}';
    }
}
