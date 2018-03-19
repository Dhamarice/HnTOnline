package com.hammerandtongues.online.hntonline;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NgonidzaIshe on 2/6/2016.
 */
public class CheckoutFragment  extends Fragment {

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences shared;
    // Progress Dialog
    private ProgressDialog pDialog;

    private static final String GETQUANTITIES_URL = "https://devshop.hammerandtongues.com/webservice/getproductquantity.php";
    //private static final String GETQUANTITIES_URL = "http://10.0.2.2:8012/webservice/getproductquantity.php";
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject json=null;

    //JSON element ids from repsonse of php script:
    private static final String TAG_ID = "id";
    private static final String TAG_BALANCE = "balance";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PRODUCTDETAILS = "posts";
    private static final String TAG_USERRES = "levelmessage";

    //int success=0;


    private String Ewall, Balance, uid, sometotal, Discount,DiscountAmount,ecash_number, Otp, accno, wallbalance, amount;
    private String DlvryType, DlvryChrg, DlvryAdd, totalPrc, PPrice, Qty, PName, imgurl, productID, post_id, StoreName, quantity, PrName;
    private TextView balance;
    private int currcart,isSumarry,success,Count, oid, flag=0, totalAll, DlvryPrc, DscntPrc, OrdrPrc, ProdId ;
    private Double total=0.0;
    DatabaseHelper dbHandler;


