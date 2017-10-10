package ge.ee.eewh.SugaModels;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by beka-work on 02.10.2017.
 */

public class BarcodesResult extends SugarRecord<BarcodesResult> {
    public String Source_ID;
    public int Entry_No_;
    public int Source_Ref__No_;
    public String Location_Code;
    public String Item_No_;
    public float Quantity;
    public String Serial_No_;
    public String Lot_No_;
    public String Created_By;
    public String ScannedBarcode;
    public String changedBy;

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getScannedBarcode() {
        return
                ScannedBarcode;
    }

    public void setScannedBarcode(String scannedBarcode) {
        ScannedBarcode = scannedBarcode;
    }

    @Ignore
    public String FilterField;

    public String getSource_ID() {
        return Source_ID;
    }

    public void setSource_ID(String source_ID) {
        Source_ID = source_ID;
    }

    public int getEntry_No_() {
        return Entry_No_;
    }

    public void setEntry_No_(int entry_No_) {
        Entry_No_ = entry_No_;
    }

    public int getSource_Ref__No_() {
        return Source_Ref__No_;
    }

    public void setSource_Ref__No_(int source_Ref__No_) {
        Source_Ref__No_ = source_Ref__No_;
    }

    public String getLocation_Code() {
        return Location_Code;
    }

    public void setLocation_Code(String location_Code) {
        Location_Code = location_Code;
    }

    public String getItem_No_() {
        return Item_No_;
    }

    public void setItem_No_(String item_No_) {
        Item_No_ = item_No_;
    }

    public float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
    }

    public String getSerial_No_() {
        return Serial_No_;
    }

    public void setSerial_No_(String serial_No_) {
        Serial_No_ = serial_No_;
    }

    public String getLot_No_() {
        return Lot_No_;
    }

    public void setLot_No_(String lot_No_) {
        Lot_No_ = lot_No_;
    }

    public String getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(String created_By) {
        Created_By = created_By;
    }

    public String getFilterField() {
        return FilterField;
    }

    public void setFilterField(String filterField) {
        FilterField = filterField;
    }
}
