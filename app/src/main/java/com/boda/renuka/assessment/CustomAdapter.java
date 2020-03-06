package com.boda.renuka.assessment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustomAdapter extends BaseAdapter{
    JSONArray productArray;

    Context context;

    public CustomAdapter(Context context,JSONArray productArray)
    {
        this.productArray = productArray;
        this.context = context;
    }

    @Override
    public int getCount() {
        return productArray.length();
    }

    @Override
    public Object getItem(int position) {
        JSONObject productJson = new JSONObject();
        try{
            productJson = productArray.getJSONObject(position);
            return productJson;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
           return productJson;

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CardView layout;
        TextView tvProductName,tvDateAdded,tvTaxName,tvTaxVal,tvViewedCount,tvOrderedCount,tvSharedCount;
        RatingBar rbVieweCount,rbOrderedCount,rbSharedCount;

        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            layout = (CardView) inflater.inflate(R.layout.product_list_row, null);
        }
        else
        {
            layout = (CardView) convertView;

        }

        tvProductName = (TextView) layout.findViewById(R.id.tvProductName);

        tvDateAdded = (TextView) layout.findViewById(R.id.tvDateAdded);

        tvTaxName = (TextView) layout.findViewById(R.id.tvTaxName);

        tvTaxVal = (TextView) layout.findViewById(R.id.tvTaxVal);

        tvViewedCount = (TextView) layout.findViewById(R.id.tvViewCount);

        tvOrderedCount = (TextView) layout.findViewById(R.id.tvOrdredCount);

        tvSharedCount = (TextView) layout.findViewById(R.id.tvSharedCount);

        rbVieweCount = (RatingBar) layout.findViewById(R.id.rbVCount);

        rbOrderedCount = (RatingBar) layout.findViewById(R.id.rbOCount);

        rbSharedCount = (RatingBar) layout.findViewById(R.id.rbSCount);

        rbVieweCount.setVisibility(View.GONE);

        rbOrderedCount.setVisibility(View.GONE);

        rbSharedCount.setVisibility(View.GONE);


        try {
            tvProductName.setText(productArray.getJSONObject(position).getString("ProductName"));
            tvDateAdded.setText(productArray.getJSONObject(position).getString("Added_Date"));
            tvTaxName.setText("Tax type: "+productArray.getJSONObject(position).getString("tax_name"));
            tvTaxVal.setText("Tax val: "+productArray.getJSONObject(position).getString("tax_val"));

            if(productArray.getJSONObject(position).getInt("viewedCount") > 0){

            tvViewedCount.setText("Viewed Count: "+productArray.getJSONObject(position).getInt("viewedCount"));
                rbVieweCount.setRating(productArray.getJSONObject(position).getInt("viewedCount"));
                Log.d("CustomAdapter","Viewed Count: "+productArray.getJSONObject(position).getInt("viewedCount"));
            }
            else {
                tvViewedCount.setVisibility(View.GONE);
                //rbVieweCount.setVisibility(View.GONE);
            }

            if(productArray.getJSONObject(position).getInt("orderedCount") > 0){
            tvOrderedCount.setText("Ordered Count: "+productArray.getJSONObject(position).getInt("orderedCount"));
                rbVieweCount.setRating(productArray.getJSONObject(position).getInt("orderedCount"));
                Log.d("CustomAdapter","Ordered Count: "+productArray.getJSONObject(position).getInt("orderedCount"));


            }else {
                tvOrderedCount.setVisibility(View.GONE);
                rbOrderedCount.setVisibility(View.GONE);
            }

            if(productArray.getJSONObject(position).getInt("sharedCount") > 0){
            tvSharedCount.setText("Shared Count: "+productArray.getJSONObject(position).getInt("sharedCount"));
                rbVieweCount.setRating(productArray.getJSONObject(position).getInt("sharedCount"));
                Log.d("CustomAdapter","Shared Count: "+productArray.getJSONObject(position).getInt("sharedCount"));


            }else {
                tvSharedCount.setVisibility(View.GONE);
                rbSharedCount.setVisibility(View.GONE);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return layout;
    }
}
