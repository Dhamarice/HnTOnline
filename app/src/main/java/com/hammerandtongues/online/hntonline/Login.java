package com.hammerandtongues.online.hntonline;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import preferences.MyPref;
import preferences.MyPrefAddress;

/**
 * Created by NgonidzaIshe on 2/6/2016.
 */
public class Login  extends Fragment {

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences shared;
    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //private static final String LOGIN_URL = "https://hammerandtongues.com/webservice/login.php";
    //private static final String EWALL_URL = "https://hammerandtongues.com/webservice/getewalletbal.php";
    private static final String LOGIN_URL = "https://devshop.hammerandtongues.com/webservice/login.php";
    //private static final String LOGIN_URL = "http://10.0.2.2:8012/webservice/login.php";
    private static final String LOGINN_URL = "https://devshop.hammerandtongues.com/webservice/register.php";
    private static final String EWALL_URL = "https://devshop.hammerandtongues.com/webservice/getewalletbal.php";
    private static final String UPDATE_URL = "https://devshop.hammerandtongues.com/webservice/updatedetails.php";
    private static final String RESET_URL = "https://devshop.hammerandtongues.com/webservice/ResetPass.php";

    private static final String GETPUSER_URL = "https://devshop.hammerandtongues.com/webservice/getuserdetail.php";
    private static final String GETCITIES_URL = "https://devshop.hammerandtongues.com/webservice/getcities.php";
    private static final String GETSURBURBS_URL = "https://devshop.hammerandtongues.com/webservice/getsurburbs.php";
    private static final String GETPICKUPLOCATIONS_URL = "https://devshop.hammerandtongues.com/webservice/getpickuplocations.php";


    //private static final String RESET_URL = "http://10.0.2.2:8012/webservice/ResetPass.php";


    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_CATEGORIES = "posts";
    TextView ewallet, pemail, address,welcum, ewall, addwarning,membership, points;
    int success=0;

    EditText username, password, nameoremail;
    String uname, pword, uEname, fEname, eEmail, sEname,Plogin,Pconfirm, userReset;
    String Email, uName,membership_level, loyalty_points, Balance="";
    String Address = "";
    String UserID = "";
    RelativeLayout tione;
    ImageView symbolcoin;
    LinearLayout add_details, forgotform, input_container, inside,BasicForm, notifyform, my_finances;
    Button update, logout, basic, shopping, addadd, back,bac,updat, reset,btnnotify, deposit_money, withdraw,my_transactions,transfer_credits,redeem_points,back_to_profile, finances;
    EditText txtadd1, txtadd2, txtsurbub, txtcity, txtregion, txtidno, txtcountry, uEName, fName, sName, email, pword_login, pword_confrim;
    String   idno, add1,add2, suburb, city, region, country;

