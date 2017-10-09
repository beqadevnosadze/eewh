package ge.ee.eewh.SugaModels;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by beka-work on 02.10.2017.
 */

public class LinesResult extends SugarRecord<LinesResult>{
    public int Line_No_;
    public String Document_No_;
    public String No_;
    public String Full_Description;
    public String Description;
    public String Manufacturer_Code;
    public String Location_Code;
    public float Quantity;

    public int ScannedQuantity;

    public int getScannedQuantity() {
        return ScannedQuantity;
    }

    public void setScannedQuantity(int scannedQuantity) {
        ScannedQuantity = scannedQuantity;
    }

    @Ignore
    public String FilterField;

    public int getLine_No_() {
        return Line_No_;
    }

    public void setLine_No_(int line_No_) {
        Line_No_ = line_No_;
    }

    public String getDocument_No_() {
        return Document_No_;
    }

    public void setDocument_No_(String document_No_) {
        Document_No_ = document_No_;
    }

    public String getNo_() {
        return No_;
    }

    public void setNo_(String no_) {
        No_ = no_;
    }

    public String getFull_Description() {
        return Full_Description;
    }

    public void setFull_Description(String full_Description) {
        Full_Description = full_Description;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getManufacturer_Code() {
        return Manufacturer_Code;
    }

    public void setManufacturer_Code(String manufacturer_Code) {
        Manufacturer_Code = manufacturer_Code;
    }

    public String getLocation_Code() {
        return Location_Code;
    }

    public void setLocation_Code(String location_Code) {
        Location_Code = location_Code;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public String getFilterField() {
        return FilterField;
    }

    public void setFilterField(String filterField) {
        FilterField = filterField;
    }
}
