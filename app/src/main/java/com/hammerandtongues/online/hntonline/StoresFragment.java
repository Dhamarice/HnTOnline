package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

/**
 * Created by NgonidzaIshe on 16/5/2016.
 */
public class StoresFragment  extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View mainView;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Product = "idKey";
    public static final String ProductName = "nameKey";
    public static  final  String StoreId = "idcateg";
    SharedPreferences sharedpreferences;

    DatabaseHelper dbHandler;
    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();


    //Global Variables
    public String Categories;
    public String findstoreletter;
    public String Products;
    public String name, desc;
    public String imgurl;
     public String post_id = "";
      int x = 1;
    int currcart;



    private TextView txtcartitems;
    private int cnt_cart,  flag=0;

    ImageView imgstore[] = new ImageView[200];// Initialised
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    EditText txtfindStore;
    Button btnfindStore;
    LinearLayout clicklayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.gc();


        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.getString("CartID", "") != null && sharedpreferences.getString("CartID", "") != "") {
            currcart = Integer.parseInt(sharedpreferences.getString("CartID", ""));
        } else {
            currcart = 0;
        }


       try {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_stores);





           sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
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



           SharedPreferences.Editor editor = sharedpreferences.edit();
           editor.putString("Categorystart", "Store");
           editor.commit();






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
                       Intent intent = new Intent(StoresFragment.this, CategoriesActivity.class);
                       startActivity(intent);
                   }
               });

               btnstore = (Button) findViewById(R.id.btn_stores);
               btnstore.setTextColor(getResources().getColor(R.color.colorPrimary));
               btnstore.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       //Intent intent = new Intent(StoresFragment.this, StoresFragment.class);
                       //startActivity(intent);
                   }
               });

               btnpromo = (Button) findViewById(R.id.btn_promos);
               btnpromo.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent intent = new Intent(StoresFragment.this, PromotionsFragment.class);
                       startActivity(intent);
                   }
               });





               final LinearLayout btnhome, btncategs, btnsearch, btncart, btnprofile;



               btnhome = (LinearLayout) findViewById(R.id.btn_home);
               btnhome.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent intent = new Intent(StoresFragment.this, MainActivity.class);
                       startActivity(intent);
                   }
               });

               btncategs = (LinearLayout) findViewById(R.id.btn_Categories);
               btncategs.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent intent = new Intent(StoresFragment.this, CategoriesActivity.class);
                       startActivity(intent);
                   }
               });

               btncart = (LinearLayout) findViewById(R.id.btn_Cart);
               btncart.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent intent = new Intent(StoresFragment.this, Chat_Webview.class);
                       startActivity(intent);
                   }
               });

               btnprofile = (LinearLayout) findViewById(R.id.btn_Profile);
               btnprofile.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent intent = new Intent(StoresFragment.this, UserActivity.class);
                       startActivity(intent);
                   }
               });

               btnsearch = (LinearLayout) findViewById(R.id.btn_Search01);
               btnsearch.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent intent = new Intent(StoresFragment.this, Search.class);
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



               txtfindStore = (EditText) findViewById(R.id.txt_find_store);



               btnfindStore = (Button) findViewById(R.id.btn_find);
               btnfindStore.setClickable(true);
               btnfindStore.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Log.e("Find button", "onClick: has been clicked" + txtfindStore.getText().toString());


                       if(txtfindStore.getText().toString().contentEquals("")){


                           Log.e("Find button", "onClick: has been clicked with no value" + txtfindStore.getText().toString());

                           loaduielements();
                       }

                       else {

                           Log.e("Find button", "onClick: has been clicked with value" + txtfindStore.getText().toString());
                           findstoreletter = txtfindStore.getText().toString();

                           loadsearch(findstoreletter);
                       }

                   }
               });

               clicklayout = (LinearLayout) findViewById(R.id.cliclayout);
               clicklayout.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Log.e("Find button", "onClick: has been clicked" + txtfindStore.getText().toString());


                       if(txtfindStore.getText().toString().contentEquals("")){


                           Log.e("Find button", "onClick: has been clicked with no value" + txtfindStore.getText().toString());

                           loaduielements();
                       }

                       else {

                           Log.e("Find button", "onClick: has been clicked with value" + txtfindStore.getText().toString());
                           findstoreletter = txtfindStore.getText().toString();

                           loadsearch(findstoreletter);
                       }

                   }
               });





               if (crtItms != null) {
                   cnt_cart = (crtItms.get_Cart_Items_Count());
               } else {
                   cnt_cart = 0;
               }
           } catch (Exception ex)

           {
               Log.e("Main Thread Exception", ex.toString());

           }
        /*
         return view;*/
           // ATTENTION: This was auto-generated to implement the App Indexing API.
           // See https://g.co/AppIndexing/AndroidStudio for more information.
           client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

       }






    catch (OutOfMemoryError  ex)
    {
        Log.e("Main Thread Exception", "Error: " + ex.toString());
        System.gc();
    }
    }

    public void loaduielements() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.storesvw);
        layout.removeAllViews();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);

        dbHandler = new DatabaseHelper(this);
        int i;


            if (dbHandler.getallStores() != null) {
                Cursor cursor = dbHandler.getallStores();
                if (cursor != null && cursor.moveToFirst()) { // staff from database
                    Log.e("Cursor Full", cursor.getColumnCount() + " Columns");
                    Log.e("Product_list", "Values" + DatabaseUtils.dumpCursorToString(cursor));

                    //for (i = 0; i < i2; i++)  imgstore.length
                    int i2 = cursor.getCount();
                    for (i = 0; i < i2; i++) {
                        LinearLayout hzvw = new LinearLayout(this);
                        LinearLayout.LayoutParams hzvwParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                        //System.out.println("");

                        hzvwParams.setMargins(12, 12, 12, 2);

                        hzvw.setOrientation(LinearLayout.HORIZONTAL);
                        hzvw.setBackgroundColor(Color.WHITE);

                        imgstore[i] = new ImageView(this);


                        hzvw.addView(imgstore[i]);
                        imgurl = "";
                        name = cursor.getString(2);
                        //name = cursor.getColumnIndex("store_name");
                        post_id = cursor.getString(1);
                        imgurl = cursor.getString(4);
                        desc = cursor.getString(2);

                        LinearLayout storeinfo = new LinearLayout(this);
                        storeinfo.setOrientation(LinearLayout.VERTICAL);

                        TextView txtShopName = new TextView(this);
                        txtShopName.setTypeface(null, Typeface.BOLD);
                        txtShopName.setPaintFlags(txtShopName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        txtShopName.setText(name.toString().toUpperCase());
                        txtShopName.setTextColor(getResources().getColor(R.color.colorOrange));
                        try {
                            if (desc.length() > 100) {
                                String s1 = desc.substring(0, 1).toUpperCase();
                                desc = s1 + desc.substring(1).toLowerCase();
                                desc = Html.fromHtml(desc.substring(0, 100)).toString() + "...";
                            } else if (desc.length() < 100 && desc.length() > 0) {
                                String s1 = desc.substring(0, 1).toUpperCase();
                                desc = s1 + desc.substring(1).toLowerCase();
                            }
                        } catch (Exception e) {

                        }

                        TextView txtDesc = new TextView(this);
                        txtDesc.setText(desc);
                        txtDesc.setTextSize(20);

                        storeinfo.setPadding(10, 20, 0, 20);
                        storeinfo.addView(txtShopName);
                        storeinfo.addView(txtDesc);


                        hzvw.addView(storeinfo);
                        layout.addView(hzvw, hzvwParams);
                        if (imgurl != "") {

                            //imageLoader.displayImage(imgurl, imgstore[i], options);
                            Picasso.with(this).load(imgurl)
                                    .placeholder(R.drawable.progress_animation)
                                    .error(R.drawable.ic_launcher_59)
                                    .into(imgstore[i]);

                            ViewGroup.LayoutParams layoutParams = imgstore[i].getLayoutParams();

                            int imglength = (int) (metrics.density * 80);
                            int imgwidth = (int) (metrics.density * 80);


                            layoutParams.width = imglength;
                            layoutParams.height = imgwidth;

                            imgstore[i].setLayoutParams(layoutParams);
                            hzvw.setId(Integer.parseInt(post_id));
                            hzvw.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    // do stuff
                                    String id1 = Integer.toString(view.getId());
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(StoreId, id1);
                                    editor.commit();
                                    Intent i = new Intent(StoresFragment.this, Store.class);
                                    startActivity(i);
                                }

                            });

                        }

                        DatabaseHelper db = new DatabaseHelper(this);
                        CartManagement crtItms = db.getCartItemsCount(currcart);
                        if (crtItms != null) {
                            cnt_cart = (crtItms.get_Cart_Items_Count());
                        } else {
                            cnt_cart = 0;
                        }

                        cursor.moveToNext();
                    }
                }
            }
        }









      public void  loadsearch( String theletter){



            LinearLayout layout = (LinearLayout) findViewById(R.id.storesvw);

          layout.removeAllViews();

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int densityDpi = (int) (metrics.density * 160f);

            dbHandler = new DatabaseHelper(this);

            int i;
            if (theletter != null) {

                Log.e("loadsearch called", "called loadsearch");


                if (dbHandler.searchStores(findstoreletter) != null) {
                    Cursor cursor = dbHandler.searchStores(findstoreletter);

                    Log.e("loadsearch called", "called handler bhoo");

                    if (cursor != null && cursor.moveToFirst()) { // staff from database
                        Log.e("Cursor Full", cursor.getColumnCount() + " Columns" + DatabaseUtils.dumpCursorToString(cursor));

                        //for (i = 0; i < i2; i++)  imgstore.length
                        int i2 = cursor.getCount();
                        for (i = 0; i < i2; i++) {
                            LinearLayout hzvw = new LinearLayout(this);
                            LinearLayout.LayoutParams hzvwParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                            //System.out.println("");

                            hzvwParams.setMargins(12, 12, 12, 2);

                            hzvw.setOrientation(LinearLayout.HORIZONTAL);
                            hzvw.setBackgroundColor(Color.WHITE);

                            imgstore[i] = new ImageView(this);


                            hzvw.addView(imgstore[i]);
                            imgurl = "";
                            name = cursor.getString(2);
                            //name = cursor.getColumnIndex("store_name");
                            post_id = cursor.getString(1);
                            imgurl = cursor.getString(4);
                            desc = cursor.getString(2);

                            LinearLayout storeinfo = new LinearLayout(this);
                            storeinfo.setOrientation(LinearLayout.VERTICAL);

                            TextView txtShopName = new TextView(this);
                            txtShopName.setTypeface(null, Typeface.BOLD);
                            txtShopName.setPaintFlags(txtShopName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            txtShopName.setText(name.toString().toUpperCase());
                            txtShopName.setTextColor(getResources().getColor(R.color.colorOrange));
                            try {
                                if (desc.length() > 100) {
                                    String s1 = desc.substring(0, 1).toUpperCase();
                                    desc = s1 + desc.substring(1).toLowerCase();
                                    desc = Html.fromHtml(desc.substring(0, 100)).toString() + "...";
                                } else if (desc.length() < 100 && desc.length() > 0) {
                                    String s1 = desc.substring(0, 1).toUpperCase();
                                    desc = s1 + desc.substring(1).toLowerCase();
                                }
                            } catch (Exception e) {

                            }

                            TextView txtDesc = new TextView(this);
                            txtDesc.setText(desc);
                            txtDesc.setTextSize(20);

                            storeinfo.setPadding(10, 20, 0, 20);
                            storeinfo.addView(txtShopName);
                            storeinfo.addView(txtDesc);


                            hzvw.addView(storeinfo);
                            layout.addView(hzvw, hzvwParams);
                            if (imgurl != "") {

                                //imageLoader.displayImage(imgurl, imgstore[i], options);
                                Picasso.with(this).load(imgurl)
                                        .placeholder(R.drawable.progress_animation)
                                        .error(R.drawable.ic_launcher_59)
                                        .into(imgstore[i]);

                                ViewGroup.LayoutParams layoutParams = imgstore[i].getLayoutParams();

                                int imglength = (int) (metrics.density * 80);
                                int imgwidth = (int) (metrics.density * 80);


                                layoutParams.width = imglength;
                                layoutParams.height = imgwidth;

                                imgstore[i].setLayoutParams(layoutParams);
                                hzvw.setId(Integer.parseInt(post_id));
                                hzvw.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        // do stuff
                                        String id1 = Integer.toString(view.getId());
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString(StoreId, id1);
                                        editor.commit();
                                        Intent i = new Intent(StoresFragment.this, Store.class);
                                        startActivity(i);
                                    }

                                });

                            }

                            DatabaseHelper db = new DatabaseHelper(this);
                            CartManagement crtItms = db.getCartItemsCount(currcart);
                            if (crtItms != null) {
                                cnt_cart = (crtItms.get_Cart_Items_Count());
                            } else {
                                cnt_cart = 0;
                            }

                            cursor.moveToNext();
                        }
                    }


                }

            }





        }
























    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "StoresFragment Page", // TODO: Define a title for the content shown.
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

        for (int i = 0; i<imgstore.length; i++ ) {
            Picasso.with(this).cancelRequest(imgstore[i]);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "StoresFragment Page", // TODO: Define a title for the content shown.
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

    class GetCategories extends AsyncTask<Void, Void, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(StoresFragment.this);
            pDialog.setMessage("Getting Stores...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            pDialog.dismiss();
/*
            dbHandler = new DatabaseHelper(StoresFragment.this, null, null, 1);
            dbHandler.async_data();
            if (dbHandler.getStores() != null) {
                Cursor cursor = dbHandler.getStores();
                if (cursor != null && cursor.moveToFirst()) {
                    return "Success";
                }
            }
            */
            Log.e("Stores Activity", "Panonzi Pano");
            return "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String posts) {
            //dismiss the dialog once asynctask  complete
            //     pDialog.dismiss();

            Categories = null;
            //if (posts != null) {
            loaduielements();
            // Toast.makeText(getActivity(), "Happy Shopping", Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(StoresFragment.this, Search.class);

            startActivity(intent);
            return true;
        }
        if (id == R.id.cart_item) {
            Intent intent = new Intent(StoresFragment.this, Cart.class);

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
            Intent intent = new Intent(StoresFragment.this, UserActivity.class);

            startActivity(intent);
        } else if (id == R.id.cart) {
            Intent intent = new Intent(StoresFragment.this, Cart.class);

        } else if (id == R.id.chat) {
            Intent intent = new Intent(StoresFragment.this, Cart.class);
            startActivity(intent);

            startActivity(intent);
        } else if (id == R.id.mytransactions) {
            Intent intent = new Intent(StoresFragment.this, TransactionHistory.class);

            startActivity(intent);

        } /* else if (id == R.id.favourites) {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);


        } else if (id == R.id.messages) {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);

        } */ else if (id == R.id.about_us) {
            Intent intent = new Intent(StoresFragment.this, AboutUs.class);

            startActivity(intent);

        } else if (id == R.id.contact_us) {
            Intent intent = new Intent(StoresFragment.this, ContactUs.class);

            startActivity(intent);

        } else if (id == R.id.terms) {
            Intent intent = new Intent(StoresFragment.this, TermsAndConditions.class);

            startActivity(intent);
        } else if (id == R.id.faqs) {
            Intent intent = new Intent(StoresFragment.this, Faqs.class);

            startActivity(intent);
        }

        else if (id == R.id.invite) {
            Intent intent = new Intent(StoresFragment.this, Invite_friends.class);
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