package com.shopping.jewellery.dto;

public class UserDTO {

    private int userId;
    private String userName;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String address;
    private long mobileNumber;
    private long panCardNo;

    public UserDTO() {
    }

    public UserDTO(int userId, String userName, String password, String role, String firstName, String lastName, String address, long mobileNumber, long panCardNo) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.panCardNo = panCardNo;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public long getPanCardNo() {
        return panCardNo;
    }

    public void setPanCardNo(long panCardNo) {
        this.panCardNo = panCardNo;
    }
}