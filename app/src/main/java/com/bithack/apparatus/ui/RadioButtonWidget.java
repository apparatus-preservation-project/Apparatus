package com.bithack.apparatus.ui;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RadioButtonWidget extends Widget implements RadioWidget {
	public Boolean checked = false;
	private Texture custom_texture = null;
	private RadioButtonWidgetGroup group;
	private int group_id;
	private Mesh mesh;
	private int tex_x;
	private int tex_y;

	public RadioButtonWidget(int id, Boolean checked2, int width, int height, int tex_x2, int tex_y2, Texture custom_texture2, RadioButtonWidgetGroup group2) {
		this.checked = checked2;
		this.id = id;
		this.width = width;
		this.height = height;
		this.tex_x = tex_x2;
		this.tex_y = tex_y2;
		this.group = group2;
		this.group_id = group2.add_button(this);
		if (this.custom_texture != null) {
			this.custom_texture = custom_texture2;
			init_mesh(this.custom_texture.getWidth(), this.custom_texture.getHeight());
		}
	}

	public void set_texture(Texture texture) {
		this.custom_texture = texture;
		init_mesh(this.custom_texture.getWidth(), this.custom_texture.getHeight());
	}

	public RadioButtonWidget(int id, Boolean checked2, int width, int height, int tex_x2, int tex_y2, RadioButtonWidgetGroup group2) {
		this.checked = checked2;
		this.id = id;
		this.width = width;
		this.height = height;
		this.tex_x = tex_x2;
		this.tex_y = tex_y2;
		this.group = group2;
		this.group_id = group2.add_button(this);
		init_mesh(256, 256);
	}

	private void init_mesh(int tex_w, int tex_h) {
		if (this.mesh != null) {
			this.mesh.dispose();
		}
		this.mesh = new Mesh(true, 4, 0, new VertexAttribute(1, 2, "a_position"), new VertexAttribute(16, 2, "a_texcoord"));
		float step = 1.0f / ((float) tex_w);
		this.mesh.setVertices(new float[]{(float) ((-this.width) / 2), (float) (this.height / 2), ((float) this.tex_x) * step, ((float) this.tex_y) * step, (float) ((-this.width) / 2), (float) ((-this.height) / 2), ((float) this.tex_x) * step, (((float) this.tex_y) * step) + (((float) this.height) * step), (float) (this.width / 2), (float) ((-this.height) / 2), (((float) this.tex_x) * step) + (((float) this.width) * step), (((float) this.tex_y) * step) + (((float) this.height) * step), (float) (this.width / 2), (float) (this.height / 2), (((float) this.tex_x) * step) + (((float) this.width) * step), ((float) this.tex_y) * step});
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void render(Texture texture, SpriteBatch batch) {
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_down_local(int x, int y) {
		if (!this.checked.booleanValue()) {
			this.group.click(this.group_id);
			if (this.callback != null) {
				this.callback.on_widget_value_change(this.id, 1.0f);
			}
		}
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_drag_local(int x, int y) {
	}

	@Override // com.bithack.apparatus.ui.IWidget
	public void touch_up_local(int x, int y) {
	}

	@Override // com.bithack.apparatus.ui.RadioWidget
	public void set_checked(boolean ch) {
		this.checked = Boolean.valueOf(ch);
	}
}
