package com.wezen.madisonpartner.employees;

import org.parceler.Parcel;

/**
 * Created by eder on 10/11/2015.
 */
@Parcel(Parcel.Serialization.BEAN)
public class Employee {

    private String name;
    private String id;
    private String avatarUrl;

    public  Employee(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
