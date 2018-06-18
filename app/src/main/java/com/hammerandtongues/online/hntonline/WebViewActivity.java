package com.hammerandtongues.online.hntonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

/**
 * Created by NgonidzaIshe on 20 Feb,2017.
 */
public final class WebViewActivity extends Activity {
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject json=null;
    int success;
    String OrderID, DlvryChrg;
    SharedPreferences shared;
    public static final String MyPREFERENCES = "MyPrefs";
    private int currcart;
    private  double total;
    private  String totalPrc, uid, amount, type;


    private static final String TAG_ID = "id";
    private static final String TAG_BALANCE = "balance";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PRODUCTDETAILS = "posts";
    public static final String Product = "idKey";
    DatabaseHelper dbHandler;

    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Runtime.getRuntime().gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        shared = this. getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (shared.getString("CartID", "") != null && shared.getString("CartID", "") !="") {

            currcart = Integer.parseInt(shared.getString("CartID", ""));

        }


        else currcart = 0;

            OrderID=  shared.getString("OrderID", "");
        DlvryChrg = (shared.getString("DlvryChrg", ""));
            uid = (shared.getString("userid", ""));

            Log.e("In Webview", "webview orderid inside cartid condition" + OrderID);

        amount = shared.getString("theamount", "");
        type = shared.getString("type", "");



//Get a reference to your WebView//
        webView = (WebView) findViewById(R.id.webView1);
        WebSettings webSettings = webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);

        //webView.getSettings().setAppCacheMaxSize( 10 * 1024 * 1024 ); // 5MB
        //webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        //webView.getSettings().setAllowFileAccess( true );
        //webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setJavaScriptEnabled( true );
        WebSettings settings = webView.getSettings();
        settings.setSupportMultipleWindows(true);
        //webView.getSettings().setBuiltInZoomControls(true);
       // webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //CookieManager.getInstance().setAcceptCookie(true);


//Specify the URL you want to display//

        if (type.contentEquals("deposit")){

            webView.loadUrl("https://shopping.hammerandtongues.com/wp-content/themes/Walleto/deposit_paynow_mobile.php?pn_action=createtransaction&am=" + amount + "&user_id=" + uid);

            shared.edit().remove("type").apply();


            SharedPreferences.Editor editor = shared.edit();
            editor.putString("ptype", "DepositPaynow");
            editor.apply();

        }

        else {
            Log.e("In Webview", "webview orderid" + OrderID + "&delivery_charge=" + DlvryChrg);
            webView.loadUrl("https://shopping.hammerandtongues.com/wp-content/themes/Walleto/paynowapi_mobile.php?action=createtransaction&order_id=" + OrderID + "&delivery_charge=" + DlvryChrg);
        }

        webView.setWebViewClient(new WebViewClient(){

            public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
                webView.loadUrl("file:///android_asset/error.html");

            }

            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {

                url = webView.getUrl();
                if (url.contains("action=return&hsh="))
                {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("ptype", "paynow");
                    editor.apply();

                    Intent intent = new Intent(WebViewActivity.this, TransactionHistory.class);
                    startActivity(intent);
                }
                return false;
            }





        });
    }
    }


