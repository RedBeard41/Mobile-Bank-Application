package com.bank.izbank.UserInfo;

import com.google.gson.annotations.Expose;

public class Address {
    @Expose private String street;
    @Expose private String neighborhood;
    @Expose private int apartmentNumber;
    @Expose private int floor;
    @Expose private int homeNumber;
    @Expose private String province;
    @Expose private String city;
    @Expose private String country;

    public Address(String street, String neighborhood, int apartmentNumber, int floor, int homeNumber, String province, String city, String country) {
        this.street = street;
        this.neighborhood = neighborhood;
        this.apartmentNumber = apartmentNumber;
        this.floor = floor;
        this.homeNumber = homeNumber;
        this.province = province;
        this.city = city;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public int getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(int apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getHomeNumber() {
        return homeNumber;
    }

    public void setHomeNumber(int homeNumber) {
        this.homeNumber = homeNumber;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
