package com.bithack.apparatus.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bithack.apparatus.graphics.G;

public class HorizontalSliderWidget extends Widget {
	private float max;
	private float min;
	private float snap;
	public float value;

	public HorizontalSliderWidget(int id) {
		this(id, -1.0f, 1.0f, 0, 0);
	}

	public HorizontalSliderWidget(int id, int size) {
		this(id, -1.0f, 1.0f, 0, 0);
		this.width = size;
	}

	public HorizontalSliderWidget(int id, int size, float snap2) {
		this(id, -1.0f, 1.0f, 0, 0);
		this.width = size;
		this.snap = snap2;
	}

	public HorizontalSliderWidget(int id, float min2, float max2, int x, int y) {
		this.snap = 0.0f;
		this.min = min2;
		this.max = max2;
		this.value = 0.0f;
		this.height = 32;
		this.width = 128;
		this.x = x;
		this.y = y;
		this.id = id;
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void render(Texture texture, SpriteBatch batch) {
		G.batch.begin();
		G.batch.setColor(Color.WHITE);
		G.batch.draw(texture, (float) this.x, (float) this.y, 0.0f, 0.0f, 16.0f, (float) this.height, 1.0f, 1.0f, 0.0f, 0, 32, 16, 32, false, false);
		G.batch.draw(texture, (float) (this.x + 16), (float) this.y, 0.0f, 0.0f, (float) (this.width - 32), (float) this.height, 1.0f, 1.0f, 0.0f, 0, 0, 32, 32, false, false);
		G.batch.draw(texture, (float) ((this.x + this.width) - 16), (float) this.y, 0.0f, 0.0f, 16.0f, (float) this.height, 1.0f, 1.0f, 0.0f, 16, 32, 16, 32, false, false);
		G.batch.draw(texture, (((float) this.x) + (((float) this.width) * ((this.value - this.min) / (this.max - this.min)))) - 8.0f, (float) this.y, 0.0f, 0.0f, 16.0f, 32.0f, 1.0f, 1.0f, 0.0f, 32, 0, 16, 32, false, false);
		G.batch.end();
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_down_local(int x, int y) {
		this.value = this.min + ((this.max - this.min) * (((float) x) / ((float) this.width)));
		if (this.snap != 0.0f) {
			this.value = (float) Math.round(this.value);
		}
		if (this.callback != null) {
			this.callback.on_widget_value_change(this.id, this.value);
		}
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_drag_local(int x, int y) {
		touch_down_local(x, y);
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_up_local(int x, int y) {
	}
}
