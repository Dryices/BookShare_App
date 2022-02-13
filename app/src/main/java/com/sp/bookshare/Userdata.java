package com.sp.bookshare;

import android.util.Log;

public class Userdata {
    public String itemName,price, category, moduleCode, description, imageURL,userID,seller;

    public Userdata() {
        //Required
    }

    public Userdata(String itemName, String itemPrice, String category, String moduleCode, String description, String imageURL,String userID,String seller) {
        this.itemName = itemName;
        this.price=itemPrice;
        this.category = category;
        this.moduleCode = moduleCode;
        this.description = description;
        this.imageURL = imageURL;
        this.userID=userID;
        this.seller=seller;

    }
}

