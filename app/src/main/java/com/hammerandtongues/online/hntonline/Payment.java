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
import android.os.Looper;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.hammerandtongues.online.hntonline.Login.isNumeric;

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
    //private static final String ECASH_PAYMENT_URL = "https://hammerandtongues.com/shopping/ecocash_api.php";
    private static final String ECASH_PAYMENT_URL = "https://devshop.hammerandtongues.com/webservice/ecocash_api.php";
    private static final String DISCOUNT_URL = "https://devshop.hammerandtongues.com/webservice/getdiscount.php";
    //private static final String TCASH_PAYMENT_URL = "http://10.0.2.2:8012/wp-content/themes/Walleto/tcasi.php";
    private static final String TCASH_PAYMENT_URL = "https://devshop.hammerandtongues.com/webservice/tcash_api.php";
    private static final String PAYNOW_PAYMENT_URL = "https://devshop.hammerandtongues.com/webservice/paynowapi.php";
    //private static final String ECORESPONSE_URL = "https://devshop.hammerandtongues.com/webservice/CheckDb.php";
    private static final String ECORESPONSE_URL = "https://devshop.hammerandtongues.com/wp-content/themes/Walleto/CheckDb.php";
    private static final String DELETEORDER_URL = "https://devshop.hammerandtongues.com/webservice/deleteoder.php";
    //ids
    private static final String CREATE_ORDERS_URL = "https://devshop.hammerandtongues.com/webservice/createorder.php";
    private static final String PAYTRANSACT_URL = "https://devshop.hammerandtongues.com/webservice/paytransact.php";
    private static final String CHECKUSERLEVEL_URL = "https://devshop.hammerandtongues.com/webservice/CheckUserLvl.php";

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
    private String Ewall, Balance, uid, totalprice, Discount,DiscountAmount,ecash_number, Otp, accno, wallbalance, amount;
    private String DlvryType, DlvryChrg, DlvryAdd, totalPrc;
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


            totalPrc = String.valueOf (Double.parseDouble(new DecimalFormat("#.##").format((Double.parseDouble(totalprice) + Double.parseDouble(DlvryChrg)))));


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
                    new GetDiscountVoucher().execute();
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
                Double ttl = Double.parseDouble(totalprice);
                Double dchrg = Double.parseDouble(DlvryChrg);
                ttl = Double.parseDouble(new DecimalFormat("#.##").format(ttl));
                Double bal = Double.parseDouble(Balance);
                bal = Double.parseDouble(new DecimalFormat("#.##").format(bal));
                totalprice = ttl.toString();
                if (DiscountAmount == null || DiscountAmount == "") {
                    DiscountAmount = "0.0";
                }
                Double disc = Double.parseDouble(DiscountAmount);
                disc = Double.parseDouble(new DecimalFormat("#.##").format(disc));
                Log.e("Total", "total is: " + ttl + dchrg);

                if ((ttl  > (bal + disc)) || (dchrg > bal)) {
                    Toast.makeText(getContext(), "Insufficent Funds in Wallet Order Total: $" + totalprice + "Wallet Balance: $" + Balance, Toast.LENGTH_SHORT).show();
                    return;
                }


            }
            if (gateway == "ecocash") {

                    isSumarry = 2;
                        if (accno.contentEquals("")) {
                            Toast.makeText(getContext(), "Please enter a mobile number!", Toast.LENGTH_SHORT).show();
                            return;
                        }

            }
            if (gateway == "telecash")

            {
                    isSumarry = 3;
                if (accno.contentEquals("") || Otp.contentEquals("") ) {

                    Toast.makeText(getContext(), "Please enter a mobile number or Otp!", Toast.LENGTH_SHORT).show();
                    return;

                }
            }

            if (gateway =="paynow")
            {
                isSumarry=4;

            }
            if (gateway =="cod")
            {
                isSumarry=1;

            }

            new ProcessRequest().execute();
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
            account.setHint("+263:");
            //prefix.setText("+263:");
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

            account.setHint("+263");
            otp.setHint("OTP");
            total.setText("Order Total: $" + totalprice);
            delivery.setText("Delivery / Pickup Charge: $"+ DlvryChrg  );
            totalpayment.setText("Total Charge: $"+ totalPrc  );

            telecash.removeAllViews();
            telecash.addView(total);
            telecash.addView(delivery);
            telecash.addView(discount);
            telecash.addView(totalpayment);
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
        Intent intent = new Intent(getActivity(), TransactionHistory.class);
        startActivity(intent);
        Log.e("logging", "intent started: " );
        //return true;
    }

    class ProcessRequest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
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
            // TODO Auto-generated method stub
            // Check for success tag

                 try {
                    // Building Parameters
                    total=0.0;
                     JSONObject json = new JSONObject();
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    dbHandler = new DatabaseHelper(getContext());
                    JSONArray JsonArr = new JSONArray();

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
                                jsonprdct.put("storeid", cursor01.getString(10));
                                jsonprdct.put("storename", cursor01.getString(11));
                                //TO DO SELLER WANGU
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

                            if (Discount == null || Discount == "") {
                                Discount = "XXX000";
                            }


                            params.add(new BasicNameValuePair("cartitems", JsonArr.toString()));
                            params.add(new BasicNameValuePair("userid", uid));
                            params.add(new BasicNameValuePair("totalprice", totalprice));
                            params.add(new BasicNameValuePair("discount", DiscountAmount));
                            params.add(new BasicNameValuePair("Coupon", Discount));
                            params.add(new BasicNameValuePair("dlvrytype", DlvryType));
                            params.add(new BasicNameValuePair("dlvrychrg", DlvryChrg));
                            params.add(new BasicNameValuePair("dlvryadd", DlvryAdd));
                            params.add(new BasicNameValuePair("amt", totalPrc));
                            params.add(new BasicNameValuePair("endUserId", accno));

                            if (isSumarry == 0) {
                                params.add(new BasicNameValuePair("ptype", "ewallet"));
                                params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                            } else if (isSumarry == 2) {
                                params.add(new BasicNameValuePair("ptype", "ecocash"));
                            } else if (isSumarry == 3) {
                                params.add(new BasicNameValuePair("ptype", "telecash"));
                            } else if (isSumarry == 4) {
                                params.add(new BasicNameValuePair("ptype", "paynow"));
                            }
                            else if (isSumarry == 1) {
                                params.add(new BasicNameValuePair("ptype", "cod"));
                            }

                            //To do dzosera create order

                            try {

                                Log.e("CreateOrder", "starting order creation");
                                json = jsonParser.makeHttpRequest(
                                        CREATE_ORDERS_URL, "POST", params);

                                // json success tag
                                try {
                                    if (TAG_SUCCESS != null) {
                                        success = json.getInt(TAG_SUCCESS);
                                        Log.d("JSon Results", "Success-" + json.getInt(TAG_SUCCESS) + "  |Message-" + json.getString(TAG_PRODUCTDETAILS));


                                        wallbalance = json.getString(TAG_BALANCE);
                                        amount = json.getString(TAG_AMOUNT);

                                        Log.e("Get Cart Success", "after wallbalance and amount" + success);
                                    }
                                } catch (Exception e) {
                                    Log.e("doInBackground error: ", e.getMessage());
                                }
                                if (Looper.myLooper() == null){
                                    Looper.prepare();}
                                if (success == 1) {


                                    Log.e("Get Cart Success", "" + success);
                                    flag = 1;
                                    oid = json.getInt(TAG_ID);

                                    Log.e("Get payment ID", "after oid" + oid);


                                    SharedPreferences.Editor editor = shared.edit();
                                    editor.putString("OrderID", String.valueOf(oid));
                                    editor.apply();

                                    success = json.getInt(TAG_SUCCESS);
                                    Log.e("JSon Results", "Success-For Pay" + json.getInt(TAG_SUCCESS) + "  |Message-" + json.getString(TAG_PRODUCTDETAILS));

                                    //return json.getString(TAG_PRODUCTDETAILS);

                                    try {
                                        // Building Parameters
                                        //List<NameValuePair> params = new ArrayList<NameValuePair>();

                                        if (isSumarry == 0) {
                                            Log.e("Processing Payment", "E-wallet:" + accno + ", Amount:" + totalPrc + ", OrderID:" + String.valueOf(oid) + "Dilivery Charge:" + DlvryChrg + "User" + uid );
                                            params.add(new BasicNameValuePair("amt", amount));
                                            params.add(new BasicNameValuePair("userid", uid));
                                            params.add(new BasicNameValuePair("ptype", "ewallet"));
                                            params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                            params.add(new BasicNameValuePair("cartitems", JsonArr.toString()));
                                            json = jsonParser.makeHttpRequest(
                                                    ECORESPONSE_URL, "POST", params);
                                            Log.e("Processing Payment", "EWallet");

                                            Ewall = "Done";
                                            return "";




                                        }
                                        if (isSumarry == 2) {

                                            Log.e("Processing Payment", "Ecocash, Mobile:" + accno + ", Amount:" + totalPrc + ", OrderID:" + String.valueOf(oid) + "Dilivery Charge:" + DlvryChrg + "User" + uid );
                                            params.add(new BasicNameValuePair("ptype", "Ecocash"));
                                            params.add(new BasicNameValuePair("endUserId", accno));
                                            params.add(new BasicNameValuePair("amt", totalPrc));
                                            params.add(new BasicNameValuePair("yes", "Proceed Now"));
                                            params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                            params.add(new BasicNameValuePair("userid", uid));
                                            //Posting user data to script
                                            json = jsonParser.makeHttpRequest(
                                                    ECASH_PAYMENT_URL, "POST", params);
                                            Ewall = "";
                                            // full json response
                                            //Log.d("Processing \nEcocash ", json.toString());


                                            try {
                                                if (TAG_SUCCESS != null) {
                                                    success = json.getInt(TAG_SUCCESS);
                                                    Log.d("JSon Results", "Success-" + json.getInt(TAG_SUCCESS) + "  |Message-" + json.getString(TAG_PRODUCTDETAILS));


                                                    Log.e("Payment Failure!", json.getString(TAG_MESSAGE));

                                                    Toast.makeText(getContext(), TAG_MESSAGE, Toast.LENGTH_LONG).show();
                                                    return json.getString(TAG_MESSAGE);
                                                }
                                            } catch (Exception e) {
                                                Log.e("Ecocash error: ", e.getMessage());
                                            }

                                            if (Looper.myLooper() == null) {
                                                Looper.prepare();
                                            }
                                            try {
                                                params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                            params.add(new BasicNameValuePair("cartitems", JsonArr.toString()));

                                                  json = jsonParser.makeHttpRequest(
                                                          ECORESPONSE_URL, "POST", params);

                                                  Log.e("cart items", "" + JsonArr.toString());

                                                  try {
                                                      if (TAG_SUCCESS != null) {
                                                          success = json.getInt(TAG_SUCCESS);
                                                          Log.d("JSon Results", "Success-" + json.getInt(TAG_SUCCESS) + "  |Message-" + json.getString(TAG_PRODUCTDETAILS));
                                                      }
                                                  } catch (Exception e) {
                                                      Log.e("Ecocashresp error: ", e.getMessage());
                                                  }

                                                  if (success == 1) {

                                                      Log.e("SUCCESS", "you can delete " + oid);
                                                      DatabaseHelper myDBHandler = new DatabaseHelper(getContext());

                                                      myDBHandler.clearCartItems();

                                                      Log.e("logging", "starting intent...............");
                                                      Intent intent = new Intent(getActivity(), TransactionHistory.class);
                                                      startActivity(intent);

                                                      Toast.makeText(getContext(), " Payment successfull", Toast.LENGTH_LONG).show();

                                                      return json.getString(TAG_MESSAGE);


                                                  }


                                                  else{

                                                      Log.e("Payment not valid!", json.getString(TAG_MESSAGE));

                                                      try {
                                                          params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                                          params.add(new BasicNameValuePair("cartitems", JsonArr.toString()));
                                                          params.add(new BasicNameValuePair("userid", uid));
                                                          json = jsonParser.makeHttpRequest(
                                                                  DELETEORDER_URL, "POST", params);

                                                          Log.e("Order cleared!", json.getString(TAG_MESSAGE));

                                                          Toast.makeText(getContext(), "Payment validation failed, Please Try again later!", Toast.LENGTH_LONG).show();
                                                          //return json.getString(TAG_MESSAGE);
                                                      }
                                                      catch(Exception x){

                                                          Toast.makeText(getContext(), "Possible Network Error!", Toast.LENGTH_LONG).show();

                                                      }
                                                  }

                                              }
                                                catch(Exception e) {


                                                }




                                        }
                                        if (isSumarry == 3) {
                                            Log.e("Processing Payment", "Telecash" + accno + "OTP" + Otp);
                                            params.add(new BasicNameValuePair("ptype", "Telecash"));
                                            params.add(new BasicNameValuePair("endUserId", accno));
                                            params.add(new BasicNameValuePair("endUserotp", Otp));
                                            params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                            params.add(new BasicNameValuePair("endUserotp", "Hammer and Tongues Online Order"));
                                            params.add(new BasicNameValuePair("amt", totalPrc));
                                            params.add(new BasicNameValuePair("userid", uid));
                                            params.add(new BasicNameValuePair("yes", "Proceed Now"));

                                            //Posting user data to script
                                            json = jsonParser.makeHttpRequest(
                                                    TCASH_PAYMENT_URL, "POST", params);
                                            Ewall = "";
                                            // full json response
                                            Log.d("Processing Telecash ", json.toString());


                                            if (success == 1) {
                                                Log.d("Payment Process Success", json.toString());

                                                params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                                params.add(new BasicNameValuePair("cartitems", JsonArr.toString()));
                                                json = jsonParser.makeHttpRequest(
                                                        ECORESPONSE_URL, "POST", params);

                                                Log.e("cart items", "" + JsonArr.toString() );

                                                try {
                                                    if (TAG_SUCCESS != null) {
                                                        success = json.getInt(TAG_SUCCESS);
                                                        Log.d("JSon Results", "Success-" + json.getInt(TAG_SUCCESS) + "  |Message-" + json.getString(TAG_PRODUCTDETAILS));
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("Ecocashresp error: ", e.getMessage());
                                                }

                                                if (success == 1) {

                                                    Log.e("SUCCESS", "you can delete " + oid);
                                                    myDBHandler myDBHandler = new myDBHandler(getContext(), null, null, 0);

                                                    myDBHandler.clearCartItems();

                                                    Log.e("logging", "starting intent...............");
                                                    Intent intent = new Intent(getActivity(), TransactionHistory.class);
                                                    startActivity(intent);

                                                    Toast.makeText(getContext(), " Payment successfull" , Toast.LENGTH_LONG).show();

                                                    return json.getString(TAG_MESSAGE);


                                                }

                                                else {
                                                    Log.e("Payment not valid!", json.getString(TAG_MESSAGE));

                                                    params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                                    params.add(new BasicNameValuePair("cartitems", JsonArr.toString()));
                                                    json = jsonParser.makeHttpRequest(
                                                            DELETEORDER_URL, "POST", params);

                                                    Log.e("Order cleared!", json.getString(TAG_MESSAGE));

                                                    Toast.makeText(getContext(), "Payment validation failed, Please Try again later!", Toast.LENGTH_LONG).show();
                                                    return json.getString(TAG_MESSAGE);

                                                }





                                            } else {
                                                Log.d("No Internet Failure!", json.getString(TAG_MESSAGE));

                                                Toast.makeText(getContext(), "Failed To process payment, Please Try again later: NETWORK ERROR", Toast.LENGTH_LONG).show();
                                                return json.getString(TAG_MESSAGE);

                                            }


                                        }

                                        if (isSumarry == 1){

                                            Log.e("Processing COD", "COD" );
                                            params.add(new BasicNameValuePair("userid", uid));
                                            params.add(new BasicNameValuePair("ptype", "COD"));

                                            //Posting user data to script
                                            json = jsonParser.makeHttpRequest(
                                                    CHECKUSERLEVEL_URL, "POST", params);
                                            Ewall = "";
                                            // full json response

                                            String resp = json.getString(TAG_USERRES);
                                            Log.e("Processing COD ","Ecoed" + resp);


                                            if (resp.equals("COD approved!")) {
                                                Log.d("Payment Process Success", json.toString());

                                                params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                                params.add(new BasicNameValuePair("cartitems", JsonArr.toString()));
                                                params.add(new BasicNameValuePair("ptype", "COD"));
                                                json = jsonParser.makeHttpRequest(
                                                        ECORESPONSE_URL, "POST", params);

                                                Log.e("cart items", "" + JsonArr.toString() );

                                                try {
                                                    if (TAG_SUCCESS != null) {
                                                        success = json.getInt(TAG_SUCCESS);
                                                        Log.d("JSon Results", "Success-" + json.getInt(TAG_SUCCESS) + "  |Message-" + json.getString(TAG_PRODUCTDETAILS));
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("COD error: ", e.getMessage());
                                                }

                                                if (success == 1) {

                                                    pDialog.dismiss();


                                                    Toast.makeText(getContext(), " Order processed successfully!" , Toast.LENGTH_LONG).show();

                                                    Log.e("SUCCESS", "you can delete " + oid);
                                                    myDBHandler myDBHandler = new myDBHandler(getContext(), null, null, 0);

                                                    myDBHandler.clearCartItems();

                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle("Order Processing")
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            })
                                                            .setNegativeButton("", null)
                                                            .setMessage(Html.fromHtml("Order Processed successfully!"))
                                                            .show();

                                                    Log.e("logging", "starting intent...............");
                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                    startActivity(intent);


                                                    return json.getString(TAG_USERRES);


                                                }

                                                else {
                                                    Log.e("Notification not sent!", json.getString(TAG_MESSAGE));


                                                    Toast.makeText(getContext(), "Sending notification failed, Please Try again later!", Toast.LENGTH_LONG).show();
                                                    return json.getString("levelmessage");

                                                }





                                            }    else {

                                                params.add(new BasicNameValuePair("oid", String.valueOf(oid)));
                                                params.add(new BasicNameValuePair("cartitems", JsonArr.toString()));
                                                json = jsonParser.makeHttpRequest(
                                                        DELETEORDER_URL, "POST", params);

                                                Log.e("Order cleared!", json.getString(TAG_MESSAGE));

                                                Toast.makeText(getContext(), "Sorry, your use level does not alow you to use this option!", Toast.LENGTH_LONG).show();
                                                //return json.getString(TAG_MESSAGE);

                                            }


                                        }


                                        if (isSumarry == 4) {

                                            //Log.e("CreateOrder", "starting order creation");
                                            //json = jsonParser.makeHttpRequest(
                                            //      CREATE_ORDERS_URL, "POST", params);
                                            //success = json.getInt(TAG_SUCCESS);
                                            //if (success == 1) {

                                            ///  Log.e("Get Cart Success", json.getString(TAG_PRODUCTDETAILS));
                                            flag = 1;
                                            oid = json.getInt(TAG_ID);
                                            Intent i = new Intent(getActivity(), WebViewActivity.class);
                                            startActivity(i);
                                        }

                                        //}
                                        // json success element
                                        success = json.getInt(TAG_SUCCESS);
                                        if (success == 1) {
                                            Log.d("Payment Process Success", json.toString());
                                            return json.getString(TAG_MESSAGE);
                                        } else {
                                            Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                                            return json.getString(TAG_MESSAGE);

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("Error:", e.toString());

                                    }
                                    //return null;


                                    return json.getString(TAG_PRODUCTDETAILS);


                                } else {

                                    Toast.makeText(getContext(), "Failed To Create Your Order, Please Try again later: NETWORK ERROR", Toast.LENGTH_LONG).show();
                                    return json.getString(TAG_MESSAGE);

                                }


                                //else{
                                // Log.e("No Internet Failure!", json.getString(TAG_MESSAGE));

                                //Toast.makeText(getContext(), "Failed To process payment, Please Try again later: NETWORK ERROR", Toast.LENGTH_LONG).show();
                                //return json.getString(TAG_MESSAGE);

                                //  }
                            } catch (JSONException e) {

                            }





                            }







                        }

                        else {

                        Log.e("Nothing", "Results null!!!!!!!");
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








            pDialog.dismiss();
            if (posts != null){
                callPaymentsAsyncTask();
                Toast.makeText(getContext(), posts, Toast.LENGTH_LONG).show();
            }

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

                    Toast.makeText(getContext(), "Payment successfull!", Toast.LENGTH_LONG).show();

                    updateewallet();


                }

            }

            if (posts != null) {



                Log.e("SUCCESS","you can delete "+ oid);
                DatabaseHelper myDBHandler=new DatabaseHelper(getContext());

                myDBHandler.clearCartItems();
                //myDBHandler.clearCartItems(Integer.parseInt(deleteId));

                Toast.makeText(getContext(),  " Type:" + isSumarry+" or delete id is "+deleteId, Toast.LENGTH_LONG).show();





            }

        }
    }



    class GetDiscountVoucher extends AsyncTask<String, String, String> {

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


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.e(" Discount Voucher", Discount);
                        params.add(new BasicNameValuePair("voucherid", Discount));
                        params.add(new BasicNameValuePair("amt", totalPrc));
                params.add(new BasicNameValuePair("dlvrychrg", DlvryChrg));
                    //Posting user data to script
                    json = jsonParser.makeHttpRequest(
                            DISCOUNT_URL, "POST", params);
                     // full json response
                    Log.d(" Voucher Result ", json.toString());



                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Payment Process Success", json.toString());
                    return json.getString(TAG_PRODUCTDETAILS);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            }catch(Exception e){
                e.printStackTrace();
                Log.e("Error:", e.toString());
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
                //Toast.makeText(getContext(),  " Results:" + posts, Toast.LENGTH_LONG).show();
               // discount.setText(posts);

                JSONArray jsonarray02 = null;
                try {
                    jsonarray02 = new JSONArray(posts);

                } catch (JSONException e) {
                    Log.e("JSON Error: ", e.toString());
                    Log.e("Detail of Error", posts);
                    e.printStackTrace();
                }
                if (jsonarray02 != null) {
                    try {
                        JSONObject jsonobject = jsonarray02.getJSONObject(0);
                        String disc  = jsonobject.optString("cpn_amt");
                        DiscountAmount=disc;
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("voucher_amount", disc);
                        editor.putString("voucher_code", Discount);
                        editor.commit();
                        editor.apply();
                        payment("ewallet");

                        Log.e("Balance", disc);
                    } catch (JSONException e) {
                        Log.e("JSON Error: ", e.toString());
                        e.printStackTrace();
                    }


                }
                else
                {
                    DiscountAmount=null;
                   // Toast.makeText(getActivity(), posts, Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
