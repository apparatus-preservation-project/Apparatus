package com.bithack.apparatus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;

public class ResourceFactory {
	public static final int STORE_EXTERNAL = 1;
	public static final int STORE_INTERNAL = 0;
	public static final int STORE_LEVEL = 2;
	public static final int TYPE_IMAGE = 0;
	public static final int TYPE_LEVEL = 1;
	public static final int TYPE_SOUND = 2;
	public static final String external_root = "Android/data/com.bithack.superslimeblob/";
	public static final String internal_root = "data/";

	public static class Adapter {
	}

	public static class Collection {
		public String[] categories;
		public ArrayList<Resource>[] resources;
	}

	public static void initialize() {
	}

	public static Collection find_by_categories(String[] cats) {
		Collection c = new Collection();
		c.categories = cats;
		c.resources = new ArrayList[cats.length];
		int x = 0;
		for (String cat : cats) {
			c.resources[x] = new ArrayList<>();
			FileHandle dir = Gdx.files.external("Android/data/com.bithack.superslimeblob/" + cat);
			if (dir.exists()) {
				FileHandle[] list = dir.list();
				int length = list.length;
				for (int i = 0; i < length; i++) {
					c.resources[x].add(new Resource(1, String.valueOf(cat) + '/' + list[i].name()));
				}
			}
			FileHandle dir2 = Gdx.files.internal("data/" + cat);
			if (dir2.exists()) {
				FileHandle[] list2 = dir2.list();
				int length2 = list2.length;
				for (int i2 = 0; i2 < length2; i2++) {
					c.resources[x].add(new Resource(0, String.valueOf(cat) + '/' + list2[i2].name()));
				}
			}
			x++;
		}
		return c;
	}

	public static class Resource {
		public final String file;
		private final FileHandle handle;
		public final String name;
		public final int store;

		public Resource(int store2, String filename) {
			switch (store2) {
				case 0:
					this.handle = Gdx.files.internal("data/" + filename);
					break;
				case 1:
					this.handle = Gdx.files.external("Android/data/com.bithack.superslimeblob/" + filename);
					break;
				default:
					this.handle = Gdx.files.internal("data/" + filename);
					break;
			}
			this.store = store2;
			this.file = filename;
			this.name = this.handle.nameWithoutExtension();
		}

		public FileHandle get_handle() {
			return this.handle;
		}

		public String get_path() {
			return this.handle.path();
		}
	}
}
