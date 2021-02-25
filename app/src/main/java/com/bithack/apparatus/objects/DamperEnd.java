package com.bithack.apparatus.objects;

import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bithack.apparatus.graphics.G;
import com.bithack.apparatus.graphics.MiscRenderer;

public class DamperEnd extends GrabableObject {
	protected static FixtureDef _fd;
	public final Damper damper;
	private Fixture f = null;
	int index;
	private Fixture sensor = null;

	public DamperEnd(World world, Damper damper2, int index2) {
		this.index = index2;
		this.damper = damper2;
		this.body = world.createBody(Damper._bd);
		this.f = this.body.createFixture(_fd);
		this.sensor = this.body.createFixture(Damper._sfd);
		this.sensor.setUserData(this);
		this.body.setUserData(this);
		this.sandbox_only = false;
		this.fixed_rotation = false;
		this.ingame_type = BodyDef.BodyType.DynamicBody;
		this.build_type = BodyDef.BodyType.DynamicBody;
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void on_click() {
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void render() {
		float f2;
		float f3 = 0.1f;
		G.gl.glPushMatrix();
		Vector2 pos = get_state().position;
		G.gl.glTranslatef(pos.x, pos.y, ((float) this.layer) + 1.0f);
		G.gl.glRotatef(get_state().angle * 57.295776f, 0.0f, 0.0f, 1.0f);
		G.gl.glTranslatef(0.0f, 0.15f, 0.0f);
		GL11 gl11 = G.gl;
		if (this.index == 0) {
			f2 = 0.1f;
		} else {
			f2 = 0.0f;
		}
		float f4 = f2 + 0.8f;
		if (this.index != 0) {
			f3 = 0.0f;
		}
		gl11.glScalef(f4, 1.75f, f3 + 0.8f);
		MiscRenderer.hqcubemesh.render(4);
		G.gl.glPopMatrix();
	}

	public void render_box() {
		G.gl.glPushMatrix();
		Vector2 pos = get_state().position;
		G.gl.glTranslatef(pos.x, pos.y, ((float) this.layer) + 1.0f);
		G.gl.glRotatef(get_state().angle * 57.295776f, 0.0f, 0.0f, 1.0f);
		G.gl.glTranslatef(0.0f, -0.752f, 0.0f);
		G.gl.glScalef(1.2f, 0.5f, 1.0f);
		MiscRenderer.hqcubemesh.render(4);
		G.gl.glPopMatrix();
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void step(float deltatime) {
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public Vector2 get_position() {
		return this.body.getPosition();
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public float get_bb_radius() {
		return 0.0f;
	}

	@Override // com.bithack.apparatus.objects.GrabableObject, com.bithack.apparatus.objects.BaseObject
	public void dispose(World world) {
		world.destroyBody(this.body);
	}

	@Override // com.bithack.apparatus.objects.GrabableObject
	public void reshape() {
		this.body.destroyFixture(this.f);
		this.f = this.body.createFixture(_fd);
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void update_properties() {
	}

	@Override // com.bithack.apparatus.objects.GrabableObject
	public void tja_translate(float x, float y) {
		translate(x, y);
	}
}
