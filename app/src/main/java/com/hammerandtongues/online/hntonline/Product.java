package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by NgonidzaIshe on 3/6/2016.
 */
public class Product extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";

    private TextView prodName, productDesc, prodPrice;
    private static final String TAG = "CART Actions";
    // Progress Dialog
    private ProgressDialog pDialog;
    int limit, offset;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    String GET_TYPE = "";

    private static final String GETPRODUCT_URL = "https://devshop.hammerandtongues.com/webservice/getsingleproduct.php";
    private static final String GETPRODUCT_VARIATION = "https://devshop.hammerandtongues.com/webservice/getproductvariation.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PRODUCTDETAILS = "posts";

    String productID, name, price, post_id, qnty, desc, seller, shop_open_day,shop_open_hrs, shop_close_hrs,theseller, shopname,shopthename, imgurl = "";
    Product product = null;
    ImageView banner;
    TextView inStock;
    EditText txtquantity, totalprice;
    int cartid, currcart;
    LinearLayout layout, produtc;
    TextView noresult;
    private int cnt_cart;
    private TextView txtcartitems;
    SharedPreferences sharedpreferences;
    Calendar calendar = Calendar.getInstance();
    int daynumber =calendar.get(Calendar.DAY_OF_WEEK);
    int hourofday = calendar.get(Calendar.HOUR_OF_DAY);
    private String openhrs, day, closehrs, dayofweek, hourofhour;
    Context context;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Runtime.getRuntime().gc();
        try {
            try {

                super.onCreate(savedInstanceState);
                setContentView(R.layout.product);
                SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                noresult = (TextView) findViewById(R.id.noresult);
                productID = (shared.getString("idKey", ""));
                produtc = (LinearLayout) findViewById(R.id.product);
                GET_TYPE = "description";
                new GetConnectionStatus().execute();

                //if (haveNetworkConnection() == true && isNetworkAvailable() == true) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                productDesc = (TextView) findViewById(R.id.description);
                prodName = (TextView) findViewById(R.id.prdctname);
                prodPrice = (TextView) findViewById(R.id.prdctprice);
                banner = (ImageView) findViewById(R.id.product_banner);
                inStock = (TextView) findViewById(R.id.instock);

                if (shared.getString("CartID", "") != null && shared.getString("CartID", "") != "") {
                    currcart = Integer.parseInt(shared.getString("CartID", ""));
                } else {
                    currcart = 0;
                }
                //
                Map<String, ?> allEntries = shared.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d("map values 123", entry.getKey() + ": " + entry.getValue().toString());
                }

                txtquantity = (EditText) findViewById(R.id.txtQnty);
                //
                final TextView Qnty = (TextView) findViewById(R.id.txtQnty);
                Qnty.setText("1");




                Button add = (Button) findViewById(R.id.addQnty);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        addbtn (Qnty);
                        settotal();



                    }

                });

                Button minus = (Button) findViewById(R.id.removeQnty);
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        int CurrQnty = 1;
                        try {
                            if (Qnty.getText().toString() == "" || Qnty.getText().toString() == null) {
                                CurrQnty = 1;
                            } else {
                                CurrQnty = Integer.parseInt(Qnty.getText().toString());
                            }
                            CurrQnty -= 1;
                            if (CurrQnty < 1) {
                                CurrQnty = 1;
                            }

                            Qnty.setText(Integer.toString(CurrQnty));
                        } catch (Exception e) {
                            Log.e("Error  Quantity", e.toString());
                        }

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("storedquantity", Integer.toString(CurrQnty));
                        editor.apply();

                        settotal();
                    }

                });

                Button addtocart = (Button) findViewById(R.id.addToCart);
                addtocart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        try {
                            if (!qnty.isEmpty() && qnty != "0") {
                                addProduct(arg0);
                            } else if (qnty == "0") {

                                //Toast.makeText(Product.this, "Sorry, This Product is Out of Stock", Toast.LENGTH_LONG).show();

                                Toast ToastMessage = Toast.makeText(Product.this,"Sorry, This Product is Out of Stock!",Toast.LENGTH_LONG);
                                View toastView = ToastMessage.getView();
                                toastView.setBackgroundResource(R.drawable.toast_background);
                                ToastMessage.show();

                            } else {
                                addProduct(arg0);
                            }
                        } catch (Exception ex) {
                            Log.e("Add To Cart Error", ex.toString());
                        }

                    }
                });

                Button backtoshop = (Button) findViewById(R.id.btn_product_back);
                backtoshop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {


                        if (sharedpreferences.getString("Categorystart", "") != null && sharedpreferences.getString("Categorystart", "") != "" ) {


                            Log.e("Intent", "Starting store class " + sharedpreferences.getString("Categorystart", ""));
                            sharedpreferences.edit().remove("Categorystart").apply();

                            Intent intent = new Intent(Product.this, Store.class);
                            startActivity(intent);

                        } else {
                            Log.e("Intent", "Starting product class " + sharedpreferences.getString("Categorystart", ""));

                            sharedpreferences.edit().remove("Categorystart").apply();

                            Intent intent = new Intent(Product.this, Products_List.class);
                            startActivity(intent);



                        }




                    }
                });


                final LinearLayout btnhome, btncategs, btnsearch, btncart, btnprofile;

                btnhome = (LinearLayout) findViewById(R.id.btn_home);
                btnhome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Product.this, MainActivity.class);

                        startActivity(intent);
                    }
                });

                btncategs = (LinearLayout) findViewById(R.id.btn_Categories);
                btncategs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Product.this, CategoriesActivity.class);

                        startActivity(intent);
                    }
                });

                btncart = (LinearLayout) findViewById(R.id.btn_Cart);
                btncart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Product.this, Chat_Webview.class);

                        startActivity(intent);
                    }
                });

                btnprofile = (LinearLayout) findViewById(R.id.btn_Profile);
                btnprofile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Product.this, UserActivity.class);

                        startActivity(intent);
                    }
                });

                btnsearch = (LinearLayout) findViewById(R.id.btn_Search01);
                btnsearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Product.this, Search.class);

                        startActivity(intent);
                    }
                });

                ImageView imgsearch, imgcategories, imghome, imgcart, imgprofile;
                imgsearch = (ImageView) findViewById(R.id.imgsearch01);
                imgsearch.setImageResource(R.drawable.searchselected);

                imghome = (ImageView) findViewById(R.id.imghome);
                imghome.setImageResource(R.drawable.home);

                imgcategories = (ImageView) findViewById(R.id.imgcategs);
                imgcategories.setImageResource(R.drawable.categories);

                imgcart = (ImageView) findViewById(R.id.imgcart);
                imgcart.setImageResource(R.drawable.chat);

                imgprofile = (ImageView) findViewById(R.id.imgprofile);
                imgprofile.setImageResource(R.drawable.profile);

            } catch (Exception ex) {
                Log.e("Main Thread Exception", "Error: " + ex.toString());
                System.gc();
            }


        } catch (Exception ex) {
            Log.e("Main Thread Exception", "Error: " + ex.toString());
            System.gc();
        } catch (OutOfMemoryError ex) {
            Log.e("Main Thread Exception", "Error: " + ex.toString());
            System.gc();
        }
    }

    public void settotal(){

if (sharedpreferences.getString("storedprice", "") != "" && sharedpreferences.getString("storedprice", "") != null ) {

    String fprice = sharedpreferences.getString("storedprice", "");
    String fquantity = sharedpreferences.getString("storedquantity", "");


    Locale locale = new Locale("en", "US");
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

    Double SubTotal = Double.parseDouble(fprice) * Double.parseDouble(fquantity);

    totalprice = (EditText) findViewById(R.id.txttotal);
    totalprice.setText(currencyFormatter.format(SubTotal).toString());

    LinearLayout.LayoutParams btnsize = new LinearLayout.LayoutParams(200, 60);
    btnsize.setMargins(20, 0, 20, 0);
    btnsize.gravity = Gravity.RIGHT;

    Log.e("Calculatng subtotal", "quantity: " + fprice + " Price: " + fquantity + "Subtotal: " + SubTotal);
}


    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getVariation() {
        try {
            GET_TYPE = "variation";
            new GetProductDetails().execute();
        } catch (Exception ex) {
            Log.e("Product Variation Error", ex.toString());
        }
    }

    public void SetBanner(String uri) {

        try {

            Picasso.with(this).load(uri)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.offline)
                    .into(banner);

            Log.e("Setting Banner", uri);
        } catch (Exception ex) {
            Log.e("Error getting Banner", ex.toString());
        }
    }

    public void newCart(View view) {
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        Log.v(TAG, "Creating New Cart");
        DatabaseHelper dbHandler = new DatabaseHelper(this);

        int status = 1;
        String formattedDate = "07/11/2017";
        Log.v(TAG, "Variables taken");
        CartManagement cartManagement =
                new CartManagement(status, formattedDate);

        dbHandler.addCart(cartManagement);
        Log.v(TAG, "now loading saved details after save");
        //   try
        //   {
        CartManagement cartno = dbHandler.findCart();
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("CartID", String.valueOf(cartno.get_CartID()));
        editor.commit();
        editor.apply();
        //Toast.makeText(this,"New Shopping Session Created"+String.valueOf(cartno.get_CartID()),Toast.LENGTH_LONG).show();

        currcart = cartno.get_CartID();

            /*
       catch (Exception ex)
        {
            Log.d("Problemo!", "Error while retrieving the saved cart id " + ex.getMessage());
        }
        */
    }


    public void addProduct(View view) {

            cartid = currcart;

       final DatabaseHelper db = new DatabaseHelper(this);
       final int qty = Integer.parseInt(txtquantity.getText().toString());
        final int id = Integer.parseInt(productID);
        double itmprice = Double.parseDouble(price);
        double subtot = qty * Double.parseDouble(price);
        //int newqty; = qty + dbqty;
        //if (sharedpreferences.getString("seller", "") != null && sharedpreferences.getString("seller", "") != "") {



       if (db.getcartproduct(id) != null) {

           Cursor cursor = db.getcartproduct(id);

           Log.e("Cursor Returns", "Current Cart=" + currcart + " , Cursor = " + DatabaseUtils.dumpCursorToString(cursor) );

           if (cursor != null &&  cursor.moveToFirst()) {

               String dbqtyst = cursor.getString(3);

               int dbqty = Integer.parseInt(dbqtyst);

               final int newqty = qty + dbqty;

               new AlertDialog.Builder(this)
                       .setTitle("Product already in Cart")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               db.updatecart(newqty, id);
                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               db.updatecart(qty, id);
                           }
                           })

                       .setMessage(Html.fromHtml("Select Yes to add quantity to exisiting quantity or No to save new quantity"))
                       .show();


               Locale locale = new Locale("en", "US");
               NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

               CartManagement crtItms = db.getCartItemsCount(cartid);
               if (crtItms != null) {



                   //DatabaseHelper db = new DatabaseHelper(this);
                   //CartManagement crtItms = db.getCartItemsCount(1);
                   cnt_cart = (crtItms.get_Cart_Items_Count());

                   cnt_cart = cnt_cart;


                   txtcartitems.setText(String.valueOf(cnt_cart));


                   Snackbar snackbar = Snackbar
                           .make(produtc, name + "Added to Cart with : " + String.valueOf((crtItms.get_Cart_Items_Count())) + " items Total amount so far: " + currencyFormatter.format(Double.parseDouble(String.valueOf(crtItms.get_Cart_Items_Value()))), Snackbar.LENGTH_LONG);

                   snackbar.setActionTextColor(Color.YELLOW);
                   snackbar.show();


               }
           }
       }

          else {
           int seller = Integer.parseInt(sharedpreferences.getString("seller", ""));

           Log.e("Notifications", "Picking up variables for saving" + String.valueOf((qty)) + " <= Quantity; " + String.valueOf(cartid) + "<=This is the current cart ID");

           CartManagement ctm = new CartManagement(cartid, name, qty, id, imgurl, subtot, itmprice, seller);
           Log.d("Notifications", "Saving the product to cart with a cart id");
           db.addCartItem(ctm);
           Log.d("Notifications", "Finished saving an item");



           Locale locale = new Locale("en", "US");
           NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

           CartManagement crtItms = db.getCartItemsCount(cartid);
           if (crtItms != null) {



               //DatabaseHelper db = new DatabaseHelper(this);
               //CartManagement crtItms = db.getCartItemsCount(1);
               cnt_cart = (crtItms.get_Cart_Items_Count());

               cnt_cart = cnt_cart;


               txtcartitems.setText(String.valueOf(cnt_cart));


               Snackbar snackbar = Snackbar
                       .make(produtc, name + "Added to Cart with : " + String.valueOf((crtItms.get_Cart_Items_Count())) + " items Total amount so far: " + currencyFormatter.format(Double.parseDouble(String.valueOf(crtItms.get_Cart_Items_Value()))), Snackbar.LENGTH_LONG);

               snackbar.setActionTextColor(Color.YELLOW);
               snackbar.show();


           }

       }



        }
    //}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem awesomeMenuItem = menu.findItem(R.id.cart_item);
        RelativeLayout cart_wrapper = (RelativeLayout) menu.findItem(R.id.cart_item).getActionView().findViewById(R.id.cartitemwrapper);
        //get cart items count, set notification item value

        DatabaseHelper db = new DatabaseHelper(this);
        CartManagement crtItms = db.getCartItemsCount(currcart);
        if (crtItms != null) {
            cnt_cart = (crtItms.get_Cart_Items_Count());
        } else {
            cnt_cart = 0;
        }

        txtcartitems = (TextView) cart_wrapper.findViewById(R.id.cartitems);
        txtcartitems.setText(String.valueOf(cnt_cart));

        View awesomeActionView = awesomeMenuItem.getActionView();
        awesomeActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(awesomeMenuItem);
            }
        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            Intent intent = new Intent(Product.this, Search.class);

            startActivity(intent);
            return true;
        }
        if (id == R.id.cart_item) {
            Intent intent = new Intent(Product.this, Cart.class);

            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ProductVariation {
        private String key;
        private String value;
        private double price;

        public String getKey
                () {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String val) {
            this.value = val;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Product.this);
            pDialog.setMessage("Getting Product Details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String pid = productID;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("productid", pid));
                JSONObject json1;
                if (GET_TYPE == "description") {
                    // getting product details by making HTTP request
                    json1 = jsonParser.makeHttpRequest(
                            GETPRODUCT_URL, "POST", params);
                    Log.e("PID + URL", productID + GETPRODUCT_VARIATION);
                } else// if ((GET_TYPE ="variance"))
                {
                    json1 = jsonParser.makeHttpRequest(
                            GETPRODUCT_VARIATION, "POST", params);
                    Log.e("PID + URL", productID + GETPRODUCT_VARIATION);

                }

                // json success tag
                if (json1 != null) {
                    success = json1.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        return json1.getString(TAG_PRODUCTDETAILS);
                    } else {
                        return json1.getString(TAG_MESSAGE);
                    }
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            getOneProduct();
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String posts) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (posts != null) {
                Log.e("JSONing", posts);
                if (GET_TYPE == "description") {
                    JSONArray jsonarray02 = null;
                    try {
                        jsonarray02 = new JSONArray(posts);
                    } catch (JSONException e) {
                    }
                    if (jsonarray02 != null) {
                        try {
                            Log.e("JSONing", jsonarray02.toString());
                            JSONObject jsonobject = jsonarray02.getJSONObject(0);
                            name = Html.fromHtml(jsonobject.optString("name")).toString();
                            //desc = Html.fromHtml(jsonobject.optString("content")).toString().trim();
                            imgurl = jsonobject.optString("imgurl");
                            price = jsonobject.optString("price");
                            qnty = jsonobject.optString("quantity");
                            post_id = jsonobject.optString("postid");
                            seller = jsonobject.optString("seller");



                            sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("seller", seller);
                            editor.putString("shopname", "");


                            editor.commit();



                        } catch (JSONException e) {
                            Log.e("JSON Error: ", e.toString());
                            e.printStackTrace();
                        }
                        productDesc.setText(desc);
                        prodName.setText(name);
                        prodName.setGravity(Gravity.CENTER);
                        prodPrice.setGravity(Gravity.CENTER);
                        inStock.setGravity(Gravity.CENTER);
                        prodPrice.setText("US $" + price);
                        if (qnty.isEmpty())
                            qnty = "0";
                        inStock.setText("Available Quantity: " + qnty + "item(s)");


                        SharedPreferences.Editor editorr = sharedpreferences.edit();
                        editorr.putString("storedprice", price);
                        editorr.putString("storedquantity", ("1"));
                        editorr.apply();


                        settotal();


                        LinearLayout.LayoutParams btnsize = new LinearLayout.LayoutParams(200, 60);
                        btnsize.setMargins(20, 0, 20, 0);
                        btnsize.gravity = Gravity.RIGHT;



                        SetBanner(imgurl);
                        getVariation();

                        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("seller", seller);
                        editor.commit();
                        editor.apply();


                    } else {
                        //Toast.makeText(Product.this, posts, Toast.LENGTH_LONG).show();

                        Toast ToastMessage = Toast.makeText(Product.this,posts,Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();

                    }
                } else if (GET_TYPE == "variation") {
                    // you can use this array to find the school ID based on name
                    ArrayList<ProductVariation> schools = new ArrayList<ProductVariation>();
                    // you can use this array to populate your spinner
                    ArrayList<String> Variations = new ArrayList<String>();

                    try {
                        JSONArray jsonArray = new JSONArray(posts);
                        Log.i(MainActivity.class.getName(),
                                "Number of entries " + jsonArray.length());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            ProductVariation prdvar = new ProductVariation();
                            prdvar.setKey(jsonObject.optString("key"));
                            prdvar.setValue(jsonObject.optString("value"));
                            prdvar.setPrice(Double.parseDouble(jsonObject.optString("price")));
                            schools.add(prdvar);
                            Variations.add("SIZE: " + jsonObject.optString("value") + " ($" + jsonObject.optString("price") + ")");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Product", "Error!!!" + e.toString());
                    }
                    Spinner mySpinner = (Spinner) findViewById(R.id.spn_variation);
                    mySpinner.setAdapter(new ArrayAdapter<String>(Product.this, android.R.layout.simple_spinner_dropdown_item, Variations));
                }

            }

        }


    }



    class GetConnectionStatus extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Product.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        protected Boolean doInBackground(String... urls) {
            // TODO: Connect
            if  (isNetworkAvailable() ==true) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    return (urlc.getResponseCode() == 200);
                } catch (IOException e) {
                    Log.e("Network Check", "Error checking internet connection", e);
                }
            } else {
                Log.d("Network Check", "No network available!");
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            // TODO: Check this.exception
            // TODO: Do something with the feed
            pDialog.dismiss();

            if (result==true)
            {
                new GetProductDetails().execute();
            }
            else{

                getOneProduct();

                //noresult.setText("Network is Currently Unavailable. Available stock maybe not be up to date when adding cart items while offline!");
//                layout.addView(noresult);


                //Toast.makeText( Product.this ,"Network is Currently Unavailable",Toast.LENGTH_LONG).show();

                Toast ToastMessage = Toast.makeText(Product.this,"Network is Currently Unavailable!",Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();

            }
        }
    }



    public void getOneProduct() {
        try {
            Log.e("Cursor items", "statrted getting product method");
            //  pDialog = new ProgressDialog(Product.this);
//        pDialog.setMessage("Getting Product Details...");
            //      pDialog.setIndeterminate(false);
            //    pDialog.setCancelable(true);
            //pDialog.show();
            SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("seller", "100");
            editor.commit();
            editor.apply();

            String pid = productID;
            limit = 15;
            offset = 0;
//try {

            Log.e("Cursor items", "statrted calling database");
            DatabaseHelper dbHandler = new DatabaseHelper(getBaseContext());
            //productID = (shared.getString("idKey", ""));
            int pID = Integer.parseInt(pid);
            dbHandler.getOneProduct(pID);
            if (dbHandler.getOneProduct(pID) != null) {
                Cursor cursor = dbHandler.getOneProduct(pID);

                Log.e("Cursor items", "statrted getting cursor items");

                //if (cursor != null && cursor.moveToFirst()) {

                Log.e("Cursor items", "dumpCursorToString()" + DatabaseUtils.dumpCursorToString(cursor));
                //cursor01.moveToFirst();

                name = cursor.getString(3);
                post_id = cursor.getString(1);
                imgurl = cursor.getString(9);
                price = cursor.getString(6);
                shopname = cursor.getString(12);
                shop_open_day = cursor.getString(14);
                shop_open_hrs = cursor.getString(15);
                shop_close_hrs = cursor.getString(16);
                seller = cursor.getString(13);


                editor.putString("opendays", shop_open_day);
                editor.putString("openhrs", shop_open_hrs);
                editor.putString("closehrs", shop_close_hrs);
                editor.putString("shopname", shopname);
                editor.putString("seller", seller);


                editor.commit();


                //LinearLayout prdctinfo = new LinearLayout(this);
                //prdctinfo.setOrientation(LinearLayout.VERTICAL);
                //layout = (LinearLayout)findViewById(R.id.product);
                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                //productDesc = (TextView) findViewById(R.id.description);
                //prodName = (TextView) findViewById(R.id.prdctname);
                //prodPrice = (TextView) findViewById(R.id.prdctprice);
                //banner = (ImageView) findViewById(R.id.product_banner);
                //inStock = (TextView) findViewById(R.id.instock);
                qnty = ("(You are adding to cart while offline, stock quantities may not be up to date, Out of stock products will be removed from your order on checkout!!)");


                //imageLoader.displayImage(imgurl, imgstore[i], options);
                Picasso.with(this).load(imgurl)
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.offline)
                        .into(banner);
                //ViewGroup.LayoutParams imglayoutParams = imgstore.getLayoutParams();

                pDialog.dismiss();
                Log.e("Setting Banner", imgurl);
                // LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                SetBanner(imgurl);
                prodName.setText(name);
                prodPrice.setText("US $" + price);
                prodPrice.setTextColor(getResources().getColor(R.color.colorAmber));
                prodPrice.setTypeface(null, Typeface.BOLD);
                prodPrice.setTextSize(13);
                prodName.setGravity(Gravity.CENTER);
                prodPrice.setGravity(Gravity.CENTER);

                SharedPreferences.Editor editorr = sharedpreferences.edit();
                editorr.putString("storedprice", price);
                editorr.putString("storedquantity", "1");
                editorr.apply();


                //TextView inStock = new TextView(this);
                inStock.setText(qnty);

                settotal();


                LinearLayout.LayoutParams btnsize = new LinearLayout.LayoutParams(200, 60);
                btnsize.setMargins(20, 0, 20, 0);
                btnsize.gravity = Gravity.RIGHT;


            }

            //}

//}

//catch (Exception e){

            //  Log.e("Database fetch","Error on fetching");
//}

            if (shared.getString("CartID", "") != null && shared.getString("CartID", "") != "") {
                currcart = Integer.parseInt(shared.getString("CartID", ""));
            } else {
                currcart = 0;
            }
            //
            Map<String, ?> allEntries = shared.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("map values 123", entry.getKey() + ": " + entry.getValue().toString());
            }

            //txtquantity = (EditText) findViewById(R.id.txtQnty);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtquantity.setGravity(Gravity.CENTER);

                    //
                    final TextView Qnty = (TextView) findViewById(R.id.txtQnty);
                    Qnty.setText("1");


                    Button add = (Button) findViewById(R.id.addQnty);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            addbtn(Qnty);

                            settotal();
                        }

                    });

                    Button minus = (Button) findViewById(R.id.removeQnty);
                    minus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            int CurrQnty = 1;
                            try {
                                if (Qnty.getText().toString() == "" || Qnty.getText().toString() == null) {
                                    CurrQnty = 1;
                                } else {
                                    CurrQnty = Integer.parseInt(Qnty.getText().toString());
                                }
                                CurrQnty -= 1;
                                if (CurrQnty < 1) {
                                    CurrQnty = 1;
                                }

                                Qnty.setText(Integer.toString(CurrQnty));
                            } catch (Exception e) {
                                Log.e("Error  Quantity", e.toString());
                            }

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("storedquantity", Integer.toString(CurrQnty));
                            editor.apply();

                            settotal();
                        }

                    });

                }
            });


            Button addtocart = (Button) findViewById(R.id.addToCart);
            addtocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    try {
                        if (!qnty.isEmpty() && qnty != "0") {
                            addProduct(arg0);
                        } else if (qnty == "0") {
                            //Toast.makeText(Product.this, "Sorry, This Product is Out of Stock", Toast.LENGTH_LONG).show();


                            Toast ToastMessage = Toast.makeText(Product.this,"Sorry, This Product is Out of Stock!",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();


                        } else {
                            addProduct(arg0);
                        }
                    } catch (Exception ex) {
                        Log.e("Add To Cart Error", ex.toString());
                    }

                }
            });

            Button backtoshop = (Button) findViewById(R.id.btn_product_back);
            backtoshop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {


                    if (sharedpreferences.getString("Categorystart", "") != null && sharedpreferences.getString("Categorystart", "") != "") {


                        Log.e("Intent", "Starting store class " + sharedpreferences.getString("Categorystart", ""));
                        sharedpreferences.edit().remove("Categorystart").apply();

                        Intent intent = new Intent(Product.this, Store.class);
                        startActivity(intent);

                    } else {
                        Log.e("Intent", "Starting product class " + sharedpreferences.getString("Categorystart", ""));

                        sharedpreferences.edit().remove("Categorystart").apply();

                        Intent intent = new Intent(Product.this, Products_List.class);
                        startActivity(intent);


                    }


                }
            });


        }

        catch (Exception e){

            Log.e(TAG, "getOneProduct: ", e );
        }

    }

    public void addbtn ( TextView txtview){


        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
       day = sharedpreferences.getString("opendays", "");
      openhrs =  sharedpreferences.getString("openhrs", "");
       closehrs = sharedpreferences.getString("closehrs", "");
        shopthename = sharedpreferences.getString("shopname", "");
        theseller = sharedpreferences.getString("seller", "");


        Log.e("addbtn: ", "values" + shopthename  + openhrs + closehrs);



        int CurrQnty = 1;

        if (daynumber == 1) { dayofweek = "Sunday";}
        else if (daynumber == 2) { dayofweek = "Monday";}
        else if (daynumber == 3) { dayofweek = "Tuesday";}
        else if (daynumber == 4) { dayofweek = "Wednesday";}
        else if (daynumber == 5) { dayofweek = "Thursday";}
        else if (daynumber == 6) { dayofweek = "Friday";}
        else if (daynumber == 7) { dayofweek = "Saturday";}


        if (hourofday == 0) { hourofhour = "00:00 AM";}
        else if (hourofday == 1) { hourofhour = "01:00 AM";}
        else if (hourofday == 2) { hourofhour = "02:00 AM";}
        else if (hourofday == 3) { hourofhour = "03:00 AM";}
        else if (hourofday == 4) { hourofhour = "04:00 AM";}
        else if (hourofday == 5) { hourofhour = "05:00 AM";}
        else if (hourofday == 6) { hourofhour = "06:00 AM";}
        else if (hourofday == 7) { hourofhour = "07:00 AM";}
        else if (hourofday == 8) { hourofhour = "08:00 AM";}
        else if (hourofday == 9) { hourofhour = "09:00 AM";}
        else if (hourofday == 10) { hourofhour = "10:00 AM";}
        else if (hourofday == 11) { hourofhour = "11:00 AM";}
        else if (hourofday == 12) { hourofhour = "12:00 PM";}
        else if (hourofday == 13) { hourofhour = "01:00 PM";}
        else if (hourofday == 14) { hourofhour = "02:00 PM";}
        else if (hourofday == 15) { hourofhour = "03:00 PM";}
        else if (hourofday == 16) { hourofhour = "04:00 PM";}
        else if (hourofday == 17) { hourofhour = "05:00 PM";}
        else if (hourofday == 18) { hourofhour = "06:00 PM";}
        else if (hourofday == 19) { hourofhour = "07:00 PM";}
        else if (hourofday == 20) { hourofhour = "08:00 PM";}
        else if (hourofday == 21) { hourofhour = "09:00 PM";}
        else if (hourofday == 22) { hourofhour = "10:00 PM";}
        else if (hourofday == 23) { hourofhour = "11:00 PM";}


        if (shopthename.toUpperCase().contentEquals("HAPPY HOUR STORE") || theseller.contentEquals("128") ) {

            Log.e("addbtn: ", "values: " + shopthename + "theseller is" + theseller  );

            //Toast.makeText(getBaseContext(), "A quantity of one only allowed!", Toast.LENGTH_LONG).show();



            Toast ToastMessage = Toast.makeText(Product.this,"A quantity of one only allowed!",Toast.LENGTH_LONG);
            View toastView = ToastMessage.getView();
            toastView.setBackgroundResource(R.drawable.toast_background);
            ToastMessage.show();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("storedquantity", Integer.toString(CurrQnty));
            editor.apply();


        }


        else {
            try {
                if (txtview.getText().toString() == "" || txtview.getText().toString() == null) {
                    CurrQnty = 1;
                } else {
                    CurrQnty = Integer.parseInt(txtview.getText().toString());
                }
                CurrQnty += 1;
                txtview.setText(Integer.toString(CurrQnty));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("storedquantity", Integer.toString(CurrQnty));
                editor.apply();
            } catch (Exception e) {
                Log.e("Error  Quantity", e.toString());
            }
        }




    }

            @Override

    public void onStop(){
        super.onStop();

             Picasso.with(this).cancelRequest(banner);
    }

}
