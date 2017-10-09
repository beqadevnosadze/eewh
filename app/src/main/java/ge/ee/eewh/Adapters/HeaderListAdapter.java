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


import ge.ee.eewh.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ge.ee.eewh.SugaModels.HeaderResult;

/**
 * Created by beka-work on 30.05.2017.
 */

public class HeaderListAdapter extends ArrayAdapter<HeaderResult> implements Filterable {

    List<HeaderResult> _originalItems;
    List<HeaderResult> _filteredItems;
    String _filterString="";


    public HeaderListAdapter(Context context, int resource, List<HeaderResult> items) {
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
            v = vi.inflate(R.layout.list_order_layout, null);
        }

        HeaderResult p = _filteredItems.get(position);

        if (p != null) {
            TextView TextOrderNumberAndDate = (TextView) v.findViewById(R.id.TextOrderNumberAndDate);
            TextView TextVendor = (TextView) v.findViewById(R.id.TextVendor);
            TextView textPostDateAndStatus = (TextView) v.findViewById(R.id.textPostDateAndStatus);
            TextView TextLocationAndUser = (TextView) v.findViewById(R.id.TextLocationAndUser);

            ConstraintLayout fullLayout = (ConstraintLayout) v.findViewById(R.id.LayCustomer);

            if (TextOrderNumberAndDate != null) {
                Timestamp stamp = new Timestamp(p.getOrder_Date()* 1000L);
                Date date = new Date(stamp.getTime());
                String dateAsText = new SimpleDateFormat("yyyy.MM.dd").format(date);
                TextOrderNumberAndDate.setText(p.getNo_() +" / "+dateAsText);
            }
            if (TextVendor != null) {
                TextVendor.setText(p.getBuy_from_Vendor_Name());
            }
            if (textPostDateAndStatus != null) {
                Timestamp stamp = new Timestamp(p.getOrder_Date()* 1000L);
                Date date = new Date(stamp.getTime());
                String dateAsText = new SimpleDateFormat("yyyy.MM.dd").format(date);
                textPostDateAndStatus.setText(dateAsText+" / "+p.getStatus());
            }
            if (TextLocationAndUser != null) {
                TextLocationAndUser.setText(p.getLocation_Code()+" - "+p.getUser_ID());
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
                    case "No_":

                        Timestamp stamp = new Timestamp(p.getOrder_Date()* 1000L);
                        Date date = new Date(stamp.getTime());
                        String dateAsText = new SimpleDateFormat("yyyy.MM.dd").format(date);

                        viewName="TextOrderNumberAndDate";
                        valueText=p.getNo_() +" / "+dateAsText;
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
            for (HeaderResult obj:_originalItems) {

                if(obj.getNo_().contains(prefixString) ){
                    obj.setFilterField("No_");
                    _filteredItems.add(obj);
                }
//                else if(obj.getName().contains(prefixString)){
//                    obj.setFilterField("Name");
//                    _filteredItems.add(obj);
//                }
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
