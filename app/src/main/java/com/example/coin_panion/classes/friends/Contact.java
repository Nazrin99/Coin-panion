package com.example.coin_panion.classes.friends;

public class Contact {

    private String contactName;
    private String contactNumber;
    private Boolean isSelected;
    private Boolean hasAccount;

    public Contact() {

    }

    public Contact(String contactName, String contactNumber) {
        this.contactName = contactName;

        if(contactNumber.matches("^\\d+$")){
            this.contactNumber = contactNumber.replaceAll("[^\\d]", "");
        }else {
            this.contactNumber = contactNumber;
        }

    }

    public Contact(String contactName, String contactNumber, Boolean isSelected) {
        this.contactName = contactName;

        if(contactNumber.matches("^\\d+$")){
            this.contactNumber = contactNumber.replaceAll("[^\\d]", "");
        }else {
            this.contactNumber = contactNumber;
        }

        this.isSelected = isSelected;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
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
