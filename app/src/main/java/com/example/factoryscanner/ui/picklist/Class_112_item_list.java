package com.example.factoryscanner.ui.picklist;

public class Class_112_item_list {
        public String Del_Plan_No,
                Sales_Order_Number,
                Sales_Order_Item_No ,
                Customer_Account_Code,
                Part_Number,
                Stock_Description,
                Issue_Number,
                Qty_Required,
                Qty_Allocated,
                Ship_Req_Date,
                Location;


    public Class_112_item_list(String Del_Plan_No,
                               String Sales_Order_Number,
                               String Sales_Order_Item_No,
                               String Customer_Account_Code,
                               String Part_Number,
                               String Stock_Description,
                               String Issue_Number,
                               String Qty_Required,
                               String Qty_Allocated,
                               String Ship_Req_Date,
                               String Location) {
        this.Del_Plan_No = Del_Plan_No;
        this.Sales_Order_Number = Sales_Order_Number;
        this.Sales_Order_Item_No = Sales_Order_Item_No;
        this.Customer_Account_Code = Customer_Account_Code;
        this.Part_Number = Part_Number;
        this.Stock_Description = Stock_Description;
        this.Issue_Number = Issue_Number;
        this.Qty_Required = Qty_Required;
        this.Qty_Allocated = Qty_Allocated;
        this.Ship_Req_Date = Ship_Req_Date;
        this.Location = Location;

    }
    public String getPart_Number() {
        return Part_Number;
    }
    public String getStock_Description() {
        return Stock_Description;
    }
    public String getDel_Plan_No() {
        return Del_Plan_No;
    }
    public String getSales_Order_Number() {
        return Sales_Order_Number;
    }
    public String getSales_Order_Item_No() {
        return Sales_Order_Item_No;
    }
    public String getCustomer_Account_Code() {
        return Customer_Account_Code;
    }
    public String getIssue_Number() {
        return Issue_Number;
    }
    public String getQty_Required() {
        return Qty_Required;
    }
    public String getQty_Allocated() {
        return Qty_Allocated;
    }
    public String getShip_Req_Date() {
        return Ship_Req_Date;
    }
    public String getLocation() {return Location;}
}
