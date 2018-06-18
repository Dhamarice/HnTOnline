package com.hammerandtongues.online.hntonline;


import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * Created by NgonidzaIshe on 16/5/2016.
 */

public class CategoriesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        /*MainActivity  extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
*/
    //View mainview;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Product = "idKey";
    public static final String ProductName = "nameKey";
    public static final String Category = "idcateg";

    SharedPreferences sharedpreferences;


    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //Global Variables
    public String Categories, lastcatid;
    public String name, price;
    public String imgurl;
    String getlastid = "0";
    public String post_id = "";
    public String CategoryID = "";
    ImageView banner;
    int x = 1;
    private DatabaseHelper dbHandler;
    private ImageView imgstore[] = new ImageView[150];
    private int cartid, currcart, limit, offset;
    private TextView txtcartitems;
    private int cnt_cart,  flag=0;

    @Override
    public void onCreate( Bundle savedInstanceState) {
try {

    super.onCreate(savedInstanceState);

    Runtime.getRuntime().gc();

    setContentView(R.layout.activity_category);
    //View view = inflater.inflate(R.layout.activity_category, container, false);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.hammerimageedited);
    setSupportActionBar(toolbar);
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);


    TabLayout tab = (TabLayout) findViewById(R.id.tabs);
    tab.setVisibility(View.GONE);

    sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    try {
        System.gc();
        final SharedPreferences shared = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (shared.getString("CartID", "") != null && shared.getString("CartID", "") != "") {
            currcart = Integer.parseInt(shared.getString("CartID", ""));
        } else {
            currcart = 0;
        }

        shared.edit().remove("lastcatid").apply();


        limit = 15;
        offset = 0;
        //loaduielement();
        new GetCategories().execute();
        final Button btncateg, btnstore, btnpromo;

        btncateg = (Button) findViewById(R.id.btn_categs);
        btncateg.setTextColor(getResources().getColor(R.color.colorPrimary));
        btncateg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
                //startActivity(intent);
            }
        });

        btnstore = (Button) findViewById(R.id.btn_stores);
        btnstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, StoresFragment.class);
                startActivity(intent);
            }
        });

        btnpromo = (Button) findViewById(R.id.btn_promos);
        btnpromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, PromotionsFragment.class);
                startActivity(intent);
            }
        });

        final LinearLayout btnhome, btncategs, btnsearch, btncart, btnprofile;

        btnhome = (LinearLayout) findViewById(R.id.btn_home);
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btncategs = (LinearLayout) findViewById(R.id.btn_Categories);
        btncategs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });

        btncart = (LinearLayout) findViewById(R.id.btn_Cart);
        btncart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, Chat_Webview.class);
                startActivity(intent);
            }
        });

        btnprofile = (LinearLayout) findViewById(R.id.btn_Profile);
        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        btnsearch = (LinearLayout) findViewById(R.id.btn_Search01);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, Search.class);
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

        DatabaseHelper db = new DatabaseHelper(this);
        CartManagement crtItms = db.getCartItemsCount(currcart);
        if (crtItms != null) {
            cnt_cart = (crtItms.get_Cart_Items_Count());
        } else {
            cnt_cart = 0;
        }
    } catch (Exception ex) {
        Log.e("Main Thread Exception", ex.toString());
    }
}
catch (OutOfMemoryError  ex)
{
    Log.e("Main Thread Exception", "Error: " + ex.toString());
    System.gc();
}

    }







    public void SetBanner( ImageView rur, String cart) {

        try {
            String imgbanner = "";

            if (cart.matches("[0-9]+")) {
                if (Integer.parseInt(cart) == 3157) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/appliances.jpg";
                } else if (Integer.parseInt(cart) == 3158) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/e_gadgets.jpg";
                } else if (Integer.parseInt(cart) == 3159) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/e_gadgets.jpg";
                } else if (Integer.parseInt(cart) == 3160) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/Grocery.png";
                } else if (Integer.parseInt(cart) == 3161) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/electronics.png";
                } else if (Integer.parseInt(cart) == 3162) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/hardware.jpeg";
                } else if (Integer.parseInt(cart) == 3163) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/health_beauty.jpg";
                } else if (Integer.parseInt(cart) == 3164) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/fruit_vegie.png";
                } else if (Integer.parseInt(cart) == 3165) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/sport.png";
                } else if (Integer.parseInt(cart) == 3166) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/school.jpg";
                } else if (Integer.parseInt(cart) == 3167) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/appliances.jpg";
                } else if (Integer.parseInt(cart) == 3168) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/Real_Estate.png";
                } else if (Integer.parseInt(cart) == 3169) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/appliance.jpg";
                } else if (Integer.parseInt(cart) == 9999) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/Auction.png";
                } else if (Integer.parseInt(cart) == 0000) {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/liquor1.jpg";
                }
            }else {
                if (cart == "NewArrivals" || cart == "99990") {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/newarrivals.jpg";
                    cart = "99990";
                } else if (cart == "OnSpecial" || cart == "99991") {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/onspecial.jpg";
                    cart = "99991";
                } else if (cart == "Popular" || cart == "99992") {
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/productbanner.jpg";
                    cart = "99992";
                } else {
                    Log.e("Category ID", cart);
                    imgbanner = "https://hammerandtongues.com/shopping/webservice/appimgs/productbanner.jpg";
                }
            }


            Picasso.with(this).load(imgbanner)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.ic_launcher_59)
                    .into(rur);


       } catch (Exception ex) {
            Log.e("Error getting Banner", ex.toString());
       }
    }











    private boolean haveNetworkConnection( ) {
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

    public void loaduielement() {
        if (haveNetworkConnection() == true) {
            CategoryID = "";
            imgurl = "";

        } else {
            //Toast.makeText(this, "No Network", Toast.LENGTH_LONG);

            Toast ToastMessage = Toast.makeText(this,"No Network Connection!",Toast.LENGTH_LONG);
            View toastView = ToastMessage.getView();
            toastView.setBackgroundResource(R.drawable.toast_background);
            ToastMessage.show();

            //return;
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // default
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.offline).delayBeforeLoading(50)
                .cacheInMemory(false) // default
                .cacheOnDisk(false)
                .considerExifParams(true).resetViewBeforeLoading(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();
        //ProgressBar pgr = (ProgressBar) findViewById(R.id.progressBar1);
        //pgr.setVisibility(View.GONE);
        LinearLayout hzntlayout = (LinearLayout) findViewById(R.id.vertical);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int)(metrics.density * 160f);

        HorizontalScrollView topbar = new HorizontalScrollView(this);
        LinearLayout btnwrapper = new LinearLayout(this);
        btnwrapper.setOrientation(LinearLayout.HORIZONTAL);

        Log.d("Load UI Elements", "Top Buttons Before");
        btnwrapper.setMinimumHeight(20);
        LinearLayout.LayoutParams btnsize = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        btnsize.setMargins(2, 2, 2, 2);



        Button btnNew = new Button(this);
        Button btnPromo = new Button(this);
        Button btnPopular = new Button(this);

        btnNew.setText("New Arrivals");
        btnNew.setTextColor(Color.WHITE);
        btnNew.setTextSize(12);
        btnNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // do stuff
                String id1 = "NewArrivals";
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Category, id1);
                editor.commit();
                Intent i = new Intent(CategoriesActivity.this, Products_List.class);
                
                startActivity(i);
            }

        });

        btnPromo.setText("On Special");
        btnPromo.setTextColor(Color.WHITE);
        btnPromo.setTextSize(12);
        btnPromo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // do stuff
                String id1 = "OnSpecial";
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Category, id1);
                editor.commit();
                Intent i = new Intent(CategoriesActivity.this, Products_List.class);
                
                startActivity(i);
            }

        });

        btnPopular.setText("Most Popular");
        btnPopular.setTextColor(Color.WHITE);
        btnPopular.setTextSize(12);
        btnPopular.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // do stuff
                String id1 = "Popular";
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Category, id1);
                editor.commit();
                Intent i = new Intent(CategoriesActivity.this, Products_List.class);
                startActivity(i);

            }

        });

            int length2 = (int)(metrics.density *125);
            int width2 = (int)(metrics.density * 35);

            LinearLayout.LayoutParams btnsize2 = new LinearLayout.LayoutParams(length2,width2,1);
            btnsize2.setMargins(15, 0, 15, 0);

            btnNew.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnNew.setLayoutParams(btnsize2);
            btnPopular.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnPopular.setLayoutParams(btnsize2);
            btnPromo.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnPromo.setLayoutParams(btnsize2);

        btnwrapper.addView(btnNew);
        btnwrapper.addView(btnPopular);
        btnwrapper.addView(btnPromo);

        topbar.addView(btnwrapper);
        hzntlayout.addView(topbar);
        Log.d("Load UI Elements", "Top Buttons After");

        //
        // new GetCategories().execute();
        dbHandler = new DatabaseHelper(this);
        //dbHandler.async_data();
        if (dbHandler.getCategories() != null) {
            Cursor cursor = dbHandler.getCategories();
            if (cursor != null && cursor.moveToFirst()) {
                Log.e("Cursor Full", cursor.getColumnCount() + " Columns");
                int cartitms[] = new int[cursor.getCount()];
                int i = 0;

                Log.e("Cursor Full", " Categories" + DatabaseUtils.dumpCursorToString(cursor));



                do {

                    name = cursor.getString(2);
                    post_id = cursor.getString(1);
                    CategoryID = cursor.getString(1);


                    LinearLayout btnMoreWrapper = new LinearLayout(this);



                    LinearLayout.LayoutParams hzvwParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    //System.out.println("");

                    hzvwParams.setMargins(12, 12, 12, 2);








                    btnMoreWrapper.setOrientation(LinearLayout.VERTICAL);
                    Button btnMore = new Button(this);
                    btnMore.setText(name);
                    btnMore.setId(Integer.parseInt(post_id));
                    btnMore.setTextSize(20);
                    btnMore.setWidth(100);
                    btnMore.setHeight(10);
                    btnMore.setTypeface(null, Typeface.BOLD);
                    btnMore.setTextColor((getResources().getColor(R.color.colorPrimary)));
                    btnMore.setBackgroundColor(Color.TRANSPARENT);
                    btnMore.setTransformationMethod(null);
                    btnMore.setPadding(2,2,2,2);
                    btnMore.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // do stuff
                            String id1 = Integer.toString(view.getId());
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(Category, id1);
                            editor.commit();
                            Intent i = new Intent(CategoriesActivity.this, Products_List.class);

                            startActivity(i);
                        }

                    });



                    TextView txt01 = new TextView(this);
                    txt01.setText(name);
                    txt01.setTypeface(null, Typeface.BOLD);
                    txt01.setTextSize(20);
                    txt01.setClickable(true);
                    txt01.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                    // do stuff
                    String id1 = Integer.toString(view.getId());
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(CategoryID, id1);
                    editor.commit();
                    Intent i = new Intent(CategoriesActivity.this, Products_List.class);

                    startActivity(i);
                }

                    });

                    HorizontalScrollView hsview = new HorizontalScrollView(this);
                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    // layout.setMinimumHeight(90);
                    //layout.setPadding(0, 0, 0, 10);

                    //Category Heading



                    //System.out.println("");




    imgstore[i] = new ImageView(this);



                    btnMoreWrapper.setGravity(Gravity.CENTER_VERTICAL | Gravity.TOP);



                    btnMoreWrapper.addView(btnMore);
                    btnMoreWrapper.addView(imgstore[i],btnsize);
                    //  btnMoreWrapper.setMinimumHeight(90);
                    hzntlayout.addView(btnMoreWrapper);
                    //hzntlayout.addView(hzvw);
                    btnMoreWrapper.setClickable(true);
                    btnMoreWrapper.setId(Integer.parseInt(post_id));


                    btnMoreWrapper.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // do stuff
                            String id1 = Integer.toString(view.getId());
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(Category, id1);
                            editor.commit();
                            Intent i = new Intent(CategoriesActivity.this, Products_List.class);

                            startActivity(i);
                        }

                    });




    imgurl = "";


    //hzvw.addView(hzntlayout);



        Log.e("Bannerload", "load banner not null");
                    SetBanner(imgstore[i], CategoryID);

                    //LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ViewGroup.LayoutParams layoutParams = imgstore[i].getLayoutParams();


                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgstore[i].getLayoutParams();

                    params.gravity=Gravity.CENTER;

                    btnMore.setGravity(Gravity.CENTER);

        int imglength = (int) (metrics.density * 200);
        int imgwidth = (int) (metrics.density * 150);


        layoutParams.width = imglength;
        layoutParams.height = imgwidth;
                    imgstore[i].setBackgroundColor(Color.TRANSPARENT);

        imgstore[i].setLayoutParams(params);

                    //imgstore[i].setPadding(2,2,2,10);
                    imgstore[i].setPadding(2,2,2,2);














                } while (cursor.moveToNext());
                cursor.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i<imgstore.length; i++ ) {
            Picasso.with(this).cancelRequest(imgstore[i]);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i<imgstore.length; i++ ) {
            Picasso.with(this).cancelRequest(imgstore[i]);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        for (int i = 0; i<imgstore.length; i++ ) {
            Picasso.with(this).cancelRequest(imgstore[i]);
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

       // Toast.makeText(CategoriesActivity.this, "Item added to Favourites!", Toast.LENGTH_LONG).show();

        Toast ToastMessage = Toast.makeText(this,"Item added to Favourites!",Toast.LENGTH_LONG);
        View toastView = ToastMessage.getView();
        toastView.setBackgroundResource(R.drawable.toast_background);
        ToastMessage.show();

        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        CartManagement crtItms = db.getCartItemsCount(cartid);
        if (crtItms != null) {
            Intent i = new Intent(CategoriesActivity.this, Products_List.class);
            startActivity(i);
            //Toast.makeText(CategoriesActivity.this, "Network is Currently Unavailable", Toast.LENGTH_LONG).show();


            Toast ToastMessage1 = Toast.makeText(this,"Network is Currently Unavailable!",Toast.LENGTH_LONG);
            View toastView1 = ToastMessage1.getView();
            toastView1.setBackgroundResource(R.drawable.toast_background);
            ToastMessage1.show();

            //idCartItems.setText("Items in cart: " + String.valueOf((crtItms.get_Cart_Items_Count())));
        } else {
            //idCartItems.setText("Items in cart: 0");
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
        DatabaseHelper db = new DatabaseHelper(this);
        int qty = 1;

        int id = Integer.parseInt(pid.toString());
        double itmprice =  db.getProductPrice(id);
        double subtot = db.getProductPrice(id);
        int seller = Integer.parseInt(sharedpreferences.getString("seller", ""));
        Log.d("Notifications", "Picking up variables for saving" + String.valueOf((qty))+ " <= Quantity; " + String.valueOf(cartid) + "<=This is the current cart ID");

        CartManagement ctm = new CartManagement(cartid ,db.getProductName(id),qty,id,imgurl,subtot,itmprice, seller);
        Log.d("Notifications", "Saving the product to cart with a cart id");
        db.addCartItem(ctm);
        Log.d("Notifications", "Finished saving an item");
        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        CartManagement crtItms = db.getCartItemsCount(cartid);
        if (crtItms != null) {
            Toast.makeText(this,"Item Added to Cart with : " + String.valueOf((crtItms.get_Cart_Items_Count())) +
                    "items Total amount so far: "+
                    currencyFormatter.format(Double.parseDouble(String.valueOf(crtItms.get_Cart_Items_Value())) )
                    ,Toast.LENGTH_LONG).show();
            Intent i = new Intent(CategoriesActivity.this, CategoriesActivity.class);
            startActivity(i);
            //idCartItems.setText("Items in cart: " + String.valueOf((crtItms.get_Cart_Items_Count())));
        }
        else
        {
            //idCartItems.setText("Items in cart: 0");
        }

    }


    public void newCart (View view) {
        SharedPreferences shared = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String TAG ="NEW CART";
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
        Toast.makeText(this,"New shopping Session Created"+String.valueOf(cartno.get_CartID()),Toast.LENGTH_LONG).show();
        currcart=cartno.get_CartID();

            /*
       catch (Exception ex)
        {
            Log.d("Problemo!", "Error while retrieving the saved cart id " + ex.getMessage());
        }
        */
    }

  class GetCategories extends AsyncTask<Void, Void, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*
                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Loading...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            */
        }

        @Override
        protected String doInBackground(Void... args) {

return  "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String posts) {
            //dismiss the dialog once asynctask  complete
          //  if (posts != null) {
                loaduielement();
            //}
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            Intent intent = new Intent(CategoriesActivity.this, Search.class);

            startActivity(intent);
            return true;
        }
        if (id == R.id.cart_item) {
            Intent intent = new Intent(CategoriesActivity.this, Cart.class);

            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.myaccount) {
            Intent intent = new Intent(CategoriesActivity.this, UserActivity.class);

            startActivity(intent);
        } else if (id == R.id.cart) {
            Intent intent = new Intent(CategoriesActivity.this, Cart.class);

            startActivity(intent);

        } else if (id == R.id.chat) {
            Intent intent = new Intent(CategoriesActivity.this, Chat_Webview.class);

            startActivity(intent);

        } else if (id == R.id.mytransactions) {
            Intent intent = new Intent(CategoriesActivity.this, TransactionHistory.class);

            startActivity(intent);

        } /* else if (id == R.id.favourites) {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);


        } else if (id == R.id.messages) {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);

        } */

        else if (id == R.id.myfavourites) {
            Intent intent = new Intent(CategoriesActivity.this, Favourites.class);
            startActivity(intent);
        }

        else if (id == R.id.about_us) {
            Intent intent = new Intent(CategoriesActivity.this, AboutUs.class);

            startActivity(intent);

        } else if (id == R.id.contact_us) {
            Intent intent = new Intent(CategoriesActivity.this, ContactUs.class);

            startActivity(intent);

        } else if (id == R.id.terms) {
            Intent intent = new Intent(CategoriesActivity.this, TermsAndConditions.class);

            startActivity(intent);
        }
        else if (id == R.id.faqs) {
            Intent intent = new Intent(CategoriesActivity.this, Faqs.class);

            startActivity(intent);
        }

        else if (id == R.id.invite) {
            Intent intent = new Intent(CategoriesActivity.this, Invite_friends.class);
            startActivity(intent);



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


}