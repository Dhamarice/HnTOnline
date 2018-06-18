package com.hammerandtongues.online.hntonline;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import preferences.MyPrefAddress;

/**
 * Created by NgonidzaIshe on 15/6/2016.
 */
public class UserDetails extends Fragment implements View.OnClickListener{
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Name = "nameKey";
    SharedPreferences sharedpreferences;
    private   RadioGroup rg02;
    private  LinearLayout ll01,ll02;
    private Spinner spin, spin1, spcountry, spprovince, spcity, spsurburb;
    private String type,value, totalprice, chrg, diliverytype, altsurbub;
    private TextView txtaddress , txterr, txtno, tell;
    private EditText txtaddline1,txtaddline2;
    private LinearLayout main,top;
    private RadioGroup rdg;
    private  RadioButton rdNormal;
    private RadioButton   rdExpress;
    private RadioButton rdExtended ;



    // Progress Dialog
    private ProgressDialog pDialog;

    private static final String GETPUSER_URL = "https://shopping.hammerandtongues.com/webservice/getuserdetail.php";
    private static final String GETCITIES_URL = "https://shopping.hammerandtongues.com/webservice/getcities.php";
    //private static final String GETSURBURBS_URL = "https://shopping.hammerandtongues.com/webservice/getsurburbs.php";
    private static final String GETSURBURBS_URL = "https://shopping.hammerandtongues.com/wp-content/themes/Walleto/getsurburbs.php";
    private static final String GETPICKUPLOCATIONS_URL = "https://shopping.hammerandtongues.com/webservice/getpickuplocations.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PRODUCTDETAILS = "posts";
    DatabaseHelper dbHandler;
    JSONArray JsonArr = new JSONArray();
    private String deleteId;
    String PPrice, post_id,imgurl,Qty="";
    private int currcart,isSumarry,Count, oid, flag=0, totalAll, DlvryPrc, DscntPrc, OrdrPrc ;
    private Double total=0.0;

