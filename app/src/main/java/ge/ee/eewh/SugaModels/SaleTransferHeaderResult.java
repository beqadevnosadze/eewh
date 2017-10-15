package ge.ee.eewh.SugaModels;

import com.orm.SugarApp;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by beka-work on 13.10.2017.
 */

public class SaleTransferHeaderResult extends SugarRecord<SaleTransferHeaderResult> {
    public int Date;
    public String Store_No_;
    public String Receipt_No_ ;
    public String Document_No_;
    public String Item_No_;
    public String Manufacturer_Code;
    public String Description;
    public String Quantity;
    public String Serial_No_ ;
    public String Status;
    public String Location_Code;
    public String ChangedById;
    public String ScannedBarcode ;
    public int SannedQuantity;

    public int getSannedQuantity() {
        return SannedQuantity;
    }

    public void setSannedQuantity(int sannedQuantity) {
        SannedQuantity = sannedQuantity;
    }

    public String getFilterField() {
        return FilterField;
    }

    public void setFilterField(String filterField) {
        FilterField = filterField;
    }

    @Ignore

    public String FilterField;

    public int getDate() {
        return Date;
    }

    public void setDate(int date) {
        Date = date;
    }

    public String getStore_No_() {
        return Store_No_;
    }

    public void setStore_No_(String store_No_) {
        Store_No_ = store_No_;
    }

    public String getReceipt_No_() {
        return Receipt_No_;
    }

    public void setReceipt_No_(String receipt_No_) {
        Receipt_No_ = receipt_No_;
    }

    public String getDocument_No_() {
        return Document_No_;
    }

    public void setDocument_No_(String document_No_) {
        Document_No_ = document_No_;
    }

    public String getItem_No_() {
        return Item_No_;
    }

    public void setItem_No_(String item_No_) {
        Item_No_ = item_No_;
    }

    public String getManufacturer_Code() {
        return Manufacturer_Code;
    }

    public void setManufacturer_Code(String manufacturer_Code) {
        Manufacturer_Code = manufacturer_Code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getSerial_No_() {
        return Serial_No_;
    }

    public void setSerial_No_(String serial_No_) {
        Serial_No_ = serial_No_;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLocation_Code() {
        return Location_Code;
    }

    public void setLocation_Code(String location_Code) {
        Location_Code = location_Code;
    }

    public String getChangedById() {
        return ChangedById;
    }

    public void setChangedById(String changedById) {
        ChangedById = changedById;
    }

    public String getScannedBarcode() {
        return ScannedBarcode;
    }

    public void setScannedBarcode(String scannedBarcode) {
        ScannedBarcode = scannedBarcode;
    }
}
