package com.reskiniggroup.customadview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowMyOwnAd {
    public static void ShowMyInter(final Activity activity, String url_dt, final int close_time) {
        final Handler handler = new Handler();
        RequestQueue mQueue;
        final Dialog mOpenWhatsDialog = new Dialog(activity, R.style.CustomAdStyle);
        mOpenWhatsDialog.setContentView(R.layout.custum_ad_ui);
        mOpenWhatsDialog.setCancelable(false);
        final ImageView close = mOpenWhatsDialog.findViewById(R.id.close_id);
        final ImageView privacypolicy = mOpenWhatsDialog.findViewById(R.id.privacy_id);
        final CardView install = mOpenWhatsDialog.findViewById(R.id.cardView);
        final TextView close_txt = mOpenWhatsDialog.findViewById(R.id.closeText);
        final TextView start_txt = mOpenWhatsDialog.findViewById(R.id.textView3);
        final ImageView ad_image_screen = mOpenWhatsDialog.findViewById(R.id.ad_image);
        final ImageView ad_image_icon = mOpenWhatsDialog.findViewById(R.id.imageView4);
        final TextView v_app_title = mOpenWhatsDialog.findViewById(R.id.app_title);
        mQueue = Volley.newRequestQueue(activity);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url_dt, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject ad_info = response.getJSONObject("ad_info");
                            final String app_package_name = ad_info.getString("app_package_name");
                            String url_image_screen = ad_info.getString("url_image_screen");
                            String url_image_icon = ad_info.getString("url_image_icon");
                            String app_title = ad_info.getString("app_title");
                            String count_stars = ad_info.getString("count_stars");
                            final String privacy_policy_txt = ad_info.getString("privacy_policy_txt");
                            final String url_privacy_policy = ad_info.getString("url_privacy_policy");
                            start_txt.setText(count_stars);
                            v_app_title.setText(app_title);
                            Picasso.get().load(url_image_screen).into(ad_image_screen);
                            Picasso.get().load(url_image_icon).into(ad_image_icon);
                            install.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("market://details?id=" + app_package_name);
                                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    try {
                                        activity.startActivity(goToMarket);
                                    } catch (ActivityNotFoundException e) {
                                        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://play.google.com/store/apps/details?id=" + app_package_name)));
                                    }
                                }
                            });
                            privacypolicy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder;
                                    builder = new AlertDialog.Builder(activity);
                                    builder.setTitle("Privacy Setting")
                                            .setMessage(privacy_policy_txt)
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with delete
                                                }
                                            })
                                            .setNegativeButton("Privacy Policy", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // do nothing
                                                    AlertDialog.Builder builderpr;
                                                    builderpr = new AlertDialog.Builder(activity);
                                                    builderpr.setCancelable(true);
                                                    WebView privacyshow = new WebView(activity);
                                                    privacyshow.getSettings().setJavaScriptEnabled(true);
                                                    privacyshow.setWebViewClient(new WebViewClient());
                                                    privacyshow.loadUrl(url_privacy_policy);
                                                    builderpr.setView(privacyshow);
                                                    AlertDialog alertDialog = builderpr.create();
                                                    alertDialog.show();
                                                }
                                            })
                                            .show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
        mQueue.getCache().clear();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenWhatsDialog.dismiss();
            }
        });
        new Thread(new Runnable() {
            int close_after = close_time;

            public void run() {
                while (close_after > 0) {
                    close_after--;
                    handler.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        public void run() {
                            close_txt.setText("close after " + String.valueOf(close_after) + "s");
                            if (close_after == 0) {
                                close.setVisibility(View.VISIBLE);
                                close_txt.setVisibility(View.GONE);
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        mOpenWhatsDialog.show();
    }
}
