package com.hammerandtongues.online.hntonline;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.fragment;

/**
 * Created by Ruvimbo on 25/10/2017.
 */

public class Login2 extends Fragment {

    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //private static final String LOGIN_URL = "https://hammerandtongues.com/webservice/login.php";
    //private static final String EWALL_URL = "https://hammerandtongues.com/webservice/getewalletbal.php";
    private static final String LOGIN_URL = "http://http://10.0.2.2:8012:8012/webservice/registerlogin.php";
    private static final String EWALL_URL = "http://http://10.0.2.2:8012:8012/webservice/getewalletbal.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_CATEGORIES = "posts";
    int success = 0;

    EditText username, password;
    String uname, pword;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.login, container, false);
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);



            username = (EditText) view.findViewById(R.id.username);
            password = (EditText) view.findViewById(R.id.password);
            Button signin = (Button) view.findViewById(R.id.btnlogin);
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    uname = username.getText().toString();
                    pword = password.getText().toString();
                    // new AttemptLogin().execute();

                }

            }

            );

        return view;
    }


    private void saveUser(final String username,final String password) {
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success", "" + s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int success = jsonObject.getInt("success");
                    if (success == 1) {
                        Toast.makeText(getContext(), (s), Toast.LENGTH_SHORT).show();
                    } else {
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
                volleyError.printStackTrace();
                Log.e("RUEERROR", "" + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> values = new HashMap();
                values.put("username", username);
                values.put("password", password);


                return values;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }
    }