    boolean isClicked = true;
    PopupWindow popUpWindow;
    LinearLayout.LayoutParams layoutParams;
    LinearLayout mainLayout;
    Button btnresend, btnforgot;
    LinearLayout containerLayout;
    TextView txtuser;
    //Global Variables

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.login, container, false);
            //TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            shared = getActivity(). getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            System.gc();


            input_container = (LinearLayout) view.findViewById(R.id.input_container);
            forgotform = (LinearLayout) view.findViewById(R.id.forgotform);
            notifyform = (LinearLayout) view.findViewById(R.id.notifyform);
            BasicForm = (LinearLayout) view.findViewById(R.id.BasicForm);
            add_details = (LinearLayout) view.findViewById(R.id.add_details);
            my_finances = (LinearLayout) view.findViewById(R.id.my_finances);
            btnforgot = (Button) view.findViewById(R.id.btnforgot);
            btnnotify = (Button) view.findViewById(R.id.btnnotify);
            btnresend = (Button) view.findViewById(R.id.btnresend);
            addadd = (Button) view.findViewById(R.id.add_add);
            logout = (Button) view.findViewById(R.id.logout);
            update = (Button) view.findViewById(R.id.update);
            back = (Button) view.findViewById(R.id.bac);
            updat = (Button) view.findViewById(R.id.updat);
            bac = (Button) view.findViewById(R.id.back);
            basic = (Button) view.findViewById(R.id.basicInfo);
            reset = (Button) view.findViewById(R.id.reset);
            shopping = (Button) view.findViewById(R.id.shopping);
            finances = (Button) view.findViewById(R.id.finances);
            deposit_money = (Button) view.findViewById(R.id.deposit_money);
            withdraw = (Button) view.findViewById(R.id.withdraw);
            //my_transactions = (Button) view.findViewById(R.id.my_transactions);
            transfer_credits = (Button) view.findViewById(R.id.transfer_credits);
            redeem_points = (Button) view.findViewById(R.id.redeem_points);
            back_to_profile = (Button) view.findViewById(R.id.back_to_profile);
           tione =(RelativeLayout) view.findViewById(R.id.tione);
            inside = (LinearLayout) view.findViewById(R.id.inside);
            welcum = (TextView) view.findViewById(R.id.pusername);
            pemail = (TextView) view.findViewById(R.id.pemail);
            ewall = (TextView) view.findViewById(R.id.ewall);
            addwarning = (TextView) view.findViewById(R.id.addwarning);
            membership = (TextView) view.findViewById(R.id.membershiplevel);
            points = (TextView) view.findViewById(R.id.loyalty_points);
            nameoremail = (EditText) view.findViewById(R.id.nameoremailll);
            txtadd1 = (EditText) view.findViewById(R.id.addressLn1);
            txtadd2 = (EditText) view.findViewById(R.id.addressLn2);
            txtsurbub = (EditText) view.findViewById(R.id.surbub);
            txtcity = (EditText) view.findViewById(R.id.city);
            txtregion = (EditText) view.findViewById(R.id.regionstate);
            txtcountry = (EditText) view.findViewById(R.id.country);
            txtidno = (EditText) view.findViewById(R.id.idno);
            uEName =(EditText) view.findViewById(R.id.uName);
            fName = (EditText) view.findViewById(R.id.fName);
            sName = (EditText) view.findViewById(R.id.sName);
            email = (EditText) view.findViewById(R.id.eEmail);
            pword_login = (EditText) view.findViewById(R.id.pword_login);
            pword_confrim = (EditText) view.findViewById(R.id.pword_confrim);
            symbolcoin = (ImageView) view.findViewById(R.id.coin);



            uEName.setText(shared.getString("uname", ""));
            uEName.setEnabled(false);
            fName.setText(shared.getString("Fname", ""));
            sName.setText(shared.getString("sname", ""));
            email.setText(shared.getString("umail", ""));
            email.setEnabled(false);

            username = (EditText) view.findViewById(R.id.username);
            password = (EditText) view.findViewById(R.id.password);
            Button signin = (Button) view.findViewById(R.id.btnlogin);
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    uname= username.getText().toString();
                    pword= password.getText().toString();
                    loginMethod( pword, uname);
                    // new AttemptLogin().execute();

                }

            });






            logout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Log.e("Logout: ", "UserId: " +  shared.getString("userid", "") + "Address: " + shared.getString("address", "") );



                    SharedPreferences.Editor editor = shared.edit();

                    editor.remove("userid");
                    editor.apply();
                    editor.commit();

                    editor.remove("address");
                    editor.apply();
                    editor.commit();

                    DatabaseHelper myDBHandler = new DatabaseHelper(getContext());
                    myDBHandler.clearCartItems();

                    input_container.setVisibility(View.VISIBLE);
                    inside.setVisibility(View.GONE);


                    Log.e("Logout: ", "UserId: " +  shared.getString("userid", "") + "Address: " + shared.getString("address", "") );


                }

            });
            shopping.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }

            });

            addadd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //idcontainer.setVisibility(View.GONE);
                    inside.setVisibility(View.GONE);
                    BasicForm.setVisibility(View.GONE);
                    tione.setVisibility(View.VISIBLE);
                    add_details.setVisibility(View.VISIBLE);
                }

            });

            deposit_money.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("request_type", "deposit");
                    editor.apply();

                    Intent myintent = new Intent(getActivity(), Finances_Webview.class);
                    getActivity().finish();
                    startActivity(myintent);
                }

            });

            withdraw.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("request_type", "withdraw");
                    editor.apply();

                    Intent myintent = new Intent(getActivity(), Finances_Webview.class);
                    getActivity().finish();
                    startActivity(myintent);
                }

            });



            transfer_credits.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("request_type", "transfer_credits");
                    editor.apply();

                    Intent myintent = new Intent(getActivity(), Finances_Webview.class);
                    getActivity().finish();
                    startActivity(myintent);
                }

            });

            redeem_points.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("request_type", "redeem_points");
                    editor.apply();

                    Intent myintent = new Intent(getActivity(), Finances_Webview.class);
                    getActivity().finish();
                    startActivity(myintent);
                }

            });

            back_to_profile.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    tione.setVisibility(View.GONE);
                    my_finances.setVisibility(View.GONE);
                    inside.setVisibility(View.VISIBLE);


                }
            });

            finances.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //idcontainer.setVisibility(View.GONE);
                    inside.setVisibility(View.GONE);
                    BasicForm.setVisibility(View.GONE);
                    tione.setVisibility(View.VISIBLE);
                    my_finances.setVisibility(View.VISIBLE);
                }

            });



            update.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {


                    //else if (next.getText().toString().trim().toUpperCase().contentEquals("SAVE")){

                    if (txtadd1.getText().toString().contentEquals("") || txtadd2.getText().toString().contentEquals("") || txtsurbub.getText().toString().contentEquals("") ||
                            txtcity.getText().toString().contentEquals("") || txtregion.getText().toString().contentEquals("") || txtidno.getText().toString().contentEquals("") ||
                            txtcountry.getText().toString().contentEquals("")) {
                        Toast.makeText(getContext(), "All fields are mandatory", Toast.LENGTH_LONG).show();
                    } else {

                        // Log.e("Submit Button Text", save.getText().toString());
                        idno = txtidno.getText().toString();
                        add1 = txtadd1.getText().toString();
                        add2 = txtadd2.getText().toString();

                        suburb = txtsurbub.getText().toString();
                        city = txtcity.getText().toString();
                        region = txtregion.getText().toString();

                        country = txtcountry.getText().toString();

                        SaveDetails(add1, add2, country, region, city, suburb, idno);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("address", add1 + add2);
                        editor.commit();

                        MyPrefAddress.saveAllAddress(getContext(), add1, add2, suburb, city, region, country);

                    }
                }
            });

            basic.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //idcontainer.setVisibility(View.GONE);
                    inside.setVisibility(View.GONE);
                    tione.setVisibility(View.VISIBLE);
                    BasicForm.setVisibility(View.VISIBLE);
                    add_details.setVisibility(View.GONE);
                }

            });

            back.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //idcontainer.setVisibility(View.GONE);
                    inside.setVisibility(View.VISIBLE);
                    tione.setVisibility(View.GONE);
                    add_details.setVisibility(View.GONE);

                }
            });

            bac.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    tione.setVisibility(View.GONE);
                    BasicForm.setVisibility(View.GONE);
                    inside.setVisibility(View.VISIBLE);


                }
            });

            updat.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (uEName.getText().toString().contentEquals("") || fName.getText().toString().contentEquals("") || sName.getText().toString().contentEquals("") ||
                            email.getText().toString().contentEquals("") ) {
                        Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_LONG).show();
                    } else {

                        uEname = uEName.getText().toString();
                        fEname = fName.getText().toString();
                        sEname = sName.getText().toString();
                        eEmail = email.getText().toString();


                        saveReg(sEname, eEmail, fEname, uEname);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("address", add1 + add2);
                        editor.commit();
                    }
                }
            });


            reset.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {


                    UserID = (shared.getString("userid", ""));

                    if (pword_login.getText().toString().contentEquals("") || pword_confrim.getText().toString().contentEquals("")){

                        Toast.makeText(getContext(), "Password fields can not be empty for reset!", Toast.LENGTH_LONG).show();


                    }

                    Plogin = pword_login.getText().toString();
                    Pconfirm = pword_confrim.getText().toString();
                    userReset = "notnecessary";
                    if(Plogin.contentEquals(Pconfirm) ) {
                        ResetPass(Plogin,UserID, userReset);
                    }

                    else{

                        Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
                    }

                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("address", add1 + add2);
                    editor.commit();

                }
            });





            btnforgot.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    input_container.setVisibility(View.GONE);
                    forgotform.setVisibility(View.VISIBLE);

                }
            });




            btnresend.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (nameoremail.getText().toString().contentEquals("")){

                        Toast.makeText(getContext(), "Please enter your username or Email", Toast.LENGTH_LONG).show();


                    }
