package com.myapp.pizzahut.model;

import java.io.Serializable;

public class CartItem implements Serializable {

    private String cartId;
    private String productName;
    private String productPrice;
    private int quantity;
    private String userEmail;
    private boolean selected;
    private String imagePath;

    public CartItem() {

    }

    public CartItem(String productName, String productPrice, int quantity, String userEmail, boolean selected,String imagePath) {

        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.userEmail = userEmail;
       this.selected = selected;
       this.imagePath = imagePath;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() { return productPrice; }
    public void setProductPrice(String productPrice) { this.productPrice = productPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
