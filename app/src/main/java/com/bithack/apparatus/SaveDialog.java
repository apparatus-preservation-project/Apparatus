package com.bithack.apparatus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class SaveDialog {
	ApparatusApplication app;
	Dialog dialog;
	final EditText textfield;

	public SaveDialog(final ApparatusApplication app2) {
		this.app = app2;
		AlertDialog.Builder builder = new AlertDialog.Builder(app2);
		View view = LayoutInflater.from(app2).inflate(2130903067, (ViewGroup) null);
		builder.setTitle(L.get("save_level"));
		builder.setView(view);
		this.textfield = (EditText) view.findViewById(2130968644);
		builder.setPositiveButton(L.get("save"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.SaveDialog.AnonymousClass1 */

			public void onClick(DialogInterface d, int which) {
				final String filename = SaveDialog.this.textfield.getText().toString();
				if (filename.length() <= 1) {
					Toast.makeText(app2, L.get("filename_too_short"), 0).show();
					ApparatusApplication apparatusApplication = app2;
					final ApparatusApplication apparatusApplication2 = app2;
					apparatusApplication.run_on_gl_thread(new Runnable() {
						/* class com.bithack.apparatus.SaveDialog.AnonymousClass1.AnonymousClass1 */

						public void run() {
							try {
								Thread.sleep(100);
							} catch (Exception e) {
							}
							apparatusApplication2.runOnUiThread(new Runnable() {
								/* class com.bithack.apparatus.SaveDialog.AnonymousClass1.AnonymousClass1.AnonymousClass1 */

								public void run() {
									ApparatusApp.backend.open_save_dialog();
								}
							});
						}
					});
					return;
				}
				app2.run_on_gl_thread(new Runnable() {
					/* class com.bithack.apparatus.SaveDialog.AnonymousClass1.AnonymousClass2 */

					public void run() {
						ApparatusApp.game.level_filename = filename;
						ApparatusApp.game.save();
						Settings.msg("Level saved!");
					}
				});
			}
		});
		builder.setNegativeButton(L.get("cancel"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.SaveDialog.AnonymousClass2 */

			public void onClick(DialogInterface dialog, int which) {
			}
		});
		this.dialog = builder.create();
	}

	public void prepare() {
		if (ApparatusApp.game.level_filename != null) {
			this.textfield.setText(ApparatusApp.game.level_filename);
		} else {
			this.textfield.setText("");
		}
	}

	public Dialog get_dialog() {
		return this.dialog;
	}
}
