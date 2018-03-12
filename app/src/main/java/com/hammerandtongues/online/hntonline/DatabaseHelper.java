package com.hammerandtongues.online.hntonline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import static com.hammerandtongues.online.hntonline.JSONParser.json;

/**
 * Created by Ruvimbo on 29/11/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    JSONParser jsonParser = new JSONParser();
    JSONObject json=null;

    private static final String GETCATEGORIES_URL = "https://devshop.hammerandtongues.com/webservice/getcategories.php";
    private static final String GETPRODUCTS_URL = "https://devshop.hammerandtongues.com/webservice/getallproducts.php";
    private static final String GETSTORES_URL = "https://devshop.hammerandtongues.com/webservice/getstores.php";
    private static final String LOGIN_URL = "https://devshop.hammerandtongues.com/webservice/login.php";
    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_CATEGORIES = "posts";
    private Context context;

    //Global Variables
    public String GET_TYPE;
    public String Categories;
    public String Products;
    public String Stores;
    private ProgressBar pDialog;


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hntonlinemall.db";
    //private static String DB_PATH = "/data/data/com.hammerandtongues.online.hntonline/databases/";
    //private static String DB_NAME = "hntonlinemall.db";
    private SQLiteDatabase myDataBase;
    //private final Context myContext;


    //THIS IS THE CART TABLE AND IT'S COLUMNS
    private static final String TABLE_CART = "tbl_Cart";
    public static final String COLUMN_CRTID = "id";
    public static final String COLUMN_CRTSTATUS = "status";
    public static final String COLUMN_CRTDATE = "date_created";

    //THIS IS THE CARTVALUES TABLE AND IT'S COLUMNS
    public static final String TABLE_CARTVALUES = "tbl_CartValues";
    public static final String COLUMN_CRTVID = "id";
    public static final String COLUMN_CRTVCID = "cart_id";
    public static final String COLUMN_CRTVDATE = "date_Created";
    public static final String COLUMN_CRTVQTY = "quantity";
    public static final String COLUMN_CRTVUP = "unit_price";
    public static final String COLUMN_CRTVTP = "total_price";
    public static final String COLUMN_CRTVVAT = "vat";
    public static final String COLUMN_CRTVPN = "product_name";
    public static final String COLUMN_CRTVPID = "product_id";
    public static final String COLUMN_CRTVIMG = "img_url";


    //THIS IS THE FAVOURITES TABLE AND IT'S COLUMNS
    public static final String TABLE_FAVORITES = "tbl_favorites";
    public static final String COLUMN_FAVID = "id";
    public static final String COLUMN_FAVTYPEID = "type_id";
    public static final String COLUMN_FAVDATE = "date_Created";
    public static final String COLUMN_FAVTYPE = "type";
    public static final String COLUMN_FAVVTYPE = "value_type";


    //THIS IS THE DELIVERY TABLE AND IT'S COLUMNS
    public static final String TABLE_DELIVERY = "tbl_Delivery";
    public static final String COLUMN_DLID = "id";
    public static final String COLUMN_DLDATE = "date_Created";
    public static final String COLUMN_DLSTATUS = "status";
    public static final String COLUMN_DLADD1 = "address_1";
    public static final String COLUMN_DLADD2 = "address_2";
    public static final String COLUMN_DLADD3 = "address_3";
    public static final String COLUMN_DLADD4 = "address_4";
    public static final String COLUMN_DLCITY = "city";


    //THIS IS THE TRANSACTION TABLE AND IT'S COLUMNS
    public static final String TABLE_TRANSACTION = "tbl_Transactions";
    public static final String COLUMN_TRANSID = "id";
    public static final String COLUMN_TRANSDATE = "date_Created";
    public static final String COLUMN_TRANSORDNUM = "order_number";
    public static final String COLUMN_TRANSPRID = "product_id";
    public static final String COLUMN_TRANSQTY = "quantity";
    public static final String COLUMN_TRANSUP = "unit_price";


    //THIS IS THE CATEGORIES TABLE AND IT'S COLUMNS
    public static final String TABLE_CATEGORIES = "tbl_Categories";
    public static final String COLUMN_CTGID = "id";
    public static final String COLUMN_CTGPID = "post_id";
    public static final String COLUMN_CTGNAME = "category_name";
    public static final String COLUMN_CTGDES = "category_description";

    //THIS IS THE PRODUCT TABLE AND IT'S COLUMNS
    public static final String TABLE_PRODUCT_LIST = "tbl_Products";
    public static final String COLUMN_PRID = "id";
    public static final String COLUMN_PRPID = "post_id";
    public static final String COLUMN_PRCTGID = "category_id";
    public static final String COLUMN_PRNAME = "product_name";
    public static final String COLUMN_PRPRID = "product_id";
    public static final String COLUMN_PRSTOCK = "in_stock";
    public static final String COLUMN_PRUP = "unit_price";
    public static final String COLUMN_PRTP = "total_price";
    public static final String COLUMN_PRVAT = "vat";
    public static final String COLUMN_PRIMG = "img_url";
    public static final String COLUMN_PRSTORE = "storeid";


    //THIS IS THE STORES TABLE AND IT'S COLUMNS
    public static final String TABLE_STORES = "tbl_Stores";
    public static final String COLUMN_STID = "id";
    public static final String COLUMN_STPID = "post_id";
    public static final String COLUMN_STNAME = "store_name";
    public static final String COLUMN_STDES = "store_description";
    public static final String COLUMN_STIMG = "img_url";
    public static final String COLUMN_STBANNER = "banner_url";
    public static final String COLUMN_STPROMO = "is_promo";
    public static final String COLUMN_STSELLER = "seller";


    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        //CREATING TABLE_CART TABLE AND IT'S COLUMNS
        String CREATE_TABLE_PRODUCTS = "CREATE TABLE `tbl_Cart` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`date_created`\tDATETIME DEFAULT (CURRENT_TIMESTAMP),\n" +
                "\t`status`\tINTEGER,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";
        db.execSQL(CREATE_TABLE_PRODUCTS);


//CREATING TABLE_CARTVALUES TABLE AND IT'S COLUMNS
        String CREATE_TABLE_CARTVALUES = "CREATE TABLE `tbl_CartValues` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`cart_id`\tINTEGER,\n" +
                "\t`date_Created`\tDATETIME DEFAULT (CURRENT_TIMESTAMP),\n" +
                "\t`quantity`\tINTEGER,\n" +
                "\t`unit_price`\tDOUBLE ( 5 , 2 ),\n" +
                "\t`total_price`\tDOUBLE ( 5 , 2 ),\n" +
                "\t`vat`\tDOUBLE ( 5 , 5 ),\n" +
                "\t`product_name`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`product_id`\tVARCHAR ( 50 , 0 ),\n" +
                "\t`img_url`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`seller`\tVARCHAR ( 500 , 0 ),\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";
        db.execSQL(CREATE_TABLE_CARTVALUES);


//CREATING TABLE_FAVORITES TABLE AND IT'S COLUMNS
        String CREATE_TABLE_FAVORITES = "CREATE TABLE `tbl_favorites` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`type_id`\tINTEGER,\n" +
                "\t`date_created`\tDATETIME DEFAULT (CURRENT_TIMESTAMP),\n" +
                "\t`type`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`value_type`\tVARCHAR ( 500 , 0 ),\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";
        db.execSQL(CREATE_TABLE_FAVORITES);


//CREATING TABLE_DELIVERY TABLE AND IT'S COLUMNS
        String CREATE_TABLE_DELIVERY = "CREATE TABLE `tbl_Delivery` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`date_created`\tDATETIME DEFAULT (CURRENT_TIMESTAMP),\n" +
                "\t`status`\tINTEGER,\n" +
                "\t`address_1`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`address_2`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`address_3`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`address_4`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`city`\tVARCHAR ( 500 , 0 ),\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";
        db.execSQL(CREATE_TABLE_DELIVERY);


//CREATING TABLE_TRANSACTION TABLE AND IT'S COLUMNS
        String CREATE_TABLE_TRANSACTION = "CREATE TABLE `tbl_Transactions` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`date_created`\tDATETIME DEFAULT (CURRENT_TIMESTAMP),\n" +
                "\t`order_number`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`product_id`\tINTEGER,\n" +
                "\t`quantity`\tINTEGER,\n" +
                "\t`unit_price`\tDOUBLE ( 5 , 2 ),\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";
        db.execSQL(CREATE_TABLE_TRANSACTION);


//CREATING TABLE_CATEGORIES TABLE AND IT'S COLUMNS
        String CREATE_TABLE_CATEGORIES = "CREATE TABLE `tbl_Categories` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`post_id`\tINTEGER UNIQUE,\n" +
                "\t`category_name`\tVARCHAR ( 100 , 0 ),\n" +
                "\t`category_description`\tVARCHAR ( 250 , 0 ),\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";
        db.execSQL(CREATE_TABLE_CATEGORIES);


//CREATING TABLE_PRODUCT_LIST TABLE AND IT'S COLUMNS
        String CREATE_TABLE_PRODUCT_LIST = "CREATE TABLE `tbl_Products` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`post_id`\tINTEGER UNIQUE,\n" +
                "\t`category_id`\tINTEGER,\n" +
                "\t`product_name`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`product_id`\tVARCHAR ( 50 , 0 ),\n" +
                "\t`in_stock`\tINTEGER,\n" +
                "\t`unit_price`\tDOUBLE ( 5 , 2 ),\n" +
                "\t`total_price`\tDOUBLE ( 5 , 2 ),\n" +
                "\t`vat`\tDOUBLE ( 5 , 5 ),\n" +
                "\t`img_url`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`storeid`\tINTEGER,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";
        db.execSQL(CREATE_TABLE_PRODUCT_LIST);


//CREATING TABLE_STORES TABLE AND IT'S COLUMNS
        String CREATE_TABLE_STORES = "CREATE TABLE `tbl_Stores` (\n" +
                "\t`id`\tINTEGER,\n" +
                "\t`post_id`\tINTEGER UNIQUE,\n" +
                "\t`store_name`\tVARCHAR ( 100 , 0 ),\n" +
                "\t`img_url`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`banner_url`\tVARCHAR ( 500 , 0 ),\n" +
                "\t`is_promo`\tBIT,\n" +
                "\t`seller`\tINTEGER,\n" +
                "\t`openning_days`\tVARCHAR ( 500 , 0 ), \n" +
                "\t`store_open`\tVARCHAR ( 500 , 0 ), \n" +
                "\t`store_close`\tVARCHAR ( 500 , 0 ), \n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");";
        db.execSQL(CREATE_TABLE_STORES);

        //fill_products();
    }


    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE_PRODUCTS = "DROP TABLE IF EXISTS" + TABLE_CART;
        db.execSQL(DROP_TABLE_PRODUCTS);

        String DROP_TABLE_CARTVALUES = "DROP TABLE IF EXISTS" + TABLE_CARTVALUES;
        db.execSQL(DROP_TABLE_CARTVALUES);

        String DROP_TABLE_FAVORITES = "DROP TABLE IF EXISTS" + TABLE_FAVORITES;
        db.execSQL(DROP_TABLE_FAVORITES);

        String DROP_TABLE_DELIVERY = "DROP TABLE IF EXISTS" + TABLE_DELIVERY;
        db.execSQL(DROP_TABLE_DELIVERY);

        String DROP_TABLE_TRANSACTION = "DROP TABLE IF EXISTS" + TABLE_TRANSACTION;
        db.execSQL(DROP_TABLE_TRANSACTION);

        String DROP_TABLE_CATEGORIES = "DROP TABLE IF EXISTS" + TABLE_CATEGORIES;
        db.execSQL(DROP_TABLE_CATEGORIES);

        String DROP_TABLE_PRODUCT_LIST = "DROP TABLE IF EXISTS" + TABLE_PRODUCT_LIST;
        db.execSQL(DROP_TABLE_PRODUCT_LIST);

        String DROP_TABLE_STORES = "DROP TABLE IF EXISTS" + TABLE_STORES;
        db.execSQL(DROP_TABLE_STORES);

        onCreate(db);
    }

    public void addCart(CartManagement product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CRTSTATUS, product.get_CartStatusID());
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CART, null, values);
        //db.close();
    }

    // Here we are saving cart items to cart
    public void addCartItem(CartManagement product) {

        ContentValues values = new ContentValues();
        values.put("cart_id", product.get_CartID()); //product.get_CartID()
        values.put("quantity", product.get_quantity());
        values.put("unit_price", product.get_unitprice());
        values.put("total_price", product.get_totalprice());
        values.put("vat", "0.0");
        values.put("product_name", product.get_product_name());
        values.put("img_url", product.get_ImgURL());
        values.put("product_id", product.get_CartProductID());
        values.put("seller", product.get_seller());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CARTVALUES, null, values);
        //db.close();
    }

    public void addFavoriteItem(CartManagement product) {
        ContentValues values = new ContentValues();
        values.put("type_id", product.get_type_id());
        values.put("type", product.get_type());
        values.put("value_type", product.get_value_type());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FAVORITES, null, values);
        //db.close();
    }

    public void addDeliveryOptions(CartManagement product) {
        ContentValues values = new ContentValues();
        values.put("status", product.get_Delivery_status());
        values.put("address_1", product.get_Address_1());
        values.put("address_2", product.get_Address_2());
        values.put("address_3", product.get_Address_3());
        values.put("address_4", product.get_Address_4());
        values.put("city", product.get_City());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_DELIVERY, null, values);
        //db.close();
    }

    public CartManagement getCartItemsCount(int Cartid) {
        try {
            String TABLE_QUERY = "tbl_CartValues";
            String COLUMN_QUERY = "CART_ID";
            String get_query = "Select distinct count(id), sum(unit_price) FROM " + TABLE_QUERY + " WHERE " + COLUMN_QUERY + " =  \"" + Cartid + "\"";
            SQLiteDatabase db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery(get_query, null);
            CartManagement cart = new CartManagement();
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                cart.set_Cart_Items_Count(Integer.parseInt(cursor.getString(0)));
                cart.set_Cart_Items_Value(Double.parseDouble(cursor.getString(1)));
                cursor.close();
            } else {
                cart = null;
            }
            //db.close();
            return cart;
        } catch (Exception ex) {
            return null;
        }

    }

    public CartManagement findCart() {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select * from tbl_Cart order by id desc limit 1 ";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(mod_query, null);
        CartManagement cart = new CartManagement();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            cart.set_CartID(Integer.parseInt(cursor.getString(0)));
            //cart.set_dateCreated(cursor.getString(1));
            //cart.set_CartStatusID(Integer.parseInt(cursor.getString((2))));
            cursor.close();
        } else {
            cart = null;
        }
        //db.close();
        return cart;
    }


    public Cursor cartItems(int CartID) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select distinct c.*, s.post_id, s.store_name, s.seller, s.openning_days, s.store_open, s.store_close from tbl_CartValues c left outer join  tbl_Products p on c.product_id = p.product_id inner join tbl_Stores s on p.storeid = s.seller  where cart_id=" + CartID;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            //db.close();
            return null;
        }

    }


    public void updatecart(String qtnty, String ProId){

        String UPDATE_TABLE_CART = "UPDATE tbl_CartValues SET quantity =" + qtnty + " WHERE product_id =" + ProId;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(UPDATE_TABLE_CART);

    }


    public Cursor getcartItems(int CartID) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select * from tbl_CartValues";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            //db.close();
            return null;
        }

    }




    public Cursor getCategories() {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select distinct * from " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();
            return cursor;
        } else {
            //db.close();
            return null;
        }

    }

    public Cursor getStores() {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + sta'tusid + "\"";
        String mod_query = "select distinct * from " + TABLE_STORES + " s inner join " + TABLE_PRODUCT_LIST + " p on s.seller = p.storeid Where s.img_url like '%png%'  Order By s.store_name asc";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }

    }


    public Cursor getallStores() {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + sta'tusid + "\"";
        String mod_query = "select distinct * from " + TABLE_STORES + " Order By store_name asc";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }

    }



    public Cursor getPromoStores() {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select distinct * from " + TABLE_STORES + " where is_promo = 1 ";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }

    }






    public Cursor getOneProduct(int ProductID) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select distinct p.*, s.post_id, s.store_name, s.seller, s.openning_days, s.store_open, s.store_close from  tbl_Products p inner join tbl_Stores s on p.storeid = s.seller  where product_id=" + ProductID + " Limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("GETONEPRODUCT", "Query yacho"+ mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {

            Log.e("GETONEPRODUCT", "No such product!!");
            db.close();
            return null;
        }

    }













    public Cursor getProducts(int CategoryID, int limit, int offset, String lastid) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query;
        if (CategoryID != 99990 && CategoryID != 99999 && CategoryID != 99998 && CategoryID != 99997 && CategoryID != 9999 && CategoryID != 0000) {
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where product_name NOT LIKE '%beer%' and product_name NOT LIKE '%whiskey%' and product_name NOT LIKE '%brandy%' and product_name NOT LIKE '%cider%' and product_name NOT LIKE '%wine%' and product_name NOT LIKE '%vodka%' and product_name NOT LIKE '%bear%' and product_name NOT LIKE '%infusions%' and product_name NOT LIKE '%rum%' and product_name NOT LIKE '%nederburg%' and product_name NOT LIKE '%vrede en lust%' and product_name NOT LIKE '%hercules paragon%' and product_name NOT LIKE '%whisky%' and product_name NOT LIKE '%castle lager%' and product_name NOT LIKE '%black label%' and category_id=" + CategoryID + " and id > " + lastid + " and img_url like '%.png%'  or img_url like '%.bmp%' ORDER BY id asc LIMIT " + limit + " Offset " + offset;
        } else if (CategoryID == 9999) {
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where  unit_price like '%AUCTION%'  and id > " + lastid + " and (img_url like '%.png%'  or img_url like '%.bmp%')  ORDER BY id asc LIMIT  " + limit + " Offset " + offset;
            ;
        } else if (CategoryID == 0000) {
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where ( product_name like '%beer%' or product_name like '%whiskey%' or product_name like '%brandy%' or product_name like '%cider%' or product_name like '%wine%' or product_name like '%vodka%' or product_name like '%bear%' or product_name like '%infusions%' or product_name like '%rum%' or product_name like '%nederburg%' or product_name like '%vrede en lust%' or product_name like '%hercules paragon%' or product_name like '%whisky%' or product_name like '%castle lager%' or product_name like '%black label%')  and id > " + lastid + " and (img_url like '%.png%'  or img_url like '%.bmp%')  ORDER BY id asc LIMIT " + limit + " Offset " + offset;
        } else if (CategoryID == 99999) {
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where (img_url like '%.png%'  or img_url like '%.bmp%')  and id > " + lastid + " ORDER BY id asc LIMIT  " + limit + " Offset " + offset;
        } else if (CategoryID == 99998) {
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where (img_url like '%.png%'  or img_url like '%.bmp%')  and id > " + lastid + " ORDER BY id asc LIMIT  " + limit + " Offset " + offset;
        } else {
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where img_url like '%.png%'  and (img_url like '%.bmp%')  and id > " + lastid + " ORDER BY id asc LIMIT  " + limit + " Offset " + offset;
        }
        Log.e("MY DB-HANDLER", mod_query);
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }
    }

    public Cursor getStoreProducts(int StoreID, int limit, int offset, String storefrom) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select distinct   p.*, s.* from " + TABLE_PRODUCT_LIST + " p left join " + TABLE_STORES + " s on p.storeid = s.seller  where p.id >" + storefrom + " and s.post_id=" + StoreID + " ORDER BY p.id asc LIMIT  " + limit + " Offset " + offset ;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("GETSTOREPRODUCTS", "" + mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }
    }

    public String getStoreBanner(int StoreID) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select distinct  banner_url from " + TABLE_STORES + " s  where s.post_id=" + StoreID + " Limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);
        String url;
        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();
            url = cursor.getString(0).toString();
            return url;
        } else {
            db.close();
            return null;
        }
    }

    public Double getProductPrice(int ProductID) {
        String mod_query = "select unit_price from " + TABLE_PRODUCT_LIST + " where post_id=" + ProductID + " Limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();
            return Double.parseDouble(cursor.getString(0).toString());
        } else {
            db.close();
            return null;
        }
    }

    public String getProductName(int ProductID) {
        String mod_query = "select product_name from " + TABLE_PRODUCT_LIST + " where post_id=" + ProductID + " Limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();
            return cursor.getString(0).toString();
        } else {
            db.close();
            return null;
        }
    }





    public String getProductImg(int ProductID) {
        String mod_query = "select img_url from " + TABLE_PRODUCT_LIST + " where post_id=" + ProductID + " Limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();
            return cursor.getString(0).toString();
        } else {
            db.close();
            return null;
        }
    }

    public void removeCartItem(Integer ID_CART) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARTVALUES, "product_id" + " = ?", new String[]{String.valueOf(ID_CART)});
    }

    public void fillcart(){
        String INSERT_TABLE_CART = "INSERT INTO tbl_Cart (id ,status) VALUES (16,0)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(INSERT_TABLE_CART);
    }

    public void fillfav(int ProductID){
        String INSERT_TABLE_FAV = "INSERT INTO tbl_favorites (type_id , type, value_type) VALUES (" + ProductID + ", 'Product', 'Big')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(INSERT_TABLE_FAV);
    }


    public Cursor getProductFav( String lastid) {
        String mod_query = "select DISTINCT c.* from  tbl_Products c  inner join tbl_favorites f where f.type_id=c.post_id and f.id  ORDER BY f.id asc";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREFAV", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }
    }





    public Cursor searchProduct( String searchString) {
        String mod_query = "select DISTINCT * from  tbl_Products where product_name LIKE '%" + searchString + "%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREFAV", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }
    }



    public Cursor searchStores( String letter) {
        String mod_query = "select DISTINCT * from  tbl_Stores where store_name LIKE '" + letter + "%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREFAV", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }
    }










    public Cursor viewFavs(String lastid) {
        String mod_query = "select DISTINCT * from  tbl_favorites  ORDER BY id asc ";
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREFAV", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }
    }

    public Cursor RemoveFavs(int ProductID ) {
        String mod_query = "delete from  tbl_favorites where type_id =" + ProductID ;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREFAV", mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();

            return cursor;
        } else {
            db.close();
            return null;
        }
    }


    public void clearCartItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARTVALUES, "1", null);
    }


    public void clearCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, "1", null);
    }

    public void clearStores() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STORES, "1", null);
    }

    public void clearProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT_LIST, "1", null);
    }


    public void fill_products(Context context){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(context);
       final SQLiteDatabase db = this.getWritableDatabase();
            //this.context= context;
        //pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GETPRODUCTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //pDialog.dismiss();

                Log.e("Success",""+s);
                Log.e("Zitapass", "" + s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonobject = new JSONObject(s);
                    int success = jsonobject.getInt("success");
                    if (success == 1) {


                        JSONArray array = jsonobject.getJSONArray("posts");

                        for (int i = 0; i < array.length(); i++) {
                            try {

                                JSONObject object = array.getJSONObject(i);

                                String post_id = object.optString("post_id");
                                String category_id = object.optString("category_id");
                                String product_name = object.optString("product_name");
                                String product_id = object.optString("post_id");
                                String unit_price = object.optString("price");
                                String total_price = object.optString("price");
                                String img_url = object.optString("imgurl");
                                String in_stock = object.optString("");
                                String vat = object.optString("0");
                                String storeid = object.optString("store_id");


                                ContentValues values = new ContentValues();
                                values.put("post_id", post_id);
                                values.put("category_id",category_id);
                                values.put("product_name",product_name);
                                values.put("product_id", product_id);
                                values.put("in_stock",in_stock);
                                values.put("unit_price",unit_price);
                                values.put("total_price",total_price);
                                values.put("vat", vat);
                                values.put("img_url",img_url);
                                values.put("storeid",storeid);
                                db.insert(TABLE_PRODUCT_LIST, null, values);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }
                    }

                    else{
                        Log.e("Success", "Not success" + s);
                        db.close();
                    }

                }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //pDialog.dismiss();
                        volleyError.printStackTrace();
                        Log.e("RUEERROR",""+volleyError);
                    }
                }){
                    //@Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> values=new HashMap();
                        // values.put("username",pword);
                        //values.put("password",uname);


                        return values;
                    }

                };

                //stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
                //requestQueue.add(stringRequest);
            }



    public void fill_stores(Context context){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(context);
        final SQLiteDatabase db = this.getWritableDatabase();
        //this.context= context;
        //pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GETSTORES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //pDialog.dismiss();

                Log.e("Success",""+s);
                Log.e("Zitapass", "" + s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonobject = new JSONObject(s);
                    int success = jsonobject.getInt("success");
                    if (success == 1) {


                        JSONArray array = jsonobject.getJSONArray("posts");

                        for (int i = 0; i < array.length(); i++) {
                            try {

                                JSONObject object = array.getJSONObject(i);

                                String post_id = object.optString("post_id");
                                String store_name = object.optString("name");
                                String img_url = object.optString("imgurl");
                                String seller = object.optString("seller");
                                String banner_url = object.optString("banner");
                                String openingdays = object.optString("shop_day");
                                String openhrs = object.optString("shop_open");
                                String closehrs = object.optString("shop_close");
                                String IsPromo = "0";

                                if ( openingdays != null && !openingdays.contentEquals("1") && !openingdays.contentEquals("null") && !openingdays.contentEquals("")) {

                                    IsPromo = "1";


                                }

                                if ( openhrs != null && !openhrs.contentEquals("null") && !openhrs.contentEquals("")) {

                                    IsPromo = "1";

                                }

                                ContentValues values = new ContentValues();
                                values.put("post_id", post_id);
                                values.put("store_name",store_name);
                                values.put("img_url", img_url);
                                values.put("seller",seller);
                                values.put("banner_url",banner_url);
                                values.put("is_promo", IsPromo);
                                values.put("openning_days", openingdays);
                                values.put("store_open", openhrs);
                                values.put("store_close", closehrs);
                                db.insert(TABLE_STORES, null, values);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }
                    }

                    else{
                        Log.e("Success", "Not success" + s);
                        db.close();
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //pDialog.dismiss();
                volleyError.printStackTrace();
                Log.e("RUEERROR",""+volleyError);
            }
        }){
            //@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                // values.put("username",pword);
                //values.put("password",uname);


                return values;
            }

        };

        //stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        //requestQueue.add(stringRequest);
    }


    public void fill_categories(Context context){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(context);
        final SQLiteDatabase db = this.getWritableDatabase();
        //this.context= context;
        //pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GETCATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //pDialog.dismiss();

                Log.e("Success",""+s);
                Log.e("Zitapass", "" + s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonobject = new JSONObject(s);
                    int success = jsonobject.getInt("success");
                    if (success == 1) {


                        JSONArray array = jsonobject.getJSONArray("posts");

                        for (int i = 0; i < array.length(); i++) {
                            try {

                                JSONObject object = array.getJSONObject(i);

                                String post_id = object.optString("post_id");
                                String category_name = object.optString("name");




                                ContentValues values = new ContentValues();
                                values.put("post_id", post_id);
                                values.put("category_name",category_name);
                                db.insert(TABLE_CATEGORIES, null, values);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }
                    }

                    else{
                        Log.e("Success", "Not success" + s);
                        db.close();
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //pDialog.dismiss();
                volleyError.printStackTrace();
                Log.e("RUEERROR",""+volleyError);
            }
        }){
            //@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                // values.put("username",pword);
                //values.put("password",uname);


                return values;
            }

        };

        //stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        //requestQueue.add(stringRequest);
    }






        }
