package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by NgonidzaIshe on 16/5/2016.
 */
public class PromotionsFragment  extends AppCompatActivity {
    View mainView;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Product = "idKey";
    public static final String ProductName = "nameKey";
    public static  final  String StoreId = "idcateg";
    SharedPreferences sharedpreferences;


    // Progress Dialog
    private ProgressDialog pDialog;
    DatabaseHelper dbHandler;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private static final String GETSTORES_URL = "https://devshop.hammerandtongues.com/webservice/getpromostores.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_CATEGORIES = "posts";

    //Global Variables
    public String Categories;
    public String Products;
    public String name, desc, openhrs, closehrs, day;
    public String imgurl;
    public String post_id = "";
    public String CategoryID = "";
    ImageView imgvw;
    int x = 1;
    private Context mcontext;
    private TextView txtcartitems;
    private int cnt_cart,  flag=0, currcart;

    ImageView imgstore[] = new ImageView[150];
    private  TextView noresult;
    Calendar calendar = Calendar.getInstance();
    int dayofweek =calendar.get(Calendar.DAY_OF_WEEK);
    int hourofday = calendar.get(Calendar.HOUR_OF_DAY);

    @Override
    public void onCreate( Bundle savedInstanceState) {
        System.gc();
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_promotions);
            //View view = inflater.inflate(R.layout.activity_promotions, container, false);
            sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            System.gc();
             noresult = (TextView) findViewById(R.id.txtinfo);




            final SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            if (shared.getString("CartID", "") != null && shared.getString("CartID", "") != "") {
                currcart = Integer.parseInt(shared.getString("CartID", ""));
            } else {
                currcart = 0;
            }



            try {

                try {
                    Products = new GetCategories().execute().get();
                } catch (InterruptedException e) {
                    Log.e("Stores Paye 01", e.toString());
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Log.e("Stores Paye 02", e.toString());
                }
                final Button btncateg, btnstore, btnpromo;

                btncateg = (Button) findViewById(R.id.btn_categs);
                btncateg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PromotionsFragment.this, CategoriesActivity.class);

