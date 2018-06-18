package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

import static com.hammerandtongues.online.hntonline.UserActivity.isNumeric;

/**
 * Created by NgonidzaIshe on 16/6/2016.
 */
public class Payment  extends Fragment implements View.OnClickListener {
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject json=null;
    //testing on Emulator:
   /* private static final String ORDER_URL = "https://devshopping.hammerandtongues.com/demo/webservice/createorder.php";
    private static final String ORDERCONTENTS_URL = "https://devshopping.hammerandtongues.com/demo/webservice/createordercontents.php";
    */
    //private static final String ECASH_PAYMENT_URL = "https://shopping.hammerandtongues.com/wp-content/themes/Walleto/lib/my_account/pay_for_item_ecash.php";
    private static final String ECASH_PAYMENT_URL = "https://shopping.hammerandtongues.com/wp-content/themes/Walleto/ecocash_api_mobile.php";
    private static final String DISCOUNT_URL = "https://shopping.hammerandtongues.com/webservice/getdiscount.php";
    //private static final String TCASH_PAYMENT_URL = "https://10.0.2.2:8012/wp-content/themes/Walleto/tcasi.php";
    private static final String TCASH_PAYMENT_URL = "https://shopping.hammerandtongues.com/wp-content/themes/Walleto/tcash_api_mobile.php";
    private static final String PAYNOW_PAYMENT_URL = "https://shopping.hammerandtongues.com/webservice/paynowapi.php";
    //private static final String ECORESPONSE_URL = "https://shopping.hammerandtongues.com/webservice/CheckDb.php";
    private static final String ECORESPONSE_URL = "https://shopping.hammerandtongues.com/wp-content/themes/Walleto/CheckDb.php";
    private static final String DELETEORDER_URL = "https://shopping.hammerandtongues.com/webservice/deleteoder.php";
    //ids
    private static final String CREATE_ORDERS_URL = "https://shopping.hammerandtongues.com/webservice/createorder.php";
    private static final String PAYTRANSACT_URL = "https://shopping.hammerandtongues.com/webservice/paytransact.php";
    private static final String CHECKUSERLEVEL_URL = "https://shopping.hammerandtongues.com/webservice/CheckUserLvl.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_ID = "id";
    private static final String TAG_BALANCE = "balance";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PRODUCTDETAILS = "posts";
    private static final String TAG_USERRES = "levelmessage";

    //Global Variables
    public static final String Product = "idKey";
    String PPrice, post_id,imgurl,Qty="";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Name = "nameKey";

    SharedPreferences shared;
    LinearLayout ewallet, paynow, ecocash, telecash, cod;
    ImageView imgewallet, imgpaynow, imgecocash, imgtelecash, imgcod;
    private Button btnApplyDiscount;
JSONArray JsonArr = new JSONArray();
    private String Ewall, Balance, uid, totalprice, Discount,DiscountAmount,ecash_number, Otp, accno, wallbalance, amount, frmgateway, idoforder = "notapplicable";
    private String DlvryType, DlvryChrg, DlvryAdd, totalPrc, paytype, orderid, resp;
    private TextView balance;
    private int currcart,isSumarry,success,Count, oid, flag=0, totalAll, DlvryPrc, DscntPrc, OrdrPrc ;
    private Double total=0.0;
    DatabaseHelper dbHandler;
     EditText account, discount, otp;

