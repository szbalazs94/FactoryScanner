package com.example.factoryscanner.ui.issuing;

public class Class_11312_item_list {
    public String Component_Unique;
    public String Batch_Number;
    public String Location;
    public String Supplier;
    public String Date_Recd;
    public String Quantity_in_Location;
    public String Allocated_Qty;
    public String Stock_Measure;
    public Float Issue_Quantity;
    public String Material_Type;
    public String Batch_Unique;


    public Class_11312_item_list(String Component_Unique,
                                 String Batch_Number,
                                 String Location,
                                 String Supplier,
                                 String Date_Recd,
                                 String Quantity_in_Location,
                                 String Allocated_Qty,
                                 String Stock_Measure,
                                 Float Issue_Quantity,
                                 String Material_Type,
                                 String Batch_Unique) {
        this.Component_Unique = Component_Unique;
        this.Batch_Number = Batch_Number;
        this.Location = Location;
        this.Supplier = Supplier;
        this.Date_Recd = Date_Recd;
        this.Quantity_in_Location = Quantity_in_Location;
        this.Allocated_Qty = Allocated_Qty;
        this.Stock_Measure = Stock_Measure;
        this.Issue_Quantity = Issue_Quantity;
        this.Material_Type = Material_Type;
        this.Batch_Unique = Batch_Unique;

    }
    public String getComponent_Unique() {
        return Component_Unique;
    }
    public String getBatch_Number() {
        return Batch_Number;
    }
    public String getLocation() {
        return Location;
    }
    public String getSupplier() {
        return Supplier;
    }
    public String getDate_Recd() {
        return Date_Recd;
    }
    public String getQuantity_in_Location() {
        return Quantity_in_Location;
    }
    public String getAllocated_Qty() {
        return Allocated_Qty;
    }
    public String getStock_Measure() {
        return Stock_Measure;
    }
    public Float getIssue_Quantity() {
        return Issue_Quantity;
    }
    public String getMaterial_Type() {
        return Material_Type;
    }
    public String getBatch_Unique() {
        return Batch_Unique;
    }
}
