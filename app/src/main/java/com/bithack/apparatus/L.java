package com.bithack.apparatus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class L {
	private static Map<String, String> fallback = new HashMap();
	protected static Map<String, String> map = new HashMap();

	public static String get(String key) {
		String ret = map.get(key);
		if (ret != null) {
			return ret;
		}
		Gdx.app.log("MSG", "Translation for " + key + " is null, falling back to english.");
		String ret2 = fallback.get(key);
		Gdx.app.log("MSG", "new translation is: " + ret2);
		return ret2;
	}

	private static Map<String, String> _load(String lang) {
		Map<String, String> ret = new HashMap<>();
		FileHandle file = Gdx.files.internal("data/lang/" + lang);
		if (file.exists()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(file.read()));
			String value = null;
			String name = null;
			int state = 0;
			while (true) {
				try {
					String line = in.readLine();
					if (line != null) {
						String line2 = line.trim();
						if (line2.equals("---")) {
							if (!(value == null || name == null)) {
								ret.put(name, value);
							}
							name = null;
							value = null;
							state = 0;
						} else {
							switch (state) {
								case 0:
									if (!line2.equals("")) {
										name = line2;
										state = 1;
										break;
									} else {
										continue;
									}
								case 1:
									if (value != null) {
										value = String.valueOf(value) + "\n" + line2;
										break;
									} else {
										value = line2;
										continue;
									}
								default:
									continue;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	public static void load(String lang) {
		Gdx.app.log("Language", lang);
		if (fallback.isEmpty()) {
			fallback = _load("en");
		}
		map.clear();
		map = _load(lang);
		if (map.isEmpty()) {
			Gdx.app.log("Language", "Falling back to english :(");
			map = _load("en");
		}
	}
}
