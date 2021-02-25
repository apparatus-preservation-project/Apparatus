package com.bithack.apparatus.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bithack.apparatus.graphics.G;

public class VerticalSliderWidget extends Widget {
	private float max;
	private float min;
	private float snap;
	public float value;

	public VerticalSliderWidget(int id) {
		this(id, -1.0f, 1.0f, 0, 0);
	}

	public VerticalSliderWidget(int id, int size) {
		this(id, -1.0f, 1.0f, 0, 0);
		this.height = size;
	}

	public VerticalSliderWidget(int id, int size, float snap2) {
		this(id, -1.0f, 1.0f, 0, 0);
		this.height = size;
		this.snap = snap2;
	}

	public VerticalSliderWidget(int id, float min2, float max2, int x, int y) {
		this.snap = 0.0f;
		this.min = min2;
		this.max = max2;
		this.value = 0.0f;
		this.height = 128;
		this.width = 32;
		this.x = x;
		this.y = y;
		this.id = id;
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void render(Texture texture, SpriteBatch batch) {
		G.batch.begin();
		G.batch.setColor(Color.WHITE);
		G.batch.draw(texture, (float) this.x, (float) this.y, 0.0f, 0.0f, 32.0f, 16.0f, 1.0f, 1.0f, 0.0f, 32, 48, 32, 16, false, false);
		G.batch.draw(texture, (float) this.x, (float) (this.y + 16), 0.0f, 0.0f, (float) this.width, (float) (this.height - 32), 1.0f, 1.0f, 0.0f, 80, 0, 32, 32, false, false);
		G.batch.draw(texture, (float) this.x, (((float) this.y) + ((float) this.height)) - 16.0f, 0.0f, 0.0f, 32.0f, 16.0f, 1.0f, 1.0f, 0.0f, 32, 32, 32, 16, false, false);
		G.batch.draw(texture, (float) this.x, (((float) this.y) + (((float) this.height) * ((this.value - this.min) / (this.max - this.min)))) - 8.0f, 48.0f, 0.0f, 32.0f, 16.0f, 1.0f, 1.0f, 0.0f, 48, 0, 32, 16, false, false);
		G.batch.end();
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_down_local(int x, int y) {
		this.value = this.min + ((this.max - this.min) * (((float) y) / ((float) this.height)));
		if (this.snap != 0.0f) {
			this.value = this.snap * ((float) ((int) (this.value / this.snap)));
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
