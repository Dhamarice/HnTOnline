package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Finances_Webview extends AppCompatActivity {

    WebView webView;
    public static final String MyPREFERENCES = "MyPrefs";
    String request_type, deposit = "deposit", withdraw = "withdraw", transfer_credits = "transfer_credits", redeem_points = "redeem_points", transactions = "transactions";

    SharedPreferences shared;
    ProgressDialog Pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Runtime.getRuntime().gc();
        super.onCreate(savedInstanceState);
               Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setContentView(R.layout.webview);

        shared = Finances_Webview.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

request_type = (shared.getString("request_type", ""));


        Log.e("OnClick ", "request_type: " +  shared.getString("request_type", "")  );

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

        if(request_type.contentEquals(deposit)) {

            webView.loadUrl("https://shopping.hammerandtongues.com/my-account/my-finances/?pg=deposit");

        }

       else if(request_type.contentEquals(transfer_credits)) {

            webView.loadUrl("https://shopping.hammerandtongues.com/my-account/my-finances/?pg=transfer");

        }

        else if(request_type.contentEquals(withdraw)) {

            webView.loadUrl("https://shopping.hammerandtongues.com/my-account/my-finances/?pg=withdraw");

        }

        else if(request_type.contentEquals(redeem_points)) {

            webView.loadUrl("https://shopping.hammerandtongues.com/my-account/my-finances/?pg=redeem");

        }

        else if(request_type.contentEquals(transactions)) {

            webView.loadUrl("https://shopping.hammerandtongues.com/my-account/my-finances/?pg=transactions");

        }

        webView.setWebViewClient(new WebViewClient(){

            public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
                webView.loadUrl("file:///android_asset/error.html");

            }







        });











    }



    public class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

            Pdialog = new ProgressDialog(Finances_Webview.this);
            Pdialog.setTitle("REDIRECTING");
            Pdialog.setMessage("Please wait...");
            Pdialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub

            super.onPageFinished(view, url);
            Pdialog.dismiss();

        }

    }

}