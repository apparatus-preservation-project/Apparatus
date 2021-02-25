package com.bithack.apparatus;

import android.os.AsyncTask;
import com.bithack.apparatus.PublishDialog;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.client.methods.HttpGet;

public class Community {

	protected class FetchTask extends AsyncTask<String, String, String> {
		protected FetchTask() {
		}

		/* access modifiers changed from: protected */
		public String doInBackground(String... params) {
			try {
				InputStream in = PublishDialog.HttpUtils.getNewHttpClient().execute(new HttpGet("http://apparatus-web.tk/internal/fetch.php?t=" + params[0] + "&m=" + params[1] + "&s=" + params[2])).getEntity().getContent();
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
		}

		/* access modifiers changed from: protected */
		public void onPostExecute(String result) {
			result.trim();
		}
	}
}
