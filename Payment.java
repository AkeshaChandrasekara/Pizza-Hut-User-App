package com.myapp.pizzahut;

public class Payment {

    private String merchantId;
    private String returnUrl;
    private String cancelUrl;
    private String orderId;
    private double amount;
    private String currency;
    private String customerEmail;

    public Payment() {
    }

    public Payment(String merchantId,String returnUrl,String cancelUrl,String orderId,double amount,String currency,String customerEmail) {
        this.merchantId = merchantId;
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
        this.orderId =orderId;
        this. amount =  amount;
        this.currency = currency;
        this. customerEmail =  customerEmail;

    }



    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }


}
