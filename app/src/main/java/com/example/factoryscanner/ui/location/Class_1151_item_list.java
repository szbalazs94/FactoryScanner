package com.example.factoryscanner.ui.location;

public class Class_1151_item_list {

    public String part_no;
    public String part_description;
    public String allocated_quantity;
    public String batch_number;
    public String location;
    public String batch_unique;
    public String quantity_in_location;
    public String stock_UOM;
    public String supplier_name;
    public String date_received;
    public String barCodeString;



    public Class_1151_item_list(String batch_unique,
                                String location,
                                String batch_number,
                                String part_no,
                                String part_description,
                                String quantity_in_location,
                                String stock_UOM,
                                String allocated_quantity,
                                String supplier_name,
                                String date_received,
                                String barCodeString) {
        this.batch_unique = batch_unique;
        this.part_no = part_no;
        this.part_description = part_description;
        this.allocated_quantity = allocated_quantity;
        this.batch_number = batch_number;
        this.quantity_in_location = quantity_in_location;
        this.stock_UOM = stock_UOM;
        this.location = location;
        this.supplier_name = supplier_name;
        this.date_received = date_received;
        this.barCodeString = barCodeString;

    }
    public String getBatch_unique(){
        return batch_unique;
    }
    public String getPart_no(){
        return part_no;
    }
    public String getPart_description() {
        return part_description;
    }
    public String getAllocated_quantity() {
        return allocated_quantity;
    }
    public String getBatch_number () {
        return batch_number;
    }
    public String getLocation() {
        return location;
    }
    public String getQuantity_in_location() {
        return quantity_in_location;
    }
    public String getStock_UOM() {
        return stock_UOM;
    }
    public String getSupplier_name() {
        return supplier_name;
    }
    public String getDate_received() {
        return date_received;
    }

    public String getBarCodeString() {
        return barCodeString;
    }
}
