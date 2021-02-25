package com.bithack.apparatus.graphics;

import com.bithack.apparatus.objects.BaseObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Pipeline {
	public static final int BATCH = 4;
	public static final int COLOR = 2;
	public static final int CUSTOM = 3;
	public static final int SPRITE = 1;
	public static final int TEXTURE = 0;
	private ArrayList<BaseObject> batch_pipeline = new ArrayList<>();
	private ArrayList<BaseObject> color_pipeline = new ArrayList<>();
	private ArrayList<BaseObject> custom_pipeline = new ArrayList<>();
	private HashMap<Integer, ArrayList> sprite_pipeline = new HashMap<>();
	private HashMap<Integer, ArrayList> texture_pipeline = new HashMap<>();

	public void remove(BaseObject o) {
		switch (o.pipeline) {
			case 0:
				ArrayList<BaseObject> l = this.texture_pipeline.get(new Integer(o.texture_id));
				if (l != null) {
					l.remove(o);
					return;
				}
				return;
			case 1:
				ArrayList<BaseObject> l2 = this.sprite_pipeline.get(new Integer(o.texture_id));
				if (l2 != null) {
					l2.remove(o);
					return;
				}
				return;
			case 2:
				this.color_pipeline.remove(o);
				return;
			case 3:
			default:
				return;
			case 4:
				this.batch_pipeline.remove(o);
				return;
		}
	}

	public void add(BaseObject o) {
		switch (o.pipeline) {
			case 0:
				Integer id = new Integer(o.texture_id);
				ArrayList<BaseObject> l = this.texture_pipeline.get(id);
				if (l == null) {
					l = new ArrayList<>();
					this.texture_pipeline.put(id, l);
				}
				l.add(o);
				return;
			case 1:
				Integer id2 = new Integer(o.texture_id);
				ArrayList<BaseObject> l2 = this.sprite_pipeline.get(id2);
				if (l2 == null) {
					l2 = new ArrayList<>();
					this.sprite_pipeline.put(id2, l2);
				}
				l2.add(o);
				return;
			case 2:
				this.color_pipeline.add(o);
				return;
			case 3:
			default:
				return;
			case 4:
				this.batch_pipeline.add(o);
				return;
		}
	}

	public void clear() {
		this.texture_pipeline.clear();
		this.sprite_pipeline.clear();
		this.color_pipeline.clear();
		this.custom_pipeline.clear();
		this.batch_pipeline.clear();
	}

	public void render_all() {
		G.gl.glEnable(3553);
		for (Map.Entry<Integer, ArrayList> entry : this.texture_pipeline.entrySet()) {
			TextureFactory.by_id.get(entry.getKey()).texture.bind();
			Iterator<BaseObject> it = entry.getValue().iterator();
			while (it.hasNext()) {
				it.next().render();
			}
		}
		G.gl.glDisable(3553);
		if (this.color_pipeline.size() > 0) {
			Iterator<BaseObject> it2 = this.color_pipeline.iterator();
			while (it2.hasNext()) {
				it2.next().render();
			}
		}
	}

	public void render_batch() {
		Iterator<BaseObject> it = this.batch_pipeline.iterator();
		while (it.hasNext()) {
			it.next().render();
		}
	}
}
