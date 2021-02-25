package com.bithack.apparatus.ui;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bithack.apparatus.graphics.G;

public class HoldButtonWidget extends Widget {
	public boolean holding = false;
	private Mesh mesh;
	private Mesh mesh2;
	private int tex_x;
	private int tex_y;

	public HoldButtonWidget(int id, int width, int height, int tex_x2, int tex_y2) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.tex_x = tex_x2;
		this.tex_y = tex_y2;
		this.mesh = new Mesh(true, 4, 0, new VertexAttribute(1, 2, "a_position"), new VertexAttribute(16, 2, "a_texcoord"));
		float[] v = {(float) ((-width) / 2), (float) (height / 2), ((float) tex_x2) * 0.0078125f, ((float) tex_y2) * 0.0078125f, (float) ((-width) / 2), (float) ((-height) / 2), ((float) tex_x2) * 0.0078125f, (((float) tex_y2) * 0.0078125f) + (((float) this.height) * 0.0078125f), (float) (width / 2), (float) ((-height) / 2), (((float) tex_x2) * 0.0078125f) + (((float) this.width) * 0.0078125f), (((float) tex_y2) * 0.0078125f) + (((float) this.height) * 0.0078125f), (float) (width / 2), (float) (height / 2), (((float) tex_x2) * 0.0078125f) + (((float) this.width) * 0.0078125f), ((float) tex_y2) * 0.0078125f};
		this.mesh.setVertices(v);
		this.mesh2 = new Mesh(true, 4, 0, new VertexAttribute(1, 2, "a_position"), new VertexAttribute(16, 2, "a_texcoord"));
		int tex_x3 = tex_x2 + 64;
		v[0] = (float) ((-width) / 2);
		v[1] = (float) (height / 2);
		v[2] = ((float) tex_x3) * 0.0078125f;
		v[3] = ((float) tex_y2) * 0.0078125f;
		v[4] = (float) ((-width) / 2);
		v[5] = (float) ((-height) / 2);
		v[6] = ((float) tex_x3) * 0.0078125f;
		v[7] = (((float) tex_y2) * 0.0078125f) + (((float) this.height) * 0.0078125f);
		v[8] = (float) (width / 2);
		v[9] = (float) ((-height) / 2);
		v[10] = (((float) tex_x3) * 0.0078125f) + (((float) this.width) * 0.0078125f);
		v[11] = (((float) tex_y2) * 0.0078125f) + (((float) this.height) * 0.0078125f);
		v[12] = (float) (width / 2);
		v[13] = (float) (height / 2);
		v[14] = (((float) tex_x3) * 0.0078125f) + (((float) this.width) * 0.0078125f);
		v[15] = ((float) tex_y2) * 0.0078125f;
		this.mesh2.setVertices(v);
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void render(Texture texture, SpriteBatch batch) {
		G.gl.glEnable(3042);
		G.gl.glEnable(3553);
		G.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		texture.bind();
		G.gl.glPushMatrix();
		G.gl.glTranslatef(((float) this.x) + (((float) this.width) / 2.0f), ((float) this.y) + (((float) this.height) / 2.0f), 0.0f);
		if (this.holding) {
			this.mesh2.render(6);
		} else {
			this.mesh.render(6);
		}
		G.gl.glPopMatrix();
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_down_local(int x, int y) {
		this.holding = true;
		if (this.callback != null) {
			this.callback.on_widget_value_change(this.id, 1.0f);
		}
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_drag_local(int x, int y) {
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_up_local(int x, int y) {
		this.holding = false;
		if (this.callback != null) {
			this.callback.on_widget_value_change(this.id, 0.0f);
		}
	}
}
