package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NgonidzaIshe on 30/6/2016.
 */
public class TransactionHistory  extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    private String cartID="";
    private TextView storeName, storeDesc;
    public static final String OrderNumber = "idKey";
    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private static final String GETTRANSACTION_HISTORY_URL = "https://shopping.hammerandtongues.com/webservice/gettransactionhistory.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PRODUCTDETAILS = "posts";
    private static final String ECORESPONSE_URL = "https://shopping.hammerandtongues.com/wp-content/themes/Walleto/CheckDb.php";
    private static final String DELETEORDER_URL = "https://shopping.hammerandtongues.com/webservice/deleteoder.php";
    private  double total;
    private  String OrderID, uid, ptype, totalPrc;
    int success;
    private int currcart;
    DatabaseHelper dbHandler;
    String userID, strOrders;
    String OrderNo,OrderDate, Total,Items="";
    String imgurl ="";
    ImageView imgstore[] = new ImageView[200];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Runtime.getRuntime().gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history);

        final SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        OrderID=  shared.getString("OrderID", "");
        uid = (shared.getString("userid", ""));
        totalPrc = (shared.getString("totalPrc", ""));

        if (shared.getString("CartID", "") != null && shared.getString("CartID", "") !="") {

            currcart = Integer.parseInt(shared.getString("CartID", ""));

        }

        else currcart = 0;


        System.gc();


        if (shared.getString("userid", "") != null && shared.getString("userid", "") !="") {
            userID = (shared.getString("userid", ""));
            GetProductDetails();


            if (shared.getString("ptype", "") != null && shared.getString("ptype", "") !="" && shared.getString("ptype", "") != "DepositPaynow") {
                ptype = (shared.getString("ptype", ""));
                //new ProcessRequest().execute();
               ProcessRequest( ptype, ECORESPONSE_URL, OrderID, uid, totalPrc );


                SharedPreferences.Editor editor = shared.edit();
                editor.remove("ptype");
                editor.apply();
            }

            if(shared.getString("ptype", "") == "DepositPaynow"){

                Intent intent = new Intent(TransactionHistory.this, Finances.class);
                startActivity(intent);

                SharedPreferences.Editor editor = shared.edit();
                editor.remove("ptype");
                editor.apply();

            }


        }
        else {
            //Toast.makeText(this,"You Are Not Logged In",Toast.LENGTH_LONG).show();

            Toast ToastMessage = Toast.makeText(this,"You Are Not Logged In!",Toast.LENGTH_LONG);
            View toastView = ToastMessage.getView();
            toastView.setBackgroundResource(R.drawable.toast_background);
            ToastMessage.show();
            ProgressBar pg1 = (ProgressBar) findViewById(R.id.progressBar1);
            pg1.setVisibility(View.GONE);
        }




        Button addtocart = (Button) findViewById(R.id.btn_back);
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(TransactionHistory.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button backtoshop = (Button) findViewById(R.id.btn_checkout);
        backtoshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(TransactionHistory.this, UserActivity.class);
                startActivity(intent);

            }
        });

    }



    public void setuielements() {
        final SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
       final LinearLayout layout = (LinearLayout) findViewById(R.id.cartlayout);
        //Log.e("Set UI Elements", strOrders);
        final ProgressBar pgr = (ProgressBar) findViewById(R.id.progressBar1);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                pgr.setVisibility(View.GONE);

            }
        });

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // default
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_error).delayBeforeLoading(50)
                .cacheInMemory(false) // default
                .cacheOnDisk(false)
                .considerExifParams(true).resetViewBeforeLoading(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();


        JSONArray jsonarray02 = null;

        try {
            jsonarray02 = new JSONArray(strOrders);
        } catch (JSONException e) {
            Log.e("String Orders Error", e.toString());
            e.printStackTrace();
        }


        if (jsonarray02 != null) {
                //iterate through transactions in JSON string and
                for (int i = 0; i < jsonarray02.length(); i++) {
                   final LinearLayout itmcontr = new LinearLayout(this);

                   final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(10, 10, 10, 10);

                    itmcontr.setOrientation(LinearLayout.HORIZONTAL);
                    itmcontr.setBackgroundColor(Color.WHITE);

                    imgstore[i] = new ImageView(this);
                    itmcontr.addView(imgstore[i]);

                    try {
                        JSONObject jsonobject = jsonarray02.getJSONObject(i);
                        OrderNo = jsonobject.getString("OrderNo");
                        OrderDate = jsonobject.getString("OrderDate");
                        Total = jsonobject.getString("totalprice");
                        Items = jsonobject.getString("items");
                    } catch (JSONException ex) {
                        Log.e("JSON Error: ", ex.toString());
                        ex.printStackTrace();
                    }

                    imageLoader.displayImage(imgurl, imgstore[i], options);
                    android.view.ViewGroup.LayoutParams imglayoutParams = imgstore[i].getLayoutParams();
                    imglayoutParams.width = 150;
                    imglayoutParams.height = 150;
                    imgstore[i].setLayoutParams(imglayoutParams);

                    Log.e("Setting Banner", imgurl);
                    LinearLayout orderinfo = new LinearLayout(this);
                    orderinfo.setOrientation(LinearLayout.VERTICAL);
                    TextView ONo = new TextView(this);
                    TextView ODate = new TextView(this);
                    TextView OItems = new TextView(this);
                    TextView OTotal = new TextView(this);
                    ODate.setText("Order Date: " + OrderDate);
                    ONo.setText("Order Number: " + OrderNo);
                    OItems.setText("Items in Order: " + Items);
                    OTotal.setText("US $" + Total);
                    OTotal.setTextColor(getResources().getColor(R.color.colorAmber));
                    OTotal.setTypeface(null, Typeface.BOLD);
                    OTotal.setTextSize(13);

                    orderinfo.addView(ONo);
                    orderinfo.addView(ODate);
                    orderinfo.addView(OItems);
                    orderinfo.addView(OTotal);
                    itmcontr.addView(orderinfo);
                    itmcontr.setId(Integer.parseInt(OrderNo));
                    itmcontr.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // do stuff
                            String id1 = Integer.toString(view.getId());
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(OrderNumber, id1);
                            editor.commit();
                            //Intent i = new Intent(TransactionHistory.this, Product.class);
                            
                            //startActivity(i);
                        }

                    });


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            layout.addView(itmcontr, layoutParams);

                        }
                    });
                }

            }


        }

        public void seterror(){
            LinearLayout layout = (LinearLayout) findViewById(R.id.cartlayout);
            final ProgressBar pgr = (ProgressBar) findViewById(R.id.progressBar1);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    pgr.setVisibility(View.GONE);

                }
            });

            LinearLayout itmcontr = new LinearLayout(this);
            itmcontr.setBackgroundColor(Color.WHITE);
            TextView NetError = new TextView(this);
            NetError.setText("You are currenly offline, Please check your network connection... ");
            NetError.setTextSize(15);
            NetError.setTextColor(Color.RED);
            itmcontr.addView(NetError);
