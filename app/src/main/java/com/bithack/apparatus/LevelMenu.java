package com.bithack.apparatus;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.bithack.apparatus.graphics.G;
import com.bithack.apparatus.graphics.MiscRenderer;
import com.bithack.apparatus.graphics.TextureFactory;
import java.util.ArrayList;
import java.util.Iterator;

public class LevelMenu extends Screen implements InputProcessor {
	public static int category = 0;
	public static Texture lchecktex;
	public static String lvl_prefix = "l-";
	public static int num_levels = ApparatusApp.num_levels;
	public static int type = 1;
	BodyDef bd;
	private final Texture btntex;
	private final Vector3 cam_vec = new Vector3();
	private final CameraHandler camera_h;
	FixtureDef fd;
	private final Vector3 initial_touch_vec = new Vector3();
	private final ImmediateModeRenderer10 irender = new ImmediateModeRenderer10();
	private ArrayList<Body> level_b = new ArrayList<>();
	private boolean moving = false;
	private int page = 0;
	private final Vector3 touch_vec = new Vector3();
	final ApparatusApp tp;
	private final Texture wintex;
	private World world = null;

	public class Lvl {
		int category;
		public boolean disabled = true;
		int highscore;
		int id;
		int page = 0;

		public Lvl(int cat, int id2) {
			this.category = cat;
			this.id = id2;
		}
	}

	public LevelMenu(ApparatusApp tp2) {
		this.tp = tp2;
		this.btntex = TextureFactory.load("data/btn.png");
		this.wintex = TextureFactory.load("data/levelselect.png");
		lchecktex = TextureFactory.load("data/lcheck.png");
		this.bd = new BodyDef();
		this.bd.type = BodyDef.BodyType.KinematicBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(2.0f, 2.0f);
		this.fd = new FixtureDef();
		this.fd.density = 1.0f;
		this.fd.shape = shape;
		this.camera_h = new CameraHandler(G.cam);
		for (int x = 0; x < ApparatusApp.num_levels; x++) {
			if (!Settings.get("l-0/" + x).equals("1")) {
				Settings.set("l-0/" + x, "");
			}
		}
		for (int x2 = 0; x2 < ApparatusApp.num_interactive_levels; x2++) {
			if (!Settings.get("li-0/" + x2).equals("1")) {
				Settings.set("li-0/" + x2, "");
			}
		}
		for (int x3 = 0; x3 < ApparatusApp.num_christmas_levels; x3++) {
			if (!Settings.get("l2-0/" + x3).equals("1")) {
				Settings.set("l2-0/" + x3, "");
			}
		}
	}

	@Override // com.bithack.apparatus.Screen
	public int tick() {
		this.page = Math.round(this.camera_h.camera_pos.x / 28.0f);
		if (this.camera_h.velocity.len() < 10.0f && !this.moving) {
			this.camera_h.camera_pos.add((((float) (this.page * 28)) - this.camera_h.camera_pos.x) * G.delta * 10.0f, 0.0f, 0.0f);
		}
		this.camera_h.tick();
		return 0;
	}

