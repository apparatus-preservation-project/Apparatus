package com.bithack.apparatus.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.bithack.apparatus.ObjectFactory;
import com.bithack.apparatus.graphics.G;
import com.bithack.apparatus.graphics.MiscRenderer;
import com.bithack.apparatus.objects.BaseObject;
import com.bithack.apparatus.ui.HoldButtonWidget;
import com.bithack.apparatus.ui.HorizontalSliderWidget;
import com.bithack.apparatus.ui.Widget;
import com.bithack.apparatus.ui.WidgetManager;
import com.bithack.apparatus.ui.WidgetValueCallback;
import java.io.IOException;
import java.util.jar.JarOutputStream;

public class Panel extends GrabableObject implements FreeObject, WidgetValueCallback {
	protected static BodyDef _bd;
	protected static FixtureDef _fd;
	static final float[] _material = {0.15f, 0.05f, 0.05f, 1.0f, 0.5f, 0.25f, 0.25f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f, 1.5f, 0.2f, 0.1f, 0.1f, 0.2f, 0.5f, 0.25f, 0.25f, 0.8f, 10.0f, 0.0f, 0.0f, 0.0f};
	protected static PolygonShape _shape;
	protected static Vector2 _tmp = new Vector2();
	protected static boolean initialized = false;
	public static int num_types = 2;
	private static Vector2[] sensor_pos = {new Vector2(0.0f, 1.0f), new Vector2(0.0f, -1.0f)};
	Connection[] connections = null;
	public Fixture f;
	private Fixture[] sensors;
	public int type = 0;
	public WidgetManager widgets;
	private final World world;

	public class Connection {
		public static final int BUTTON = 2;
		public static final int POS_LEFT_0 = 0;
		public static final int POS_LEFT_1 = 1;
		public static final int POS_RIGHT_0 = 2;
		public static final int POS_RIGHT_1 = 3;
		public static final int SLIDER = 0;
		public static final int TOGGLER = 1;
		boolean available = true;
		PanelCable cable;
		PanelCableEnd cableend;
		int cableend_id = -1;
		Panel panel = null;
		Vector2 pos = new Vector2();
		public int pos_type;
		Vector2 screen_pos = new Vector2();
		public int type;
		Widget widget;

		public Connection(Panel p, int id, int type2, int screen_pos2, Vector2 pos2) {
			this.panel = p;
			this.pos.set(pos2);
			this.pos_type = screen_pos2;
			switch (type2) {
				case 0:
					this.widget = new HorizontalSliderWidget(id, 256);
					break;
				case 2:
					this.widget = new HoldButtonWidget(id, 64, 64, 0, 64);
					break;
			}
			switch (screen_pos2) {
				case 0:
					this.screen_pos.set(64.0f, 64.0f);
					break;
				case 1:
					this.screen_pos.set(64.0f, 192.0f);
					break;
				case 2:
					if (type2 != 0) {
						this.screen_pos.set((float) ((G.width - 64) - 64), 64.0f);
						break;
					} else {
						this.screen_pos.set((float) ((G.width - 64) - 256), 64.0f);
						break;
					}
				case 3:
					if (type2 != 0) {
						this.screen_pos.set((float) ((G.width - 64) - 64), 192.0f);
						break;
					} else {
						this.screen_pos.set((float) ((G.width - 64) - 256), 192.0f);
						break;
					}
			}
			this.type = type2;
		}

		public void play() {
			switch (this.type) {
				case 0:
					((HorizontalSliderWidget) this.widget).value = -1.0f;
					return;
				case 1:
				default:
					return;
				case 2:
					((HoldButtonWidget) this.widget).holding = false;
					return;
			}
		}

		public void attach(PanelCableEnd end) {
			if (this.available) {
				this.cableend_id = end.__unique_id;
				this.cableend = end;
				this.cable = end.cable;
				this.available = false;
			}
		}

		public void detach() {
			if (!this.available) {
				this.cable = null;
				this.cableend = null;
				this.cableend_id = -1;
				this.available = true;
			}
		}
	}

	public Panel(World world2) {
		if (!initialized) {
			init();
		}
		if (!Plank._initialized) {
			Plank._init();
		}
		this.world = world2;
		this.layer = 0;
		set_type(0);
		reshape();
		this.body.setUserData(this);
		this.sandbox_only = false;
		this.fixed_layer = true;
		this.properties = new BaseObject.Property[]{new BaseObject.Property("type", BaseObject.Property.Type.INT, 0)};
		this.ingame_type = BodyDef.BodyType.DynamicBody;
		this.build_type = BodyDef.BodyType.DynamicBody;
	}