    //totalAll = totalprice + DlvryChrg - Discount;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.gc();
        try {
            super.onCreate(savedInstanceState);
            View view = inflater.inflate(R.layout.payment, container, false);
            // TODO Auto-generated method stub

            shared = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if (shared.getString("CartID", "") != null && shared.getString("CartID", "") != "") {
                currcart = Integer.parseInt(shared.getString("CartID", ""));
            } else {
                currcart = 0;
            }

            //// TODO: 18/11/2017  UNCOMMENT LINE BELOW
            Balance = (shared.getString("balance", ""));


            totalprice = (shared.getString("total", ""));
            uid = (shared.getString("userid", ""));
            DlvryChrg = (shared.getString("DlvryChrg", ""));
            DlvryAdd = (shared.getString("DlvryAdd", ""));
            DlvryType = (shared.getString("DlvryType", ""));
            //linear layouts:
            ewallet = (LinearLayout) view.findViewById(R.id.ewallet);
            paynow = (LinearLayout) view.findViewById(R.id.paynow);
            ecocash = (LinearLayout) view.findViewById(R.id.ecocash);
            telecash = (LinearLayout) view.findViewById(R.id.telecash);
            cod = (LinearLayout) view.findViewById(R.id.cod);

            //imageviews
            imgewallet = (ImageView) view.findViewById(R.id.imgewallet);
            imgpaynow = (ImageView) view.findViewById(R.id.imgpaynow);
            imgecocash = (ImageView) view.findViewById(R.id.imgecocash);
            imgtelecash = (ImageView) view.findViewById(R.id.imgtelecash);
            imgcod = (ImageView) view.findViewById(R.id.imgcod);
            btnApplyDiscount = (Button) view.findViewById(R.id.btnVoucher);
            discount = (EditText) view.findViewById(R.id.txtDiscount);

            //DlvryPrc = Integer.parseInt(DlvryChrg);
            //DscntPrc = Integer.parseInt(Discount);
            //OrdrPrc = Integer.parseInt(totalprice);

            //totalAll = DlvryPrc + OrdrPrc;

            //totalPrc = Integer.toString(totalAll);


            totalPrc = String.valueOf (Double.parseDouble(totalprice) + Double.parseDouble(DlvryChrg));


            SharedPreferences.Editor editor = shared.edit();
            editor.putString("totalPrc", totalPrc);
            editor.apply();


            imgewallet.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    payment("ewallet");
                }
            });

            imgpaynow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    payment("paynow");
                }
            });

            imgecocash.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    payment("ecocash");
                }
            });

            imgtelecash.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    payment("telecash");
                }
            });


            imgcod.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    payment("cod");
                }
            });

            btnApplyDiscount.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Discount = discount.getText().toString();
                    GetDiscountVoucher();
                }
            });

            //Toast.makeText(getActivity(),"Number: " +  ecash_number + " Amount: " + totalprice + " Other Amt:" + amount, Toast.LENGTH_SHORT).show();

            return view;
       }
       catch (Exception ex)
       {
         Log.e("Main Thread Exception", "Error: " + ex.toString());
           System.gc();
           return null;
       }

    }

    public  void payment(final String gateway )
    {
        balance = new TextView(getContext());
        //loginbalance = (TextView) getView().findViewById(R.id.)
        TextView prefix = new TextView(getContext());
        TextView total = new TextView(getContext());
        TextView delivery = new TextView(getContext());
        TextView discount = new TextView(getContext());
        TextView totalpayment = new TextView(getContext());
        TextView notice = new TextView(getContext());
        account = new EditText(getContext());
        otp = new EditText(getContext());
        Button submit = new Button(getContext());
        submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ecash_number = account.getText().toString();
            accno = account.getText().toString();
            Otp = otp.getText().toString();
            //Toast.makeText(getActivity(),"Number: " +  ecash_number + " Amount: " + totalprice + " Other Amt:" + amount, Toast.LENGTH_LONG).show();
            if (gateway == "ewallet") {
                isSumarry = 0;
                paytype = "ewallet";
                Otp = "notapplicable";
                accno = "notapplicable";
                Discount =  "notapplicable";


                SharedPreferences.Editor editor = shared.edit();
                editor.putString("ptype", paytype);
                editor.apply();

                Double ttl = Double.parseDouble(totalprice);
                Double dchrg = Double.parseDouble(DlvryChrg);
                //ttl = Double.parseDouble(new DecimalFormat("#.##").format(ttl));
                Double bal = Double.parseDouble(Balance);
                //bal = Double.parseDouble(new DecimalFormat("#.##").format(bal));
                totalprice = ttl.toString();
                if (DiscountAmount == null || DiscountAmount == "") {
                    DiscountAmount = "0.0";
                }
                Double disc = Double.parseDouble(DiscountAmount);
                //disc = Double.parseDouble(new DecimalFormat("#.##").format(disc));
                Log.e("Total", "total is: " + ttl + dchrg);



                if ((ttl  > (bal + disc)) || (dchrg > bal)) {
                    //Toast.makeText(getContext(), "Insufficent Funds in Wallet Order Total: $" + totalprice + "Wallet Balance: $" + Balance, Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Info")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            })
                            .setNegativeButton("", null)
                            .setMessage(Html.fromHtml("Insufficent Funds in Wallet. Order Total: $" + totalprice + " Wallet Balance: $" + Balance ))
                            .show();


                    return;
                }

                else {


                    ProcessOrder(paytype, CREATE_ORDERS_URL, idoforder);


                //ProcessRequest(paytype, ECORESPONSE_URL, idoforder);


                }




            }
            if (gateway == "ecocash") {

                    isSumarry = 2;
                paytype = "Ecocash";
                Otp = "notapplicable";
                Discount =  "notapplicable";

                SharedPreferences.Editor editor = shared.edit();
                editor.putString("ptype", paytype);
                editor.apply();
                if (DiscountAmount == null || DiscountAmount == "") {
                    DiscountAmount = "0.0";
                }
                        if (accno.contentEquals("")) {
                            //Toast.makeText(getContext(), "Please enter a mobile number!", Toast.LENGTH_SHORT).show();

                            Toast ToastMessage = Toast.makeText(getContext(),"Please enter a mobile number!",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();

                            return;
                        }

                        else{


                            ProcessOrder(paytype, CREATE_ORDERS_URL, idoforder);


                        }



            }
            if (gateway == "telecash")

            {
                    isSumarry = 3;
                paytype = "Telecash";
                Discount =  "notapplicable";

                SharedPreferences.Editor editor = shared.edit();
                editor.putString("ptype", paytype);
                editor.apply();
                if (DiscountAmount == null || DiscountAmount == "") {
                    DiscountAmount = "0.0";
                }
                if (accno.contentEquals("") || Otp.contentEquals("") ) {

                    //Toast.makeText(getContext(), "Please enter a mobile number or Otp!", Toast.LENGTH_SHORT).show();

                    Toast ToastMessage = Toast.makeText(getContext(),"Please enter a mobile number or Otp!!",Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();


                    return;

                }

                else{


                    ProcessOrder(paytype, CREATE_ORDERS_URL, idoforder);


                }
            }

            if (gateway =="paynow")
            {
                isSumarry=4;
                Otp = "notapplicable";
                accno = "notapplicable";
                Discount =  "notapplicable";
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("ptype", paytype);
                editor.apply();
                if (DiscountAmount == null || DiscountAmount == "") {
                    DiscountAmount = "0.0";
                }

                flag = 1;
                paytype = "paynow";
                ProcessOrder(paytype, CREATE_ORDERS_URL, idoforder);



            }
            if (gateway =="cod")
            {
                isSumarry=1;

                paytype = "COD";
                Discount =  "notapplicable";
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("ptype", paytype);
                editor.apply();
                if (DiscountAmount == null || DiscountAmount == "") {
                    DiscountAmount = "0.0";
                }


                resp = shared.getString("resp", "");

                ProcessRequest(paytype, CHECKUSERLEVEL_URL, idoforder);


            }

            //new ProcessRequest().execute();
        }
    });
        submit.setText("Pay");
        if (gateway=="ewallet")
        {
            if (Balance != null && isNumeric(Balance)) {
                if (Double.parseDouble(Balance) == 0) {
                    balance.setTextColor(Color.RED);
                } else {
                    balance.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
            else {
                Balance = "0.00";
            }
            balance.setText("Your Ewallet Balance is: $" +Balance);
            total.setText("Order Total: $"+ totalprice  );
            delivery.setText("Delivery / Pickup Charge: $"+ DlvryChrg  );
            totalpayment.setText("Total Charge: $"+ totalPrc  );
            if (DiscountAmount != null) {
                discount.setText("Discount Amount: $" + DiscountAmount);
            }
            else {
                discount.setText("Discount Amount: $0.00");
            }

            ewallet.removeAllViews();
            ewallet.addView(balance);
            ewallet.addView(total);
            ewallet.addView(delivery);
            ewallet.addView(discount);
            ewallet.addView(totalpayment);
            ewallet.addView(submit);
            ewallet.setVisibility(View.VISIBLE);
            paynow.setVisibility(View.GONE);
            ecocash.setVisibility(View.GONE);
            telecash.setVisibility(View.GONE);
            cod.setVisibility(View.GONE);
        }

        if (gateway=="ecocash")
        {
            account.setHint("Ecocash number");
            prefix.setText("+263:");
            //account.setText("+263:");
            total.setText("Order Total: $"+ totalprice  );
            delivery.setText("Delivery / Pickup Charge: $"+ DlvryChrg  );
            if (DiscountAmount != null) {
                discount.setText("Discount Amount: $" + DiscountAmount);
            }
            else {
                discount.setText("Discount Amount: $0.00");
            }
            //totalpayment.setText("Total Payment: $" + totalAll);

            totalpayment.setText("Total Charge: $"+ totalPrc  );

            ecocash.removeAllViews();
            ecocash.addView(total);
            ecocash.addView(delivery);
            ecocash.addView(discount);
            ecocash.addView(totalpayment);
            ecocash.addView(prefix);
            ecocash.addView(account);
            ecocash.addView(submit);
            ewallet.setVisibility(View.GONE);
            paynow.setVisibility(View.GONE);
            ecocash.setVisibility(View.VISIBLE);
            telecash.setVisibility(View.GONE);
            cod.setVisibility(View.GONE);
        }

        if (gateway=="paynow")
        {
            if (DiscountAmount != null) {
                discount.setText("Discount Amount: $" + DiscountAmount);
            }
            else {
                discount.setText("Discount Amount: $0.00");
            }

            //account.setHint("Enter Your Card No.");
            total.setText("Order Total: $"+ totalprice  );
            delivery.setText("Delivery / Pickup Charge: $"+ DlvryChrg  );
            totalpayment.setText("Total Charge: $"+ totalPrc  );

            paynow.removeAllViews();
            paynow.addView(total);
            paynow.addView(delivery);
            paynow.addView(discount);
            paynow.addView(totalpayment);
            paynow.addView(submit);
            ewallet.setVisibility(View.GONE);
            paynow.setVisibility(View.VISIBLE);
            ecocash.setVisibility(View.GONE);
            telecash.setVisibility(View.GONE);
            cod.setVisibility(View.GONE);
        }

        if (gateway=="telecash")
        {
            if (DiscountAmount != null) {
                discount.setText("Discount Amount: $" + DiscountAmount);
            }
            else {
                discount.setText("Discount Amount: $0.00");
            }

            account.setHint("Telecel number ");
            prefix.setText("+263:");
            otp.setHint("OTP");
            total.setText("Order Total: $" + totalprice);
            delivery.setText("Delivery / Pickup Charge: $"+ DlvryChrg  );
            totalpayment.setText("Total Charge: $"+ totalPrc  );

            telecash.removeAllViews();
            telecash.addView(total);
            telecash.addView(delivery);
            telecash.addView(discount);
            telecash.addView(totalpayment);
            telecash.addView(prefix);
            telecash.addView(account);
            telecash.addView(otp);
            telecash.addView(submit);
            ewallet.setVisibility(View.GONE);
            paynow.setVisibility(View.GONE);
            ecocash.setVisibility(View.GONE);
            cod.setVisibility(View.GONE);
            telecash.setVisibility(View.VISIBLE);
        }

        if (gateway=="cod")
        {
            if (DiscountAmount != null) {
                discount.setText("Discount Amount: $" + DiscountAmount);
            }
            else {
                discount.setText("Discount Amount: $0.00");
            }
            //account.setHint("Payment type");
            //account.setHint("Enter Your Card No.");
            total.setText("Order Total: $"+ totalprice  );
            delivery.setText("Delivery / Pickup Charge: $"+ DlvryChrg  );
            totalpayment.setText("Total Charge: $"+ totalPrc  );
            notice.setText("You are about to mark this order as, c.o.d, click proceed to continue...");
            notice.setTextColor(Color.RED);
            submit.setText("Proceed");

            cod.removeAllViews();
            cod.addView(total);
            cod.addView(delivery);
            //cod.addView(discount);
            cod.addView(totalpayment);
            cod.addView(notice);
            //cod.addView(account);
            cod.addView(submit);
            cod.setVisibility(View.VISIBLE);
            ewallet.setVisibility(View.GONE);
            paynow.setVisibility(View.GONE);
            ecocash.setVisibility(View.GONE);
            telecash.setVisibility(View.GONE);
        }




    }
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        /*switch (v.getId()) {
            case R.id.btn_user_cancel:

                String username = user.getText().toString();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Name, username);
                editor.commit();
                // new AttemptLogin().execute();
                break;
            case R.id.btn_next:
                Intent i = new Intent(getActivity(), Register.class);
                startActivity(i);
                break;

            default:
                break;
        }*/
    }

    public  void callPaymentsAsyncTask(){
        new ProcessPayment().execute();
    }