itmcontr.setVisibility(View.VISIBLE);
            layout.addView(itmcontr);
        }


    public void setnohistory(){
       final LinearLayout layout = (LinearLayout) findViewById(R.id.cartlayout);
        final ProgressBar pgr = (ProgressBar) findViewById(R.id.progressBar1);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                pgr.setVisibility(View.GONE);

            }
        });

       final LinearLayout itmcontr1 = new LinearLayout(this);
        itmcontr1.setBackgroundColor(Color.WHITE);
        TextView NetError = new TextView(this);
        NetError.setText("You have no history to display at the moment!... ");
        NetError.setTextSize(15);
        NetError.setTextColor(Color.RED);
        itmcontr1.addView(NetError);
        itmcontr1.setVisibility(View.VISIBLE);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                layout.addView(itmcontr1);

            }
        });

    }


    private void GetProductDetails() {


        //Log.e("NUMBER", (shared.getString("telno", "")));

        pDialog = new ProgressDialog(TransactionHistory.this);
        pDialog.setMessage("Getting  Transaction History...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        try {
            // Building Parameters
            total=0.0;
            final JSONObject json = new JSONObject();







            com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(TransactionHistory.this);



            StringRequest stringRequest = new StringRequest(Request.Method.POST, GETTRANSACTION_HISTORY_URL, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String s) {

                    //pDialog.dismiss();

                    Log.e("Success", "" + s);
                    //{"success":1,"message":"Username Successfully Added!"}

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int success = 0;
                        if (jsonObject.has("success")) {
                            success = jsonObject.getInt("success");
                        }
                        String message = "";
                        if (jsonObject.has("message")) {
                            message = jsonObject.getString("message");
                        }


                        Log.e("Get Cart Success", "" + success);


                        if (success == 1) {


                            Log.e("Get Cart Success", jsonObject.getString(TAG_PRODUCTDETAILS));
                            strOrders = jsonObject.getString(TAG_PRODUCTDETAILS);

                            setuielements();




                        }


                        else {

                            setnohistory();
                            //return jsonObject.getString(TAG_MESSAGE);

                            new AlertDialog.Builder(TransactionHistory.this)
                                    .setTitle("Info")
                                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {




                                        }
                                    })
                                    .setNegativeButton("", null)
                                    .setMessage(Html.fromHtml(jsonObject.getString(TAG_MESSAGE)))
                                    .show();
                        }


                    }

                            catch (JSONException e) {
                    e.printStackTrace();

                    Log.e("Try error: ", "Exception when continuing" + e.toString());

                    Toast ToastMessage = Toast.makeText(TransactionHistory.this, "No Intenet connection!", Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();
                }


            }


        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //pDialog.dismiss();

                Log.e("RUEERROR", "" + volleyError);

                //Toast.makeText(getContext(), "No Intenet connection!", Toast.LENGTH_SHORT).show();

                seterror();



            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> values = new HashMap();
                values.put("uid", userID);


                return values;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 500, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

        pDialog.dismiss();


    }


        catch (Exception e) {
        e.printStackTrace();
        Log.e("Process Error ", e.toString());


    }

}


    private void ProcessRequest( final String paytype, final String URL, final String OrderId, final String userid, final String totalPrice ) {


        //Log.e("NUMBER", (shared.getString("telno", "")));

        pDialog = new ProgressDialog(TransactionHistory.this);
        pDialog.setMessage("Processing Order & Payment...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        try {
            // Building Parameters
            total=0.0;
            final JSONObject json = new JSONObject();







                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(TransactionHistory.this);



                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {

                            //pDialog.dismiss();

                            Log.e("Success", "" + s);
                            //{"success":1,"message":"Username Successfully Added!"}

                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                int success = 0;
                                if (jsonObject.has("success")) {
                                    success = jsonObject.getInt("success");
                                }
                                String message = "";
                                if (jsonObject.has("message")) {
                                    message = jsonObject.getString("message");
                                }


                                Log.e("Get Cart Success", "" + success);


                                if (success == 1) {


                                    /*
                                    Log.e("Get Cart Success", "" + success);
                                    flag = 1;
                                    //oid = jsonObject.getInt(TAG_ID);

                                    if (oid != 0){Log.e("Get payment ID", "after oid" + oid);

                                        Log.e("Get order ID from prefs", "orderid: " + OrderId);


                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString("OrderID", String.valueOf(oid));
                                        editor.apply();}
*/


                                    if(paytype != null ){

                                        new AlertDialog.Builder(TransactionHistory.this)
                                                .setTitle("Info")
                                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        new AlertDialog.Builder(TransactionHistory.this)
                                                                .setTitle("")
                                                                .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {


                                                                        myDBHandler myDBHandler = new myDBHandler(TransactionHistory.this, null, null, 0);

                                                                        myDBHandler.clearCartItems();


                                                                        Intent intent = new Intent(TransactionHistory.this, MainActivity.class);
                                                                        startActivity(intent);




                                                                    }
                                                                })
                                                                .setNegativeButton("No", null)
                                                                .setMessage(Html.fromHtml("Clear cart?"))
                                                                .show();




                                                    }
                                                })
                                                .setNegativeButton("", null)
                                                .setMessage(Html.fromHtml(message))
                                                .show();



                                    }





                                }

                                else {
                                    new AlertDialog.Builder(TransactionHistory.this)
                                            .setTitle("Info")
                                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {




                                                }
                                            })
                                            .setNegativeButton("", null)
                                            .setMessage(Html.fromHtml(message))
                                            .show();
                                }


                            }

                            catch (JSONException e) {
                                e.printStackTrace();

                                Log.e("Try error: ", "Exception when continuing" + e.toString());

                                Toast ToastMessage = Toast.makeText(TransactionHistory.this, "No Intenet connection!", Toast.LENGTH_LONG);
                                View toastView = ToastMessage.getView();
                                toastView.setBackgroundResource(R.drawable.toast_background);
                                ToastMessage.show();
                            }


                        }


                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //pDialog.dismiss();

                            Log.e("RUEERROR", "" + volleyError);

                            //Toast.makeText(getContext(), "No Intenet connection!", Toast.LENGTH_SHORT).show();





                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> values = new HashMap();
                            values.put("userid", userid);
                            values.put("amt", totalPrice);
                            values.put("oid", OrderId);
                            values.put("ptype", paytype);

                            return values;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 500, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    requestQueue.add(stringRequest);

                    pDialog.dismiss();


        }


        catch (Exception e) {
            e.printStackTrace();
            Log.e("Process Error ", e.toString());


        }

    }





}

