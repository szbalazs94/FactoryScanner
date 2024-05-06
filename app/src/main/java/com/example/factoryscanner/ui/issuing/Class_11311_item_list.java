package com.example.factoryscanner.ui.issuing;

public class Class_11311_item_list {
    public String LOP_Code;
    public String Component_Unique;
    public String Material_Type;
    public String Part_Number;
    public String Material_Form;
    public String Description;
    public String Quantity_Reqd;
    public String Issued_Qty;
    public Float Still_Quantity_Reqd_UOM;
    public String Quantity_In_Location;
    public String QC;

    public Class_11311_item_list(String LOP_Code,
                                 String Component_Unique,
                                 String Material_Type,
                                 String Part_Number,
                                 String Material_Form,
                                 String Description,
                                 String Quantity_Reqd,
                                 String Issued_Qty,
                                 Float Still_Quantity_Reqd_UOM,
                                 String Quantity_In_Location,
                                 String QC) {
        this.LOP_Code = LOP_Code;
        this.Component_Unique = Component_Unique;
        this.Material_Type = Material_Type;
        this.Part_Number = Part_Number;
        this.Material_Form = Material_Form;
        this.Description = Description;
        this.Quantity_Reqd = Quantity_Reqd;
        this.Issued_Qty = Issued_Qty;
        this.Still_Quantity_Reqd_UOM = Still_Quantity_Reqd_UOM;
        this.Quantity_In_Location = Quantity_In_Location;
        this.QC = QC;

    }

    public String getLOP_Code() {
        return LOP_Code;
    }

    public String getComponent_Unique() {
        return Component_Unique;
    }

    public String getMaterial_Type() {
        return Material_Type;
    }

    public String getPart_Number() {
        return Part_Number;
    }

    public String getMaterial_Form() {
        return Material_Form;
    }

    public String getDescription() {
        return Description;
    }

    public String getQuantity_Reqd() {
        return Quantity_Reqd;
    }

    public String getIssued_Qty() {
        return Issued_Qty;
    }

    public Float getStill_Quantity_Reqd_UOM() {
        return Still_Quantity_Reqd_UOM;
    }

    public String getQuantity_In_Location() {
        return Quantity_In_Location;
    }

    public String getQC() {
        return QC;
    }
}
