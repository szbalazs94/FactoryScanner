package com.example.factoryscanner.ui.purchase;

public class Class_111_item_list {
    public String Purchase_Order_No, Supplier_Code, Supplier_Name, Date, Lines;

    public Class_111_item_list(String Purchase_Order_No, String Supplier_Code, String Supplier_Name, String Date, String Lines) {
        this.Purchase_Order_No = Purchase_Order_No;
        this.Supplier_Code = Supplier_Code;
        this.Supplier_Name = Supplier_Name;
        this.Lines = Lines;
        this.Date = Date;
    }
    public String getPurchase_Order_No() {
        return Purchase_Order_No;
    }
    public String getSupplier_Code() {
        return Supplier_Code;
    }
    public String getSupplier_Name() {
        return Supplier_Name;
    }
    public String getDate() {
        return Date;
    }
    public String getLines() {
        return Lines;
    }

}

