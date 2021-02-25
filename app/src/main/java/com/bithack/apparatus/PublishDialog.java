package com.bithack.apparatus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class PublishDialog {
	final ApparatusApplication activity;
	Dialog dialog;
	final View view;

	public PublishDialog(ApparatusApplication app) {
		this.activity = app;
		AlertDialog.Builder builder = new AlertDialog.Builder(app);
		this.view = LayoutInflater.from(app).inflate(2130903065, (ViewGroup) null);
		builder.setTitle(L.get("publish_community_level"));
		builder.setView(this.view);
		builder.setNeutralButton(L.get("publish"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.PublishDialog.AnonymousClass1 */

			public void onClick(DialogInterface dialog, int which) {
				new PublishTask().execute(((TextView) PublishDialog.this.view.findViewById(2130968603)).getText().toString(), ((TextView) PublishDialog.this.view.findViewById(2130968610)).getText().toString(), ((TextView) PublishDialog.this.view.findViewById(2130968640)).getText().toString());
			}
		});
		builder.setNegativeButton(L.get("cancel"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.PublishDialog.AnonymousClass2 */

			public void onClick(DialogInterface dialog, int which) {
			}
		});
		this.dialog = builder.create();
	}

	public void prepare() {
		EditText name = (EditText) this.view.findViewById(2130968603);
		EditText descr = (EditText) this.view.findViewById(2130968610);
		EditText tags = (EditText) this.view.findViewById(2130968640);
		name.setText("");
		descr.setText("");
		tags.setText("");
		Game game = ApparatusApp.game;
		if (!game.level.name.equals("")) {
			name.setText(game.level.name);
			descr.setText(game.level.description);
			tags.setText(game.level.tags);
		} else if (ApparatusApp.game.level_filename != null) {
			name.setText(ApparatusApp.game.level_filename);
		}
		if (!game.level.community_id.equals("")) {
			this.dialog.setTitle(L.get("publish_update"));
		} else {
			this.dialog.setTitle(L.get("publish_community_level"));
		}
	}

	public Dialog get_dialog() {
		return this.dialog;
	}

	public static class HttpUtils {
		public static HttpClient getNewHttpClient() {
			try {
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				SSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, "UTF-8");
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				registry.register(new Scheme("https", sf, 443));
				return new DefaultHttpClient(new ThreadSafeClientConnManager(params, registry), params);
			} catch (Exception e) {
				e.printStackTrace();
				return new DefaultHttpClient();
			}
		}
	}

	public static class EasySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public EasySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new X509TrustManager() {
				/* class com.bithack.apparatus.PublishDialog.EasySSLSocketFactory.AnonymousClass1 */

				@Override // javax.net.ssl.X509TrustManager
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override // javax.net.ssl.X509TrustManager
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			this.sslContext.init(null, new TrustManager[]{tm}, null);
		}

		@Override // org.apache.http.conn.scheme.LayeredSocketFactory, org.apache.http.conn.ssl.SSLSocketFactory
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override // org.apache.http.conn.scheme.SocketFactory, org.apache.http.conn.ssl.SSLSocketFactory
		public Socket createSocket() throws IOException {
			return this.sslContext.getSocketFactory().createSocket();
		}
	}

	protected class PublishTask extends AsyncTask<String, String, String> {
		public byte[] level_data = null;
		public int status = 0;

		protected PublishTask() {
		}

		/* access modifiers changed from: protected */
		/* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
			java.lang.Thread.sleep(200);
		 */
		public String doInBackground(final String... params) {
			PublishDialog.this.activity.run_on_gl_thread(new Runnable() {
				/* class com.bithack.apparatus.PublishDialog.PublishTask.AnonymousClass1 */

				public void run() {
					int _s;
					int read;
					Game game = ApparatusApp.game;
					game.prepare_save();
					game.level.name = params[0];
					game.level.description = params[1];
					game.level.tags = params[2];
					File f = Settings.get_tmp_file();
					byte[] data = null;
					try {
						game.level.save_jar(f);
						InputStream i = new FileInputStream(f);
						long len = f.length();
						if (len > 2147483647L) {
							throw new Exception();
						}
						data = new byte[((int) len)];
						int offs = 0;
						while (offs < data.length && (read = i.read(data, offs, data.length - offs)) >= 0) {
							offs += read;
						}
						i.close();
						f.delete();
						game.save();
						_s = 1;
						synchronized (this) {
							if (_s != 1 || data == null) {
								this.status = 2;
							} else {
								this.level_data = data;
								this.status = 1;
							}
						}
					} catch (Exception e) {
						_s = 2;
					}
				}
			});
			int x = 0;
			while (true) {
				if (x >= 1000) {
					break;
				}
				synchronized (this) {
					if (this.status != 0) {
						Gdx.app.log("breaking out", "ksda");
					}
				}
				x++;
			}
			if (this.status != 1) {
				return "Interrupted";
			}
			HttpClient client = HttpUtils.getNewHttpClient();
			HttpPost req = new HttpPost("http://apparatus-web.tk/internal/upload.php?t=" + Settings.get("community-token"));
			ByteArrayEntity ent = new ByteArrayEntity(this.level_data);
			ent.setContentType("binary/octet-stream");
			req.setEntity(ent);
			try {
				InputStream in = client.execute(req).getEntity().getContent();
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
			PublishDialog.this.activity.dismissDialog(2);
			PublishDialog.this.activity.showDialog(3);
		}

		/* access modifiers changed from: protected */
		public void onPostExecute(String result) {
			String result2 = result.trim();
			boolean error = false;
			if (result2.length() <= 0 || !result2.substring(0, 3).equals("OK:")) {
				error = true;
			} else {
				final String token = result2.substring(3).trim();
				PublishDialog.this.activity.run_on_gl_thread(new Runnable() {
					/* class com.bithack.apparatus.PublishDialog.PublishTask.AnonymousClass2 */

					public void run() {
						ApparatusApp.game.level.community_id = token;
						ApparatusApp.game.save();
					}
				});
				Toast.makeText(PublishDialog.this.activity, L.get("published_successfully"), 0).show();
			}
			PublishDialog.this.activity.dismissDialog(3);
			if (!error) {
				return;
			}
			if (result2.equals("UNAUTH")) {
				Toast.makeText(PublishDialog.this.activity, L.get("log_in_to_publish"), 0).show();
				PublishDialog.this.activity.showDialog(11);
			} else if (result2.equals("INVALID3")) {
				Toast.makeText(PublishDialog.this.activity, L.get("name_required"), 0).show();
				PublishDialog.this.activity.showDialog(2);
			} else {
				Toast.makeText(PublishDialog.this.activity, String.valueOf(L.get("server error")) + "(" + result2 + ")", 0).show();
				PublishDialog.this.activity.showDialog(2);
			}
		}
	}
}
