package com.bithack.apparatus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import com.bithack.apparatus.graphics.MiscRenderer;

public class GraphicsDialog {
	final ApparatusApplication activity;
	final CheckBox bg_cb;
	final CheckBox bloom_cb;
	Dialog dialog;
	final CheckBox hqmeshes_cb;
	final CheckBox reflection_cb;
	int rope_quality = 0;
	final SeekBar sb;
	final CheckBox sf;
	final CheckBox sh;
	final View view;

	public GraphicsDialog(ApparatusApplication app) {
		this.activity = app;
		AlertDialog.Builder builder = new AlertDialog.Builder(app);
		this.view = LayoutInflater.from(app).inflate(2130903059, (ViewGroup) null);
		builder.setTitle("Graphics Settings");
		builder.setView(this.view);
		this.sb = (SeekBar) this.view.findViewById(2130968627);
		this.sh = (CheckBox) this.view.findViewById(2130968622);
		this.sf = (CheckBox) this.view.findViewById(2130968628);
		this.reflection_cb = (CheckBox) this.view.findViewById(2130968624);
		this.bloom_cb = (CheckBox) this.view.findViewById(2130968623);
		this.hqmeshes_cb = (CheckBox) this.view.findViewById(2130968625);
		this.bg_cb = (CheckBox) this.view.findViewById(2130968626);
		this.sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			/* class com.bithack.apparatus.GraphicsDialog.AnonymousClass1 */

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seek) {
				GraphicsDialog.this.rope_quality = seek.getProgress();
			}
		});
		builder.setNeutralButton(L.get("ok"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.GraphicsDialog.AnonymousClass2 */

			public void onClick(DialogInterface dialog, int which) {
				final boolean sf_checked = GraphicsDialog.this.sf.isChecked();
				final boolean hqmeshes_checked = GraphicsDialog.this.hqmeshes_cb.isChecked();
				final boolean bg_checked = GraphicsDialog.this.bg_cb.isChecked();
				final boolean sh_checked = !bg_checked ? false : GraphicsDialog.this.sh.isChecked();
				final boolean bloom_checked = GraphicsDialog.this.bloom_cb.isChecked();
				final boolean reflection_checked = GraphicsDialog.this.reflection_cb.isChecked();
				final int rq = GraphicsDialog.this.rope_quality;
				Settings.set("drawfps", sf_checked ? "yes" : "no");
				Settings.set("enableshadows", sh_checked ? "yes" : "no");
				Settings.set("ropequality", Integer.toString(rq));
				Settings.set("enablebg", bg_checked ? "yes" : "no");
				Settings.set("hqmeshes", hqmeshes_checked ? "yes" : "no");
				Settings.set("bloom", bloom_checked ? "yes" : "no");
				Settings.set("reflection", reflection_checked ? "yes" : "no");
				GraphicsDialog.this.activity.run_on_gl_thread(new Runnable() {
					/* class com.bithack.apparatus.GraphicsDialog.AnonymousClass2.AnonymousClass1 */

					public void run() {
						Game.draw_fps = sf_checked;
						Game.enable_shadows = sh_checked;
						Game.rope_quality = rq;
						Game.enable_hqmeshes = hqmeshes_checked;
						Game.enable_bloom = bloom_checked;
						Game.enable_reflections = reflection_checked;
						Game.enable_bg = bg_checked;
						ApparatusApp.game.update_ropes();
						MiscRenderer.update_quality();
					}
				});
				Settings.save();
			}
		});
		builder.setNegativeButton(L.get("cancel"), new DialogInterface.OnClickListener() {
			/* class com.bithack.apparatus.GraphicsDialog.AnonymousClass3 */

			public void onClick(DialogInterface dialog, int which) {
			}
		});
		this.dialog = builder.create();
	}

	public Dialog get_dialog() {
		return this.dialog;
	}

	public void prepare() {
		this.rope_quality = Game.rope_quality;
		this.sh.setChecked(Game.enable_shadows);
		this.sb.setProgress(Game.rope_quality);
		this.sf.setChecked(Game.draw_fps);
		this.bg_cb.setChecked(Game.enable_bg);
		this.bloom_cb.setChecked(Game.enable_bloom);
		this.reflection_cb.setChecked(Game.enable_reflections);
		this.hqmeshes_cb.setChecked(Game.enable_hqmeshes);
	}
}