else {
                        userReset = nameoremail.getText().toString();
                        UserID = "unknownhapanazeronada";
                        Plogin = "unknownhapanazeronada";

                        ResetPass(Plogin, UserID, userReset);
                    }

                }
            });

            btnnotify.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    startActivity(intent);

                }
            });






            if (shared.getString("userid", "") != null && shared.getString("userid", "") !="" && shared.getString("userid", "") !="null" && !(shared.getString("userid", "").contentEquals("null")) && !(shared.getString("userid", "").contentEquals(""))) {
                UserID = (shared.getString("userid", ""));
                Address = (shared.getString("address", ""));
                uName = (shared.getString("uname", ""));
                Email = (shared.getString("umail", ""));
                Balance = (shared.getString("balance", ""));
                membership_level = (shared.getString("membership", ""));
                loyalty_points = (shared.getString("points", ""));
                input_container.setVisibility(View.GONE);



                new GetConnectionStatus().execute();

                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                  //  welcum.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    //ewall.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                //}


                welcum.setText(Html.fromHtml("<b>" + uName + "</b>"  ));
                pemail.setText(Html.fromHtml( "<b>" + Email + "</b>"));


                if (Address !=null && Address !="" && Address !="null" && !Address.contentEquals("null")){


                    addwarning.setText(Html.fromHtml( "<b>" + Address + "</b>"));


                    inside.setVisibility(View.VISIBLE);

                }
                else {

                    addwarning.setTextColor(Color.BLACK);
                    addwarning.setText("Please register your address details, for a better shopping experience!!!");

                    inside.setVisibility(View.GONE);
                    BasicForm.setVisibility(View.GONE);
                    tione.setVisibility(View.VISIBLE);
                    add_details.setVisibility(View.VISIBLE);

                }


                if (Balance != null && isNumeric(Balance)) {
                    if (Double.parseDouble(Balance) == 0.00) {
                        ewall.setTextColor(Color.RED);
                    } else {
                        ewall.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                }
                else {
                    ewall.setTextColor(Color.RED);
                    Balance = "$0.00";
                }

                ewall.setText("$" + Balance);


                if (membership_level == null || membership_level.contentEquals("") ) {
                membership_level = "Bronze";
                }
            membership.setText(Html.fromHtml( "<b>" + membership_level + "</b>"));


                if (membership_level.contentEquals("Platinum")){

                    symbolcoin.setImageResource(R.drawable.platinumalltypes);
                }

                else if (membership_level.contentEquals("Gold")){

                    symbolcoin.setImageResource(R.drawable.goldalltypes);
                }

                else if (membership_level.contentEquals("Silver")){

                    symbolcoin.setImageResource(R.drawable.silveralltypes);
                }
                else if (membership_level.contentEquals("Bronze")){

                    symbolcoin.setImageResource(R.drawable.bronzealltypes);
                }

                if (loyalty_points == null || loyalty_points.contentEquals("") ) {
                    loyalty_points = "0";
                }
                points.setText(Html.fromHtml( "<b>" + loyalty_points + " points </b>"));

                return view;



            }

            else{

                welcum.setText(Html.fromHtml("<b>You are Logged in, \n \n Hello!</b> " ));

                addwarning.setText(Html.fromHtml("<b>Happy Shopping!!!</b> "));
                //inside.setVisibility(View.VISIBLE);

                //Toast.makeText(getContext(), "PLEASE ADD YOUR ADDRESS DETAILS!", Toast.LENGTH_LONG).show();

            }








            Button signup = (Button) view.findViewById(R.id.btnSignUp);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                }

            });

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






    private void loginMethod(final String uname, final String pword){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success",""+s);
                Log.e("Zitapass", "" + pword);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonobject=new JSONObject(s);
                    int success=jsonobject.getInt("success");
					String message = jsonobject.getString("message");
                    if(success==1){



                        JSONArray array=jsonobject.getJSONArray("posts");

                        JSONObject object=array.getJSONObject(0);





                        String post_id = object.optString("id");
                        String address = object.optString("add");
                        String province = object.optString("province");
                        String country = object.optString("country");
                        String uname = object.optString("uname");
                        String Fname = object.optString("Fname");
                        String sname = object.optString("sname");
                        String umail = object.optString("umail");
                        String balance = object.optString("balance");
                        String number = object.optString("mobile_number");
                        String membership = object.optString("level");
                        String points = object.optString("loyalty_points");
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("userid", post_id);
                            editor.putString("address", address);

                       MyPrefAddress.saveAllAddress(getContext(), address, address, uname, province, province, country);

                        editor.putString("uname", uname);
                        editor.putString("Fname", Fname);
                        editor.putString("sname", sname);
                        editor.putString("umail", umail);
                        editor.putString("balance", balance);
                        editor.putString("telno", number);
                        editor.putString("membership", membership);
                        editor.putString("points", points);
                        editor.putString("options", "checkout");
                        editor.commit();
                        editor.apply();

                        //zvese.setVisibility(View.VISIBLE);
                        //input_container.setVisibility(View.GONE);
                        //inside.setVisibility(View.VISIBLE);



                        if(shared.getString("fromCheckout", "") != null && shared.getString("fromCheckout", "") !="")   {

                            editor.remove("fromCheckout");
                            editor.apply();

                            Intent intent = new Intent(getActivity(), CheckOut_Activity.class);
                            getActivity().finish();
                            startActivity(intent);


                        }


else {
                            Intent intent = new Intent(getContext(), UserActivity.class);
                            startActivity(intent);
                        }

                        Toast.makeText(getContext(), (message), Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getContext(), (message), Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();
                volleyError.printStackTrace();
                Log.e("RUEERROR",""+volleyError);
                Toast.makeText(getContext(), "Please check your Intenet and try again!", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                values.put("username",pword);
                values.put("password",uname);


                return values;
            }


//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Map<String,String> headers=new HashMap();
//                headers.put("Accept","application/json");
//                headers.put("Content-Type","application/json");
//                return headers;
//            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        //requestQueue.add(stringRequest);




    }




    private void SaveDetails(final String address1, final String address2, final String country, final String regionstate, final String city, final String surbub, final String Idno){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, UPDATE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success",""+s);
                Log.e("Zitapass", "" +  (shared.getString("userid", "")));
                Log.e("Address pass", "" +  (shared.getString("address", "")));
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonobject=new JSONObject(s);
                    int success=jsonobject.getInt("success");
                    if(success==1){

                        Toast.makeText(getContext(), ("Update Successfull!"), Toast.LENGTH_SHORT).show();





                        inside.setVisibility(View.VISIBLE);
                        tione.setVisibility(View.GONE);
                        add_details.setVisibility(View.GONE);


                    }else{
                        Toast.makeText(getContext(), ("An error occured please try again..."), Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();
                volleyError.printStackTrace();
                Log.e("RUEERROR",""+volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                values.put("userid",(shared.getString("userid", "")));
                values.put("address1",address1);
                values.put("address2",address2);
                values.put("country",country);
                values.put("regionstate",regionstate);
                values.put("city", city);
                values.put("surbub", surbub);
                values.put("Idno", Idno);
                values.put("telno", MyPref.getPhone(getContext()));

                return values;
            }


//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Map<String,String> headers=new HashMap();
//                headers.put("Accept","application/json");
//                headers.put("Content-Type","application/json");
//                return headers;
//            }
        };
        requestQueue.add(stringRequest);




    }








    class AttemptLogin extends AsyncTask<Void, Void, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


//            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            success=0;
            // Building Parameters
            JSONObject json = new JSONObject();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", uname));
            params.add(new BasicNameValuePair("password", pword));
            json = jsonParser.makeHttpRequest(
                    LOGIN_URL, "POST", params);
//            Log.d("request!", "ending Get Products" + json.toString());


            if (json != null)    // check your log for json response
            {
                Log.d("Login attempt", json.toString());

                // json success tag
                success = Integer.parseInt(json.optString (TAG_SUCCESS));
                if (success == 1) {
                    Log.d("GET Successful!", json.optString(TAG_CATEGORIES));
                    return json.optString(TAG_CATEGORIES);
                } else {
                    Log.d("Login Failure!", json.optString(TAG_MESSAGE));
                    return json.optString(TAG_CATEGORIES);
                }
            }
            return "No Data";

        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String posts) {
            //dismiss the dialog once asynctask  complete
            //    pDialog.dismiss();
            if (posts != null) {
                if (success==1)
                {
                    Log.e("JSON Output: ", posts);
                    // Toast.makeText(getActivity(),"Welcome - Happy Shopping",Toast.LENGTH_LONG).show();
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
                            String post_id = jsonobject.optString("id");
                            String address = jsonobject.optString("add");
                            String uname = jsonobject.optString("uname");
                            String umail = jsonobject.optString("umail");
                            String balance = jsonobject.optString("balance");
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("userid", post_id);

                            if (address != null && address != "") {
                                editor.putString("address", address);
                            }

                            else {editor.putString("address", "");}

                            editor.putString("uname", uname);
                            editor.putString("umail", umail);
                            editor.putString("balance", balance);
                            editor.putString("options", "checkout");
                            editor.commit();


                            Log.e("User ID", post_id);
                            Log.e("User Address",address);
                        } catch (JSONException e) {
                            Log.e("JSON Error: ", e.toString());
                            e.printStackTrace();
                        }


                    }
                    else
                    {
                        Toast.makeText(getActivity(), posts, Toast.LENGTH_LONG).show();
                    }
                    TabLayout tabLayout=(TabLayout) getActivity().findViewById(R.id.tabs02);
                    final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager02);
                    final PagerAdapter adapter = new PagerAdapter02 (getFragmentManager(), tabLayout.getTabCount());
                    viewPager.setAdapter(adapter);
                    viewPager.setCurrentItem(1);

                }
                else
                {
                    Toast.makeText(getActivity(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                }
            }

        }




    }


    class GetEWalletBalance extends AsyncTask<Void, Void, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Loading, Please Wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            success=0;
            // Building Parameters
            JSONObject json = new JSONObject();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", shared.getString("userid", "")));
            json = jsonParser.makeHttpRequest(
                    EWALL_URL, "POST", params);
            Log.d("request!", "ending Get Products"+ json.toString());


            if (json != null)    // check your log for json response
            {
                Log.d("Login attempt", json.toString());

                // json success tag
                success = Integer.parseInt(json.optString (TAG_SUCCESS));
                if (success == 1) {
                    Log.d("GET Successful!", json.optString(TAG_CATEGORIES));
                    return json.optString(TAG_CATEGORIES);
                } else {
                    Log.d("Login Failure!", json.optString(TAG_MESSAGE));
                    return json.optString(TAG_CATEGORIES);
                }
            }
            return "No Data";

        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String posts) {
            //dismiss the dialog once asynctask  complete
            pDialog.dismiss();
            if (posts != null) {
                if (success==1)
                {
                    try {
                        Log.e("JSON Output: ", posts);
                        //Toast.makeText(getActivity(),"Welcome - Happy Shopping",Toast.LENGTH_LONG).show();
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
                                String balance = jsonobject.optString("balance");
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("balance", balance);
                                editor.putString("options", "checkout");
                                editor.commit();

                                if (balance != null){
                                    ewallet.setText("E-Wallet Balance: " + balance);
                                }

                                else {
                                    ewallet.setText("E-Wallet Balance: " + 0.00);
                                }
                                Log.e("Balance", balance);

                            } catch (JSONException e) {
                                Log.e("JSON Error: ", e.toString());
                                e.printStackTrace();
                            }


                        }
                        else
                        {
                            Toast.makeText(getActivity(), posts, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Error: ", e.toString());

                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                }
            }

        }

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

                    //todo getwallet balance
//                    new GetEWalletBalance().execute();



                } else

                {
                    Toast.makeText(getContext(), "Network is Currently Unavailable", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception ex) {
//                Toast.makeText(getContext(), "Weak / No Network Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void saveReg(final String surname, final String email,
                          final String firstname,final String username){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(getContext());


        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, LOGINN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success",""+s);
                Log.e("NUMBER to update","Number equals" + MyPref.getPhone(getContext()));
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject=new JSONObject(s);
                    int success=jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if(success==1){

                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        startActivity(intent);


                        //  mChangeAdapter.callbackToZero(0);
                        Toast.makeText(getContext(), "Your account was created successful", Toast.LENGTH_SHORT).show();
                        Fragment fragment = new Register();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.idcontainer, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();


                    }else{
                        Toast.makeText(getContext(), (s), Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();

                Log.e("RUEERROR",""+volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                values.put("update", "update");
                values.put("username",username);
                values.put("surname", surname);

                values.put("email", email);
                values.put("telno", MyPref.getPhone(getContext()));

                values.put("firstname", firstname);

                return values;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);




    }

    private void ResetPass(final String Pass, final String userID, final String useroremail){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(getContext());

        Log.e("NUMBER",(shared.getString("telno", "")));


        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, RESET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success",""+s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject=new JSONObject(s);
                    int success=jsonObject.getInt("success");
                    String message=jsonObject.getString("message");

                    Log.e("PassedName",useroremail);

                    if(success==1){

                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        startActivity(intent);


                        //  mChangeAdapter.callbackToZero(0);
                        Toast.makeText(getContext(), "Your password was reset successful Password to " + Pass, Toast.LENGTH_SHORT).show();



                    }

                    else if (success == 2){



                        Toast.makeText(getContext(), "An sms and an email has been sent to you ", Toast.LENGTH_SHORT).show();


                        notifyform.setVisibility(View.VISIBLE);
                        forgotform.setVisibility(View.GONE);


                    }


                    else{
                        Toast.makeText(getContext(), (message), Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();

                Log.e("RUEERROR",""+volleyError);

                Toast.makeText(getContext(), "No Intenet connection!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                values.put("password",Pass);
                values.put("userid",userID);
                values.put("useroremail",useroremail);

                return values;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);




    }




}
