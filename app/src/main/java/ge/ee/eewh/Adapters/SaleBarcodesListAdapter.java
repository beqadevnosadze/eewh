package ge.ee.eewh.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ge.ee.eewh.R;
import ge.ee.eewh.SugaModels.BarcodesResult;
import ge.ee.eewh.SugaModels.SaleTransferBarcodes;

/**
 * Created by beka-work on 30.05.2017.
 */

public class SaleBarcodesListAdapter extends ArrayAdapter<SaleTransferBarcodes> implements Filterable {

    List<SaleTransferBarcodes> _originalItems;
    List<SaleTransferBarcodes> _filteredItems;
    String _filterString="";


    public SaleBarcodesListAdapter(Context context, int resource, List<SaleTransferBarcodes> items) {
        super(context, resource, items);
        _originalItems=new ArrayList<>(items);;
        _filteredItems=items;
    }

    final BackgroundColorSpan fcs = new BackgroundColorSpan(Color.YELLOW);
    final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_barcodes_layout, null);
        }

        SaleTransferBarcodes p = _filteredItems.get(position);

        if (p != null) {
            TextView TextOriginalSerial = (TextView) v.findViewById(R.id.TextOriginalSerial);
            TextView TextScannedSerial = (TextView) v.findViewById(R.id.TextScannedSerial);



            //ConstraintLayout fullLayout = (ConstraintLayout) v.findViewById(R.id.LayCustomer);

            if (TextOriginalSerial != null) {
                TextOriginalSerial.setText(p.getSerial_No_());
            }
            if (TextScannedSerial != null) {
                TextScannedSerial.setText(p.getScannedBarcode());
            }

//
//            if (fullLayout != null ) {
//                fullLayout.setBackgroundColor(p.getStatusColor());
//            }

            String filterField=p.getFilterField();
            if(_filterString.length() > 0 && filterField != null){
                if(convertView==null) return v;
                Context context=convertView.getContext();
                if(context==null) return v;

                String viewName="";
                String valueText="";

                switch (filterField){
                    case "Serial_No_":
                        viewName="TextOriginalSerial";
                        valueText=p.getSerial_No_();
                        break;
                    case "ScannedBarcode":
                        viewName="TextScannedSerial";
                        valueText=p.getScannedBarcode();
                        break;
//                    case "NumeratorNumber":
//                        viewName="textNumeratorNumber";
//                        valueText=p.getNumeratorNumber();
//                        break;
                }

                if(viewName.length() > 0 && valueText.length() > 0){
                    int id = convertView.getResources().getIdentifier(viewName, "id", context.getPackageName());
                    TextView textView = (TextView)convertView.findViewById(id);
                    if(textView != null){
                        final SpannableStringBuilder sb = new SpannableStringBuilder(valueText);

                        try {
                            int index= TextUtils.indexOf(valueText,_filterString );
                            if(index >= 0 && _filterString!=null && _filterString.length() > 0){
                                int length=_filterString.length();
                                sb.setSpan(fcs,index, index+length, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                sb.setSpan(bss,index, index+length, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                textView.setText(sb);
                            }
                            else {
                                textView.setText(valueText);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }

            }
        }
       return v;
    }
//    @Override
    public Filter getFilter() {
        return myFilter;
    }

    boolean FilterStarted=false;


    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(FilterStarted){
                return new FilterResults();
            }

            if(constraint==null){
                return new FilterResults();
            }
            String prefixString = constraint.toString();//.toLowerCase()
            FilterResults filterResults = new FilterResults();
            //ArrayList<CustomerListModel> tempList=new ArrayList<CustomerListModel>();
            FilterStarted=true;
            _filteredItems.clear();
            //Log.d("eewh",prefixString);
            //Log.v("eewhfilter2",String.valueOf(_originalItems.size()));
            for (SaleTransferBarcodes obj:_originalItems) {

                if(obj.getSerial_No_().contains(prefixString) ){
                    obj.setFilterField("Serial_No_");
                    _filteredItems.add(obj);
                }
                else if(obj.getScannedBarcode()!=null && obj.getScannedBarcode().contains(prefixString)){
                    obj.setFilterField("ScannedBarcode");
                    _filteredItems.add(obj);
                }
//                else if(obj.getAddress().contains(prefixString)){
//                    obj.setFilterField("Address");
//                    _filteredItems.add(obj);
//                }
//                else if(obj.getNumeratorNumber().contains(prefixString)){
//                    obj.setFilterField("NumeratorNumber");
//                    _filteredItems.add(obj);
//                }
            }

            filterResults.values = _filteredItems;
            filterResults.count = _filteredItems.size();
            FilterStarted=false;
            _filterString=prefixString;
            //Log.v("eewhfilter",String.valueOf(filterResults.count));
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            //_filteredItems = (ArrayList<CustomerListModel>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                _filterString="";
                notifyDataSetInvalidated();
            }
        }
    };

}
