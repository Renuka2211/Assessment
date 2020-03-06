package com.boda.renuka.assessment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteDatabaseManager extends SQLiteOpenHelper {
    private static final String NAME = "E-Commerce";
    private static final int VERSION = 1;

    //table category
    private static final String TABLE_CATEGORY = "Category";

    private static final String COLUMN_CATEGORY_ID = "Category_Id";

    private static final String COLUMN_CATEGORY_NAME = "Category_Name";

    private static final String COLUMN_PRODUCT_ID = "Product_Id";

    private static final String COLUMN_PRODUCT_NAME = "Product_Name";

    private static final String COLUMN_PRODUCT_DATE_ADDED = "Added_date";

    //table Sub category
    private static final String TABLE_SUB_CATEGORY = "Sub_Category";

    private static final String COLUMN_SUB_CATEGORY_ID = "Sub_Cat_Id";

    //table Products
    private static final String TABLE_PRODUCTS = "Products";

    private static final String COLUMN_PRODUCT_VARIANT_ID = "Product_Var_Id";

    //table rankings
    private static final String TABLE_RANKINGS = "Rankings";
    private static final String COLUMN_RANKING = "ranking";

    private static final String TABLE_PRODUCTS_VARIANTS = "Products_Variants";
    private static final String COLUMN_PRODUCT_COLOR = "color";

    private static final String COLUMN_PRODUCT_SIZE = "size";

    private static final String COLUMN_PRODUCT_PRICE = "price";

    private static final String COLUMN_PRODUCT_TAX_NAME = "tax_name";

    private static final String COLUMN_PRODUCT_TAX_VALUE = "tax_value";

    private static final String COLUMN_PRODUCT_VIEWED_COUNT = "viewed_count";

    private static final String COLUMN_PRODUCT_ORDERED_COUNT = "ordered_count";

    private static final String COLUMN_PRODUCT_SAHRED_COUNT = "shared_count";



    SQLiteDatabase db;

    public SQLiteDatabaseManager(Context context) {
        super(context, NAME,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            //db = this.getWritableDatabase();
            //this.db = db;
            String categoryQuery = "Create table " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + " integer PRIMARY KEY NOT NULL," + COLUMN_CATEGORY_NAME + " varchar(50))";
            db.execSQL(categoryQuery);

            String subCategoryQuery = "Create table " + TABLE_SUB_CATEGORY + "(" + COLUMN_SUB_CATEGORY_ID + " integer PRIMARY KEY NOT NULL," + COLUMN_CATEGORY_ID + " integer)";
            db.execSQL(subCategoryQuery);

            String productQuery = "Create table " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + " integer PRIMARY KEY NOT NULL," + COLUMN_PRODUCT_NAME + " varchar(100)," + COLUMN_PRODUCT_DATE_ADDED + " varchar(20)," +COLUMN_PRODUCT_TAX_NAME+" varchar(100),"+COLUMN_PRODUCT_TAX_VALUE+" Double)";
            db.execSQL(productQuery);

            String productVariantsQuery = "Create table " + TABLE_PRODUCTS_VARIANTS + "(" + COLUMN_PRODUCT_VARIANT_ID + " integer PRIMARY KEY NOT NULL,"+COLUMN_PRODUCT_ID+" integer," + COLUMN_PRODUCT_COLOR + " varchar(100)," + COLUMN_PRODUCT_SIZE + " Double," + COLUMN_PRODUCT_PRICE + " Double)";
            db.execSQL(productVariantsQuery);

            String rankingsQuery = "Create table " + TABLE_RANKINGS + "(" + COLUMN_RANKING + " varchar(100)," + COLUMN_PRODUCT_ID + " Integer,"+COLUMN_PRODUCT_ORDERED_COUNT+" Integer,"+COLUMN_PRODUCT_VIEWED_COUNT+ " Integer,"+COLUMN_PRODUCT_SAHRED_COUNT+" Integer)";
            db.execSQL(rankingsQuery);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insertDataCatagory(int catagoryId,String catagoryName)
    {
        String insertQuery = "INSERT OR REPLACE into "+TABLE_CATEGORY+"("+COLUMN_CATEGORY_ID+","+COLUMN_CATEGORY_NAME+")"+"values('"+catagoryId+"','"+catagoryName+"')";

        this.getWritableDatabase().execSQL(insertQuery);

    }

    public void insertDataProduct(int productId,String productName,String date_added,String taxName,double taxVal)
    {
        String insertQuery = "INSERT OR REPLACE into "+TABLE_PRODUCTS+"("+COLUMN_PRODUCT_ID+","+COLUMN_PRODUCT_NAME+","+COLUMN_PRODUCT_DATE_ADDED+","+COLUMN_PRODUCT_TAX_NAME+","+COLUMN_PRODUCT_TAX_VALUE+")"+"values('"+productId+"','"+productName+"','"+date_added+"','"+taxName+"',"+taxVal+")";

        this.getWritableDatabase().execSQL(insertQuery);

    }
    public void insertDataProductVariant(int productId,String color,double size,double price,int parProdId)
    {
        String insertQuery = "INSERT OR REPLACE into "+TABLE_PRODUCTS_VARIANTS+"("+COLUMN_PRODUCT_VARIANT_ID+","+COLUMN_PRODUCT_ID+","+COLUMN_PRODUCT_COLOR+","+COLUMN_PRODUCT_SIZE+","+COLUMN_PRODUCT_PRICE+")"+"values('"+productId+"','"+parProdId+"','"+color+"',"+size+","+price+")";

        this.getWritableDatabase().execSQL(insertQuery);

    }
    public void insertDataSubCatagory(int categoryId,int subCategoryId)
    {
        String insertQuery = "INSERT OR REPLACE into "+TABLE_SUB_CATEGORY+"("+COLUMN_CATEGORY_ID+","+COLUMN_SUB_CATEGORY_ID+")"+"values('"+categoryId+"','"+subCategoryId+"')";

        this.getWritableDatabase().execSQL(insertQuery);

    }
    public void insertDataRanking(int id,String ranking,int orderedCount,int sharedCount,int viewedCount)
    {
        String insertQuery = "INSERT OR REPLACE into "+TABLE_RANKINGS+"("+COLUMN_PRODUCT_ID+","+COLUMN_RANKING+","+COLUMN_PRODUCT_ORDERED_COUNT+","+COLUMN_PRODUCT_SAHRED_COUNT+","+COLUMN_PRODUCT_VIEWED_COUNT+")"+"values("+id+",'"+ranking+"','"+orderedCount+"','"+sharedCount+"','"+viewedCount+"')";


        this.getWritableDatabase().execSQL(insertQuery);

    }

    public HashMap<String,String> getParentCategoryList()
    {

        HashMap<String,String> parentCategory = new HashMap<>();

        try{
            String categoryQuery = "SELECT Category_Id, Category_Name FROM Category WHERE Category_Id NOT IN (SELECT Category_Id FROM Sub_Category)";

            Cursor cursor = this.getWritableDatabase().rawQuery(categoryQuery,null);

            if (cursor.moveToFirst()){
                do {
                    // Passing values
                    String category_id = cursor.getString(0);
                    String category_name = cursor.getString(1);
                    // Do something Here with values
                    parentCategory.put(category_id,category_name);
                } while(cursor.moveToNext());
            }
            Log.d("SQLiteDatabaseManager","Parent category:: "+parentCategory);
            cursor.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return parentCategory;
    }
    public JSONArray getAllProducts()
    {

        JSONArray productArray = new JSONArray();
        JSONObject productJson;
        int viewedCount = 0,orderedCount = 0,sharedCount = 0;

        try{
            String productsQuery = "Select pr."+COLUMN_PRODUCT_ID+",pr."+COLUMN_PRODUCT_NAME+",pr."+COLUMN_PRODUCT_DATE_ADDED+",pr."+COLUMN_PRODUCT_TAX_NAME+
                    ",pr."+COLUMN_PRODUCT_TAX_VALUE+ ",pv."+COLUMN_PRODUCT_VARIANT_ID+"" +
                    ",pv."+COLUMN_PRODUCT_COLOR+",pv."+COLUMN_PRODUCT_SIZE+",pv."+COLUMN_PRODUCT_PRICE+
                    " from Products pr,Products_Variants pv where pr.Product_Id = pv.Product_Id";

            Cursor cursor = this.getWritableDatabase().rawQuery(productsQuery,null);

            if (cursor.moveToFirst()){
                do {
                    productJson = new JSONObject();
                    int productId = Integer.parseInt(cursor.getString(0));

                    int productVarId = Integer.parseInt(cursor.getString(5));
                    productJson.put("ProductId",productId);
                    productJson.put("ProductName",cursor.getString(1));
                    productJson.put("Added_Date",cursor.getString(2));
                    productJson.put("tax_name",cursor.getString(3));
                    productJson.put("tax_val",cursor.getString(4));
                    productJson.put("ProductVarId",productVarId);
                    productJson.put("color",cursor.getString(6));
                    productJson.put("size",cursor.getString(7));
                    productJson.put("price",cursor.getString(8));
                    productArray.put(productJson);
                    Log.d("SQLiteDatabaseManager","getAllProducts productJson:: "+productJson);

                    viewedCount = getViewedCount(productVarId);

                    orderedCount = getOrderedCount(productVarId);

                    sharedCount = getSharedCount(productVarId);

                    productJson.put("viewedCount",viewedCount);
                    productJson.put("orderedCount",orderedCount);
                    productJson.put("sharedCount",sharedCount);



                } while(cursor.moveToNext());
            }
            Log.d("SQLiteDatabaseManager","getAllProducts productArray:: "+productArray);
            cursor.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return productArray;
    }

    public int getViewedCount(int productId)
    {

        JSONArray productArray = new JSONArray();
        int viewedCount = 0;

        try{
            String productsQuery = "select * from Rankings where Product_Id = "+productId+" and "+COLUMN_RANKING+" = 'Most Viewed Products'";

            Cursor cursor = this.getWritableDatabase().rawQuery(productsQuery,null);

            if (cursor.moveToFirst()){
                do {

                    viewedCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_VIEWED_COUNT)));


                } while(cursor.moveToNext());
            }
            Log.d("SQLiteDatabaseManager","getAllProducts productArray:: "+productArray);
            cursor.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return viewedCount;
    }

    public int getOrderedCount(int productId)
    {

        JSONArray productArray = new JSONArray();
        int orderedCount = 0;

        try{
            String productsQuery = "select * from Rankings where Product_Id = "+productId+" and "+COLUMN_RANKING+" = 'Most OrdeRed Products'";

            Cursor cursor = this.getWritableDatabase().rawQuery(productsQuery,null);

            if (cursor.moveToFirst()){
                do {

                    orderedCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ORDERED_COUNT)));


                } while(cursor.moveToNext());
            }
            Log.d("SQLiteDatabaseManager","getAllProducts productArray:: "+productArray);
            cursor.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return orderedCount;
    }

    public int getSharedCount(int productId)
    {

        JSONArray productArray = new JSONArray();
        int sharedCount = 0;

        try{
            String productsQuery = "select * from Rankings where Product_Id = "+productId+" and "+COLUMN_RANKING+" = 'Most ShaRed Products'";

            Cursor cursor = this.getWritableDatabase().rawQuery(productsQuery,null);

            if (cursor.moveToFirst()){
                do {

                    sharedCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_SAHRED_COUNT)));


                } while(cursor.moveToNext());
            }
            Log.d("SQLiteDatabaseManager","getAllProducts productArray:: "+productArray);
            cursor.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return sharedCount;
    }

    public JSONArray getAllProducts(String sort)
    {

        JSONArray productArray = new JSONArray();
        JSONObject productJson;
        int viewedCount = 0,orderedCount = 0,sharedCount = 0;

        try{
            String productsQuery = "Select pr."+COLUMN_PRODUCT_ID+",pr."+COLUMN_PRODUCT_NAME+",pr."+COLUMN_PRODUCT_DATE_ADDED+",pr."+COLUMN_PRODUCT_TAX_NAME+
                    ",pr."+COLUMN_PRODUCT_TAX_VALUE+ ",pv."+COLUMN_PRODUCT_VARIANT_ID+"" +
                    ",pv."+COLUMN_PRODUCT_COLOR+",pv."+COLUMN_PRODUCT_SIZE+",pv."+COLUMN_PRODUCT_PRICE+
                    " from Products pr,Products_Variants pv where pr.Product_Id = pv.Product_Id";

            Cursor cursor = this.getWritableDatabase().rawQuery(productsQuery,null);

            if (cursor.moveToFirst()){
                do {
                    productJson = new JSONObject();
                    int productId = Integer.parseInt(cursor.getString(0));

                    int productVarId = Integer.parseInt(cursor.getString(5));
                    productJson.put("ProductId",productId);
                    productJson.put("ProductName",cursor.getString(1));
                    productJson.put("Added_Date",cursor.getString(2));
                    productJson.put("tax_name",cursor.getString(3));
                    productJson.put("tax_val",cursor.getString(4));
                    productJson.put("ProductVarId",productVarId);
                    productJson.put("color",cursor.getString(6));
                    productJson.put("size",cursor.getString(7));
                    productJson.put("price",cursor.getString(8));
                    productArray.put(productJson);
                    Log.d("SQLiteDatabaseManager","getAllProducts productJson:: "+productJson);

                    viewedCount = getViewedCount(productVarId);

                    orderedCount = getOrderedCount(productVarId);

                    sharedCount = getSharedCount(productVarId);

                    productJson.put("viewedCount",viewedCount);
                    productJson.put("orderedCount",orderedCount);
                    productJson.put("sharedCount",sharedCount);



                } while(cursor.moveToNext());
            }
            Log.d("SQLiteDatabaseManager","getAllProducts productArray:: "+productArray);
            cursor.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return productArray;
    }
}
