package com.example.factoryscanner.ui.picklist;

public class Class_1121_item_list {
    public String Batch_Unique,
            Location,
            Batch_Number,
            Date_Received,
            Supplier_Name,
            Quantity_In_Location,
            Stock_UOM,
            Quantity_To_Pick,
            Allocated_Quantity;


    public Class_1121_item_list() {
    }

    public Class_1121_item_list(String Batch_Unique, String Location, String Batch_Number, String Date_Received, String Supplier_Name, String Quantity_In_Location, String Stock_UOM, String Quantity_To_Pick, String Allocated_Quantity) {
        this.Batch_Unique = Batch_Unique;
        this.Location = Location;
        this.Batch_Number = Batch_Number;
        this.Date_Received = Date_Received;
        this.Supplier_Name = Supplier_Name;
        this.Quantity_In_Location = Quantity_In_Location;
        this.Stock_UOM = Stock_UOM;
        this.Quantity_To_Pick = Quantity_To_Pick;
        this.Allocated_Quantity = Allocated_Quantity;

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
    public String getDate_Received() {
        return Date_Received;
    }
    public String getSupplier_Name() {
        return Supplier_Name;
    }
    public String getQuantity_In_Location() {
        return Quantity_In_Location;
    }
    public String getStock_UOM() {
        return Stock_UOM;
    }
    public String getQuantity_To_Pick() {
        return Quantity_To_Pick;
    }
    public String getAllocated_Quantity() {
        return Allocated_Quantity;
    }

}
