package com.example.factoryscanner.ui.inventory;

public class Class_114_item_list {
    public String Batch_Unique,
            Location,
            Batch_Number,
            Part_Number,
            Part_Description,
            Quantity_In_Location,
            Stock_UOM,
            Allocated_Quantity,
            Date_Received,
            Supplier_Name;


    public Class_114_item_list(
            String Batch_Unique,
            String Location,
            String Batch_Number,
            String Part_Number,
            String Part_Description,
            String Quantity_In_Location,
            String Stock_UOM,
            String Allocated_Quantity,
            String Date_Received,
            String Supplier_Name) {
        this.Batch_Unique = Batch_Unique;
        this.Location = Location;
        this.Batch_Number = Batch_Number;
        this.Part_Number = Part_Number;
        this.Part_Description = Part_Description;
        this.Quantity_In_Location = Quantity_In_Location;
        this.Stock_UOM = Stock_UOM;
        this.Allocated_Quantity = Allocated_Quantity;
        this.Date_Received = Date_Received;
        this.Supplier_Name = Supplier_Name;


    }

    public Class_114_item_list() {

    }

    public String getBatch_Unique() {
        return Batch_Unique;
    }
    public String getLocation() {
        return Location;
    }
    public String getBatch_Number() {
        return Batch_Number;
    }
    public String getPart_Number() {
        return Part_Number;
    }
    public String getPart_Description() {
        return Part_Description;
    }
    public String getQuantity_In_Location() {
        return Quantity_In_Location;
    }
    public String getStock_UOM() {
        return Stock_UOM;
    }
    public String getAllocated_Quantity() {
        return Allocated_Quantity;
    }
    public String getDate_Received() {
        return Date_Received;
    }
    public String getSupplier_Name() {
        return Supplier_Name;
    }
}
