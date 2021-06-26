package com.arish.propertylookup;

import java.util.ArrayList;

public class PropertyPostModelClass {

    private String postID; // each property post has a unique ID
    private String propertyType; // like House/Villa, Apartment, Land, etc ( Defined in values -> strings -> property_type_array
    private String buyOrSell; // tells if post is about buy property or sell property
    private String rent; // true if property is for rent else false
    private ArrayList<String> images; // list of property images
    // propery address fields
    private String hnoStreetName; // optional
    private String locality;
    private String city;
    private String cityLowercase; // this field is used for searching
    private Double price; // price of property
    private Double age;
    private String otherDetails; // other details of property
    // details of users who posts the property
    private String userID;
    private String userName;
    private String userEmail;
    private String userMobile;
    private String userAddress;

    // constructor
    public PropertyPostModelClass(String postID, String propertyType, String buyOrSell, String rent, ArrayList<String> images, String hnoStreetName, String locality, String city, String cityLowercase, Double price, Double age, String otherDetails, String userID, String userName, String userEmail, String userMobile, String userAddress) {
        this.postID = postID;
        this.propertyType = propertyType;
        this.buyOrSell = buyOrSell;
        this.rent = rent;
        this.images = images;
        this.hnoStreetName = hnoStreetName;
        this.locality = locality;
        this.city = city;
        this.cityLowercase = cityLowercase;
        this.price = price;
        this.age = age;
        this.otherDetails = otherDetails;
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userMobile = userMobile;
        this.userAddress = userAddress;
    }

    // getters and setters
    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getBuyOrSell() {
        return buyOrSell;
    }

    public void setBuyOrSell(String buyOrSell) {
        this.buyOrSell = buyOrSell;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getHnoStreetName() {
        return hnoStreetName;
    }

    public void setHnoStreetName(String hnoStreetName) {
        this.hnoStreetName = hnoStreetName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityLowercase() {
        return cityLowercase;
    }

    public void setCityLowercase(String cityLowercase) {
        this.cityLowercase = cityLowercase;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public String getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}
