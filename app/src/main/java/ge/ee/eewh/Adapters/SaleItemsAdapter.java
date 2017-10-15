package ge.ee.eewh.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
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
import ge.ee.eewh.SugaModels.LinesResult;
import ge.ee.eewh.SugaModels.SaleTransferHeaderResult;

/**
 * Created by beka-work on 30.05.2017.
 */

public class SaleItemsAdapter extends ArrayAdapter<SaleTransferHeaderResult> implements Filterable {

    List<SaleTransferHeaderResult> _originalItems;
    List<SaleTransferHeaderResult> _filteredItems;
    String _filterString="";
    int _blueColor=0;

    public SaleItemsAdapter(Context context, int resource, List<SaleTransferHeaderResult> items) {
        super(context, resource, items);
        _originalItems=new ArrayList<>(items);
        _filteredItems=items;
        _blueColor=context.getResources().getColor(R.color.colorKtgLogo);
    }

    final BackgroundColorSpan fcs = new BackgroundColorSpan(Color.YELLOW);
    final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_layout, null);
        }

        SaleTransferHeaderResult p = _filteredItems.get(position);

        if (p != null) {
            TextView TextItemAndNo = (TextView) v.findViewById(R.id.TextItemAndNo);
            TextView TextDescription = (TextView) v.findViewById(R.id.TextDescription);
            TextView textManufacturerAndShortDesc = (TextView) v.findViewById(R.id.textManufacturerAndShortDesc);
            TextView TextLocation = (TextView) v.findViewById(R.id.TextLocation);
            TextView TextQuantity = (TextView) v.findViewById(R.id.TextQuantity);

            //ConstraintLayout fullLayout = (ConstraintLayout) v.findViewById(R.id.LayCustomer);

            if (TextItemAndNo != null) {
                TextItemAndNo.setText(p.getItem_No_());
            }
            if (TextDescription != null) {
                TextDescription.setText(p.getManufacturer_Code());
            }
            if (textManufacturerAndShortDesc != null) {
                textManufacturerAndShortDesc.setText(p.getDescription());
            }
            if (TextLocation != null) {
                TextLocation.setText(p.getLocation_Code());
            }
            if (TextQuantity != null) {
                //String quantity=String.format("%.0f",p.getQuantity());
                TextQuantity.setText(p.getSannedQuantity()+"/"+String.valueOf(p.getQuantity()));
                if(p.getSannedQuantity()==Integer.valueOf(p.getQuantity())){

                    TextQuantity.setTextColor(_blueColor);
                }
            }
//
//            if (fullLayout != null ) {
//                fullLayout.setBackgroundColor(p.getStatusColor());
//            }

            if(_filterString.length() > 0 && p.getFilterField() != null){
                if(convertView==null) return v;
                Context context=convertView.getContext();
                if(context==null) return v;

                String viewName="";
                String valueText="";
                String filterField=p.getFilterField();
                switch (filterField){
                    case "Item_No_":
                        viewName="TextItemAndNo";
                        valueText=p.getItem_No_();
                        break;
                    case "Description":
                        viewName="textManufacturerAndShortDesc";
                        valueText=p.getDescription();
                        break;
                    case "Full_Description":
                        viewName="TextDescription";
//                        valueText=p.getFull_Description();
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
            String prefixString = constraint.toString();//
            FilterResults filterResults = new FilterResults();
            //ArrayList<CustomerListModel> tempList=new ArrayList<CustomerListModel>();
            FilterStarted=true;
            _filteredItems.clear();
            Log.d("eewh",prefixString);
            if(prefixString!=null && prefixString!="") {
                for (SaleTransferHeaderResult obj : _originalItems) {

                    if (obj.getItem_No_() != null && obj.getItem_No_().contains(prefixString)) {
                        obj.setFilterField("Item_No_");
                        _filteredItems.add(obj);
                    } else if (String.valueOf(obj.getDescription()).contains(prefixString)) {
                        obj.setFilterField("Description");
                        _filteredItems.add(obj);
                    }
//                else if(obj.getFull_Description()!=null && obj.getFull_Description().contains(prefixString)){
//                    obj.setFilterField("Full_Description");
//                    _filteredItems.add(obj);
//                }
//                else if(obj.getNumeratorNumber().contains(prefixString)){
//                    obj.setFilterField("NumeratorNumber");
//                    _filteredItems.add(obj);
//                }
                }
            }

            filterResults.values = _filteredItems;
            filterResults.count = _filteredItems.size();
            FilterStarted=false;
            _filterString=prefixString;
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
