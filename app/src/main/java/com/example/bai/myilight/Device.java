package com.example.bai.myilight;

public class Device {
    private int id;
    private int product_id;
    private String name;
    private String description;
    private int img_id;

    public Device(int id, int product_id, String name, String description, int img_id) {
        this.id = id;
        this.product_id = product_id;
        this.name = name;
        this.description = description;
        this.img_id = img_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public  int getImg_id(){
        return img_id;
    }

    public int getProduct_id() {
        return product_id;
    }
}
