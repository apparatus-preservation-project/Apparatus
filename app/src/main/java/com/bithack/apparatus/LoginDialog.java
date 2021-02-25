package com.bithack.apparatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bithack.apparatus.PublishDialog;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.HttpGet;

public class LoginDialog {
	final Activity activity;
	Dialog dialog;

	public LoginDialog(Activity app) {
		this.activity = app;
		AlertDialog.Builder builder = new AlertDialog.Builder(app);
		final View view = LayoutInflater.from(app).inflate(2130903062, (ViewGroup) null);
		((TextView) view.findViewById(2130968634)).setOnClickListener(new View.OnClickListener() {
			/* class com.bithack.apparatus.LoginDialog.AnonymousClass1 */

			public void onClick(View v) {
				if (LoginDialog.this.activity instanceof CommunityActivity) {
					LoginDialog.this.activity.dismissDialog(1);
					LoginDialog.this.activity.showDialog(0);
					return;
				}
				LoginDialog.this.activity.dismissDialog(11);
				LoginDialog.this.activity.showDialog(12);
			}
		});
		builder.setTitle(L.get("login"));
		builder.setView(view);
		builder.setNeutralButton(L.get("login"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.LoginDialog.AnonymousClass2 */

			public void onClick(DialogInterface dialog, int which) {
				new LoginTask().execute(((TextView) view.findViewById(2130968600)).getText().toString(), ((TextView) view.findViewById(2130968633)).getText().toString());
			}
		});
		builder.setNegativeButton(L.get("cancel"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.LoginDialog.AnonymousClass3 */

			public void onClick(DialogInterface dialog, int which) {
			}
		});
		this.dialog = builder.create();
	}

	public Dialog get_dialog() {
		return this.dialog;
	}

	protected class LoginTask extends AsyncTask<String, String, String> {
		protected LoginTask() {
		}

		/* access modifiers changed from: protected */
		public String doInBackground(String... params) {
			try {
				InputStream in = PublishDialog.HttpUtils.getNewHttpClient().execute(new HttpGet("http://apparatus-web.tk/internal/login2.php?u=" + params[0] + "&p=" + params[1])).getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				while (true) {
					String data = reader.readLine();
					if (data == null) {
						in.close();
						return str.toString();
					}
					str.append(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}

		/* access modifiers changed from: protected */
		public void onPreExecute() {
			if (LoginDialog.this.activity instanceof CommunityActivity) {
				LoginDialog.this.activity.showDialog(2);
			} else {
				LoginDialog.this.activity.showDialog(9);
			}
		}

		/* access modifiers changed from: protected */
		public void onPostExecute(String result) {
			String result2 = result.trim();
			boolean error = false;
			if (result2.length() <= 0 || !result2.substring(0, 3).equals("OK:")) {
				error = true;
			} else {
				String token = result2.substring(3).trim();
				if (token.length() != 40) {
					error = true;
					result2 = "Temporary server error.";
				} else {
					Settings.set("community-token", token);
					Settings.save();
					if (LoginDialog.this.activity instanceof CommunityActivity) {
						Map<String, String> headers = new HashMap<>();
						headers.put("Referer", ((CommunityActivity) LoginDialog.this.activity).webview.getUrl());
						((CommunityActivity) LoginDialog.this.activity).webview.loadUrl("http://apparatus-web.tk/internal/tokenlogin.php?t=" + token, headers);
					} else {
						LoginDialog.this.activity.showDialog(2);
					}
				}
			}
			if (LoginDialog.this.activity instanceof CommunityActivity) {
				LoginDialog.this.activity.dismissDialog(2);
			} else {
				LoginDialog.this.activity.dismissDialog(9);
			}
			if (error) {
				Toast.makeText(LoginDialog.this.activity, result2, 0).show();
				if (LoginDialog.this.activity instanceof CommunityActivity) {
					LoginDialog.this.activity.showDialog(1);
				} else {
					LoginDialog.this.activity.showDialog(11);
				}
			}
		}
	}
}
