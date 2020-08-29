package com.georgeampartzidis.greenfridge.data;

public class ScannedProduct {
    String productBrand;
    String productName;

    public ScannedProduct(String brand, String name){
        productBrand =  brand;
        productName = name;
    }

    public String getProductBrand(){
        return productBrand;
    }

    public String getProductName(){
        return productName;
    }
}
