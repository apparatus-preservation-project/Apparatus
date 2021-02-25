package com.bithack.apparatus;

import java.io.File;

public class Settings {
	private static Adapter adapter = new DummyAdapter();

	public static abstract class Adapter {
		public abstract String get(String str);

		public abstract File get_tmp_file();

		public abstract void msg(String str);

		public abstract void save();

		public abstract void set(String str, String str2);

		public abstract void start_tracing();

		public abstract void stop_tracing();
	}

	public static class DummyAdapter extends Adapter {
		@Override // com.bithack.apparatus.Settings.Adapter
		public String get(String key) {
			return "";
		}

		@Override // com.bithack.apparatus.Settings.Adapter
		public void set(String key, String value) {
		}

		@Override // com.bithack.apparatus.Settings.Adapter
		public void save() {
		}

		@Override // com.bithack.apparatus.Settings.Adapter
		public void msg(String s) {
		}

		@Override // com.bithack.apparatus.Settings.Adapter
		public File get_tmp_file() {
			return null;
		}

		@Override // com.bithack.apparatus.Settings.Adapter
		public void start_tracing() {
		}

		@Override // com.bithack.apparatus.Settings.Adapter
		public void stop_tracing() {
		}
	}

	public static File get_tmp_file() {
		return adapter.get_tmp_file();
	}

	public static void set_adapter(Adapter adapter2) {
		adapter = adapter2;
	}

	public static String get(String key) {
		return adapter.get(key);
	}

	public static void start_tracing() {
		adapter.start_tracing();
	}

	public static void stop_tracing() {
		adapter.stop_tracing();
	}

	public static void set(String key, String value) {
		adapter.set(key, value);
	}

	public static void save() {
		adapter.save();
	}

	public static void msg(String string) {
		adapter.msg(string);
	}
}
