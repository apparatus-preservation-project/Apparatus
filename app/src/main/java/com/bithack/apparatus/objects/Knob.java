package com.bithack.apparatus.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bithack.apparatus.ApparatusApp;
import com.bithack.apparatus.graphics.G;
import com.bithack.apparatus.graphics.MiscRenderer;
import com.bithack.apparatus.objects.BaseObject;

public class Knob extends Wheel {
	static final float[] _material = {0.15f, 0.0f, 0.0f, 1.0f, 0.4f, 0.0f, 0.0f, 1.0f, 0.8f, 0.0f, 0.0f, 1.0f, 1.5f, 0.0f, 0.0f, 0.0f};

	public Knob(World world, float size) {
		super(world, size);
		this.size = size;
		this.ingame_type = BodyDef.BodyType.DynamicBody;
		this.build_type = BodyDef.BodyType.DynamicBody;
	}

	@Override // com.bithack.apparatus.objects.GrabableObject
	public void play() {
		super.play();
	}

	@Override // com.bithack.apparatus.objects.BaseObject, com.bithack.apparatus.objects.Wheel
	public void on_click() {
	}

	@Override // com.bithack.apparatus.objects.BaseObject, com.bithack.apparatus.objects.Wheel
	public void render() {
		G.gl.glPushMatrix();
		BaseObject.State s = get_state();
		Vector2 pos = s.position;
		G.gl.glTranslatef(pos.x, pos.y, ((float) this.layer) + 1.0f);
		G.gl.glRotatef((float) (((double) s.angle) * 57.29577951308232d), 0.0f, 0.0f, 1.0f);
		G.gl.glScalef(this.size, this.size, 0.8f);
		MiscRenderer.draw_hqcylinder();
		G.gl.glPopMatrix();
	}

	@Override // com.bithack.apparatus.objects.Wheel
	public void render_lq() {
		G.gl.glPushMatrix();
		BaseObject.State s = get_state();
		Vector2 pos = s.position;
		G.gl.glTranslatef(pos.x, pos.y, ((float) this.layer) + 1.0f);
		G.gl.glRotatef((float) (((double) s.angle) * 57.29577951308232d), 0.0f, 0.0f, 1.0f);
		G.gl.glScalef(this.size, this.size, 0.8f);
		MiscRenderer.draw_cylinder();
		G.gl.glPopMatrix();
	}

	@Override // com.bithack.apparatus.objects.GrabableObject, com.bithack.apparatus.objects.Wheel
	public void reshape() {
		ApparatusApp.game.remove_potential_fixture_pair(this.body);
		if (this.f != null) {
			this.body.destroyFixture(this.f);
		}
		_shape.setRadius(this.size);
		_fd.density = 0.5f;
		_fd.restitution = 0.4f;
		this.f = this.body.createFixture(_fd);
	}

	public static void init_materials() {
		G.gl.glMaterialfv(1032, 4608, _material, 0);
		G.gl.glMaterialfv(1032, 4609, _material, 4);
		G.gl.glMaterialfv(1032, 4610, _material, 8);
		G.gl.glMaterialfv(1032, 5633, _material, 12);
	}
}
