package com.choicehotels.vpix;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class VirtualTourActivity extends AppCompatActivity {

    private static final String ERROR_VIRTUAL_TOUR = "Virtual Tour has not started loading within 5 seconds. Trying to load Google instead.";
    private static final String ERROR_GOOGLE = "Google has not started loading within 2 seconds. Finishing.";

    Uri uri;
    WebView web;
    ProgressBar progress;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri = getIntent().getData();
        if (uri == null) {
            finish();
            return;
        }
        web = new WebView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                handler.removeMessages(AlertHandler.SHOW_ALERT);
                super.onPageStarted(view, url, favicon);
                Log.d("VPIX", "onPageStarted: " + url);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("VPIX", "onPageFinished" + url);
                progress.setVisibility(View.GONE);
            }
        });
        web.setWebChromeClient(new WebChromeClient());
        web.getSettings().setJavaScriptEnabled(true);
        setContentView(web);
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(uri.toString());
                handler.sendMessageDelayed(handler
                    .obtainMessage(AlertHandler.SHOW_ALERT, ERROR_VIRTUAL_TOUR),
                    5000
                );
            }
        });
        progress = new ProgressBar(this);
        addContentView(progress,
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ){{ gravity = Gravity.CENTER; }}
        );
        progress.setVisibility(View.GONE);
        handler = new AlertHandler(this);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (web != null) {
            ViewGroup parent = (ViewGroup) web.getParent();
            if (parent != null) {
                parent.removeView(web);
            }
            web.stopLoading();
            web.destroy();
            web = null;
        }
    }

    private static class AlertHandler extends Handler {

        public static final int SHOW_ALERT = 1;

        WeakReference<VirtualTourActivity> activity;

        public AlertHandler(VirtualTourActivity activity) {
            this.activity = new WeakReference<VirtualTourActivity>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final VirtualTourActivity activity = this.activity.get();
            if (activity != null) {
                final String message = (String) msg.obj;
                new AlertDialog.Builder(activity)
                    .setTitle("Error!")
                    .setMessage(String.valueOf(msg.obj))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ERROR_VIRTUAL_TOUR.equals(message)) {
                                activity.web.loadUrl("https://www.google.com");
                                sendMessageDelayed(obtainMessage(SHOW_ALERT, ERROR_GOOGLE), 2000);
                            } else {
                                activity.finish();
                            }
                        }
                    })
                    .create()
                    .show();
            }
        }
    }

}
