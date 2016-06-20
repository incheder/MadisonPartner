package com.wezen.madisonpartner.request;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by eder on 15/09/2015.
 */
public class HomeServiceRequest {

    private String id;
    private String userAvatar;
    private String name;
    private String description;
    private int review;
    private String date;
    private HomeServiceRequestStatus status;
    private LatLng userLocation;
    private String homeServiceID;
    private String address;
    private String userID;
    private String providerName;
    private String phone;
    private Date dateForService;


    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HomeServiceRequestStatus getStatus() {
        return status;
    }

    public void setStatus(HomeServiceRequestStatus status) {
        this.status = status;
    }

    public LatLng getLocation() {
        return userLocation;
    }

    public void setLocation(LatLng location) {
        this.userLocation = location;
    }

    public String getHomeServiceID() {
        return homeServiceID;
    }

    public void setHomeServiceID(String homeServiceID) {
        this.homeServiceID = homeServiceID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDateForService() {
        return dateForService;
    }

    public void setDateForService(Date dateForService) {
        this.dateForService = dateForService;
    }
}
