package ge.ee.eewh.SugaModels;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;


/**
 * Created by beqa on 01.10.2017.
 */

public class HeaderResult extends SugarRecord<HeaderResult>{
    public String No_;
    public String Buy_from_Vendor_Name ;
    public int Posting_Date;
    public int Order_Date ;
    public String Location_Code ;
    public String User_ID;
    public int Status;
    @Ignore
    public String FilterField;

    public String getFilterField() {
        return FilterField;
    }

    public void setFilterField(String filterField) {
        FilterField = filterField;
    }

    public String getNo_() {

        return No_;
    }

    public void setNo_(String no_) {
        No_ = no_;
    }

    public String getBuy_from_Vendor_Name() {
        return Buy_from_Vendor_Name;
    }

    public void setBuy_from_Vendor_Name(String buy_from_Vendor_Name) {
        Buy_from_Vendor_Name = buy_from_Vendor_Name;
    }

    public int getPosting_Date() {
        return Posting_Date;
    }

    public void setPosting_Date(int posting_Date) {
        Posting_Date = posting_Date;
    }

    public int getOrder_Date() {
        return Order_Date;
    }

    public void setOrder_Date(int order_Date) {
        Order_Date = order_Date;
    }

    public String getLocation_Code() {
        return Location_Code;
    }

    public void setLocation_Code(String location_Code) {
        Location_Code = location_Code;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
