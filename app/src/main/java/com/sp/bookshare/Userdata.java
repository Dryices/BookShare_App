package com.sp.bookshare;

public class Userdata {
    public String itemName,price, category, moduleCode, description, imageURL;

    public Userdata() {
        //Required
    }

    public Userdata(String itemName, String itemPrice, String category, String moduleCode, String description, String imageURL) {
        this.itemName = itemName;
        this.price=itemPrice;
        this.category = category;
        this.moduleCode = moduleCode;
        this.description = description;
        this.imageURL = imageURL;
    }
}

