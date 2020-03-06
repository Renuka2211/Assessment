package com.boda.renuka.assessment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    EcommerceApi ecommerceApi;
    private LayoutInflater layoutInflater;

    private LayoutInflater layoutInflater1;

    private LayoutInflater layoutInflater2;

    private View widgetContainer;

    ListView lvProductList;

    CustomAdapter customAdapter;

    private int REQUEST_CODE = 200;

    private ImageView ivSort,ivFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            init();
            ecommerceApi = RetrofitClientInstance.getRetrofitInstance().create(EcommerceApi.class);

            new GetDataTask().execute();


            lvProductList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.product_variant_dialog);

                    try {
                        JSONObject productJson = (JSONObject) customAdapter.getItem(position);

                        ((TextView) dialog.findViewById(R.id.tvColor)).setText(productJson.getString("color"));

                        ((TextView) dialog.findViewById(R.id.tvSize)).setText(productJson.getString("size"));

                        ((TextView) dialog.findViewById(R.id.tvPrice)).setText(productJson.getString("price"));

                        ((TextView) dialog.findViewById(R.id.tvProductName)).setText(productJson.getString("ProductName"));


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.show();


                }
            });

            ivSort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.sort_dialog_layout);
                        dialog.show();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            ivFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.filter_dialog_layout);
                        dialog.show();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

        }
        else {
            setPermission();
        }
    }

    private void init() {
        lvProductList = (ListView) findViewById(R.id.lvProductList);
        ivSort = (ImageView) findViewById(R.id.ivSort);
        ivFilter = (ImageView) findViewById(R.id.ivFilter);
    }

    class GetDataTask extends AsyncTask{
        SQLiteDatabaseManager databaseManager = new SQLiteDatabaseManager(MainActivity.this);
        ProgressDialog progressDialog = null;
        HashMap<String,String> parentCatMap = new HashMap<>();
        JSONArray productArray = new JSONArray();


        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            Log.d("MainActivity","GetDataTask:: onPreExecute");
            progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait... ", "Loading... ", false, true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String requestString = "";
            String contenttype = "application/json";
            try {
                Call<JsonElement> callBack = ecommerceApi.getData();
                callBack.enqueue(new Callback<JsonElement>(){

                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        Log.d("MainActivity", response.body().toString());
                        insertDataInDb(response);


                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Log.d("MainActivity", "Failure");
                    }
                });

                parentCatMap = new HashMap<>();
                parentCatMap = databaseManager.getParentCategoryList();
                Log.d("MainActivity","Parent category:: "+parentCatMap);
                productArray = databaseManager.getAllProducts();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            progressDialog.dismiss();
            //super.onPostExecute(o);

            Log.d("MainActivity","onPostExecute productArray :: "+productArray);

           /* customAdapter = new CustomAdapter(MainActivity.this,productArray);
            lvProductList.setAdapter(customAdapter);*/


        }

        private void insertDataInDb(Response response)
        {
            JSONObject dataJson = new JSONObject();
            try{
                if(response!= null && response.body()!= null)
                {
                    dataJson = new JSONObject(response.body().toString());
                    String categoryName = "";
                    int categoryId = -1;


                    if(dataJson.has("categories"))
                    {
                        JSONArray categoryArray = dataJson.getJSONArray("categories");
                        JSONArray productsArray;


                        for(int i = 0;i < categoryArray.length();i++)
                        {
                            JSONObject categoryJson = categoryArray.getJSONObject(i);

                            if(categoryJson.has("products")) {
                                productsArray = categoryJson.getJSONArray("products");

                                for(int p = 0;p < productsArray.length();p++)
                                {
                                    int productId = -1;
                                    String productName = "",date_added = "",taxName = "";
                                    double taxValue = 0.0;
                                    JSONObject productJson = productsArray.getJSONObject(p);
                                    if(productJson.has("id"))
                                    {
                                        productId = Integer.parseInt(productJson.getString("id"));
                                    }
                                    if(productJson.has("name"))
                                    {
                                        productName = productJson.getString("name");
                                    }
                                    if(productJson.has("date_added"))
                                    {
                                        date_added = productJson.getString("date_added");
                                    }


                                    if(productJson.has("tax"))
                                    {
                                        JSONObject taxJson = productJson.getJSONObject("tax");
                                        if(taxJson.has("name"))
                                        {
                                            taxName = taxJson.getString("name");
                                        }
                                        if(taxJson.has("value"))
                                        {
                                            taxValue = Double.parseDouble(taxJson.getString("value"));
                                        }
                                    }



                                    databaseManager.insertDataProduct(productId,productName,date_added,taxName,taxValue);


                                    if(productJson.has("variants"))
                                    {
                                        JSONArray productVariantArray = productJson.getJSONArray("variants");

                                        for(int v = 0;v< productVariantArray.length();v++)
                                        {
                                            JSONObject productVarJson = productVariantArray.getJSONObject(v);
                                            int productVarId = -1;
                                            String color = "";
                                            double size = -1,price = 0;

                                            //JSONObject productVarJson = productsArray.getJSONObject(p);
                                            if(productVarJson.has("id"))
                                            {
                                                productVarId = Integer.parseInt(productVarJson.getString("id"));
                                            }
                                            if(productVarJson.has("color"))
                                            {
                                                color = productVarJson.getString("color");
                                            }
                                            if(productVarJson.has("size") && !productVarJson.getString("size").equalsIgnoreCase("") && !productVarJson.getString("size").equalsIgnoreCase("null"))
                                            {
                                                size = Double.parseDouble(productVarJson.getString("size"));
                                            }
                                            if(productVarJson.has("price") && !productVarJson.getString("price").equalsIgnoreCase(""))
                                            {
                                                price = Double.parseDouble(productVarJson.getString("price"));
                                            }
                                            databaseManager.insertDataProductVariant(productVarId,color,size,price,productId);

                                        }
                                    }


                                }


                            }


                            Log.d("MainActivity",categoryJson+"");
                            if(categoryJson.has("id"))
                            {
                                categoryId = Integer.parseInt(categoryJson.getString("id"));
                            }
                            if(categoryJson.has("name"))
                            {
                                categoryName = categoryJson.getString("name");
                            }
                            databaseManager.insertDataCatagory(categoryId,categoryName);

                            if(categoryJson.has("child_categories"))
                            {
                                JSONArray subCategoryArray = categoryJson.getJSONArray("child_categories");

                                for(int s = 0;s < subCategoryArray.length();s++)
                                {
                                    databaseManager.insertDataSubCatagory(categoryId,subCategoryArray.getInt(s));
                                }
                            }

                        }
                    }

                    if(dataJson.has("rankings"))
                    {
                        JSONArray rankingsArray = dataJson.getJSONArray("rankings");
                        for(int r = 0;r < rankingsArray.length();r++)
                        {
                            JSONObject rankingJson = rankingsArray.getJSONObject(r);

                            if(rankingJson.has("products"))
                            {
                                JSONArray productArray = rankingJson.getJSONArray("products");

                                for(int pr = 0; pr < productArray.length();pr++)
                                {
                                    int id = -1,orderedCount = 0,viewedCount = 0,sharedCount = 0;

                                    JSONObject productRJson = productArray.getJSONObject(pr);



                                    if(productRJson.has("id"))
                                    {
                                        id = productRJson.getInt("id");
                                        if(rankingJson.has("ranking") && rankingJson.getString("ranking").contains("OrdeRed"))
                                        {
                                            if(productRJson.has("order_count")) {
                                                orderedCount = productRJson.getInt("order_count");
                                            }
                                        }
                                        if(rankingJson.has("ranking") && rankingJson.getString("ranking").contains("ShaRed"))
                                        {
                                            if(productRJson.has("shares")) {
                                                sharedCount = productRJson.getInt("shares");
                                            }

                                        }
                                        if(rankingJson.has("ranking") && rankingJson.getString("ranking").contains("Viewed"))
                                        {
                                            if(productRJson.has("view_count")) {
                                                viewedCount = productRJson.getInt("view_count");
                                            }
                                        }
                                    }

                                    databaseManager.insertDataRanking(id,rankingJson.getString("ranking"),orderedCount,sharedCount,viewedCount);
                                }
                            }

                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            new SetDataTask().execute();
        }
    }
    private void setPermission()
    {
        String[] permissions = { "android.permission.READ_PHONE_STATE", "android.permission.WRITE_EXTERNAL_STORAGE" };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            switch (requestCode)
            {
                case 200:

                    if (grantResults.length > 0)
                    {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                                && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                            {


                                init();
                                ecommerceApi = RetrofitClientInstance.getRetrofitInstance().create(EcommerceApi.class);

                                new GetDataTask().execute();


                                lvProductList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        Dialog dialog = new Dialog(MainActivity.this);
                                        dialog.setContentView(R.layout.product_variant_dialog);

                                        try {
                                            JSONObject productJson = (JSONObject) customAdapter.getItem(position);

                                            ((TextView) dialog.findViewById(R.id.tvColor)).setText(productJson.getString("color"));

                                            ((TextView) dialog.findViewById(R.id.tvSize)).setText(productJson.getString("size"));

                                            ((TextView) dialog.findViewById(R.id.tvPrice)).setText(productJson.getString("price"));

                                            ((TextView) dialog.findViewById(R.id.tvProductName)).setText(productJson.getString("ProductName"));


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.show();


                                    }
                                });
                            }}}}}
    }
    class SetDataTask extends AsyncTask{
        SQLiteDatabaseManager databaseManager = new SQLiteDatabaseManager(MainActivity.this);
        ProgressDialog progressDialog = null;
        HashMap<String,String> parentCatMap = new HashMap<>();
        JSONArray productArray = new JSONArray();


        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            Log.d("MainActivity","GetDataTask:: onPreExecute");
            progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait... ", "Loading... ", false, true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            parentCatMap = new HashMap<>();
            parentCatMap = databaseManager.getParentCategoryList();
            Log.d("MainActivity","Parent category:: "+parentCatMap);
            productArray = databaseManager.getAllProducts();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
            progressDialog.dismiss();
            //super.onPostExecute(o);

            Log.d("MainActivity","onPostExecute productArray rr :: "+productArray);

            customAdapter = new CustomAdapter(MainActivity.this,productArray);
            lvProductList.setAdapter(customAdapter);
            customAdapter.notifyDataSetChanged();
        }
    }

    class SetSortedDataTask extends AsyncTask{
        SQLiteDatabaseManager databaseManager = new SQLiteDatabaseManager(MainActivity.this);
        ProgressDialog progressDialog = null;
        HashMap<String,String> parentCatMap = new HashMap<>();
        JSONArray productArray = new JSONArray();


        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            Log.d("MainActivity","GetDataTask:: onPreExecute");
            progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait... ", "Loading... ", false, true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            parentCatMap = new HashMap<>();
            parentCatMap = databaseManager.getParentCategoryList();
            Log.d("MainActivity","Parent category:: "+parentCatMap);
            productArray = databaseManager.getAllProducts();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
            progressDialog.dismiss();
            //super.onPostExecute(o);

            Log.d("MainActivity","onPostExecute productArray rr :: "+productArray);

            customAdapter = new CustomAdapter(MainActivity.this,productArray);
            lvProductList.setAdapter(customAdapter);
            customAdapter.notifyDataSetChanged();
        }
    }
}
