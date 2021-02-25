package com.bithack.apparatus.objects;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.bithack.apparatus.Game;
import com.bithack.apparatus.SilhouetteMesh;
import com.bithack.apparatus.graphics.G;
import com.bithack.apparatus.graphics.MiscRenderer;
import com.bithack.apparatus.objects.BaseObject;
import java.io.IOException;
import java.util.jar.JarOutputStream;

public class Hinge extends BaseObject {
	private static boolean _initialized = false;
	private static RevoluteJointDef _rjd = null;
	public static Mesh _silhouette_mesh;
	private static WeldJointDef _wjd = null;
	public Vector2 anchor = new Vector2();
	public GrabableObject b1;
	public GrabableObject b2;
	public int body1_id = 0;
	public int body2_id = 0;
	public boolean culled = false;
	private boolean disposed = false;
	public boolean fixed = true;
	private boolean indestructable = false;
	public Joint joint = null;
	public float rot_extra = 90.0f;
	public boolean same_layer = false;
	public int type = 0;
	private World world;

	public Hinge(World world2) {
		if (!_initialized) {
			_init();
		}
		this.world = world2;
		this.properties = new BaseObject.Property[]{new BaseObject.Property("body1", BaseObject.Property.Type.INT, new Integer(0)), new BaseObject.Property("body2", BaseObject.Property.Type.INT, new Integer(0)), new BaseObject.Property("jx", BaseObject.Property.Type.FLOAT, new Float(0.0f)), new BaseObject.Property("jy", BaseObject.Property.Type.FLOAT, new Float(0.0f)), new BaseObject.Property("type", BaseObject.Property.Type.INT, new Integer(0)), new BaseObject.Property("rot_extra", BaseObject.Property.Type.FLOAT, new Float(90.0f)), new BaseObject.Property("same_layer", BaseObject.Property.Type.BOOLEAN, Boolean.FALSE)};
	}

	public static void _init() {
		if (!_initialized) {
			_initialized = true;
			_rjd = new RevoluteJointDef();
			_rjd.collideConnected = false;
			_wjd = new WeldJointDef();
			_wjd.collideConnected = false;
			Vector3 light_dir = new Vector3(Game.light_pos);
			light_dir.nor();
			light_dir.mul(-1.0f);
			SilhouetteMesh.Edge[] edges = new SilhouetteMesh.Edge[24];
			SilhouetteMesh.Face[] faces = new SilhouetteMesh.Face[10];
			for (int c = 0; c < 8; c++) {
				float x = (float) Math.cos(((double) c) * 0.7853981633974483d);
				float y = (float) Math.sin(((double) c) * 0.7853981633974483d);
				if (c == 0) {
					edges[c] = new SilhouetteMesh.Edge(new Vector3(x, y, -0.25f), new Vector3(x, y, 0.25f), c, 7);
				} else {
					edges[c] = new SilhouetteMesh.Edge(new Vector3(x, y, -0.25f), new Vector3(x, y, 0.25f), c - 1, c);
				}
				faces[c] = new SilhouetteMesh.Face(new Vector3(x, y, 0.0f));
			}
			for (int c2 = 0; c2 < 8; c2++) {
				float x2 = (float) Math.cos(((double) c2) * 0.7853981633974483d);
				float y2 = (float) Math.sin(((double) c2) * 0.7853981633974483d);
				float nx = (float) Math.cos(((double) (c2 + 1)) * 0.7853981633974483d);
				float ny = (float) Math.sin(((double) (c2 + 1)) * 0.7853981633974483d);
				edges[(c2 * 2) + 8] = new SilhouetteMesh.Edge(new Vector3(x2, y2, 0.25f), new Vector3(nx, ny, 0.25f), c2, 9);
				edges[(c2 * 2) + 8 + 1] = new SilhouetteMesh.Edge(new Vector3(x2, y2, -0.25f), new Vector3(nx, ny, -0.25f), c2, 8);
			}
			faces[9] = new SilhouetteMesh.Face(new Vector3(0.0f, 0.0f, 1.0f));
			faces[8] = new SilhouetteMesh.Face(new Vector3(0.0f, 0.0f, -1.0f));
			SilhouetteMesh transformed = new SilhouetteMesh(edges, faces).transform(0.0f, 0.0f, 0.0f, 0.1f, 0.1f, 5.0f, 0.0f);
			transformed.mark_back_faces(light_dir);
			_silhouette_mesh = transformed.generate_mesh((Mesh) null, light_dir, 2);
		}
	}

