package com.example.bai.myilight;

public class Product {
    private int id;
    private String name;
    private int img_id;
    private int ptype;
    private int product_id;

    public Product(int id, String name, int img_id, int ptype, int product_id) {
        this.id = id;
        this.name = name;
        this.img_id = img_id;
        this.ptype = ptype;
        this.product_id = product_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }

    public int getPtype(){
        return ptype;
    }

    public int getProduct_id() {
        return product_id;
    }
}