private String deleteId;
    public  void updateewallet(){

        //update shared preference bal


        Log.e("logging", "in update e wallet");
        dbHandler.clearCartItems();
        Log.e("logging", "after clear items dbhandler");
        balance.setText("Your Updated Ewallet Balance is: " + wallbalance);
        Log.e("Update Ewallet Display", "Updating" + wallbalance);
        Log.e("logging", "starting intent...............");
       // Intent intent = new Intent(getActivity(), TransactionHistory.class);
        //startActivity(intent);
        Log.e("logging", "intent started: " );
        //return true;
    }



    private void ProcessRequest( final String ptype, final String URL, final String OrderId) {


        //Log.e("NUMBER", (shared.getString("telno", "")));

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Processing Order & Payment...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        try {
            // Building Parameters
            total=0.0;
          final JSONObject json = new JSONObject();


            dbHandler = new DatabaseHelper(getContext());


            if (dbHandler.cartItems(currcart) != null) {
                Cursor cursor01 = dbHandler.cartItems(currcart);
                if (cursor01 != null && cursor01.moveToFirst()) {
                    Log.e("Cart Cursor Count", "Items in Cart: " + cursor01.getCount()
                            + "  PrdID:" + cursor01.getString(8)
                            + "  qnty:" + cursor01.getString(3)
                            + "  price:" + cursor01.getString(4)
                            + "  title:" + cursor01.getString(7)
                            + "  storeid:" + cursor01.getString(11)
                            + "  storename:" + cursor01.getString(12)
                            + "  seller:" + cursor01.getString(13)
                            + "  discount:"
                            + "  variation:"
                    );

                    deleteId = cursor01.getString(8);
                    do {
                        JSONObject jsonprdct = new JSONObject();
                        jsonprdct.put("productid", cursor01.getString(8));
                        jsonprdct.put("qnty", cursor01.getString(3));
                        jsonprdct.put("variation", "");
                        jsonprdct.put("price", cursor01.getString(4));
                        jsonprdct.put("ptitle", cursor01.getString(7));
                        jsonprdct.put("storeid", cursor01.getString(11));
                        jsonprdct.put("storename", cursor01.getString(12));

                        jsonprdct.put("seller", cursor01.getString(13));
                        jsonprdct.put("discount", "0.0");

                        PPrice = cursor01.getString(4);
                        Qty = cursor01.getString(3);
                        JsonArr.put(jsonprdct);
                        Double SubTotal = 0.0;
                        SubTotal = (Double.parseDouble(PPrice) * Double.parseDouble(Qty));
                        total = total + SubTotal;
                        Count++;
                    } while (cursor01.moveToNext());
                    cursor01.close();




                    Log.e("Cart Items JSON String", JsonArr.toString());

                    //Log.e("Mapping values", "userid: " + uid + " totalprice " + DiscountAmount + " Coupon " + Discount + " dlvrytype " + DlvryType + " lvrychrg " +  DlvryChrg + " dlvryadd " + DlvryAdd + " amt " + totalPrc + " endUserId " + accno + " oid " + orderid + " endUserotp " + Otp + " ptype " + ptype );


                    if (Discount == null || Discount == "") {
                        Discount = "XXX000";
                    }


                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getActivity());



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

                                else  if (jsonObject.has("levelmessage")) {
                                    message = jsonObject.getString("levelmessage");
                                }


                                Log.e("Get Cart Success", "" + success);
                                flag = 1;


                                //if (oid != 0){}


                                if (jsonObject.has(TAG_ID)) {

                                    oid = jsonObject.getInt(TAG_ID);
                                    idoforder = String.valueOf(oid);
                                    Log.e("Get payment ID", "after oid" + oid);

                                    Log.e("Get order ID from prefs", "orderid: " + idoforder);


                                    SharedPreferences.Editor editor = shared.edit();
                                    editor.putString("OrderID", String.valueOf(oid));
                                    editor.apply();}

                                else {


                                }


                                if (jsonObject.has(TAG_USERRES)) {

                                    String respp = jsonObject.getString(TAG_USERRES);


                                    SharedPreferences.Editor editor = shared.edit();
                                    editor.putString("resp", respp);
                                    editor.apply();

                                    if (respp.equals("COD approved!")) {

                                        ProcessOrder(paytype, CREATE_ORDERS_URL, idoforder);

                                    }


                                }




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


                                    if(ptype != null ){

                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Info")
                                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        new AlertDialog.Builder(getActivity())
                                                                .setTitle("")
                                                                .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        Log.e("SUCCESS", "you can delete " + oid);
                                                                        myDBHandler myDBHandler = new myDBHandler(getContext(), null, null, 0);

                                                                        myDBHandler.clearCartItems();


                                                                        Intent intent = new Intent(getActivity(), MainActivity.class);
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
                                    new AlertDialog.Builder(getActivity())
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

                                Toast ToastMessage = Toast.makeText(getContext(), "No Intenet connection!", Toast.LENGTH_LONG);
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
                            values.put("cartitems", JsonArr.toString());
                            values.put("userid", uid);
                            values.put("discount", DiscountAmount);
                            values.put("totalprice", totalprice);
                            values.put("Coupon", Discount);
                            values.put("dlvrytype", DlvryType);
                            values.put("dlvrychrg", DlvryChrg);
                            values.put("dlvryadd", DlvryAdd);
                            values.put("amt", totalPrc);
                            values.put("yes", "Proceed Now");
                            values.put("endUserId", accno);
                            values.put("oid", OrderId);
                            values.put("endUserotp", Otp);
                            values.put("ptype", ptype);

                            return values;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 500, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    requestQueue.add(stringRequest);

                    pDialog.dismiss();

                }


            }


            else{



                new AlertDialog.Builder(getActivity())
                        .setTitle("Info")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);


                            }
                        })
                        .setNegativeButton("", null)
                        .setMessage(Html.fromHtml("No products in cart"))
                        .show();

            }

        }


        catch (Exception e) {
            e.printStackTrace();
            Log.e("Process Error ", e.toString());


        }

    }






    private void ProcessOrder( final String ptype, final String URL, final String OrderId) {


        //Log.e("NUMBER", (shared.getString("telno", "")));

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Processing Order & Payment...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        try {
            // Building Parameters
            total=0.0;
            final JSONObject json = new JSONObject();


            dbHandler = new DatabaseHelper(getContext());


            if (dbHandler.cartItems(currcart) != null) {
                Cursor cursor01 = dbHandler.cartItems(currcart);
                if (cursor01 != null && cursor01.moveToFirst()) {
                    Log.e("Cart Cursor Count", "Items in Cart: " + cursor01.getCount()
                            + "  PrdID:" + cursor01.getString(8)
                            + "  qnty:" + cursor01.getString(3)
                            + "  price:" + cursor01.getString(4)
                            + "  title:" + cursor01.getString(7)
                            + "  storeid:" + cursor01.getString(11)
                            + "  storename:" + cursor01.getString(12)
                            + "  seller:" + cursor01.getString(13)
                            + "  discount:"
                            + "  variation:"
                    );

                    deleteId = cursor01.getString(8);
                    do {
                        JSONObject jsonprdct = new JSONObject();
                        jsonprdct.put("productid", cursor01.getString(8));
                        jsonprdct.put("qnty", cursor01.getString(3));
                        jsonprdct.put("variation", "");
                        jsonprdct.put("price", cursor01.getString(4));
                        jsonprdct.put("ptitle", cursor01.getString(7));
                        jsonprdct.put("storeid", cursor01.getString(11));
                        jsonprdct.put("storename", cursor01.getString(12));

                        jsonprdct.put("seller", cursor01.getString(13));
                        jsonprdct.put("discount", "0.0");

                        PPrice = cursor01.getString(4);
                        Qty = cursor01.getString(3);
                        JsonArr.put(jsonprdct);
                        Double SubTotal = 0.0;
                        SubTotal = (Double.parseDouble(PPrice) * Double.parseDouble(Qty));
                        total = total + SubTotal;
                        Count++;
                    } while (cursor01.moveToNext());
                    cursor01.close();




                    Log.e("Cart Items JSON String", JsonArr.toString());

                    //Log.e("Mapping values", "userid: " + uid + " totalprice " + DiscountAmount + " Coupon " + Discount + " dlvrytype " + DlvryType + " lvrychrg " +  DlvryChrg + " dlvryadd " + DlvryAdd + " amt " + totalPrc + " endUserId " + accno + " oid " + orderid + " endUserotp " + Otp + " ptype " + ptype );


                    if (Discount == null || Discount == "") {
                        Discount = "XXX000";
                    }


                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getActivity());



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

                                else if (jsonObject.has("levelmessage")) {
                                    message = jsonObject.getString("levelmessage");
                                }


                                Log.e("Get Cart Success", "" + success);
                                flag = 1;


                                //if (oid != 0){}


                                if (jsonObject.has(TAG_ID)) {

                                    oid = jsonObject.getInt(TAG_ID);
                                    idoforder = String.valueOf(oid);
                                    Log.e("Get payment ID", "after oid" + oid);

                                    Log.e("Get order ID from prefs", "orderid: " + idoforder);


                                    SharedPreferences.Editor editor = shared.edit();
                                    editor.putString("OrderID", String.valueOf(oid));
                                    editor.apply();}

                                else {


                                }


                                if (jsonObject.has(TAG_USERRES)) {

                                    String respp = jsonObject.getString(TAG_USERRES);


                                    SharedPreferences.Editor editor = shared.edit();
                                    editor.putString("resp", respp);
                                    editor.apply();}




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

                                    if (ptype == "ewallet") {
                                        ProcessRequest(paytype, ECORESPONSE_URL, idoforder);
                                    }

                                    else if (ptype == "Ecocash"){

                                        ProcessRequest(paytype, ECASH_PAYMENT_URL, idoforder);

                                    }

                                    else if (ptype == "Telecash"){

                                        ProcessRequest(paytype, TCASH_PAYMENT_URL, idoforder);

                                    }


                                    else if (ptype == "paynow"){

                                        Intent i = new Intent(getActivity(), WebViewActivity.class);
                                        startActivity(i);

                                    }

                                    else if (ptype == "COD"){

                                        //ProcessRequest(paytype, TCASH_PAYMENT_URL, idoforder);

                                        ProcessRequest(paytype, ECORESPONSE_URL, idoforder);

                                    }


                                    if(ptype != null ){

                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Info")
                                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        new AlertDialog.Builder(getActivity())
                                                                .setTitle("")
                                                                .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        Log.e("SUCCESS", "you can delete " + oid);
                                                                        myDBHandler myDBHandler = new myDBHandler(getContext(), null, null, 0);

                                                                        myDBHandler.clearCartItems();


                                                                        Intent intent = new Intent(getActivity(), MainActivity.class);
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
                                    new AlertDialog.Builder(getActivity())
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

                                Toast ToastMessage = Toast.makeText(getContext(), "No Intenet connection!", Toast.LENGTH_LONG);
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
                            values.put("cartitems", JsonArr.toString());
                            values.put("userid", uid);
                            values.put("discount", DiscountAmount);
                            values.put("totalprice", totalprice);
                            values.put("Coupon", Discount);
                            values.put("dlvrytype", DlvryType);
                            values.put("dlvrychrg", DlvryChrg);
                            values.put("dlvryadd", DlvryAdd);
                            values.put("amt", totalPrc);
                            values.put("yes", "Proceed Now");
                            values.put("endUserId", accno);
                            values.put("oid", OrderId);
                            values.put("endUserotp", Otp);
                            values.put("ptype", ptype);

                            return values;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 500, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    requestQueue.add(stringRequest);

                    pDialog.dismiss();

                }


            }


            else{



                new AlertDialog.Builder(getActivity())
                        .setTitle("Info")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);


                            }
                        })
                        .setNegativeButton("", null)
                        .setMessage(Html.fromHtml("No products in cart"))
                        .show();

            }

        }


        catch (Exception e) {
            e.printStackTrace();
            Log.e("Process Error ", e.toString());


        }

    }












    class ProcessPayment extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Processing Order & Payment...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {


           return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String posts) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

            if (Ewall == "Done")
            {
                if (isSumarry == 0) {

                    SharedPreferences.Editor editor = shared.edit();
                    //Balance = String.valueOf (Double.parseDouble(new DecimalFormat("#.##").format((Double.parseDouble(Balance) - Double.parseDouble(totalprice)))));
                    editor.putString("balance", wallbalance);
                    editor.apply();
                    Log.e( "bal: ","remAINING BAL"+wallbalance );

                    //update shared preferences

                    //Toast.makeText(getContext(), "Payment successfull!", Toast.LENGTH_LONG).show();


                    /*
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Info")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            })
                            .setNegativeButton("", null)
                            .setMessage(Html.fromHtml("Payment processed successfully. Thank you!" ))
                            .show();
*/


                    updateewallet();


                }

            }

            if (posts != null) {



                Log.e("SUCCESS","you can delete "+ oid);
                DatabaseHelper myDBHandler=new DatabaseHelper(getContext());

                myDBHandler.clearCartItems();
                //myDBHandler.clearCartItems(Integer.parseInt(deleteId));

                //Toast.makeText(getContext(),  " Type:" + isSumarry+" or delete id is "+deleteId, Toast.LENGTH_LONG).show();





            }

        }
    }

    private void GetDiscountVoucher() {

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Processing Order & Payment...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        Log.e("NUMBER", (shared.getString("telno", "")));


        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOUNT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success", "" + s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");

                    if (success == 1) {


                        JSONArray array = jsonObject.getJSONArray("posts");

                        JSONObject object = array.getJSONObject(0);


                        String disc = object.optString("cpn_amt");

                        DiscountAmount=disc;

                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("voucher_amount", disc);
                        editor.putString("voucher_code", Discount);
                        editor.commit();
                        editor.apply();
                        payment("ewallet");

                        Log.e("Balance", disc);


                        new AlertDialog.Builder(getActivity())
                                .setTitle("Info")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                })
                                .setNegativeButton("", null)
                                .setMessage(Html.fromHtml(message))
                                .show();

                        pDialog.dismiss();

                    } else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Info")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setNegativeButton("", null)
                                        .setMessage(Html.fromHtml("Invalid voucher number!" ))
                                        .show();
                            }

                        });

                        DiscountAmount=null;

                        pDialog.dismiss();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    //Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();

                    Toast ToastMessage = Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();

                    pDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();

                Log.e("RUEERROR", "" + volleyError);

                //Toast.makeText(getContext(), "No Intenet connection!", Toast.LENGTH_SHORT).show();

                Toast ToastMessage = Toast.makeText(getActivity(), "No Intenet connection!", Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();
                pDialog.dismiss();


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> values = new HashMap();
                values.put("voucherid", Discount);
                values.put("amt", totalPrc);
                values.put("dlvrychrg", DlvryChrg);

                return values;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);


    }


}