	@Override // com.bithack.apparatus.objects.GrabableObject, com.bithack.apparatus.objects.BaseObject
	public void translate(float x, float y) {
		super.translate(x, y);
		for (int i = 0; i < this.connections.length; i++) {
			if (!this.connections[i].available) {
				this.connections[i].cableend.update_pos();
			}
		}
	}

	public void disconnect_all() {
		if (this.connections != null) {
			for (int x = 0; x < this.connections.length; x++) {
				if (!this.connections[x].available) {
					this.connections[x].cableend.detach();
				}
			}
		}
	}

	public void set_type(int id) {
		this.widgets = new WidgetManager("uicontrols.png", this);
		this.widgets.set_tolerance(64);
		disconnect_all();
		switch (id) {
			case 0:
				this.connections = new Connection[3];
				this.connections[0] = new Connection(this, 0, 0, 0, new Vector2(-0.6f, -0.2f));
				this.connections[1] = new Connection(this, 1, 2, 2, new Vector2(0.0f, -0.2f));
				this.connections[2] = new Connection(this, 2, 2, 3, new Vector2(0.6f, -0.2f));
				break;
			case 1:
				this.connections = new Connection[2];
				this.connections[0] = new Connection(this, 0, 2, 0, new Vector2(-0.6f, -0.2f));
				this.connections[1] = new Connection(this, 1, 2, 2, new Vector2(0.6f, -0.2f));
				break;
		}
		for (int x = 0; x < this.connections.length; x++) {
			this.widgets.add_widget(this.connections[x].widget, (int) this.connections[x].screen_pos.x, (int) this.connections[x].screen_pos.y);
		}
		this.type = id;
	}

	private static void init() {
		if (!initialized) {
			initialized = true;
			_shape = new PolygonShape();
			_fd = new FixtureDef();
			_fd.isSensor = true;
			_fd.density = 0.0f;
			_fd.shape = _shape;
		}
	}

	private void create_sensors() {
		destroy_sensors();
		for (int x = 0; x < sensor_pos.length; x++) {
			_shape.setAsBox(1.0f + (Math.abs(sensor_pos[x].y) * 0.5f), 0.25f + (Math.abs(sensor_pos[x].x) * 0.5f), sensor_pos[x], 0.0f);
			this.sensors[x] = this.body.createFixture(_fd);
			this.sensors[x].setUserData(sensor_pos[x]);
		}
	}

