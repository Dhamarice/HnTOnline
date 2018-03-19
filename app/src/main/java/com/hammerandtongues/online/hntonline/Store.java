package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by NgonidzaIshe on 4/6/2016.
 */
public class Store extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";

    private Spinner spinVaritions;
    private TextView storeName, storeDesc;
    public static final String Product = "idKey";
    public static final String category = "idcateg";
    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();


    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PRODUCTDETAILS = "posts";
    String storeID, PName,PPrice, post_id,imgurl, imgurl02 = "";
    ImageView banner;
    Boolean GetProducts = Boolean.FALSE;
    ImageView imgstore[] = new ImageView[1001];
    DatabaseHelper dbHandler;
    int cartid, currcart, limit, offset;
    private TextView txtcartitems;
    private int cnt_cart,  flag=0;
    private TextView noresult;
    String msg = "";
    SharedPreferences shared;
    Calendar calendar = Calendar.getInstance();
    int daynumber =calendar.get(Calendar.DAY_OF_WEEK);
    int hourofday = calendar.get(Calendar.HOUR_OF_DAY);
    private String openhrs, day, closehrs, dayofweek, hourofhour;
    String lastid = "",getlastid = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
       try {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.store);

           try {
               final SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
               if (shared.getString("CartID", "") != null && shared.getString("CartID", "") != "") {
                   currcart = Integer.parseInt(shared.getString("CartID", ""));
               } else {
                   currcart = 0;
               }

               shared.edit().remove("lastsid").apply();

               SharedPreferences.Editor editor = shared.edit();
               editor.putString("Categorystart", "Store");
               editor.commit();

               Log.e("Intent in ProductList", "Starting storeproducts now");

               limit = 25;
               offset = 0;
               noresult = (TextView) findViewById(R.id.txtinfo);
               storeID = (shared.getString("idcateg", ""));;

               if (storeID.contentEquals("NewArrivals"))
                   storeID="99999";
               else if (storeID.contentEquals("Popular"))
                   storeID="99998";
               else if (storeID.contentEquals("OnSpecial"))
                   storeID="99997";
               else
               storeID = storeID;

               GetProducts = Boolean.FALSE;
               new GetProductDetails().execute();
               String StoreName = (shared.getString("nameKey", ""));
               storeName = (TextView) findViewById(R.id.storename);
               storeName.setText(StoreName);


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










           } catch (Exception ex) {
               Log.e("Oncreate Error", ex.toString());
           }


           final LinearLayout btnhome, btncategs, btnsearch, btncart, btnprofile;
           btnhome = (LinearLayout) findViewById(R.id.btn_home);
           btnhome.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(Store.this, MainActivity.class);

                   startActivity(intent);
               }
           });

           btncategs = (LinearLayout) findViewById(R.id.btn_home);
           btncategs.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(Store.this, CategoriesActivity.class);

                   startActivity(intent);
               }
           });

           btncart = (LinearLayout) findViewById(R.id.btn_Cart);
           btncart.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(Store.this, Chat_Webview.class);

                   startActivity(intent);
               }
           });

           btnprofile = (LinearLayout) findViewById(R.id.btn_Profile);
           btnprofile.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(Store.this, UserActivity.class);

                   startActivity(intent);
               }
           });

           btnsearch = (LinearLayout) findViewById(R.id.btn_Search01);
           btnsearch.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(Store.this, Search.class);

                   startActivity(intent);
               }
           });


           final NestedScrollView sv = (NestedScrollView) findViewById(R.id.svmain);
           sv.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
               @Override
               public void onScrollChanged() {
                   //int scrollY = rootScrollView.getScrollY(); // For ScrollView
                   //int scrollX = rootScrollView.getScrollX(); // For HorizontalScrollView
                   // DO SOMETHING WITH THE SCROLL COORDINATES
                   LinearLayout linearLayout = (LinearLayout) findViewById(R.id.child);
                   if ((linearLayout.getMeasuredHeight()) <= (sv.getScrollY() +
                           sv.getHeight())) {
                       offset= offset+limit;
                       setuielements();
                       //Toast.makeText(Products_List.this,"Hechoni",Toast.LENGTH_LONG).show();
                   } else {
                       //do nothing
                       //Toast.makeText(Products_List.this,"Zvikudotekaira",Toast.LENGTH_LONG).show();
                   }
                   Log.e("Scrollview", "Height - " + sv.getScrollY());
               }
           });

           DatabaseHelper dbHandler = new DatabaseHelper(this);
           CartManagement crtItms = dbHandler.getCartItemsCount(currcart);
           if (crtItms != null) {
               cnt_cart = (crtItms.get_Cart_Items_Count());
           }
           else {
               cnt_cart =0;
           }
       }
        catch (Exception ex)
       {
           Log.e("Main Thread Exception", "Error: " + ex.toString());
           System.gc();
       }
    }


    public void setuielements() {
        final SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        ProgressBar pgr = (ProgressBar) findViewById(R.id.progressBar2);
        pgr.setVisibility(View.GONE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.storelayout);
        storeDesc = (TextView) findViewById(R.id.storedesc);
        banner = (ImageView) findViewById(R.id.store_banner);
        imgurl02 = dbHandler.getStoreBanner(Integer.parseInt(storeID));
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int i = 0;

        Log.e("Intent in Store", "Starting Storewacho with ID " + storeID);

        if (storeID.contentEquals("NewArrivals"))
            storeID="99999";
        else if (storeID.contentEquals("Popular"))
            storeID="99998";
        else if (storeID.contentEquals("OnSpecial"))
            storeID="99997";
        else
            storeID = storeID;


        if (shared.getString("lastsid", "") != null && shared.getString("lastsid", "") != "") {

            getlastid = shared.getString("lastsid", "");
        }


        int store = Integer.parseInt(storeID);
        dbHandler = new DatabaseHelper(this);
        if (dbHandler.getStoreProducts(store,limit,offset, getlastid) != null) {
            Cursor cursor01 = dbHandler.getStoreProducts(store,limit,offset,getlastid);

            day = cursor01.getString(18);
            openhrs = cursor01.getString(19);
            closehrs = cursor01.getString(20);




            if (cursor01 != null && cursor01.moveToFirst()) {




                Log.e("Cursor", "Values" + DatabaseUtils.dumpCursorToString(cursor01));






                if (!day.contentEquals(dayofweek) && day != null && !day.contentEquals("1") && !day.contentEquals("null") && !day.contentEquals("")) {

                    msg = "This store is curently closed and opens every " + day;

                    noresult.setText(msg);
                    //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

                    Toast ToastMessage = Toast.makeText(this,msg,Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();

                    noresult.setVisibility(View.VISIBLE);


                }

                else if (!openhrs.contentEquals(hourofhour) && openhrs != null && !openhrs.contentEquals("null") && !openhrs.contentEquals("")) {

                    msg = "This store is curently closed and opens at " + openhrs;

                    noresult.setText(msg);
                    //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

                    Toast ToastMessage = Toast.makeText(this,msg,Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();

                    noresult.setVisibility(View.VISIBLE);


                }


else {
                    int i2 = cursor01.getCount();
                    for (i = 0; i < i2; i++) {
                        LinearLayout itmcontr = new LinearLayout(this);


                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(10, 10, 10, 10);

                        itmcontr.setOrientation(LinearLayout.HORIZONTAL);
                        itmcontr.setBackgroundColor(Color.WHITE);

                        imgstore[i] = new ImageView(this);
                        itmcontr.addView(imgstore[i]);


                        PName = cursor01.getString(3);
                        post_id = cursor01.getString(1);
                        imgurl = cursor01.getString(9);
                        lastid = cursor01.getString(0);
                        PPrice = cursor01.getString(6);

                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("lastsid", lastid);
                        editor.commit();
                        editor.apply();




                        Log.e("Cursor", "Values" + day + openhrs + closehrs);

                        //imageLoader.displayImage(imgurl, imgstore[i], options);
                        Picasso.with(this).load(imgurl)
                                .placeholder(R.drawable.progress_animation)
                                .error(R.drawable.offline)
                                .into(imgstore[i]);

                        android.view.ViewGroup.LayoutParams imglayoutParams = imgstore[i].getLayoutParams();

                        /*
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            imglayoutParams.width = 300;
                            imglayoutParams.height = 300;
                        } else {
                            imglayoutParams.width = 150;
                            imglayoutParams.height = 150;
                        }

                        */


                        int imglength = (int)(metrics.density *80);
                        int imgwidth = (int)(metrics.density * 80);


                        imglayoutParams.width = imglength;
                        imglayoutParams.height = imgwidth;

                        imgstore[i].setLayoutParams(imglayoutParams);

                        Log.e("Setting Banner", imgurl);
                        LinearLayout prdctinfo = new LinearLayout(this);
                        prdctinfo.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout theBut = new LinearLayout(this);
                        theBut.setOrientation(LinearLayout.HORIZONTAL);
                        TextView ProductName = new TextView(this);
                        TextView Price = new TextView(this);
                        ProductName.setText(PName);
                        Price.setText("US $" + PPrice);
                        Price.setTextColor(getResources().getColor(R.color.colorAmber));
                        Price.setTypeface(null, Typeface.BOLD);
                        Price.setTextSize(13);

                        LinearLayout.LayoutParams btnsize = new LinearLayout.LayoutParams(200, 60);
                        btnsize.setMargins(20, 0, 20, 0);
                        btnsize.gravity = Gravity.RIGHT;

                        Button AddToCart = new Button(this);
                        AddToCart.setText("View Product");
                        AddToCart.setTextColor(getResources().getColor(R.color.colorAmber));
                        AddToCart.setTypeface(null, Typeface.BOLD);
                        AddToCart.setTextSize(13);
                        AddToCart.setTextColor(Color.WHITE);
                        AddToCart.setTextSize(12);
                        //btnPopular.setPadding(15, 0, 15, 5);
                        AddToCart.setLayoutParams(btnsize);
                        AddToCart.setId(Integer.parseInt(post_id));
                        //AddToCart.setGravity(View.FOCUS_RIGHT);
                        AddToCart.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String id1 = Integer.toString(view.getId());
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString(Product, id1);
                                editor.commit();
                                Intent i = new Intent(Store.this, Product.class);
                                startActivity(i);
                            }

                        });

                        Button AddToFav = new Button(this);
                        AddToFav.setText("Favourite");
                        AddToFav.setTextColor(Color.WHITE);
                        AddToFav.setTextSize(12);
                        AddToFav.setId(Integer.parseInt(post_id));
                        AddToFav.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // do stuff
                                String id1 = Integer.toString(view.getId());
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString(Product, id1);
                                editor.commit();
                                String productID = (shared.getString(Product, ""));
                                addtoFav(id1);


                            }

                        });

                        int length = (int) (metrics.density * 100);
                        int width = (int) (metrics.density * 35);


                        LinearLayout.LayoutParams btnsize2 = new LinearLayout.LayoutParams(length, width, 1);
                        btnsize2.setMargins(15, 0, 15, 0);

                        AddToCart.setLayoutParams(btnsize2);
                        AddToCart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        AddToFav.setLayoutParams(btnsize2);
                        AddToFav.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        //prdctinfo.setOrientation(LinearLayout.HORIZONTAL);
                        theBut.addView(AddToCart);

                        theBut.addView(AddToFav);
                        prdctinfo.addView(theBut);

                        prdctinfo.addView(ProductName);
                        prdctinfo.addView(Price);
                        //prdctinfo.addView(AddToCart);
                        prdctinfo.setLayoutParams(layoutParams);


                        itmcontr.addView(prdctinfo);
                        itmcontr.setId(Integer.parseInt(post_id));
                        itmcontr.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // do stuff
                                String id1 = Integer.toString(view.getId());
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString(Product, id1);
                                editor.commit();
                                Intent i = new Intent(Store.this, Product.class);
                                startActivity(i);
                            }

                        });
                        layout.addView(itmcontr, layoutParams);

                        cursor01.moveToNext();
                    }
                    noresult.setVisibility(View.GONE);
                }
            }
            else {

                if (offset == 0) {
                    msg = "No Listings To Display Under This Category At The Moment";
                } else {
                    msg = "No Additional Products to Display";
                }
                noresult.setText(msg);
                //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

                Toast ToastMessage = Toast.makeText(this,msg,Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();
                noresult.setVisibility(View.VISIBLE);
            }
            noresult.setVisibility(View.GONE);
        }
        else {
            String msg = "";
            if (offset == 0) {
                msg = "No Listings To Display Under This Category At The Moment";
            } else {
                msg = "No Additional Products to Display";
            }
            noresult.setText(msg);
            //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            Toast ToastMessage = Toast.makeText(this,msg,Toast.LENGTH_LONG);
            View toastView = ToastMessage.getView();
            toastView.setBackgroundResource(R.drawable.toast_background);
            ToastMessage.show();
            noresult.setVisibility(View.VISIBLE);
        }
    }


    public void addProduct(View view, Integer pid) {

        if (currcart==0)
        {
            newCart(view);
        }
        else
        {
            cartid=currcart;
        }
        myDBHandler db = new myDBHandler(this,null,null,1);
        int qty = 1;

        int id = Integer.parseInt(pid.toString());
        double itmprice =  db.getProductPrice(id);
        double subtot = db.getProductPrice(id);
        int seller = Integer.parseInt(shared.getString("seller", ""));
        Log.d("Notifications", "Picking up variables for saving" + String.valueOf((qty)) + " <= Quantity; " + String.valueOf(cartid) + "<=This is the current cart ID");

        CartManagement ctm = new CartManagement(cartid ,db.getProductName(id),qty,id,db.getProductImg(id),subtot,itmprice, seller);
        Log.d("Notifications", "Saving the product to cart with a cart id");
        db.addCartItem(ctm);
        Log.d("Notifications", "Finished saving an item");
        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        CartManagement crtItms = db.getCartItemsCount(cartid);
        if (crtItms != null) {
            Intent i = new Intent(Store.this, Store.class);
            startActivity(i);

            Toast.makeText(this,"Item Added to Cart with : " + String.valueOf((crtItms.get_Cart_Items_Count()))
                    + "items Total amount so far: "+
                    currencyFormatter.format(Double.parseDouble(String.valueOf(crtItms.get_Cart_Items_Value())) )
                    ,Toast.LENGTH_LONG).show();
            //idCartItems.setText("Items in cart: " + String.valueOf((crtItms.get_Cart_Items_Count())));
        }
        else
        {
            //idCartItems.setText("Items in cart: 0");
        }


    }


    public void addtoFav(String id1) {

        String type = "product";
//type_id = PID;
        DatabaseHelper db = new DatabaseHelper(this);
        int qty = 1;

        int id = Integer.parseInt(id1.toString());
        //int catId = Integer.parseInt(categoryID);
        double itmprice = db.getProductPrice(id);
        double subtot = db.getProductPrice(id);
        Log.d("Notifications", "Picking up variables for saving" + String.valueOf((qty)) + " <= Quantity; " + String.valueOf(cartid) + "<=This is the current cart ID");

        CartManagement ctm = new CartManagement(id, type, db.getProductName(id));
        Log.e("Notifications", "Saving the product to favs!");
        db.addFavoriteItem(ctm);
        Log.e("Notifications", "Finished saving an item" + id);

       // Toast.makeText(Store.this, "Item added to Favourites!", Toast.LENGTH_LONG).show();

        Toast ToastMessage = Toast.makeText(this,"Item added to Favourites!",Toast.LENGTH_LONG);
        View toastView = ToastMessage.getView();
        toastView.setBackgroundResource(R.drawable.toast_background);
        ToastMessage.show();

        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        CartManagement crtItms = db.getCartItemsCount(cartid);
        if (crtItms != null) {
            //Intent i = new Intent(Store.this, Products_List.class);
            //startActivity(i);
            //Toast.makeText(Store.this, "Network is Currently Unavailable", Toast.LENGTH_LONG).show();
            //idCartItems.setText("Items in cart: " + String.valueOf((crtItms.get_Cart_Items_Count())));
        } else {
            //idCartItems.setText("Items in cart: 0");
        }
    }



    public void newCart (View view) {
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String TAG ="NEW CART";
        Log.v(TAG, "Creating New Cart");
        myDBHandler dbHandler = new myDBHandler(this, null, null, 1);

        int status = 1;
        String formattedDate = "07/06/2016";
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
        Toast.makeText(this,"New Shopping Session Created"+String.valueOf(cartno.get_CartID()),Toast.LENGTH_LONG).show();
        currcart=cartno.get_CartID();

            /*
       catch (Exception ex)
        {
            Log.d("Problemo!", "Error while retrieving the saved cart id " + ex.getMessage());
        }
        */
    }
    public void SetBanner()
    {
        Picasso.with(this).load(imgurl02)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.offline)
                .into(banner);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem awesomeMenuItem = menu.findItem(R.id.cart_item);
        RelativeLayout cart_wrapper = (RelativeLayout) menu.findItem(R.id.cart_item).getActionView().findViewById(R.id.cartitemwrapper);

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
            Intent intent = new Intent(Store.this, Search.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.cart_item) {
            Intent intent = new Intent(Store.this, Cart.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        Picasso.with(this).cancelRequest(banner);
        for (int i = 0; i<imgstore.length; i++ ) {
            Picasso.with(this).cancelRequest(imgstore[i]);
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
          /*  pDialog = new ProgressDialog(Store.this);
            pDialog.setMessage("Getting Product Details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            */
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag

            int success;
            int pid = Integer.parseInt(storeID);
            dbHandler = new DatabaseHelper(Store.this);
            //dbHandler.async_data();
            if (dbHandler.getStoreProducts(pid,limit,offset, getlastid) != null) {
                Cursor cursor =  dbHandler.getStores();
                if (cursor != null && cursor.moveToFirst()) {
                    return "Success";
                }
            }
            return  "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String posts) {
            // dismiss the dialog once product deleted
            //pDialog.dismiss();
            if (posts != null) {
                setuielements();
                SetBanner();
                }
                else
                {
                    Toast.makeText(Store.this, posts, Toast.LENGTH_LONG).show();

                    Toast ToastMessage = Toast.makeText(Store.this,posts,Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();
                }

            }

        }

    }