    String UserID, Address, Options, city, Address1, Address2, Country, Region, Surbub="";
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private int success;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.userdetail, container, false);



        //try {

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        System.gc();

        final SharedPreferences shared = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        rg02 = (RadioGroup) view.findViewById(R.id.radioGroup2);
        ll01 = (LinearLayout) view.findViewById(R.id.editables);
        ll02 = (LinearLayout) view.findViewById(R.id.dlvryoptions);
        spin = (Spinner) view.findViewById(R.id.spin_city);
        spin1 = (Spinner) view.findViewById(R.id.spin_pickuplocations);
        spcountry = (Spinner) view.findViewById(R.id.spin_country);
        spprovince = (Spinner) view.findViewById(R.id.spin_Province);
        spcity = (Spinner) view.findViewById(R.id.spin_city1);
        spsurburb = (Spinner) view.findViewById(R.id.spin_surburbs);
        txtaddress = (TextView) view.findViewById(R.id.address);
        txtaddline1 = (EditText) view.findViewById(R.id.addressLn1);
        txtaddline2 = (EditText) view.findViewById(R.id.addressLn2);
        txterr = (TextView) view.findViewById(R.id.txtinfo);
        txtno = (TextView) view.findViewById(R.id.txtNo);
        main = (LinearLayout) view.findViewById(R.id.editables);
        //top = (LinearLayout) view.findViewById(R.id.dlvryoptions);


        rdg = new RadioGroup(getContext());
        rdg.setOrientation(LinearLayout.VERTICAL);
        rdNormal = new RadioButton(getContext());
        rdExpress = new RadioButton(getContext());
        rdExtended = new RadioButton(getContext());
        tell = (TextView) view.findViewById(R.id.txttell);

        if (shared.getString("userid", "") != null && shared.getString("userid", "") != "") {
            UserID = (shared.getString("userid", ""));
            Address = (shared.getString("address", ""));


            Options = (shared.getString("options", ""));
            // UserID=(shared.getString("userid", ""));
            //Address=(shared.getString("address", ""));;
            //totalprice = (shared.getString("total", ""));
            Address1 = MyPrefAddress.getAddress1(getContext());
            Address2 = MyPrefAddress.getAddress2(getContext());
            Country = MyPrefAddress.getCountry(getContext());
            city = MyPrefAddress.getRegion(getContext());
            Region = MyPrefAddress.getRegion(getContext());
            Surbub = MyPrefAddress.getSurbub(getContext());
            //Radio Button Delivery Selected


            ll01.setVisibility(View.GONE);
            ll02.setVisibility(View.VISIBLE);


            view.findViewById(R.id.rd_delivery).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    //Make Delivery Options Visible
                    rg02.setVisibility(View.VISIBLE);
                    txtno.setVisibility(View.GONE);
                    //Make Alternative Address Details Invisible
                    ll01.setVisibility(View.GONE);
                    //Make City Spinner invisible
                    spin.setAdapter(null);
                    spin.setVisibility(View.GONE);

                    spin1.setAdapter(null);
                    spin1.setVisibility(View.GONE);

                    txtaddress.setText(Address);
                    txtaddress.setVisibility(View.VISIBLE);
                    rdg.setVisibility(View.VISIBLE);
                    rdExpress.setVisibility(View.VISIBLE);
                    rdExtended.setVisibility(View.VISIBLE);
                    rdNormal.setVisibility(View.VISIBLE);

                    ll02.removeView(rdg);
                    // Toast.makeText(getContext(),"Delivery Selected",Toast.LENGTH_SHORT).show();
                }
            });

            //Radio Button Pickup Selected
            view.findViewById(R.id.rd_pickup).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //MAKE DELIVERY OPTIONS INVISIBLE
                    rg02.setVisibility(View.GONE);
                    ll02.removeView(rdg);
                    txtno.setVisibility(View.GONE);
                    //Make Alternative Address Details Invisible
                    ll01.setVisibility(View.GONE);
                    // Toast.makeText(getContext(),"Pickup Selected",Toast.LENGTH_SHORT).show();

                    //Make City Spinner visible
                    if (isNetworkAvailable() == true) {
                        //new getCities().execute();
                        getCities( UserID);
                    } else {
                        //Toast.makeText(getContext(), "Please Check Your Network Connectivity", Toast.LENGTH_LONG).show();

                        Toast ToastMessage = Toast.makeText(getActivity(),"Please Check Your Network Connectivity",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();
//                        pDialog.dismiss();
                    }
                    spin.setVisibility(View.VISIBLE);
                    txtno.setVisibility(View.GONE);
                    txtaddress.setVisibility(View.GONE);
                }
            });

            //Radio Button Registered Address Selected
            view.findViewById(R.id.rd_RegAdd).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    try {
                        //Make Alternative Address Details Invisible
                        ll01.setVisibility(View.GONE);
                        ll02.removeView(rdg);
                        //Make City Spinner invisible
                        //rdg.removeAllViews();
                        spin.setVisibility(View.GONE);
                        //txtaddress.setText(Address);
                        ll01.setVisibility(View.GONE);

                        ll02.setVisibility(View.VISIBLE);

/*
                        type = "DlvryChrg";
                        value = Surbub;


                        getDropDownValues(type, value);
                        */
                        if (Address.toUpperCase().contains("HARARE") || Address.toUpperCase().contains("BULAWAYO")) {


                            ll01.setVisibility(View.GONE);

                            ll02.setVisibility(View.VISIBLE);


                            type = "DlvryChrg";
                            value = Surbub;


                            getDropDownValues(type, value);
                        } else {

                        }
                    } catch (Exception ex) {
                        Log.e("Error:", ex.toString());
                    }
                }
            });

            //Radio Button Alternative address Selected
            view.findViewById(R.id.rd_AltAdd).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    txtno.setVisibility(View.GONE);

                    //Make Alternative Address Details Visible
                    ll01.setVisibility(View.VISIBLE);
                    //Make City Spinner invisible
                    spin.setVisibility(View.GONE);
                    txtaddress.setVisibility(View.GONE);
                    //Get Regions Zimbabwe
                    if (spcountry.getSelectedItemPosition() == 1) {
                        type = "Province";
                        value = "Zimbabwe";
                        diliverytype = "N/A";
                                altsurbub = "N/A";
                        getDropDownValues(type, value);

                    } else {
                        txterr.setText("No Delivery Option available for the Selected Country");



                    }
                }
            });


            final RadioButton rddlvry, rdpckup, rdregadd, rdaltadd;
            rddlvry = (RadioButton) view.findViewById(R.id.rd_delivery);
            rdpckup = (RadioButton) view.findViewById(R.id.rd_pickup);
            rdregadd = (RadioButton) view.findViewById(R.id.rd_RegAdd);
            rdaltadd = (RadioButton) view.findViewById(R.id.rd_AltAdd);


            final Button btn_next;
            btn_next = (Button) view.findViewById(R.id.btn_submit);
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!rddlvry.isChecked() && !rdpckup.isChecked()) {
                        //Toast.makeText(getActivity(), "Please Select either Delivery or Pickup Options", Toast.LENGTH_LONG).show();

                        Toast ToastMessage = Toast.makeText(getActivity(),"Please Select either Delivery or Pickup Options",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();
                        return;
                    }
                    if (rddlvry.isChecked()) {
                        if (!rdregadd.isChecked() && !rdaltadd.isChecked()) {
                            //Toast.makeText(getActivity(), "Please Select either Registered Add or Alternative", Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(getActivity(),"Please Select either Registered Add or Alternative",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();
                            return;
                        } else if (rdregadd.isChecked() && !rdaltadd.isChecked()) {





                            diliverytype = "registred";
                            altsurbub = "Bhoo zvekuti";


                            //rdg.setVisibility(View.VISIBLE);
                            //rdExpress.setVisibility(View.VISIBLE);
                            //rdExtended.setVisibility(View.VISIBLE);
                            //rdNormal.setVisibility(View.VISIBLE);
                            txtaddress.setVisibility(View.VISIBLE);
                            //rdg.removeAllViews();
                            //ll02.removeView(rdg);
                            //rdg.addView(rdNormal);
                            //rdg.addView(rdExpress);
                            //rdg.addView(rdExtended);
                            //rdg.setOrientation(LinearLayout.HORIZONTAL);
                            //ll02.addView(rdg);







                            if (txtaddress.getText().toString().isEmpty() || txtaddress.getText().toString() == null || txtaddress.getText().toString().contentEquals("null")) {
                                //Toast.makeText(getActivity(), "No Valid Registered Delivery Address Detected", Toast.LENGTH_LONG).show();

                                Toast ToastMessage = Toast.makeText(getActivity(),"No Valid Registered Delivery Address Detected",Toast.LENGTH_LONG);
                                View toastView = ToastMessage.getView();
                                toastView.setBackgroundResource(R.drawable.toast_background);
                                ToastMessage.show();
                                return;
                            }


                           else if (!Country.toUpperCase().contentEquals("ZIMBABWE")){

                                Log.e("Getting saved adress", " Wrong Country " );
                                Log.e("Getting saved adress", "Adress: " + Address1 + " " + Address2 + " " + Region + " " + Surbub + " " + city + " " + Country);

                                txtno.setVisibility(View.VISIBLE);
                                txtno.setText("No Delivery Option available for the registered Country, Please select another option.");


                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Info")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                            }
                                        })
                                        .setNegativeButton("", null)
                                        .setMessage(Html.fromHtml("No Delivery Option available for the registered Country, Please select another option." ))
                                        .show();


                                return;


                            }
                            else if(!Region.contains("Harare") && !Region.contains("Bulawayo")){

                                Log.e("Getting saved adress", " Wrong Province " + Region);
                                Log.e("Getting saved adress", "Adress: " + Address1 + " " + Address2 + " " + Region + " " + Surbub + " " + city + " " + Country);

                                txtno.setVisibility(View.VISIBLE);
                                txtno.setText("No Delivery Option available for the registered Province, Please select another option.");


                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Info")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                            }
                                        })
                                        .setNegativeButton("", null)
                                        .setMessage(Html.fromHtml("No Delivery Option available for the registered Province, Please select another option." ))
                                        .show();

                                return;
                            }




                            else {



                                ll01.setVisibility(View.GONE);

                                ll02.setVisibility(View.VISIBLE);


                                type = "DlvryChrg";
                                value = Surbub;


                                getDropDownValues(type, value);


                                if (rdg.getCheckedRadioButtonId() > -1) {
                                    int selectedId = rdg.getCheckedRadioButtonId();
// find the radiobutton by returned id
                                    RadioButton rd = (RadioButton) getActivity().findViewById(selectedId);
                                    String chrg = rd.getText().toString();

                                    int startIndex = chrg.indexOf("(");
                                    int endIndex = chrg.indexOf(")");
                                    String id1 = chrg.substring(startIndex + 2, endIndex);



                                        String DlvryAdd;
                                        DlvryAdd = Address1 + " " + Address2 + " " + Region + " " + Surbub + " " + city + " " + Country;

                                        Log.e("Getting saved adress", "Adress: " + DlvryAdd);

                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString("DlvryChrg", id1);
                                        editor.putString("DlvryAdd", DlvryAdd);
                                        editor.putString("DlvryType", chrg);
                                        editor.commit();
                                        //Toast.makeText(getContext(), "Your Delivery Charge is: $" + id1, Toast.LENGTH_LONG).show();


                                    Toast ToastMessage = Toast.makeText(getActivity(),"Your Delivery Charge is: $" + id1,Toast.LENGTH_LONG);
                                    View toastView = ToastMessage.getView();
                                    toastView.setBackgroundResource(R.drawable.toast_background);
                                    ToastMessage.show();



                                        nexttab();

                                } else {
                                    //Toast.makeText(getActivity(), "Please Select a Delivery Service (Normal / Express / Extended)", Toast.LENGTH_LONG).show();

                                    Toast ToastMessage = Toast.makeText(getActivity(),"Please Select a Delivery Service (Normal / Express / Extended)",Toast.LENGTH_LONG);
                                    View toastView = ToastMessage.getView();
                                    toastView.setBackgroundResource(R.drawable.toast_background);
                                    ToastMessage.show();

                                    return;
                                }
                            }
                        } else if (!rdregadd.isChecked() && rdaltadd.isChecked()) {


                            diliverytype = "alternate";

                            if (txtaddline1.getText().toString().isEmpty()) {
                                //Toast.makeText(getActivity(), "Invalid Delivery Address Detected", Toast.LENGTH_LONG).show();

                                Toast ToastMessage = Toast.makeText(getActivity(),"Invalid Delivery Address Detected",Toast.LENGTH_LONG);
                                View toastView = ToastMessage.getView();
                                toastView.setBackgroundResource(R.drawable.toast_background);
                                ToastMessage.show();
                                return;
                            } else {
                                //nexttab();
                                ll01.setVisibility(View.GONE);

                                ll02.setVisibility(View.VISIBLE);


                                type = "DlvryChrg";
                               // value = "Parkview";
                                altsurbub = Surbub;


                                getDropDownValues(type, value);




                                if (rdg.getCheckedRadioButtonId() > -1) {
                                    int selectedId = rdg.getCheckedRadioButtonId();
// find the radiobutton by returned id
                                    RadioButton rd = (RadioButton) getActivity().findViewById(selectedId);
                                    String chrg = rd.getText().toString();

                                    int startIndex = chrg.indexOf("(");
                                    int endIndex = chrg.indexOf(")");
                                    String id1 = chrg.substring(startIndex + 2, endIndex);

                                    String DlvryAdd;
                                    DlvryAdd = txtaddline1.getText().toString() + "," + txtaddline2.getText().toString() +
                                            "," + Country + "," +
                                            Region + "," +
                                            Surbub;

                                    altsurbub = Surbub;
                                    SharedPreferences.Editor editor = shared.edit();
                                    editor.putString("DlvryChrg", id1);
                                    editor.putString("DlvryAdd", DlvryAdd);
                                    editor.putString("DlvryType", chrg);
                                    editor.commit();
                                    Toast.makeText(getContext(), "Your Delivery Charge is: $" + id1, Toast.LENGTH_LONG).show();
                                    nexttab();
                                } else {
                                    Toast.makeText(getContext(), "Select dilivery charge!", Toast.LENGTH_LONG).show();
                                    return;
                                }


                            }
                        }
                    }

                    if (rdpckup.isChecked()) {
                        if (spin.getSelectedItem().toString().isEmpty() || spin.getSelectedItem().toString().contains("Select")) {
                            //Toast.makeText(getActivity(), "Please Select a City/Town", Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(getActivity(),"Please Select a City/Town",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();
                            return;
                        }
                        //if (!rdg.isSelected()) {
                        String city = spin.getSelectedItem().toString();
                        if (city.contentEquals("Harare")) {
                            Spinner sItems = (Spinner) getActivity().findViewById(R.id.spin_pickuplocations);

                            if (sItems.getSelectedItem().toString() !=  null) {

                                chrg = sItems.getSelectedItem().toString();
                            }
                            if (!chrg.isEmpty() && chrg != null) {
                                int startIndex = chrg.indexOf("(");
                                int endIndex = chrg.indexOf(")");
                                String id1 = chrg.substring(startIndex + 2, endIndex);
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("DlvryChrg", id1);
                                editor.putString("DlvryType", "pickup");
                                editor.putString("DlvryAdd", chrg);
                                editor.commit();
                                Toast.makeText(getContext(), "Your Pickup Charge is: $" + id1, Toast.LENGTH_LONG).show();
                                nexttab();
                            }
                        } else {
                            //Toast.makeText(getActivity(), "No Pickup Locations in Selected City" + spin.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(getActivity(),"No Pickup Locations in Selected City",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();
                            return;
                        }
                        //}
                    }

                }
            });
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    final String newValue = (String) spin.getItemAtPosition(position);
                    //Toast.makeText(getContext(),"city selected" +newValue,Toast.LENGTH_LONG).show();
                    txtaddress.setText("");
                    txtaddress.setVisibility(View.GONE);

                    city = newValue;
                    if (isNetworkAvailable() == true) {
                        getPickUpLocations();
                    } else {
                        Toast.makeText(getContext(), "Please Check Your Network Connectivity", Toast.LENGTH_LONG).show();

                        Toast ToastMessage = Toast.makeText(getActivity(),"Please Check Your Network Connectivity",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });


            spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    final String newValue = (String) spin1.getItemAtPosition(position);
                    //Toast.makeText(getContext(),"city selected" +newValue,Toast.LENGTH_LONG).show();
                    txtaddress.setText(newValue);
                    txtaddress.setVisibility(View.VISIBLE);
                    txtaddress.setTextColor(getResources().getColor(R.color.colorAccent));

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            spcountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    final String newValue = (String) spin.getItemAtPosition(position);
                    //Toast.makeText(getContext(),"city selected" +newValue,Toast.LENGTH_LONG).show();
                    if (spcountry.getSelectedItemPosition() == 0) {
                        type = "Province";
                        value = "Zimbabwe";

                        if (isNetworkAvailable() == true) {

                            diliverytype = "N/A";
                            altsurbub = "N/A";
                            getDropDownValues(type, value);
                        } else {
                            //Toast.makeText(getContext(), "Please Check Your Network Connectivity", Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(getActivity(),"Please Check Your Network Connectivity",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();
                            //pDialog.dismiss();
                        }
                        txterr.setText("");
                        txterr.setVisibility(View.GONE);
                    } else {
                        txterr.setText("No Delivery Option available for the Selected Country");
                        txterr.setVisibility(View.VISIBLE);
                        spprovince.setAdapter(null);
                        spcity.setAdapter(null);
                        spsurburb.setAdapter(null);

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Info")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                })
                                .setNegativeButton("", null)
                                .setMessage(Html.fromHtml("No Delivery Option available for the Selected Country." ))
                                .show();

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            spprovince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    final String newValue = (String) spprovince.getItemAtPosition(position);
                    //Toast.makeText(getContext(),"city selected" +newValue,Toast.LENGTH_LONG).show();
                    if (spprovince.getSelectedItemPosition() < 2) {
                        type = "City";
                        value = newValue;
                        Region = newValue;

                        if (isNetworkAvailable() == true) {
                            diliverytype = "N/A";
                            altsurbub = "N/A";
                            getDropDownValues(type, value);
                        } else {
                           // Toast.makeText(getContext(), "Please Check Your Network Connectivity", Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(getActivity(),"Please Check Your Network Connectivity",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();
                        }
                        txterr.setText("");
                        txterr.setVisibility(View.GONE);
                    } else {
                        txterr.setText("No Delivery Option available for the Selected Province");
                        txterr.setVisibility(View.VISIBLE);
                        spcity.setAdapter(null);
                        spsurburb.setAdapter(null);

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Info")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                })
                                .setNegativeButton("", null)
                                .setMessage(Html.fromHtml("No Delivery Option available for the Selected Province." ))
                                .show();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            spcity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    final String newValue = (String) spcity.getItemAtPosition(position);
                    //Toast.makeText(getContext(),"city selected" +newValue,Toast.LENGTH_LONG).show();
                    type = "Surburb";
                    value = newValue;
                    city = newValue;

                    if (isNetworkAvailable() == true) {
                        diliverytype = "N/A";
                        altsurbub = "N/A";
                        getDropDownValues(type, value);
                    } else {
                        //Toast.makeText(getContext(), "Please Check Your Network Connectivity", Toast.LENGTH_LONG).show();

                        Toast ToastMessage = Toast.makeText(getActivity(),"Please Check Your Network Connectivity",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();
                    }
                    txterr.setText("");
                    txterr.setVisibility(View.GONE);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });


            spsurburb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    final String newValue = (String) spsurburb.getItemAtPosition(position);
                    //Toast.makeText(getContext(),"city selected" +newValue,Toast.LENGTH_LONG).show();
                    type = "DlvryChrg";
                    value = newValue;
                    Surbub = newValue;

                   /*
                    if (isNetworkAvailable() == true) {
                        new getDropDownValues().execute();
                    } else {
                        Toast.makeText(getContext(), "Please Check Your Network Connectivity", Toast.LENGTH_LONG).show();
                    }
                    */



                    txterr.setText("");
                    txterr.setVisibility(View.GONE);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        }

        //} catch (OutOfMemoryError ex) {
           // Log.e("Main Thread Exception", "Error: " + ex.toString());
           // System.gc();
       // }

        return view;

        }




    public void nexttab()
    {

        rdg.removeAllViews();
        TabLayout tabLayout=(TabLayout) getActivity().findViewById(R.id.tabs02);
        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager02);
        final PagerAdapter adapter = new PagerAdapter02 (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
        Log.e("Next 02", "Thus Far the lord has brought us");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity(). getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_submit:
                rdg.removeAllViews();

                TabLayout tabLayout=(TabLayout) getActivity().findViewById(R.id.tabs02);
                final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager02);
                final PagerAdapter adapter = new PagerAdapter02 (getFragmentManager(), tabLayout.getTabCount());
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(2);
                Log.e("Next 02", "Thus Far the lord has brought us");
                break;


            default:
                break;
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
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting Product Details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String pid = UserID;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("productid", pid));


                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json1 = jsonParser.makeHttpRequest(
                        GETPUSER_URL, "POST", params);
                Log.e("PID + URL",UserID + GETPUSER_URL);
                // check your log for json response
                Log.d("Login attempt", json1.toString());

                // json success tag
                success = json1.getInt(TAG_SUCCESS);
                if (success == 1) {
                    return json1.getString(TAG_PRODUCTDETAILS);
                } else {
                    return json1.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
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

                   // Toast.makeText(getActivity(), posts, Toast.LENGTH_LONG).show();
                }

            }

        }


    private void getPickUpLocations() {
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getActivity());


        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Getting Pick Up Locations..." );
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, GETPICKUPLOCATIONS_URL, new Response.Listener<String>() {
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


                        List<String> spinnerArray = new ArrayList<String>();


                        if (array.length() > 0 && array != null) {
                            try {
                                Log.e("JSONing", array.toString());

                                for (int i = 0; i < array.length() - 1; i++) {
                                    String name, chrg = "";
                                    JSONObject jsonobject = array.getJSONObject(i);
                                    name = Html.fromHtml(jsonobject.optString("shop")).toString();
                                    chrg = Html.fromHtml(jsonobject.optString("chrge")).toString();
                                    spinnerArray.add("($" + chrg + ".00)  " + name);
                                }
                            } catch (JSONException e) {
                                Log.e("PickUp Locations Error", e.toString());
                                e.printStackTrace();
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    getContext(), android.R.layout.simple_spinner_item, spinnerArray);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            Spinner sItems = (Spinner) getActivity().findViewById(R.id.spin_pickuplocations);
                            sItems.setAdapter(adapter);
                            sItems.setVisibility(View.VISIBLE);

                        } else {
                            Spinner sItems = (Spinner) getActivity().findViewById(R.id.spin_pickuplocations);
                            sItems.setAdapter(null);
                            sItems.setVisibility(View.GONE);
                            txtaddress.setText("No Pick Up Points Available in Your City/Town: " + city);
                            txtaddress.setTextColor(getResources().getColor(R.color.colorOrange));
                            //spin.setSelection(0);
                            txtaddress.setVisibility(View.VISIBLE);
                            //Toast.makeText(getContext(), "No Pick Up Points Available in Your City/Town: " + city, Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(getActivity(), "No Pick Up Points Available in Your City/Town: ", Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();
                            Log.d("Spinner Value ", city);
                        }

                    }

                }catch (JSONException e) {
                        e.printStackTrace();

                        //Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();

                    Log.e("PickUp Locations Error", e.toString());



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


                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> values = new HashMap();
                    values.put("city", city);

                    return values;
                }
            };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);


        }




    private void getCities( final String pid) {
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getActivity());


        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Getting Pick Up Cities..." );
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, GETCITIES_URL, new Response.Listener<String>() {
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


                        List<String> spinnerArray = new ArrayList<String>();


                        if (array.length() > 0) {
                            try {
                                Log.e("JSONing", array.toString());

                                for (int i = 0; i < array.length() - 1; i++) {
                                    String name, chrg = "";
                                    JSONObject jsonobject = array.getJSONObject(i);
                                    name = Html.fromHtml(jsonobject.optString("cty_name")).toString();
                                    //chrg = Html.fromHtml(jsonobject.optString("chrge")).toString();
                                    spinnerArray.add(name);
                                }
                            } catch (JSONException e) {
                                Log.e("PickUp Locations Error", e.toString());
                                e.printStackTrace();
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    getContext(), android.R.layout.simple_spinner_item, spinnerArray);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            Spinner sItems = (Spinner) getActivity().findViewById(R.id.spin_city);
                            sItems.setAdapter(adapter);
                            sItems.setVisibility(View.VISIBLE);

                        } else {


                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    //Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();

                    Log.e("PickUp Locations Error", e.toString());

                    Toast ToastMessage = Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();

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


                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> values = new HashMap();
                    values.put("", pid);

                    return values;
                }
            };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);



    }



    private void getDropDownValues(final String type, final String value) {


        try {
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


                    com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Getting " + type + "...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, GETSURBURBS_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            pDialog.dismiss();

                            Log.e("Type", "" + s);
                            Log.e("Type..>>>>>>", ">>>" + type);
                            Log.e("Value>>>>>", ">>>>>" + value);
                            //{"success":1,"message":"Username Successfully Added!"}

                            try {
                                JSONObject jsonobject = new JSONObject(s);
                                int success = jsonobject.getInt("success");
                                if (success == 1) {

                                    JSONArray array = jsonobject.getJSONArray("posts");


                                    List<String> spinnerArray = new ArrayList<String>();

                                    if (array != null) {
                                        try {


                                            for (int i = 0; i < array.length(); i++) {


                                                JSONObject object = array.getJSONObject(i);

                                                if (object.has("name")) {

                                                    String name = object.optString("name");
                                                    spinnerArray.add(name);
                                                }


                                            }
                                        } catch (JSONException e) {
                                            Log.e("Cities JSON Error: ", e.toString());
                                            e.printStackTrace();
                                        }


                                        if (type != "DlvryChrg") {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    getContext(), android.R.layout.simple_spinner_item, spinnerArray);

                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Spinner sItems;
                                            if (type == "Province")
                                                sItems = spprovince;
                                            else if (type == "City")
                                                sItems = spcity;
                                            else if (type == "Surburb")
                                                sItems = spsurburb;
                                            else
                                                sItems = null;
                                            if (sItems != null)
                                                sItems.setAdapter(adapter);
                                        } else {

                                            String Normal, Express, Extended = "";
                                            try {
                                                rdg.removeAllViews();


                                                //tell.setTextColor(getResources().getColor(R.color.colorDarkness));
                                                //tell.setTypeface(null, Typeface.BOLD);
                                                //tell.setTextSize(13);
                                                //tell.setVisibility(View.VISIBLE);

                                                //tell.setText("Select a Dilivery Service");
                                                JSONObject object = array.getJSONObject(0);

                                                Normal = object.optString("normal");
                                                rdNormal.setText("Normal ($" + Normal + ")");


                                                if (object.has("express")) {

                                                    Express = object.optString("express");
                                                    rdExpress.setText("Express ($" + Express + ")");
                                                }

                                                Extended = object.optString("extended");
                                                rdExtended.setText("Extended ($" + Extended + ")");

                                                //Log.e("Values gotten","Express: "+Express +"Extended: " + Extended);

                                                rdg.removeAllViews();
                                                //main.removeAllViews();
                                                ll02.removeAllViews();
                                                //rdg.addView(tell);
                                                rdg.addView(rdNormal);

                                                if (object.has("express")) {

                                                    rdg.addView(rdExpress);
                                                }
                                                rdg.addView(rdExtended);
                                                rdg.setOrientation(LinearLayout.VERTICAL);
                                                rdg.setVisibility(View.VISIBLE);
                                                ll02.addView(rdg);


                                                //rdg.removeAllViews();
                                                //ll02.removeAllViews();
                                                //rdg.addView(rdNormal);
                                                //rdg.addView(rdExpress);
                                                //rdg.addView(rdExtended);
                                                //rdNormal.setText("Normal ($5)");
                                                //rdExpress.setText("Express ($10)");
                                                //rdExtended.setText("Extended ($3)");
                                                //rdg.setOrientation(LinearLayout.HORIZONTAL);
                                                //rdg.setVisibility(View.VISIBLE);
                                                //ll02.addView(rdg);

                                            } catch (Exception e) {
                                                Log.e("Cities JSON Error: ", e.toString());
                                                e.printStackTrace();
                                            }


                                        }

                                    } else {
                                        //Toast.makeText(getContext(), ("An error occured please try again..."), Toast.LENGTH_SHORT).show();

                                        Toast ToastMessage = Toast.makeText(getActivity(), "An error occured please try again...", Toast.LENGTH_LONG);
                                        View toastView = ToastMessage.getView();
                                        toastView.setBackgroundResource(R.drawable.toast_background);
                                        ToastMessage.show();
                                    }


                                } else {

                                    if (type != "DlvryChrg") {


                                        // Toast.makeText(getContext(), ("Select another surbub..."), Toast.LENGTH_SHORT).show();

                                        //txterr.setVisibility(View.VISIBLE);
                                        //txterr.setText("Please kindly second another surbub as this one is out of our zone.");

                                        Log.e("Suceecc", ">>>>>>>>>>>" + s);

                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Info")
                                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                    }
                                                })
                                                .setNegativeButton("", null)
                                                .setMessage(Html.fromHtml("Please select another option as this one is out of our zone."))
                                                .show();
                                        return;

                                    }


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();

                                Log.e("Exception", ">>>>>>>>>>>" + e);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pDialog.dismiss();
                            volleyError.printStackTrace();
                            Log.e("RUEERROR", "" + volleyError);


                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> values = new HashMap();
                            values.put("cartitems", JsonArr.toString());
                            values.put("type", type);
                            values.put("value", value);
                            values.put("userid", (sharedpreferences.getString("userid", "")));
                            values.put("con_address", diliverytype);
                            values.put("surbub", altsurbub);
                            values.put("amt", (sharedpreferences.getString("total", "")));

                            Log.e("POSTING", "values" + type +value +  diliverytype + altsurbub);

                            return values;
                        }


                    };
                    requestQueue.add(stringRequest);


                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JSONING Error ", e.toString());

        }


    }

        }