    String totalprice, totalpriceupdated;
    RelativeLayout checkout;
    LinearLayout checkoutt ;
    TextView Response, Order, Nameinfo ;
    Button shopping;




    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {

            Runtime.getRuntime().gc();
            View view = inflater.inflate(R.layout.checkout_frag, container, false);
            //TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            shared = getActivity(). getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            System.gc();
            checkout = (RelativeLayout) view.findViewById(R.id.container);
            Order = (TextView) view.findViewById(R.id.order);
            Response = (TextView) view.findViewById(R.id.response);
            Nameinfo = (TextView) view.findViewById(R.id.nameinfo);
            shopping = (Button) view.findViewById(R.id.shopp);

            shopping.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }

            });

            shared = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if (shared.getString("CartID", "") != null && shared.getString("CartID", "") != "") {
                currcart = Integer.parseInt(shared.getString("CartID", ""));
            } else {
                currcart = 0;
            }


            if (shared.getString("userid", "") != null && shared.getString("userid", "") !="") {

                totalprice = (shared.getString("total", ""));

                new GetConnectionStatus().execute();
            }
            else{

                SharedPreferences.Editor editor = shared.edit();
                editor.remove("total");
                editor.apply();
                Response.setText(Html.fromHtml("<b>You are not logged in, Please go to my account and login to proceed...</b>"));
            }

            return view;
        }
        catch (Exception ex)
        {
            Log.e("Main Thread Exception", "Error: " + ex.toString());
            System.gc();
            return null;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity(). getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void nexttab()
    {
        TabLayout tabLayout=(TabLayout) getActivity().findViewById(R.id.tabs02);
        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager02);
        final PagerAdapter adapter = new PagerAdapter02 (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        //Log.e("Next 02", "Thus Far the lord has brought us");
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }


    class GetConnectionStatus extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(" Please Wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        protected Boolean doInBackground(String... urls) {
            // TODO: Connect
            if (isNetworkAvailable() == true) {
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
            try {


                if (result == true)

                {

                    new ProcessProducts().execute();

                    if (shared.getString("options", "") != null && shared.getString("options", "") != "") {

                        Log.e("Reloading activity","Tag" + totalprice );

                        Intent intent = new Intent(getActivity(), CheckOut_Activity.class);
                        startActivity(intent);
                        //Toast.makeText(getActivity(),"Order total updated!",Toast.LENGTH_SHORT).show();

                        Toast ToastMessage = Toast.makeText(getActivity(),"Order total updated!",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();

                        getActivity().finish();
                    }


                    if (totalprice.contentEquals("0.0")) {

                        SharedPreferences.Editor editor = shared.edit();
                        editor.remove("total");
                        editor.apply();

                        Order.setText(Html.fromHtml("<b>Product(s) out of stock and have been removed from cart.</b>"));
                        Response.setText(Html.fromHtml("<b>You can not proceed with an empty order!</b>"));

                    } else {

                        totalprice = shared.getString("total", "");

                        Order.setText(Html.fromHtml("<b>Your order total is: $</b>" + "<b>" + totalprice + "</b>"));
                        Response.setText(Html.fromHtml("<b>Out of stock product(s) Have been removed from your order.    Go back to cart to view remaining products or Go to dilivery/pickup tab to select pickup/dilivery option, and then payment tab to process payment...</b>"));

                    }


                } else

                {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.remove("total");
                    editor.apply();
                    Response.setText(Html.fromHtml("<b>You are currently offline, Please connect to the internet and checkout again to proceed...</b>"));


                }


            }
            catch (Exception ex) {
//                Toast.makeText(getContext(), "Weak / No Network Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }







    class ProcessProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Processing products...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
//            pDialog.show();


        }


        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag

            SharedPreferences.Editor editor = shared.edit();
            editor.remove("total");
            editor.apply();


            dbHandler = new DatabaseHelper(getContext());
            JSONArray JsonArr = new JSONArray();



            int Count=0;
            Double total=0.0;
            dbHandler = new DatabaseHelper(getContext());

            try {
                if (dbHandler.cartItems(currcart) != null) {
                    Cursor cursor = dbHandler.cartItems(currcart);
                    if (cursor != null &&  cursor.moveToFirst()) {
                        // Toast.makeText(this,"Items:" + cursor.getCount(), Toast.LENGTH_SHORT).show();

                        Log.e("Cursor", "Values" + DatabaseUtils.dumpCursorToString(cursor));
                        int cartitms[] = new int[cursor.getCount()];
                        int i = 0;
                        do  {

                            Log.e("Cart Returns", "Current Cart=" + currcart + " , Product IDs = " + productID);

                            //String varaible1 = cursor.getString(cursor.getColumnIndex("column_name1"));

                            PName = cursor.getString(7);
                            Qty = cursor.getString(3);
                            productID = cursor.getString(8);
                            StoreName = cursor.getString(10);


                            Log.e("Cursor", "Values" + DatabaseUtils.dumpCursorToString(cursor));

                            SharedPreferences.Editor edit = shared.edit();
                            edit.putString("productID", productID);
                            edit.commit();
                            edit.apply();
                            final int pID =  Integer.parseInt(productID.toString());





                            JSONObject json = new JSONObject();
                            List<NameValuePair> params = new ArrayList<NameValuePair>();






                            try {

                                Log.e("GetQuantities", "starting getting quantities!");
                                params.add(new BasicNameValuePair("productid", productID));
                                json = jsonParser.makeHttpRequest(
                                        GETQUANTITIES_URL, "POST", params);

                                // json success tag
                                try {
                                    if (TAG_SUCCESS != null) {
                                        success = json.getInt(TAG_SUCCESS);
                                        Log.d("JSon Results", "Success-" + json.getInt(TAG_SUCCESS) + "  |Message-" + json.getString(TAG_PRODUCTDETAILS));


                                        Log.e("Get Result Success", "Success obtained is" + success);
                                    }
                                } catch (Exception e) {
                                    Log.e("doInBackground error: ", e.getMessage());
                                }
                                if (success == 1) {


                                    JSONArray array = json.getJSONArray("posts");

                                    Log.e("Get Result Success", "Inside after success" + array);


                                    for (int k = 0; k < array.length(); k++) {
                                        try {

                                            JSONObject object = array.getJSONObject(k);

                                            String PostId = object.optString("post_id");

                                             ProdId = Integer.parseInt(PostId);
                                             quantity = object.optString("quantity");
                                            PPrice = object.optString("price");
                                             PName = object.optString("name");



                                            Double SubTotal = Double.parseDouble(PPrice) * Double.parseDouble(Qty);
                                            total = total + SubTotal;
                                            total = Math.round(total*100.0)/100.0;
                                            Count++;



                                            if (!quantity.contentEquals("")) {
                                                int quntonline = Integer.parseInt(quantity);
                                                int quntlocal = Integer.parseInt(Qty);

                                                   if (quntlocal>quntonline){

                                                       Qty = quantity;

                                                    dbHandler.updatecart(quntonline, ProdId);


                                                    SharedPreferences.Editor editorr = shared.edit();
                                                    editorr.putString("Name", PName);
                                                    editorr.commit();



                                                }

                                            }

                                            if (quantity.contentEquals("0")|| quantity.contentEquals("") ){

                                                if (Looper.myLooper() == null){
                                                    Looper.prepare();}

                                                dbHandler.removeCartItem(ProdId);


                                                //SharedPreferences.Editor editorr = shared.edit();
                                                //editorr.putString("Name", PName);
                                                //editorr.commit();







                                            }


                                    else{
                                                if (Looper.myLooper() == null){
                                                    Looper.prepare();}

                                                Log.e("notify", "Tikuti zvese bhoo ruru. Quantities:" + quantity);

                                                //Toast.makeText(getActivity(),"Text!",Toast.LENGTH_SHORT).show();

                                            }


                                            ContentValues values = new ContentValues();
                                            values.put("post_id", ProdId);
                                            values.put("category_name",quantity);



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }



                                    }






                                } else {

                                    //editor.putString("total", total.toString());
                                    //editor.commit();
                                    //editor.apply();

                                    //SharedPreferences.Editor editor = shared.edit();
                                    editor.remove("options");
                                    editor.apply();


                                        //Toast.makeText(getActivity(), "Unknown error! Please try again later", Toast.LENGTH_LONG).show();

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast ToastMessage = Toast.makeText(getActivity(),"Unknown error! Please try again later",Toast.LENGTH_LONG);
                                            View toastView = ToastMessage.getView();
                                            toastView.setBackgroundResource(R.drawable.toast_background);
                                            ToastMessage.show();
                                        }
                                    });





                                    Intent intent = new Intent(getActivity(), Cart.class);
                                    getActivity().finish();
                                    startActivity(intent);




                                }


                                //else{
                                // Log.e("No Internet Failure!", json.getString(TAG_MESSAGE));

                                //Toast.makeText(getContext(), "Failed To process payment, Please Try again later: NETWORK ERROR", Toast.LENGTH_LONG).show();
                                //return json.getString(TAG_MESSAGE);

                                //  }
                            } catch (JSONException e) {

                            }






















                            if (PPrice == "" || PPrice == null) {
                                PPrice = "0.0";
                                cartitms[i] = Integer.parseInt(cursor.getString(11));
                                Log.e("Get Cart Items", Integer.toString(cartitms[i]));
                                i++;

                            }

                            //SharedPreferences.Editor edit = shared.edit();
                            editor.putString("total", total.toString());
                            editor.commit();
                            editor.apply();

                            //SharedPreferences.Editor editor = shared.edit();
                            editor.remove("options");
                            editor.apply();

                            Log.e("New totall!", "Total after product sync is: " + (shared.getString("total", "")));

                            totalpriceupdated = total.toString();

                        } while (cursor.moveToNext());
                        cursor.close();

                        String TotalAmount = "";
                        TotalAmount = String.valueOf(new DecimalFormat("#.##").format(total));





                    }




                }

                else{

                    Log.e("Empty cart","Db handler null!" + currcart);

                    if (Looper.myLooper() == null){
                        Looper.prepare();}

                    //SharedPreferences.Editor editor = shared.edit();
                    editor.putString("total", "0.0");
                    editor.remove("options");
                    editor.commit();
                    editor.apply();

                }

            }








            catch (Exception e) {
                e.printStackTrace();
                Log.e("Cart Async Task Error ", e.toString());

            }

            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String posts) {
            // dismiss the dialog once product deleted








            //pDialog.dismiss();
            if (posts != null){
                //Toast.makeText(getContext(), posts, Toast.LENGTH_LONG).show();

                Toast ToastMessage = Toast.makeText(getActivity(),posts,Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();


                if (shared.getString("Name", "")!= null && shared.getString("Name", "")!= "") {

                    PrName = shared.getString("Name", "");


                    //Toast.makeText(getActivity(), PrName + "is now out of stock!!", Toast.LENGTH_LONG).show();

                    Toast ToastMessage1 = Toast.makeText(getActivity(),"is now out of stock!!",Toast.LENGTH_LONG);
                    View toastView1 = ToastMessage1.getView();
                    toastView1.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage1.show();



                }
            }

        }

    }
























}
