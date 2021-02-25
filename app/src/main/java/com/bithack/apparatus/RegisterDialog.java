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
import java.net.URLEncoder;
import org.apache.http.client.methods.HttpGet;

public class RegisterDialog {
	final Activity activity;
	Dialog dialog;

	public RegisterDialog(Activity app) {
		this.activity = app;
		AlertDialog.Builder builder = new AlertDialog.Builder(app);
		final View view = LayoutInflater.from(app).inflate(2130903066, (ViewGroup) null);
		builder.setTitle(L.get("register_account"));
		builder.setView(view);
		builder.setNeutralButton(L.get("register"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.RegisterDialog.AnonymousClass1 */

			public void onClick(DialogInterface dialog, int which) {
				new RegisterTask().execute(((TextView) view.findViewById(2130968600)).getText().toString(), ((TextView) view.findViewById(2130968633)).getText().toString(), ((TextView) view.findViewById(2130968643)).getText().toString(), ((TextView) view.findViewById(2130968642)).getText().toString());
			}
		});
		builder.setNegativeButton(L.get("cancel"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.RegisterDialog.AnonymousClass2 */

			public void onClick(DialogInterface dialog, int which) {
			}
		});
		this.dialog = builder.create();
	}

	public Dialog get_dialog() {
		return this.dialog;
	}

	protected class RegisterTask extends AsyncTask<String, String, String> {
		protected RegisterTask() {
		}

		/* access modifiers changed from: protected */
		public String doInBackground(String... params) {
			try {
				InputStream in = PublishDialog.HttpUtils.getNewHttpClient().execute(new HttpGet("http://apparatus-web.tk/internal/register.php?u=" + URLEncoder.encode(params[0]) + "&p=" + URLEncoder.encode(params[1]) + "&pt=" + URLEncoder.encode(params[2]) + "&e=" + URLEncoder.encode(params[3]))).getEntity().getContent();
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
			try {
				if (RegisterDialog.this.activity instanceof CommunityActivity) {
					RegisterDialog.this.activity.showDialog(5);
				} else {
					RegisterDialog.this.activity.showDialog(13);
				}
			} catch (Exception e) {
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
					result2 = L.get("temporary_server_error");
				} else {
					Settings.set("community-token", token);
					Settings.save();
					Toast.makeText(RegisterDialog.this.activity, L.get("registered_and_logged_in"), 0).show();
					try {
						if (RegisterDialog.this.activity instanceof CommunityActivity) {
							((CommunityActivity) RegisterDialog.this.activity).webview.loadUrl("http://apparatus-web.tk/internal/tokenlogin.php?t=" + token);
						} else {
							RegisterDialog.this.activity.showDialog(2);
						}
					} catch (Exception e) {
					}
				}
			}
			try {
				if (RegisterDialog.this.activity instanceof CommunityActivity) {
					RegisterDialog.this.activity.dismissDialog(5);
				} else {
					RegisterDialog.this.activity.dismissDialog(13);
				}
			} catch (Exception e2) {
			}
			if (error) {
				Toast.makeText(RegisterDialog.this.activity, result2, 0).show();
				try {
					if (RegisterDialog.this.activity instanceof CommunityActivity) {
						RegisterDialog.this.activity.showDialog(0);
					} else {
						RegisterDialog.this.activity.showDialog(12);
					}
				} catch (Exception e3) {
				}
			}
		}
	}
}