                        startActivity(intent);
                    }
                });

                btnstore = (Button) findViewById(R.id.btn_stores);
                btnstore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PromotionsFragment.this, StoresFragment.class);
                        startActivity(intent);
                    }
                });

                btnpromo = (Button) findViewById(R.id.btn_promos);
                btnpromo.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnpromo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent intent = new Intent(PromotionsFragment.this, PromotionsFragment.class);

                        //startActivity(intent);
                    }
                });

                final LinearLayout btnhome, btncategs, btnsearch, btncart, btnprofile;

                btnhome = (LinearLayout) findViewById(R.id.btn_home);
                btnhome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PromotionsFragment.this, MainActivity.class);

                        startActivity(intent);
                    }
                });

                btncategs = (LinearLayout) findViewById(R.id.btn_Categories);
                btncategs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PromotionsFragment.this, CategoriesActivity.class);

                        startActivity(intent);
                    }
                });

                btncart = (LinearLayout) findViewById(R.id.btn_Cart);
                btncart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PromotionsFragment.this, Chat_Webview.class);

                        startActivity(intent);
                    }
                });

                btnprofile = (LinearLayout) findViewById(R.id.btn_Profile);
                btnprofile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PromotionsFragment.this, UserActivity.class);

                        startActivity(intent);
                    }
                });

                btnsearch = (LinearLayout) findViewById(R.id.btn_Search01);
                btnsearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PromotionsFragment.this, Search.class);

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

    public void loaduielements() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.promovw);

        dbHandler = new DatabaseHelper(this);
        //dbHandler.async_data();
        if (dbHandler.getPromoStores() != null) {
            Cursor cursor = dbHandler.getPromoStores();
            if (cursor != null && cursor.moveToFirst()) {
                Log.e("Cursor Full", cursor.getColumnCount() + " Columns");
                int cartitms[] = new int[cursor.getCount()];

                for (int i = 0; i < cursor.getCount(); i++) {
                    LinearLayout hzvw = new LinearLayout(this);
                    LinearLayout.LayoutParams hzvwParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    hzvwParams.setMargins(12, 12, 12, 2);

                    hzvw.setOrientation(LinearLayout.HORIZONTAL);
                    hzvw.setBackgroundColor(Color.WHITE);

                    imgurl = "";
                    name = cursor.getString(2);
                    post_id = cursor.getString(1);
                    imgurl = cursor.getString(4);
                    desc = cursor.getString(3);
                    day = cursor.getString(7);
                    openhrs = cursor.getString(8);
                    closehrs = cursor.getString(9);


                    LinearLayout promos = new LinearLayout(this);
                    promos.setOrientation(LinearLayout.VERTICAL);
                    TextView txtShopName = new TextView(this);
                    txtShopName.setTypeface(null, Typeface.BOLD);
                    txtShopName.setPaintFlags(txtShopName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    txtShopName.setText(name.toString().toUpperCase());
                    txtShopName.setTextColor(getResources().getColor(R.color.colorOrange));

                    try {
                        imgstore[i] = new ImageView(this);
                        promos.addView(txtShopName);
                        promos.addView(imgstore[i]);
                        hzvw.addView(promos);
                        layout.addView(hzvw, hzvwParams);
                        if (imgurl != "") {

                            //imageLoader.displayImage(imgurl, imgstore[i], options);
                            Picasso.with(this).load(imgurl)
                                    .placeholder(R.drawable.progress_animation)
                                    .error(R.drawable.ic_error)
                                    .into(imgstore[i]);

                            android.view.ViewGroup.LayoutParams layoutParams = imgstore[i].getLayoutParams();
                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            //layoutParams.height = 305;
                            imgstore[i].setLayoutParams(layoutParams);
                            hzvw.setId(Integer.parseInt(post_id));

                            hzvw.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

Log.e("Day of week", "from java" + dayofweek);
                                    Log.e("Hour of day", "from java" + hourofday);

                                    Log.e("Day of shop", "from database" + day);

                                    Log.e("Opening hour", "from database" + openhrs);

                                    Log.e("Closing hour", "from database" + closehrs);

                                    // do stuff
                                    String id1 = Integer.toString(view.getId());
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(StoreId, id1);
                                    editor.commit();
                                    Intent i = new Intent(PromotionsFragment.this, Store.class);
                                    startActivity(i);
                                }

                            });

                        }
                    } catch (Exception ex) {
                        Log.e("Image Button Error", ex.toString());
                    }
            cursor.moveToNext();
                }

                noresult.setVisibility(View.GONE);
            }else {
                noresult.setText("No Promotional Stores to Display At The Moment");
                Toast.makeText(this, "No Promotional Stores to Display At The Moment", Toast.LENGTH_LONG).show();
                noresult.setVisibility(View.VISIBLE);
            }

            noresult.setVisibility(View.GONE);
        }else {
            noresult.setText("No Promotional Stores to Display At The Moment");
            Toast.makeText(this,"No Promotional Stores to Display At The Moment",Toast.LENGTH_LONG).show();
            noresult.setVisibility(View.VISIBLE);
        }

    }

    class GetCategories extends AsyncTask<Void, Void, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */

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
            dbHandler = new DatabaseHelper(PromotionsFragment.this);
            //dbHandler.async_data();
            if (dbHandler.getPromoStores() != null) {
                Cursor cursor = dbHandler.getPromoStores();
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
            //dismiss the dialog once asynctask  complete


            //     pDialog.dismiss();

            Categories = null;
            if (posts != null) {
                loaduielements();
                // Toast.makeText(getActivity(), "Happy Shopping", Toast.LENGTH_LONG).show();
            }

        }

    }
}