	@Override // com.bithack.apparatus.Screen
	public void render() {
		G.clear();
		Vector3 cam_pos = G.cam.position;
		if (cam_pos.x < 0.0f) {
			G.cam.position.set(0.0f, cam_pos.y, 0.0f);
		}
		if (((double) cam_pos.x) > 28.0d * Math.floor((double) (((float) num_levels) / 12.0f))) {
			G.cam.position.set(28.0f * ((float) Math.floor((double) (((float) num_levels) / 12.0f))), cam_pos.y, 0.0f);
		}
		if (cam_pos.y < -95.0f) {
			G.cam.position.set(cam_pos.x, -95.0f, 0.0f);
		}
		G.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		G.gl.glMatrixMode(5889);
		G.gl.glLoadIdentity();
		G.gl.glMatrixMode(5888);
		G.gl.glLoadIdentity();
		G.gl.glEnable(3553);
		this.wintex.bind();
		MiscRenderer.draw_textured_box();
		G.cam.apply(G.gl);
		G.gl.glEnable(3042);
		G.gl.glEnable(3553);
		this.btntex.bind();
		Iterator<Body> it = this.level_b.iterator();
		while (it.hasNext()) {
			Body b = it.next();
			G.gl.glPushMatrix();
			G.gl.glTranslatef(b.getPosition().x, b.getPosition().y, 0.0f);
			G.gl.glScalef(2.0f, 2.0f, 1.0f);
			if (((Lvl) b.getUserData()).disabled) {
				G.gl.glColor4f(1.0f, 1.0f, 1.0f, 0.3f);
			} else {
				G.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
			MiscRenderer.draw_textured_box();
			G.gl.glPopMatrix();
		}
		G.gl.glColor4f(0.5f, 1.0f, 0.5f, 1.0f);
		lchecktex.bind();
		Iterator<Body> it2 = this.level_b.iterator();
		while (it2.hasNext()) {
			Body b2 = it2.next();
			if (((Lvl) b2.getUserData()).highscore == 1) {
				G.gl.glPushMatrix();
				G.gl.glTranslatef(b2.getPosition().x + 1.0f, b2.getPosition().y - 1.0f, 0.0f);
				G.gl.glScalef(1.0f, 1.0f, 1.0f);
				MiscRenderer.draw_textured_box();
				G.gl.glPopMatrix();
			}
		}
		G.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		G.batch.setProjectionMatrix(G.cam.combined);
		G.batch.begin();
		Iterator<Body> it3 = this.level_b.iterator();
		while (it3.hasNext()) {
			Body b3 = it3.next();
			Lvl l = (Lvl) b3.getUserData();
			ApparatusApp.font.draw(G.batch, " " + (l.id + 1) + " ", b3.getPosition().x - 0.4f, b3.getPosition().y + 0.8f);
			int i = l.highscore;
		}
		G.batch.end();
		G.gl.glMatrixMode(5889);
		G.gl.glLoadIdentity();
		G.gl.glMatrixMode(5888);
		G.gl.glLoadIdentity();
		G.gl.glEnable(3042);
		this.irender.begin(6);
		this.irender.color(0.0f, 0.0f, 0.0f, 0.0f);
		this.irender.vertex(0.7f, 0.734f, 0.0f);
		this.irender.color(0.0f, 0.0f, 0.0f, 0.0f);
		this.irender.vertex(0.7f, -0.734f, 0.0f);
		this.irender.color(0.0f, 0.0f, 0.0f, 1.0f);
		this.irender.vertex(1.0f, -0.734f, 0.0f);
		this.irender.color(0.0f, 0.0f, 0.0f, 1.0f);
		this.irender.vertex(1.0f, 0.734f, 0.0f);
		this.irender.end();
		this.irender.begin(6);
		this.irender.color(0.0f, 0.0f, 0.0f, 1.0f);
		this.irender.vertex(-1.0f, 0.734f, 0.0f);
		this.irender.color(0.0f, 0.0f, 0.0f, 1.0f);
		this.irender.vertex(-1.0f, -0.734f, 0.0f);
		this.irender.color(0.0f, 0.0f, 0.0f, 0.0f);
		this.irender.vertex(-0.7f, -0.734f, 0.0f);
		this.irender.color(0.0f, 0.0f, 0.0f, 0.0f);
		this.irender.vertex(-0.7f, 0.734f, 0.0f);
		this.irender.end();
		G.gl.glLoadIdentity();
	}

	@Override // com.bithack.apparatus.Screen
	public void resume() {
		Gdx.input.setInputProcessor(this);
		G.set_clear_color(0.0f, 0.0f, 0.0f);
		this.camera_h.load();
		this.camera_h.camera_pos.y = -10.0f;
		ApparatusApp.font.setScale(0.0625f);
		Settings.save();
		if (this.world != null) {
			this.world.dispose();
		}
		this.level_b.clear();
		this.world = new World(new Vector2(0.0f, 0.0f), true);
		if (type != 1) {
			lvl_prefix = "li-";
			num_levels = ApparatusApp.num_interactive_levels;
		} else if (category == 2) {
			lvl_prefix = "l2-";
			num_levels = ApparatusApp.num_christmas_levels;
		} else {
			lvl_prefix = "l-";
			num_levels = ApparatusApp.num_levels;
		}
		if (Gdx.app.getGraphics().getType() != Graphics.GraphicsType.AndroidGL) {
			ApparatusApp.num_completed = 100;
		} else {
			ApparatusApp.num_completed = 0;
		}
		for (int x = 0; x < num_levels; x++) {
			float ox = (float) ((x % 4) * 6);
			float oy = (float) ((x / 4) % 3);
			Body b = this.world.createBody(this.bd);
			this.level_b.add(b);
			Fixture f = b.createFixture(this.fd);
			Lvl ll = new Lvl(0, x);
			ll.page = x / 12;
			if (x - ApparatusApp.num_completed < 3) {
				ll.disabled = false;
			} else {
				ll.disabled = true;
			}
			String val = Settings.get(String.valueOf(lvl_prefix) + 0 + "/" + x);
			if (!val.equals("")) {
				ll.highscore = Integer.parseInt(val);
				ApparatusApp.num_completed++;
			} else {
				ll.highscore = -1;
			}
			f.setUserData(ll);
			b.setUserData(ll);
			b.setTransform(new Vector2((((float) (ll.page * 24)) - 0.46875f) + ox + ((float) (ll.page * 4)), -4.0f - (6.0f * oy)), 0.0f);
		}
	}

	@Override // com.bithack.apparatus.Screen
	public boolean screen_to_world(int x, int y, Vector2 out) {
		return false;
	}

	@Override // com.bithack.apparatus.Screen
	public boolean ready() {
		return false;
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean keyDown(int key) {
		switch (key) {
			case 4:
			case 30:
				this.tp.open_mainmenu();
				return true;
			default:
				return true;
		}
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean keyTyped(char arg0) {
		return false;
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean keyUp(int arg0) {
		return false;
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean touchDown(int x, int y, int pointer, int btn) {
		if (pointer == 0) {
			this.moving = true;
			this.cam_vec.set((float) x, (float) y, 0.0f);
			G.cam.unproject(this.cam_vec);
			this.touch_vec.set(this.cam_vec);
			this.initial_touch_vec.set(this.cam_vec);
		}
		if (x < 64 && y > G.realheight - 100) {
			this.tp.open_mainmenu();
			return false;
		} else if (x <= G.realwidth - 300 || y <= G.realheight - 64) {
			return false;
		} else {
			ApparatusApp.backend.open_beginner_help_videos();
			return false;
		}
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean touchDragged(int x, int y, int pointer) {
		if (pointer != 0) {
			return false;
		}
		this.cam_vec.set((float) x, (float) y, 0.0f);
		G.cam.unproject(this.cam_vec);
		this.camera_h.add_velocity((-(this.cam_vec.x - this.touch_vec.x)) * 14.0f, 0.0f);
		this.touch_vec.set(this.cam_vec);
		return false;
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean touchUp(int x, int y, int pointer, int btn) {
		if (pointer == 0) {
			this.moving = false;
			this.cam_vec.set((float) x, (float) y, 0.0f);
			G.cam.unproject(this.cam_vec);
			if (this.initial_touch_vec.dst(this.cam_vec) < 2.0f) {
				this.world.QueryAABB(new QueryCallback() {
					/* class com.bithack.apparatus.LevelMenu.AnonymousClass1 */

					@Override // com.badlogic.gdx.physics.box2d.QueryCallback
					public boolean reportFixture(Fixture f) {
						Lvl l = (Lvl) f.getUserData();
						if (l.disabled) {
							Settings.msg(L.get("complete_more_levels"));
							return true;
						}
						LevelMenu.this.camera_h.save();
						Game.sandbox = false;
						Gdx.app.log("autosave", "Checking autosave for .autosave_" + l.id);
						if (Gdx.files.getFileHandle("/ApparatusLevels/.autosave_" + l.id + (LevelMenu.category == 2 ? "_2" : "") + ".jar", Files.FileType.External).exists()) {
							Gdx.app.log("autosave", "found autosave!");
							Game.autosave_id = l.id;
							ApparatusApp.backend.open_autosave_challenge_dialog();
							return true;
						}
						Gdx.app.log("autosave", "no autosave found.");
						SoundManager.play_startlevel();
						LevelMenu.this.tp.play(LevelMenu.type, l.id);
						return true;
					}
				}, this.cam_vec.x - 0.01f, this.cam_vec.y - 0.01f, this.cam_vec.x + 0.01f, this.cam_vec.y + 0.01f);
			}
		}
		return false;
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean scrolled(int arg0) {
		return false;
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean mouseMoved(int arg0, int arg1) {
		return false;
	}
}
