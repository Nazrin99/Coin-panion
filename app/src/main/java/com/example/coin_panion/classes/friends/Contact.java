package com.example.coin_panion.classes.friends;

public class Contact {

    private String contactName;
    private Integer contactNumber;
    private Boolean isSelected;
    private Boolean hasAccount;

    public Contact() {

    }

    public Contact(String contactName, Integer contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public Contact(String contactName, Integer contactNumber, Boolean isSelected) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.isSelected = isSelected;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Integer getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Integer contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    /*TODO based on contact number check if the contact number exist in database*/
    public Boolean getHasAccount() {
        return hasAccount;
    }
}