	public void setup(GrabableObject b12, GrabableObject b22, Vector2 pos) {
		this.anchor.set(pos);
		if (this.type == 0) {
			_rjd.initialize(b12.body, b22.body, this.anchor);
			this.joint = this.world.createJoint(_rjd);
		} else {
			_wjd.initialize(b12.body, b22.body, this.anchor);
			this.joint = this.world.createJoint(_wjd);
		}
		this.b1 = b12;
		this.b2 = b22;
		this.body1_id = b12.__unique_id;
		this.body2_id = b22.__unique_id;
		b12.num_hinges++;
		b22.num_hinges++;
		if ((b12 instanceof Knob) || (b22 instanceof Knob)) {
			this.indestructable = true;
		}
	}

	public void save() {
		if (this.joint != null) {
			this.anchor.set(this.joint.getAnchorA());
		}
	}

	public void recreate(World world2) {
		if (this.joint != null) {
			world2.destroyJoint(this.joint);
		}
		if (this.type == 0) {
			_rjd.initialize(this.b1.body, this.b2.body, this.anchor);
			this.joint = world2.createJoint(_rjd);
			return;
		}
		_wjd.initialize(this.b1.body, this.b2.body, this.anchor);
		this.joint = world2.createJoint(_wjd);
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void dispose(World world2) {
		if (!this.disposed) {
			if (this.joint != null) {
				world2.destroyJoint(this.joint);
			}
			if (this.b1 != null) {
				GrabableObject grabableObject = this.b1;
				grabableObject.num_hinges--;
			}
			if (this.b2 != null) {
				GrabableObject grabableObject2 = this.b2;
				grabableObject2.num_hinges--;
			}
			this.disposed = true;
		}
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void on_click() {
	}

	public void tick() {
		if (this.joint != null && !this.indestructable && this.joint.getReactionForce(66.66667f).len2() > 8.1E7f) {
			this.world.destroyJoint(this.joint);
			this.joint = null;
		}
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void render() {
		if (this.culled || this.joint == null) {
			return;
		}
		if (!this.same_layer) {
			Vector2 pos = get_state().position;
			G.gl.glPushMatrix();
			int layer = Math.max(this.b1.layer, this.b2.layer);
			int min = Math.min(this.b1.layer, this.b2.layer);
			if (layer == 2 && min == 0) {
				G.gl.glTranslatef(pos.x, pos.y, 2.0f);
				G.gl.glScalef(0.1f, 0.1f, 3.0f);
			} else {
				G.gl.glTranslatef(pos.x, pos.y, 0.5f + ((float) layer));
				G.gl.glScalef(0.1f, 0.1f, 2.5f);
			}
			MiscRenderer.draw_smallcylinder();
			G.gl.glPopMatrix();
			return;
		}
		Vector2 pos2 = get_state().position;
		G.gl.glPushMatrix();
		G.gl.glTranslatef(pos2.x, pos2.y, ((float) this.b1.layer) + 1.0f);
		G.gl.glRotatef(((float) (((double) ((GrabableObject) this.joint.getBodyA().getUserData()).get_state().angle) * 57.29577951308232d)) + this.rot_extra, 0.0f, 0.0f, 1.0f);
		G.gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
		G.gl.glScalef(0.1f, 0.1f, 1.0f);
		MiscRenderer.draw_smallcylinder();
		G.gl.glPopMatrix();
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void set_property(String property, Object value) {
		if (property.equals("body1")) {
			this.body1_id = ((Integer) value).intValue();
		} else if (property.equals("body2")) {
			this.body2_id = ((Integer) value).intValue();
		} else if (property.equals("jx")) {
			this.anchor.x = ((Float) value).floatValue();
		} else if (property.equals("jy")) {
			this.anchor.y = ((Float) value).floatValue();
		} else if (property.equals("type")) {
			this.type = ((Integer) value).intValue();
		} else if (property.equals("rot_extra")) {
			this.rot_extra = ((Float) value).floatValue();
		} else if (property.equals("same_layer")) {
			this.same_layer = ((Boolean) value).booleanValue();
		}
		super.set_property(property, value);
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void update_properties() {
		set_property("body1", Integer.valueOf(this.body1_id));
		set_property("body2", Integer.valueOf(this.body2_id));
		this.anchor.set(this.joint.getAnchorA());
		set_property("jx", Float.valueOf(this.anchor.x));
		set_property("jy", Float.valueOf(this.anchor.y));
		set_property("type", Integer.valueOf(this.type));
		set_property("rot_extra", Float.valueOf(this.rot_extra));
		set_property("same_layer", this.same_layer ? Boolean.TRUE : Boolean.FALSE);
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void write_to_stream(JarOutputStream o) throws IOException {
		update_properties();
		super.write_to_stream(o);
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void step(float deltatime) {
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void translate(float x, float y) {
		this.anchor.set(x, y);
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public Vector2 get_position() {
		if (this.joint != null) {
			return this.joint.getAnchorA();
		}
		return null;
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public float get_bb_radius() {
		return 0.0f;
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public float get_angle() {
		return 0.0f;
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void set_angle(float angle) {
	}
}
