package com.bithack.apparatus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.Gdx;

public class ApparatusPlay extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = new Intent(this, ApparatusApplication.class);
		i.setFlags(604045312);
		Gdx.app.log("intent 1", new StringBuilder().append(getIntent()).toString());
		Gdx.app.log("intent", getIntent().getScheme());
		Gdx.app.log("", new StringBuilder().append(getIntent().getData()).toString());
		i.putExtra("id", 64);
		startActivity(i);
	}

	public void onResume() {
		super.onResume();
	}

	public void onDestroy() {
		Settings.save();
		super.onDestroy();
	}
}
