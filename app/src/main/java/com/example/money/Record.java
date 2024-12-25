package com.example.money;

/**
 * 记录类，用于存储数据。
 */
public class Record {
    private int id;
    private String supplierName, productName, unit, signerName, time, remarks;
    private double quantity, unitPrice, otherFees, totalAmount;

    public Record(int id, String supplierName, String productName, String unit, double quantity, double unitPrice, double otherFees, double totalAmount, String signerName, String time, String remarks) {
        this.id = id;
        this.supplierName = supplierName;
        this.productName = productName;
        this.unit = unit;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.otherFees = otherFees;
        this.totalAmount = totalAmount;
        this.signerName = signerName;
        this.time = time;
        this.remarks = remarks;
    }

    public int getId() { return id; }
    public String getSupplierName() { return supplierName; }
    public String getProductName() { return productName; }
    public String getUnit() { return unit; }
    public double getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getOtherFees() { return otherFees; }
    public double getTotalAmount() { return totalAmount; }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setOtherFees(double otherFees) {
        this.otherFees = otherFees;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSignerName() { return signerName; }
    public String getTime() { return time; }
    public String getRemarks() { return remarks; }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", supplierName='" + supplierName + '\'' +
                ", productName='" + productName + '\'' +
                ", unit='" + unit + '\'' +
                ", signerName='" + signerName + '\'' +
                ", time='" + time + '\'' +
                ", remarks='" + remarks + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", otherFees=" + otherFees +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