	private void destroy_sensors() {
		for (int x = 0; x < sensor_pos.length; x++) {
			if (this.sensors[x] != null) {
				if (this.body.getFixtureList().contains(this.sensors[x], false)) {
					this.body.destroyFixture(this.sensors[x]);
				}
				this.sensors[x] = null;
			}
		}
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void on_click() {
	}

	public void render_box() {
		if (!this.culled) {
			G.gl.glPushMatrix();
			Vector2 pos = get_state().position;
			G.gl.glTranslatef(pos.x, pos.y, (float) (this.layer + 1));
			G.gl.glRotatef(get_state().angle * 57.295776f, 0.0f, 0.0f, 1.0f);
			G.gl.glScalef(2.0f, 1.2f, 0.5f);
			MiscRenderer.hqcubemesh.render(4);
			G.gl.glPopMatrix();
		}
	}

	public void render_sockets() {
		if (!this.culled) {
			Vector2 pos = get_state().position;
			G.gl.glPushMatrix();
			G.gl.glTranslatef(pos.x, pos.y, ((float) this.layer) + 1.25f);
			G.gl.glRotatef((float) (((double) get_state().angle) * 57.29577951308232d), 0.0f, 0.0f, 1.0f);
			for (int x = 0; x < this.connections.length; x++) {
				G.gl.glPushMatrix();
				G.gl.glTranslatef(this.connections[x].pos.x, this.connections[x].pos.y, 0.0f);
				G.gl.glScalef(0.5f, 0.5f, 0.25f);
				MiscRenderer.hqcubemesh.render(4);
				G.gl.glPopMatrix();
			}
			G.gl.glPopMatrix();
		}
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void render() {
		if (!this.culled) {
			G.gl.glPushMatrix();
			Vector2 pos = get_state().position;
			G.gl.glTranslatef(pos.x, pos.y, (float) (this.layer + 1));
			G.gl.glRotatef(get_state().angle * 57.295776f, 0.0f, 0.0f, 1.0f);
			G.gl.glScalef(2.0f, 1.2f, 0.5f);
			MiscRenderer.hqcubemesh.render(4);
			G.gl.glPopMatrix();
			if (this.culled) {
			}
		}
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

	@Override // com.bithack.apparatus.objects.BaseObject
	public void set_property(String name, Object value) {
		if (name.equals("type")) {
			set_type(((Integer) value).intValue());
		}
		super.set_property(name, value);
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void update_properties() {
		super.set_property("type", new Integer(this.type));
	}

	@Override // com.bithack.apparatus.objects.BaseObject
	public void write_to_stream(JarOutputStream s) throws IOException {
		update_properties();
		super.write_to_stream(s);
	}

	public static void init_materials() {
		G.gl.glMaterialfv(1032, 4608, _material, 0);
		G.gl.glMaterialfv(1032, 4609, _material, 4);
		G.gl.glMaterialfv(1032, 4610, _material, 8);
		G.gl.glMaterialfv(1032, 5633, _material, 12);
	}

	@Override // com.bithack.apparatus.objects.FreeObject
	public void set_layer() {
	}

	@Override // com.bithack.apparatus.objects.GrabableObject
	public void reshape() {
		init();
		disconnect_all();
		this.sensors = new Fixture[sensor_pos.length];
		if (this.body != null) {
			this.world.destroyBody(this.body);
		}
		this.body = ObjectFactory.create_rectangular_body(this.world, 1.0f, 0.6f, 1.0f, 0.2f, 0.1f);
		this.f = this.body.getFixtureList().get(0);
		this.body.setUserData(this);
		create_sensors();
	}

	public Connection get_available_socket(Vector2 pos, int o_id) {
		Connection nearest = null;
		float lastdist = 1.0E8f;
		for (int x = 0; x < this.connections.length; x++) {
			if (this.connections[x].available || this.connections[x].cableend_id == o_id) {
				float dist = this.body.getWorldPoint(this.connections[x].pos).dst(pos);
				if (dist < lastdist) {
					lastdist = dist;
					nearest = this.connections[x];
				}
			}
		}
		return nearest;
	}

	public Connection _get_available_socket(PanelCableEnd c) {
		return get_available_socket(c.body.getPosition(), c.__unique_id);
	}

	@Override // com.bithack.apparatus.objects.GrabableObject
	public void play() {
		super.play();
		for (int x = 0; x < this.connections.length; x++) {
			if (!this.connections[x].available) {
				on_widget_value_change(x, -10.0f);
				this.widgets.enable(this.connections[x].widget);
				this.connections[x].play();
			} else {
				this.widgets.disable(this.connections[x].widget);
			}
		}
	}

	@Override // com.bithack.apparatus.ui.WidgetValueCallback
	public void on_widget_value_change(int id, float value) {
		int i = 1;
		Connection conn = this.connections[id];
		if (conn.type == 0) {
			if (value == -10.0f) {
				value = -1.0f;
			}
			PanelCable c = conn.cable;
			if (c != null) {
				Battery b = c.get_battery();
				if (b != null) {
					b.set_output((value + 1.0f) / 2.0f);
					return;
				}
				RocketEngine e = c.get_rengine();
				if (e != null) {
					e.set_output((value + 1.0f) / 2.0f);
					return;
				}
				Hub h = c.get_hub();
				if (h != null) {
					h.panel_output = (value + 1.0f) / 2.0f;
					h.update();
				}
			}
		} else if (conn.type == 2 || conn.type == 1) {
			if (value == -10.0f) {
				value = 0.0f;
			}
			PanelCable c2 = conn.cable;
			if (c2 != null) {
				Battery b2 = c2.get_battery();
				if (b2 == null) {
					RocketEngine e2 = c2.get_rengine();
					if (e2 == null) {
						Hub h2 = c2.get_hub();
						if (h2 != null) {
							if (value == 0.0f) {
								i = 0;
							}
							h2.panel_output = (float) i;
							h2.update();
						}
					} else if (value == 0.0f) {
						e2.set_output(0.0f);
					} else {
						e2.set_output(1.0f);
					}
				} else if (value == 0.0f) {
					b2.set_output(0.0f);
				} else {
					b2.set_output(1.0f);
				}
			}
		}
	}

	@Override // com.bithack.apparatus.objects.GrabableObject, com.bithack.apparatus.objects.BaseObject
	public void dispose(World world2) {
		for (int x = 0; x < this.connections.length; x++) {
			if (!this.connections[x].available) {
				this.connections[x].cableend.detach();
			}
		}
		super.dispose(world2);
	}

	public Widget find(int pos) {
		for (int x = 0; x < this.connections.length; x++) {
			if (!this.connections[x].available && this.connections[x].pos_type == pos) {
				return this.connections[x].widget;
			}
		}
		return null;
	}

	@Override // com.bithack.apparatus.objects.GrabableObject
	public void tja_translate(float x, float y) {
		translate(x, y);
	}
}
