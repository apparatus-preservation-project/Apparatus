package com.bithack.apparatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.bithack.apparatus.ApparatusApplication;

public class CommunityActivity extends Activity {
	public static final int LOADING_DIALOG = 2;
	public static final int MENU = 4;
	public static final int PAGELOADING_DIALOG = 3;
	public static final int REGISTERING_DIALOG = 5;
	Bundle bundle;
	public WebView webview;
	/*

	public void onResume() {
		super.onResume();
		Gdx.app.log("RESUMEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE", "DSAAAAAAAAAAAAAAAAAA");
		this.webview.restoreState(this.bundle);
	}

	public void onCreate(Bundle b) {
		Gdx.app.log("CREATEEEEEEEEEEEEEEEEE", "DSAJKJKLKKKKKKKKKKKKKKKKKKKK");
		super.onCreate(b);
		this.bundle = b;
		requestWindowFeature(1);
		setContentView(2130903063);
		this.webview = (WebView) findViewById(2130968635);
		this.webview.setScrollBarStyle(0);
		this.webview.setBackgroundColor(17170446);
		WebSettings settings = this.webview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		this.webview.setWebViewClient(new WebViewClient() {

			@Override // android.webkit.WebViewClient
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Uri uri = Uri.parse(url);
				if (url.substring(0, 12).equals("apparatus://")) {
					if (url.equals("apparatus://register")) {
						ApparatusApplication.showDialog(0);
					} else if (url.equals("apparatus://login")) {
						this.showDialog(1);
					} else {
						int id = Integer.parseInt(url.substring(12), 10);
						Intent i = new Intent(CommunityActivity.this, ApparatusApplication.class);
						i.putExtra("id", id);
						CommunityActivity.this.webview.saveState(CommunityActivity.this.bundle);
						Settings.set("c_url", CommunityActivity.this.webview.getUrl());
						this.startActivity(i);
						this.finish();
					}
				} else if (uri.getHost().equals("apparatus-web.tk")) {
					view.loadUrl(url);
				} else {
					CommunityActivity.this.startActivity(new Intent("android.intent.action.VIEW", uri));
				}
				return true;
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				this.showDialog(3);
			}

			public void onPageFinished(WebView view, String url) {
				try {
					this.dismissDialog(3);
				} catch (Exception e) {
				}
			}

			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				try {
					this.dismissDialog(3);
				} catch (Exception e) {
				}
				Toast.makeText(this, L.get("error_connecting_to_community_server"), 1).show();
				CommunityActivity.this.webview.loadData("", "text/html", "utf8");
				System.gc();
				this.finish();
			}
		});
		String url = Settings.get("c_url");
		if (url == null || url.equals("")) {
			String token = Settings.get("community-token");
			if (token == null || token.length() != 40) {
				this.webview.loadUrl("http://apparatus-web.tk/");
			} else {
				this.webview.loadUrl("http://apparatus-web.tk/internal/tokenlogin.php?t=" + token);
			}
		} else {
			this.webview.loadUrl(url);
		}
	}

	public void onPause() {
		super.onPause();
		Settings.set("c_url", this.webview.getUrl());
		this.webview.saveState(this.bundle);
	}

	public Dialog onCreateDialog(int id) {
		Dialog ret = null;
		switch (id) {
			case 0:
				ret = new RegisterDialog(this).get_dialog();
				break;
			case 1:
				ret = new LoginDialog(this).get_dialog();
				break;
			case 2:
				ret = ProgressDialog.show(this, "", L.get("signingin"), true, false);
				break;
			case 3:
				ret = ProgressDialog.show(this, "", L.get("loading"), true, false);
				break;
			case 4:
				AlertDialog.Builder bld = new AlertDialog.Builder(this);
				CharSequence[] sbitems = {L.get("mainmenu"), L.get("quit")};
				bld.setTitle(L.get("menu"));
				bld.setItems(sbitems, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								ApparatusApp.instance.open_mainmenu();
								ApparatusApp.instance.fade = 0.0f;
								Settings.set("c_url", CommunityActivity.this.webview.getUrl());
								this.finish();
								return;
							case 1:
								Settings.save();
								CommunityActivity.this.webview.loadData("", "text/html", "utf8");
								CommunityActivity.this.webview.clearCache(false);
								CommunityActivity.this.webview.clearHistory();
								CommunityActivity.this.webview.clearView();
								Settings.set("c_url", CommunityActivity.this.webview.getUrl());
								this.moveTaskToBack(true);
								return;
							default:
								return;
						}
					}
				});
				ret = bld.create();
				break;
			case 5:
				ret = ProgressDialog.show(this, "", L.get("registeringaccount"), true, false);
				break;
		}
		if (ret == null) {
			return null;
		}
		ret.getWindow().setFlags(1024, 1024);
		return ret;
	}

	public boolean onKeyDown(int code, KeyEvent ev) {
		if (code == 4) {
			if (this.webview.canGoBack()) {
				this.webview.goBack();
				return true;
			}
			ApparatusApp.instance.open_mainmenu();
			ApparatusApp.instance.fade = 0.0f;
			System.gc();
			Settings.set("c_url", this.webview.getUrl());
			finish();
			return true;
		} else if (code != 82) {
			return false;
		} else {
			showDialog(4);
			return true;
		}
	}
	*/
}
