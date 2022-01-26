package com.sp.bookshare;

public class Userdata {
    public String itemName, category,moduleCode,description,imageURL;

    public Userdata (){

    };

    public Userdata(String itemName,String category,String moduleCode,String description,String imageURL){
        this.itemName=itemName;
        this.category=category;
        this.moduleCode=moduleCode;
        this.description=description;
        this.imageURL=imageURL;
    }
}

