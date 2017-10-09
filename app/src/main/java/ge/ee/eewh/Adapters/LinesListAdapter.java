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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ge.ee.eewh.R;
import ge.ee.eewh.SugaModels.HeaderResult;
import ge.ee.eewh.SugaModels.LinesResult;

/**
 * Created by beka-work on 30.05.2017.
 */

public class LinesListAdapter extends ArrayAdapter<LinesResult> implements Filterable {

    List<LinesResult> _originalItems;
    List<LinesResult> _filteredItems;
    String _filterString="";


    public LinesListAdapter(Context context, int resource, List<LinesResult> items) {
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
            v = vi.inflate(R.layout.list_item_layout, null);
        }

        LinesResult p = _filteredItems.get(position);

        if (p != null) {
            TextView TextItemAndNo = (TextView) v.findViewById(R.id.TextItemAndNo);
            TextView TextDescription = (TextView) v.findViewById(R.id.TextDescription);
            TextView textManufacturerAndShortDesc = (TextView) v.findViewById(R.id.textManufacturerAndShortDesc);
            TextView TextLocation = (TextView) v.findViewById(R.id.TextLocation);
            TextView TextQuantity = (TextView) v.findViewById(R.id.TextQuantity);

            ConstraintLayout fullLayout = (ConstraintLayout) v.findViewById(R.id.LayCustomer);

            if (TextItemAndNo != null) {
                TextItemAndNo.setText(p.getLine_No_() +" / "+p.getNo_());
            }
            if (TextDescription != null) {
                TextDescription.setText(p.getFull_Description());
            }
            if (textManufacturerAndShortDesc != null) {
                textManufacturerAndShortDesc.setText(p.getManufacturer_Code()+" / "+p.getDescription());
            }
            if (TextLocation != null) {
                TextLocation.setText(p.getLocation_Code());
            }

            if (TextQuantity != null) {
                String quantity=String.format("%.0f",p.getQuantity());
                TextQuantity.setText(quantity);
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
                switch (p.getFilterField()){
                    case "Line_No_":
                    case "No_":
                        viewName="TextItemAndNo";
                        valueText=p.getLine_No_() +" / "+p.getNo_();
                        break;
//                    case "Address":
//                        viewName="textAddress";
//                        valueText=p.getAddress();
//                        break;
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
            Log.d("eewh",prefixString);
            for (LinesResult obj:_originalItems) {

                if(obj.getNo_().contains(prefixString) ){
                    obj.setFilterField("No_");
                    _filteredItems.add(obj);
                }
                else if(String.valueOf(obj.getLine_No_()).contains(prefixString)){
                    obj.setFilterField("Line_No_");
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
