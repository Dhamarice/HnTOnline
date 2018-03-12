package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Favourites extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Product = "idKey";

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    String categoryID,PName, PID, lastfavid, PPrice="";
    Boolean GetProducts = Boolean.FALSE;
    ImageView imgstore[] = new ImageView[1001];
    DatabaseHelper dbHandler;
    int   limit, offset;

    private static final String GETPRODUCT_URL = "https://devshop.hammerandtongues.com/webservice/getsingleproduct.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PRODUCTDETAILS = "posts";

    String productID, name, price,post_id, stock,  imgurl = "";
    String getlastid = "0";
    String lastid = "0";
    Product product = null;
    ImageView banner;
    TextView inStock;
    int cartid, currcart;
    LinearLayout layout, productlist;
    View main;

    private int cnt_cart;
    private TextView txtcartitems;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.products_list);

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
            pDialog = new ProgressDialog(Favourites.this);
            pDialog.setMessage("Checking Stock Availability...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            try {

                final SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                if (shared.getString("CartID", "") != null && shared.getString("CartID", "") != "") {
                    currcart = Integer.parseInt(shared.getString("CartID", ""));
                } else {
                    currcart = 0;
                }



                shared.edit().remove("lastfavid").apply();


                categoryID = (shared.getString("idKey", ""));
                productID = (shared.getString("productID", ""));
                banner = (ImageView) findViewById(R.id.list_banner);
                limit = 15;
                offset = 0;
                if (categoryID == "NewArrivals") {
                    categoryID = "99990";
                } else if (categoryID == "OnSpecial") {
                    categoryID = "99991";
                } else if (categoryID == "Popular") {
                    categoryID = "99992";
                }

                GetProducts = Boolean.FALSE;
                new Favourites.GetProductDetails().execute();
            } catch (Exception ex) {
                Log.e("OnCreate Error", ex.toString());
            }


            final LinearLayout btnhome, btncategs, btnsearch, btncart, btnprofile;

            productlist = (LinearLayout) findViewById(R.id.productslist);


            btnhome = (LinearLayout) findViewById(R.id.btn_home);
            btnhome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Favourites.this, MainActivity.class);

                    startActivity(intent);
                }
            });

            btncategs = (LinearLayout) findViewById(R.id.btn_Categories);
            btncategs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Favourites.this, CategoriesActivity.class);

                    startActivity(intent);
                }
            });

            btncart = (LinearLayout) findViewById(R.id.btn_Cart);
            btncart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Favourites.this, Chat_Webview.class);

                    startActivity(intent);
                }
            });

            btnprofile = (LinearLayout) findViewById(R.id.btn_Profile);
            btnprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Favourites.this, UserActivity.class);

                    startActivity(intent);
                }
            });

            btnsearch = (LinearLayout) findViewById(R.id.btn_Search01);
            btnsearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Favourites.this, Search.class);

                    startActivity(intent);
                }
            });

            ImageView imgsearch, imgcategories, imghome, imgcart, imgprofile;
            imgsearch = (ImageView) findViewById(R.id.imgsearch01);
            imgsearch.setImageResource(R.drawable.search);

            imghome = (ImageView) findViewById(R.id.imghome);
            imghome.setImageResource(R.drawable.homeselected);

            imgcategories = (ImageView) findViewById(R.id.imgcategs);
            imgcategories.setImageResource(R.drawable.categories);

            imgcart = (ImageView) findViewById(R.id.imgcart);
            imgcart.setImageResource(R.drawable.chat);

            imgprofile = (ImageView) findViewById(R.id.imgprofile);
            imgprofile.setImageResource(R.drawable.profile);
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

            final Button btncateg, btnstore, btnpromo;

            btncateg = (Button) findViewById(R.id.btn_categs);
            btncateg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Favourites.this, CategoriesActivity.class);

                    startActivity(intent);

                }
            });

            btnstore = (Button) findViewById(R.id.btn_stores);
            btnstore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Favourites.this, StoresFragment.class);
                    startActivity(intent);
                }
            });

            btnpromo = (Button) findViewById(R.id.btn_promos);
            btnpromo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Favourites.this, PromotionsFragment.class);
                    startActivity(intent);
                }
            });
            System.gc();

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
                        //setuielements();
                        //Toast.makeText(Products_List.this,"Hechoni",Toast.LENGTH_LONG).show();
                    } else {
                        //do nothing
                        //Toast.makeText(Products_List.this,"Zvikudotekaira",Toast.LENGTH_LONG).show();
                    }
                    Log.e("Scrollview", "Height - " + sv.getScrollY());
                }
            });


            DatabaseHelper db = new DatabaseHelper(this);
            CartManagement crtItms = db.getCartItemsCount(currcart);
            if (crtItms != null) {
                cnt_cart = (crtItms.get_Cart_Items_Count());
            } else {
                cnt_cart = 0;
            }


        } catch (Exception ex) {
            Log.e("Main Thread Exception", "Error: " + ex.toString());
            System.gc();
        }
    }





    public void setuielements() {
        ProgressBar pgr = (ProgressBar) findViewById(R.id.progressBar3);
        pgr.setVisibility(View.GONE);

        final SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        LinearLayout layout = (LinearLayout) findViewById(R.id.productslist);


        categoryID = (shared.getString("idKey", ""));

        TextView noresult = (TextView) findViewById(R.id.txtinfo);

        //Drawable img = (Drawable) getResources().getDrawable(R.drawable.appliances);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);


        if (categoryID.contentEquals("NewArrivals"))
            categoryID = "99999";
        else if (categoryID.contentEquals("Popular"))
            categoryID = "99998";
        else if (categoryID.contentEquals("OnSpecial"))
            categoryID = "99997";
        else
            categoryID = categoryID;
        int catid = Integer.parseInt(categoryID);
        int i;


        if (productID == null) {
            String msg = "";
            msg = "";

        } else {
            //int pid = Integer.parseInt(productID);


            dbHandler = new DatabaseHelper(this);
            //.dbHandler.async_data();

            if (shared.getString("lastfavid", "") != null && shared.getString("lastfavid", "") != "") {

                getlastid = shared.getString("lastfavid", "");
            }

            Cursor cursor02 = dbHandler.viewFavs(getlastid);
            Log.e("Cursor", "Values" + DatabaseUtils.dumpCursorToString(cursor02));

            if (dbHandler.getProductFav(getlastid) != null) {
                Cursor cursor01 = dbHandler.getProductFav(getlastid);
                Log.e("Cursor", "Values" + DatabaseUtils.dumpCursorToString(cursor01));
                if (cursor02 != null && cursor02.moveToFirst()) {

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
                        lastfavid = cursor01.getString(0);
                        post_id = cursor01.getString(1);
                        imgurl = cursor01.getString(9);
                        PPrice = cursor01.getString(6);

                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("lastfavid", lastfavid);
                        editor.commit();


                        //imageLoader.displayImage(imgurl, imgstore[i], options);
                        Picasso.with(this).load(R.drawable.progress_animation)
                                .placeholder(R.drawable.progress_animation)
                                .error(R.drawable.offline)
                                .into(imgstore[i]);
                        ViewGroup.LayoutParams imglayoutParams = imgstore[i].getLayoutParams();

                        int imglength = (int) (metrics.density * 80);
                        int imgwidth = (int) (metrics.density * 80);


                        imglayoutParams.width = imglength;
                        imglayoutParams.height = imgwidth;

                        imgstore[i].setLayoutParams(imglayoutParams);

                        Log.e("Setting Banner", imgurl);
                        //LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        LinearLayout prdctinfo = new LinearLayout(this);
                        prdctinfo.setOrientation(LinearLayout.VERTICAL);

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

                        prdctinfo.addView(ProductName);
                        prdctinfo.addView(Price);

                        LinearLayout theBut = new LinearLayout(this);
                        theBut.setOrientation(LinearLayout.HORIZONTAL);

                        Button AddToCart = new Button(this);
                        AddToCart.setText("View Product");
                        AddToCart.setTextColor(Color.WHITE);
                        AddToCart.setTextSize(12);
                        AddToCart.setId(Integer.parseInt(post_id));
                        AddToCart.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // do stuff
                                String id1 = Integer.toString(view.getId());
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString(Product, id1);
                                editor.commit();
                                Intent i = new Intent(Favourites.this, Product.class);
                                startActivity(i);


                            }

                        });


                        Button AddToFav = new Button(this);
                        AddToFav.setText("Remove");
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
                                productID = (shared.getString(Product, ""));
                                RemoveFav(id1);
                                Intent i = new Intent(Favourites.this, Favourites.class);
                                startActivity(i);


                            }

                        });


                        int length = (int) (metrics.density * 100);
                        int width = (int) (metrics.density * 35);
                        //}// else {
                        // AddToCart.setText("Auction Only");
                        // AddToCart.setOnClickListener(new View.OnClickListener() {

                        //  @Override
                        // public void onClick(View view) {
                        // do stuff
                        //  Toast.makeText( Products_List.this, "Auction Only", Toast.LENGTH_SHORT).show();
                        //}

                        //});
                        // }

                        // int length = (int)(metrics.density *100);
                        //int width = (int)(metrics.density * 35);


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
                                Intent i = new Intent(Favourites.this, Product.class);
                                startActivity(i);
                            }

                        });
                        layout.addView(itmcontr, layoutParams);
                        cursor01.moveToNext();
                    }
                    noresult.setVisibility(View.GONE);
                } else {
                    String msg = "";
                    if (offset == 0) {
                        msg = "";
                    } else {
                        msg = "No Additional Products to Display";
                    }
                    noresult.setText(msg);
                    Snackbar snackbar = Snackbar
                            .make(productlist,"NO FAVOURITE PRODUCTS TO DISPLAY", Snackbar.LENGTH_LONG);

                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                    noresult.setVisibility(View.VISIBLE);
                }
                noresult.setVisibility(View.GONE);
            } else {
                String msg = "";
                if (offset == 0) {
                    msg = "";
                } else {
                    msg = "No Additional Products to Display";
                }
                noresult.setText(msg);

                Snackbar snackbar = Snackbar
                        .make(productlist,"NO FAVOURITE PRODUCTS TO DISPLAY", Snackbar.LENGTH_LONG);

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                noresult.setVisibility(View.VISIBLE);
            }
        }

    }
    public void addProduct(View view, Integer pid) {

        String curimgurl="";
        if (currcart == 0) {
            newCart(view);
        } else {
            cartid = currcart;


        }
        DatabaseHelper db = new DatabaseHelper(this);
        int qty = 1;

        int id = Integer.parseInt(pid.toString());

        int seller = Integer.parseInt(shared.getString("seller", ""));
        double itmprice = db.getProductPrice(id);
        double subtot = db.getProductPrice(id);
        Log.d("Notifications", "Picking up variables for saving" + String.valueOf((qty)) + " <= Quantity; " + String.valueOf(cartid) + "<=This is the current cart ID");

        CartManagement ctm = new CartManagement(cartid, db.getProductName(id), qty, id, db.getProductImg(id), subtot, itmprice, seller);
        Log.d("Notifications", "Saving the product to cart with a cart id");
        db.addCartItem(ctm);
        Log.d("Notifications", "Finished saving an item");
        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        CartManagement crtItms = db.getCartItemsCount(cartid);
        if (crtItms != null) {
            Intent i = new Intent(Favourites.this, Products_List.class);
            startActivity(i);
            Toast.makeText(this, "Item Added to Cart with : " + String.valueOf((crtItms.get_Cart_Items_Count())) +
                            "items Total amount so far: " +
                            currencyFormatter.format(Double.parseDouble(String.valueOf(crtItms.get_Cart_Items_Value()))),
                    Toast.LENGTH_LONG).show();
            //idCartItems.setText("Items in cart: " + String.valueOf((crtItms.get_Cart_Items_Count())));
        } else {
            //idCartItems.setText("Items in cart: 0");
        }


    }



    public void newCart(View view) {
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String TAG = "NEW CART";
        Log.v(TAG, "Creating New Cart");
        DatabaseHelper dbHandler = new DatabaseHelper(this);

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
        editor.apply();
        Toast.makeText(this, "New Shopping Session Created" + String.valueOf(cartno.get_CartID()), Toast.LENGTH_LONG).show();
        currcart = cartno.get_CartID();

            /*
       catch (Exception ex)
        {
            Log.d("Problemo!", "Error while retrieving the saved cart id " + ex.getMessage());
        }
        */
    }








    public void RemoveFav(String id) {

        int idl = Integer.parseInt(id.toString());
        dbHandler.RemoveFavs(idl);
        Toast.makeText(Favourites.this , "Item removed from Favourites!", Toast.LENGTH_LONG).show();
        Log.e("Deleting", "removing from favs");

    }


    public void SetBanner() {

        try {
            String imgbanner = "";

            if (categoryID.matches("[0-9]+")) {
                if (Integer.parseInt(categoryID) == 3157) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/appliances.jpg";
                } else if (Integer.parseInt(categoryID) == 3158) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/e_gadgets.jpg";
                } else if (Integer.parseInt(categoryID) == 3159) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/e_gadgets.jpg";
                } else if (Integer.parseInt(categoryID) == 3160) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/grocery.jpg";
                } else if (Integer.parseInt(categoryID) == 3161) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/electronics.png";
                } else if (Integer.parseInt(categoryID) == 3162) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/hardware.jpeg";
                } else if (Integer.parseInt(categoryID) == 3163) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/health_beauty";
                } else if (Integer.parseInt(categoryID) == 3164) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/fruit_vegie.png";
                } else if (Integer.parseInt(categoryID) == 3165) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/sport.png";
                } else if (Integer.parseInt(categoryID) == 3166) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/school.jpg";
                } else if (Integer.parseInt(categoryID) == 3167) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/appliances.jpg";
                } else if (Integer.parseInt(categoryID) == 3168) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/homebanner05.png";
                } else if (Integer.parseInt(categoryID) == 3169) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/appliance.jpg";
                } else if (Integer.parseInt(categoryID) == 9999) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/homebanner02.png";
                } else if (Integer.parseInt(categoryID) == 0000) {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/liquor1.jpg";
                }
            } else {
                if (categoryID == "NewArrivals" || categoryID == "99990") {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/newarrivals.jpg";
                    categoryID = "99990";
                } else if (categoryID == "OnSpecial" || categoryID == "99991") {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/onspecial.jpg";
                    categoryID = "99991";
                } else if (categoryID == "Popular" || categoryID == "99992") {
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/productbanner.jpg";
                    categoryID = "99992";
                } else {
                    Log.e("Category ID", categoryID);
                    imgbanner = "https://hammerandtongues.com/demo/webservice/appimgs/productbanner.jpg";
                }
            }


            Picasso.with(this).load(R.drawable.favbanner)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.favbanner)
                    .into(banner);

        } catch (Exception ex) {
            Log.e("Error getting Banner", ex.toString());
        }
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
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Products_List Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.hammerandtongues.online.hntonline/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Picasso.with(this).cancelRequest(banner);
        for (int i = 0; i<imgstore.length; i++ ) {
            Picasso.with(this).cancelRequest(imgstore[i]);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Products_List Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.hammerandtongues.online.hntonline/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            Intent intent = new Intent(Favourites.this, Search.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.cart_item) {
            Intent intent = new Intent(Favourites.this, Cart.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Favourites.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            pDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            int catId = Integer.parseInt(categoryID);
            dbHandler = new DatabaseHelper(Favourites.this);


            //dbHandler.async_data();
            if (dbHandler.getProducts(catId, limit, offset, lastid) != null) {
                Cursor cursor = dbHandler.getStores();
                if (cursor != null && cursor.moveToFirst()) {
                    return "Success";
                }
            }
            return "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String posts) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (posts != null && GetProducts == Boolean.FALSE) {
                setuielements();
                SetBanner();
            } else {
                Toast.makeText(Favourites.this, posts, Toast.LENGTH_LONG).show();
            }

        }

    }


    class GetStockAvailability extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog.show();
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
                // getting product details by making HTTP request
                json1 = jsonParser.makeHttpRequest(
                        GETPRODUCT_URL, "POST", params);
                Log.e("PID + URL", productID + GETPRODUCT_URL);


                // json success tag
                success = json1.getInt(TAG_SUCCESS);
                if (success == 1) {
                    return json1.getString(TAG_PRODUCTDETAILS);
                } else {
                    return json1.getString(TAG_MESSAGE);
                }
            }
            catch (JSONException e) {
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
                JSONArray jsonarray02 = null;
                try {
                    jsonarray02 = new JSONArray(posts);
                } catch (JSONException e) {
                }
                if (jsonarray02 != null) {
                    try {
                        Log.e("JSONing", jsonarray02.toString());
                        JSONObject jsonobject = jsonarray02.getJSONObject(0);
                        price = jsonobject.optString("price");
                        stock = jsonobject.optString("quantity");
                    } catch (JSONException e) {
                        Log.e("JSON Error: ", e.toString());
                        e.printStackTrace();
                    }
                    Log.e("Checking Stock  ", " == " + stock);
                    if (!stock.isEmpty() || !stock.contentEquals("0")) {
                        int pid = Integer.parseInt(productID);
                        addProduct(main, pid);
                    }
                    else {
                        Toast.makeText(Favourites.this, "Product Out Of Stock", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    Toast.makeText(Favourites.this, "Product Out Of Stock", Toast.LENGTH_LONG).show();
                    return;
                }
            }


        }

    }


}





