package com.sp.bookshare;

public class Userdata {
    public String name, category,moduleCode,description,imageURL;

    public Userdata (){

    };

    public Userdata(String name,String category,String moduleCode,String description,String imageURL){
        this.name=name;
        this.category=category;
        this.moduleCode=moduleCode;
        this.description=description;
        this.imageURL=imageURL;
    }
}

