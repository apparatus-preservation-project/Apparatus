package com.bithack.apparatus.ui;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RotorWidget extends Widget {
	private Mesh mesh;
	public float value = 0.0f;

	public RotorWidget(int id) {
		this.id = id;
		this.mesh = new Mesh(Mesh.VertexDataType.VertexBufferObject, true, 4, 0, new VertexAttribute(1, 2, "a_position"), new VertexAttribute(16, 2, "a_texcoord"));
		this.width = 64;
		this.height = 64;
		this.mesh.setVertices(new float[]{(float) ((-this.width) / 2), (float) (this.height / 2), 0.0f * 0.0078125f, 128.0f * 0.0078125f, (float) ((-this.width) / 2), (float) ((-this.height) / 2), 0.0f * 0.0078125f, 64.0f * 0.0078125f, (float) (this.width / 2), (float) ((-this.height) / 2), 64.0f * 0.0078125f, 64.0f * 0.0078125f, (float) (this.width / 2), (float) (this.height / 2), 64.0f * 0.0078125f, 128.0f * 0.0078125f});
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void render(Texture texture, SpriteBatch batch) {
	}

	private void set(int x, int y) {
		int x2 = x - 32;
		int y2 = y - 32;
		float l = (float) Math.sqrt((double) ((float) ((x2 * x2) + (y2 * y2))));
		this.value = (float) Math.atan2((double) (((float) y2) / l), (double) (((float) x2) / l));
		this.value = ((float) ((int) (this.value / 0.3926991f))) * 0.3926991f;
		if (this.callback != null) {
			this.callback.on_widget_value_change(this.id, this.value);
		}
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_down_local(int x, int y) {
		set(x, y);
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_drag_local(int x, int y) {
		set(x, y);
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_up_local(int x, int y) {
	}
}
