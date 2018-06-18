package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Chat_Webview extends AppCompatActivity {

    WebView webView;
    ProgressDialog Pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat__webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Runtime.getRuntime().gc();
        setContentView(R.layout.webview);



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

        webView.loadUrl("https://tawk.to/hntshoppingmobile" );

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

            Pdialog = new ProgressDialog(Chat_Webview.this);
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
