package com.hammerandtongues.online.hntonline; /**
 * Created by Calvin on 07 Jun,2016.
 */
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class myDBHandler extends SQLiteOpenHelper {

    JSONParser jsonParser = new JSONParser();

    private static final String GETCATEGORIES_URL = "https://devshop.hammerandtongues.com/webservice/getcategories.php";
    private static final String GETPRODUCTS_URL = "https://devshop.hammerandtongues.com/webservice/getallproducts.php";
    private static final String GETSTORES_URL = "https://devshop.hammerandtongues.com/webservice/getstores.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_CATEGORIES = "posts";

    //Global Variables
    public String GET_TYPE ;
    public String Categories ;
    public String Products ;
    public String Stores;
    private ProgressBar pDialog;


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hntonlinemall.db";
    private static String DB_PATH = "/data/data/com.hammerandtongues.online.hntonline/databases/";
    private static String DB_NAME = "hntonlinemall.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    private static final String TABLE_PRODUCTS = "tbl_Cart";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE_CREATED = "date_created";
    public static final String COLUMN_STATUS = "status";

    //These are variables for the database table names
    public static final String TABLE_CARTVALUES = "tbl_CartValues";
    public static final String TABLE_FAVORITES="tbl_favorites";
    public static final String TABLE_DELIVERY="tbl_delivery";
    public static final String TABLE_TRANSACTION="tbl_Transactions";
    public static final String TABLE_CATEGORIES="tbl_Categories";
    public static final String TABLE_PRODUCT_LIST="tbl_Products";
    public static final String TABLE_STORES="tbl_Stores";


    // These are table creation statements
    //tbl_cart
    private static final String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
            TABLE_PRODUCTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_DATE_CREATED
            + " DATETIME DEFAULT (CURRENT_TIMESTAMP)," + COLUMN_STATUS + " INTEGER" + ")";

    //tbl_Categories table
    private  static  final String CREATE_CATEGORIES = "CREATE TABLE tbl_Categories (id INTEGER PRIMARY KEY, post_id INTEGER UNIQUE, category_name VARCHAR (100, 0),  category_description VARCHAR (250, 0) )";
    //tbl_Categories table
    private  static  final String CREATE_PRODUCTS = "CREATE TABLE tbl_Products (id INTEGER PRIMARY KEY,  post_id INTEGER, category_id INTEGER, product_name VARCHAR (500, 0),  product_id VARCHAR (50, 0), in_stock INTEGER, unit_price DOUBLE (5, 2), total_price DOUBLE (5, 2), vat DOUBLE (5, 5),  img_url VARCHAR (500, 0), storeid INTEGER)";
    // tbl_cart_values table
    private  static  final String CREATE_CART_VALUES = "CREATE TABLE tbl_CartValues (id INTEGER PRIMARY KEY, cart_id INTEGER, date_Created DATETIME DEFAULT (CURRENT_TIMESTAMP), quantity INTEGER, unit_price DOUBLE (5, 2), total_price DOUBLE (5, 2), vat DOUBLE (5, 5), product_name VARCHAR (500, 0),  product_id VARCHAR (50, 0),  img_url VARCHAR (500, 0))";
    //tbl_favorites
        private  static  final  String CREATE_USER_FAVORITES = "CREATE TABLE tbl_favorites (id INTEGER PRIMARY KEY, type_id INTEGER, date_created DATETIME DEFAULT (CURRENT_TIMESTAMP), type VARCHAR (500, 0), value_type VARCHAR (500, 0))";
    //tbl_Delivery
    private static final String CREATE_DELIVERY_DETAILS = "CREATE TABLE tbl_Delivery (id INTEGER PRIMARY KEY, date_created DATETIME DEFAULT (CURRENT_TIMESTAMP), status INTEGER, address_1 VARCHAR(500,0), address_2 VARCHAR(500,0), address_3 VARCHAR(500,0), address_4 VARCHAR(500,0), city VARCHAR(500,0))";
    //tbl_Transactions
    private static  final String CREATE_TRANSACTION_DETAILS = "CREATE TABLE tbl_Transactions (id INTEGER PRIMARY KEY, date_created DATETIME DEFAULT (CURRENT_TIMESTAMP), order_number VARCHAR(500,0), product_id INTEGER, quantity INTEGER, unit_price DOUBLE(5,2))";
    //tbl_Stores table
    private  static  final String CREATE_STORES = "CREATE TABLE tbl_Stores (id INTEGER PRIMARY KEY, post_id INTEGER UNIQUE, store_name VARCHAR (100, 0),  store_description VARCHAR (500, 0), img_url VARCHAR (500, 0), banner_url VARCHAR (500, 0), is_promo BIT, seller INTEGER )";

    // Here we are creating the database
 /*   public myDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
*/
    public myDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }
    public void fill() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
        copyDataBase();

    }

    catch (Exception e){
        Log.e("onCreate: ","Failed to copy database" );
    }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    /*
    @Override
    public void onCreate(SQLiteDatabase db){

        //Now we create the tables for the database
        try {
            db.execSQL(CREATE_PRODUCTS_TABLE);
            db.execSQL(CREATE_CART_VALUES);
            db.execSQL(CREATE_USER_FAVORITES);
            db.execSQL(CREATE_DELIVERY_DETAILS);
            db.execSQL(CREATE_TRANSACTION_DETAILS);
            db.execSQL(CREATE_CATEGORIES);
            db.execSQL(CREATE_PRODUCTS);
            db.execSQL(CREATE_STORES);
            System.gc();
        }

        catch (Exception ex)
        {
            Log.e("Memory exceptions", "Error on saving cart and cart values" + ex);
        }

        //String CREATE_CART_ITEMS_TABLE = "CREATE TABLE tbl_CartValues (id INTEGER PRIMARY KEY AUTOINCREMENT, cartid INTEGER, productid INTEGER, dateCreated DATETIME DEFAULT (CURRENT_TIMESTAMP), quantity INTEGER, unit_price DOUBLE (5, 2), totalprice DOUBLE (5, 2), vat DOUBLE (5, 5), productname VARCHAR (500, 0))";

    }
    @Override
    public void  onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTVALUES);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELIVERY);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_LIST);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }
*/
    // Here we are saving the cart
    public void addCart(CartManagement product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, product.get_CartStatusID());
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }
    // Here we are saving cart items to cart
    public void addCartItem(CartManagement product) {

        ContentValues values = new ContentValues();
        values.put("cart_id", product.get_CartID()); //product.get_CartID()
        values.put("quantity",product.get_quantity());
        values.put("unit_price",product.get_unitprice());
        values.put("total_price",product.get_totalprice());
        values.put("vat",product.get_vat());
        values.put("product_name",product.get_product_name());
        values.put("img_url",product.get_ImgURL());
        values.put("product_id", product.get_CartProductID());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CARTVALUES, null, values);
        db.close();
    }

    public void addFavoriteItem(CartManagement product){
        ContentValues values = new ContentValues();
        values.put("type_id", product.get_type_id());
        values.put("type",product.get_type());
        values.put("value_type",product.get_value_type());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    public void addDeliveryOptions(CartManagement product){
        ContentValues values = new ContentValues();
        values.put("status", product.get_Delivery_status());
        values.put("address_1",product.get_Address_1());
        values.put("address_2",product.get_Address_2());
        values.put("address_3",product.get_Address_3());
        values.put("address_4",product.get_Address_4());
        values.put("city",product.get_City());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_DELIVERY, null, values);
        db.close();
    }

    public CartManagement getCartItemsCount(int Cartid){
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
            db.close();
            return cart;
        }
        catch (Exception ex){
            return null;
        }

    }

    public CartManagement findCart() {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select * from tbl_Cart order by _id desc limit 1 ";
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
        db.close();
        return cart;
    }


    public Cursor cartItems(int CartID) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select c.*, s.post_id, s.store_name, s.seller from tbl_CartValues c left outer join  tbl_Products p on c.product_id = p.product_id left outer join tbl_Stores s on p.storeid = s.seller  where cart_id=" + CartID;
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

    public Cursor getCategories() {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select distinct * from " + TABLE_CATEGORIES ;
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

    public Cursor getStores() {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + sta'tusid + "\"";
        String mod_query = "select distinct s.* from " + TABLE_STORES + " s inner join " + TABLE_PRODUCT_LIST  + " p on s.seller = p.storeid Where s.img_url like '%png%'  Order By s.store_name asc" ;
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
        String mod_query = "select distinct * from " + TABLE_STORES + " where post_id in (61440,59891,19084)" ;
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
    public Cursor getProducts(int CategoryID, int limit, int offset) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query;
        if (CategoryID != 99990 &&  CategoryID != 99999 && CategoryID != 99998 && CategoryID != 99997 && CategoryID != 9999 && CategoryID != 0000)  {
             mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where category_id=" + CategoryID + " and img_url like '%.png%'  or img_url like '%.bmp%' ORDER BY RANDOM() LIMIT " + limit + " Offset " + offset;
        }
        else if (CategoryID == 9999){
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where  unit_price like '%AUCTION%' and (img_url like '%.png%'  or img_url like '%.bmp%')  ORDER BY id desc LIMIT  " + limit + " Offset " + offset;;
        }
        else if (CategoryID == 0000){
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where ( product_name like '%beer%' or product_name like '%whiskey%' or product_name like '%brandy%' or product_name like '%cider%') and (img_url like '%.png%'  or img_url like '%.bmp%')  ORDER BY id desc LIMIT " + limit + " Offset " + offset;
        }
        else if (CategoryID == 99999) {
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where (img_url like '%.png%'  or img_url like '%.bmp%')  ORDER BY id desc LIMIT  " + limit + " Offset " + offset;
        }
        else if (CategoryID == 99998) {
            mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where (img_url like '%.png%'  or img_url like '%.bmp%') ORDER BY RANDOM() LIMIT  " + limit + " Offset " + offset;
        }else{
             mod_query = "select distinct * from " + TABLE_PRODUCT_LIST + " where img_url like '%.png%'  and (img_url like '%.bmp%') ORDER BY RANDOM() LIMIT  " + limit + " Offset " + offset;
        }
        Log.e("MY DB-HANDLER",mod_query);
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

    public Cursor getStoreProducts(int StoreID, int limit, int offset) {
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_STATUS + " =  \"" + statusid + "\"";
        String mod_query = "select distinct   * from " + TABLE_PRODUCT_LIST + " p inner join " + TABLE_STORES + " s on p.storeid = s.seller  where s.post_id=" + StoreID + " LIMIT  " + limit + " Offset " + offset ;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS",mod_query);
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
        String mod_query = "select distinct  banner_url from " +   TABLE_STORES + " s  where s.post_id=" + StoreID + " Limit 1" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS",mod_query);
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
          String mod_query = "select unit_price from " + TABLE_PRODUCT_LIST + " where post_id=" + ProductID+ " Limit 1" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS",mod_query);
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
         String mod_query = "select product_name from " + TABLE_PRODUCT_LIST + " where post_id=" + ProductID+ " Limit 1" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS",mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();
            return  cursor.getString(0).toString();
        } else {
            db.close();
            return null;
        }
    }
    public String getProductImg(int ProductID) {
        String mod_query = "select img_url from " + TABLE_PRODUCT_LIST + " where post_id=" + ProductID+ " Limit 1" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("GETSTOREPRODUCTS",mod_query);
        Cursor cursor = db.rawQuery(mod_query, null);

        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();
            return  cursor.getString(0).toString();
        } else {
            db.close();
            return null;
        }
    }

    public  void removeCartItem(Integer ID_CART) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARTVALUES, "product_id" + " = ?", new String[]{String.valueOf(ID_CART)});
    }

    public  void clearCartItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARTVALUES, "1",null);
    }


    public  void clearCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, "1",null);
    }

    public  void clearStores() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STORES, "1",null);
    }

    public  void clearProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT_LIST, "1",null);
    }



    public void fill_categories ()
        {


        if (Categories !=null) {
            Log.d("Categories JSON", "Testing" + Categories);
            JSONArray jsonarray = null;
            try {
                jsonarray = new JSONArray(Categories);
            } catch (JSONException e) {
                Log.e("JSON Error: ", e.toString());
                e.printStackTrace();
            }
            if (jsonarray != null) {
                for (int k = 0; k < jsonarray.length(); k++) {
                    try {
                        JSONObject jsonobject = jsonarray.getJSONObject(k);

                        ContentValues values = new ContentValues();
                        values.put("post_id", jsonobject.getString("post_id"));
                        values.put("category_name",jsonobject.getString("name"));
                        //values.put("category_description",jsonobject.getString("slug"));

                        SQLiteDatabase db = this.getWritableDatabase();
                        db.insertWithOnConflict(TABLE_CATEGORIES, null, values, SQLiteDatabase.CONFLICT_FAIL);
                        db.close();
                    } catch (Exception ex) {
                        Log.e("Fill Categories Error", ex.toString());
                    }

                }
                try {
                    GET_TYPE =  "CATEGORY";
                    new GetCategories().execute().get();
                } catch (InterruptedException e) {
                    Log.e("Pakare Paye 01", e.toString());
                } catch (ExecutionException e) {
                    Log.e("Pakare Paye 02", e.toString());
                }
            }
        }
                    }

    public void fill_products()
    {
        //json = jsonParser.makeHttpRequest(

                //GETPRODUCTS_URL, "REQUEST", values);
        //Log.e("request!", "ending Get Products" + json.toString());
     if (TAG_CATEGORIES !=null) {
            Log.e("Products JSON", "Testing" + Products);
            JSONArray jsonarray = null;
            try {
                jsonarray = new JSONArray(TAG_CATEGORIES );
            } catch (JSONException e) {
                Log.e("JSON Error: ", e.toString());
                e.printStackTrace();
            }
            if (jsonarray != null) {
                for (int k = 0; k < jsonarray.length(); k++) {
                    try {
                        JSONObject jsonobject = jsonarray.getJSONObject(k);

                        ContentValues values = new ContentValues();

                        values.put("post_id", jsonobject.getString("post_id"));
                        values.put("category_id",jsonobject.getString("category_id"));
                        values.put("product_name",jsonobject.getString("product_name"));
                        values.put("product_id", jsonobject.getString("post_id"));
                        values.put("in_stock","");
                        values.put("unit_price",jsonobject.getString("price"));
                        values.put("total_price",jsonobject.getString("price"));
                        values.put("vat", 0);
                        values.put("img_url",jsonobject.getString("imgurl"));
                        values.put("storeid",jsonobject.getString("store_id"));
                        SQLiteDatabase db = this.getWritableDatabase();
                        db.insert(TABLE_PRODUCT_LIST, null, values);
                        db.close();



                    } catch (Exception ex) {
                        Log.e("Fill Products Error", ex.toString());
                    }

                }

                try {
                    GET_TYPE =  "PRODUCT";
                    new GetCategories().execute().get();
                } catch (InterruptedException e) {
                    Log.e("Pakare Paye 01", e.toString());
                } catch (ExecutionException e) {
                    Log.e("Pakare Paye 02", e.toString());
                }

            }
        }

    }

    public void fill_stores()
    {

        if (Stores !=null) {
            Log.d("DBHandler Get Stores", "Testing" + Stores);
            JSONArray jsonarray = null;
            try {
                jsonarray = new JSONArray(GETSTORES_URL);
            } catch (JSONException e) {
                Log.e("JSON Error: ", e.toString());
                e.printStackTrace();
            }
            if (jsonarray != null) {
                for (int k = 0; k < jsonarray.length(); k++) {
                    try {
                        JSONObject jsonobject = jsonarray.getJSONObject(k);

                        ContentValues values = new ContentValues();

                        values.put("post_id", jsonobject.getString("post_id"));
                        values.put("store_name",jsonobject.getString("name"));
                        values.put("store_description",jsonobject.getString("desc"));
                        values.put("img_url",jsonobject.getString("imgurl"));
                        values.put("seller",jsonobject.getString("seller"));
                        values.put("banner_url",jsonobject.getString("banner"));
                        SQLiteDatabase db = this.getWritableDatabase();
                        db.insert(TABLE_STORES,null,values);
                        db.insertWithOnConflict(TABLE_STORES, null, values, SQLiteDatabase.CONFLICT_FAIL);
                        db.close();
                    } catch (Exception ex) {
                        Log.e("Fill Stores Error", ex.toString());
                    }

                }
                try {
                    GET_TYPE =  "STORE";
                    Products = new GetCategories().execute().get();
                } catch (InterruptedException e) {
                    Log.e("Pakare Paye 01", e.toString());
                } catch (ExecutionException e) {
                    Log.e("Pakare Paye 02", e.toString());
                }
            }
        }

    }


    private ProgressDialog progress;
    public  void setProgressBar(ProgressDialog bar){
        this.progress = bar;
    }


    class GetCategories extends AsyncTask<Void, Void, String> {
    /**
     * Before starting background thread Show Progress Dialog
     * */



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress.setMessage("Loading data, this may take a while, Please Wait...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
    }

    @Override
    protected String doInBackground(Void... args) {
        // TODO Auto-generated method stub
        // Check for success tag

        int success;

        // Building Parameters
        JSONObject json = new JSONObject();
        ContentValues values=new ContentValues();
        if (GET_TYPE ==  "CATEGORY") {
            Log.d("request!", "starting Get Categories");
            // getting Categories by making HTTP request
            json = jsonParser.makeHttpRequest(GETCATEGORIES_URL, "posts", values);
        } else  if (GET_TYPE ==  "PRODUCT") {
            // getting product details by making HTTP request
            json = jsonParser.makeHttpRequest(
                    GETPRODUCTS_URL, "REQUEST", values);
            Log.e("request!", "ending Get Products" + json.toString());

        } else  if (GET_TYPE ==  "STORE") {
            // getting product details by making HTTP request
            json = jsonParser.makeHttpRequest(
                    GETSTORES_URL, "posts", values);
            Log.e("request!", "ending Get Stores" + json.toString());
        }


        if (json != null)    // check your log for json response
        {
            Log.d("DBHandler Login attempt", json.toString());

            // json success tag
            success = Integer.parseInt(json.optString(TAG_SUCCESS));
            if (success == 1) {
                Log.d("GET Successful!", json.optString(TAG_CATEGORIES));
                Categories = json.optString(TAG_CATEGORIES);
                return json.optString(TAG_CATEGORIES);
            } else {
                Log.d("Login Failure!", json.optString(TAG_MESSAGE));
                return json.optString(TAG_CATEGORIES);
            }
        }
        else
        {
            Log.d("DBHandler JSON Failure!", "Empty String Baba" + GET_TYPE);
        }


        return "No Data";

    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String posts) {
        //dismiss the dialog once asynctask  complete

//pDialog.setVisibility(View.GONE);
        if (posts != null) {
            if (GET_TYPE=="CATEGORY") {
                fill_categories();
            }
            else if (GET_TYPE=="PRODUCT")
            {
                fill_products();
            }
            else if (GET_TYPE=="STORE")
            {
                fill_stores();
            }
            else
            {
                Log.e("DBHandler","Invalid GET_TYPE");
            }
            Log.e("DB Handler Async Task", "Success");

        }

    }

}

}

