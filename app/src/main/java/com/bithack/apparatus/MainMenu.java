package com.bithack.apparatus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.bithack.apparatus.graphics.G;
import com.bithack.apparatus.graphics.MiscRenderer;
import com.bithack.apparatus.graphics.TextureFactory;

public class MainMenu extends Screen implements InputProcessor {
	private final Texture bgtex = TextureFactory.load("data/apparatusmenu.png");
	final ApparatusApp tp;

	public MainMenu(ApparatusApp tp2) {
		this.tp = tp2;
	}

	@Override // com.bithack.apparatus.Screen
	public int tick() {
		return 0;
	}

	@Override // com.bithack.apparatus.Screen
	public void render() {
		G.gl.glMatrixMode(5889);
		G.gl.glLoadIdentity();
		G.gl.glMatrixMode(5888);
		G.gl.glLoadIdentity();
		G.gl.glDisable(2929);
		G.gl.glDepthMask(false);
		G.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		G.gl.glEnable(3553);
		this.bgtex.bind();
		MiscRenderer.draw_textured_box();
		G.gl.glEnable(3042);
		G.gl.glBlendFunc(770, 771);
		LevelMenu.lchecktex.bind();
		if (Game.enable_sound) {
			G.gl.glPushMatrix();
			G.gl.glTranslatef(-0.84749997f, -0.8025f, 0.0f);
			G.gl.glScalef(0.04f, 0.06666667f, 1.0f);
			MiscRenderer.draw_textured_box();
			G.gl.glPopMatrix();
		}
		if (Game.enable_music) {
			G.gl.glPushMatrix();
			G.gl.glTranslatef(-0.46f, -0.8025f, 0.0f);
			G.gl.glScalef(0.04f, 0.06666667f, 1.0f);
			MiscRenderer.draw_textured_box();
			G.gl.glPopMatrix();
		}
		G.gl.glDepthMask(true);
		G.gl.glEnable(2929);
	}

	@Override // com.bithack.apparatus.Screen
	public void resume() {
		Gdx.input.setInputProcessor(this);
		SoundManager.play_music();
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
	public boolean keyDown(int arg0) {
		if (arg0 != 4) {
			return false;
		}
		Settings.save();
		ApparatusApp.backend.exit2();
		return false;
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
		boolean z;
		boolean z2;
		float px = ((float) x) / ((float) G.realwidth);
		float py = ((float) y) / ((float) G.realheight);
		Gdx.app.log("px,py", String.valueOf(px) + " " + py + " " + G.realwidth + " " + G.realheight);
		if (px > 0.75f && py < 0.11f) {
			if (px > 0.93f) {
				ApparatusApp.backend.open_twitter();
			} else if (px > 0.8f) {
				ApparatusApp.backend.open_youtube();
			} else {
				ApparatusApp.backend.open_facebook();
			}
		}
		if (py <= 0.26f || py >= 0.53f) {
			if (py > 0.53f && py < 0.73f) {
				if (px > 0.04f && px < 0.34f) {
					this.tp.open_sandbox();
				} else if (px > 0.41f && px < 0.76f) {
					ApparatusApp.backend.open_settings();
				}
			}
		} else if (px > 0.03f && px < 0.34f) {
			ApparatusApp.backend.open_packchooser();
		} else if (px > 0.34f && px < 0.8f) {
			ApparatusApp.backend.open_community();
		}
		if (px > 0.04f && px < 0.12f && py > 0.81f) {
			if (Game.enable_sound) {
				z2 = false;
			} else {
				z2 = true;
			}
			Game.enable_sound = z2;
			if (Game.enable_sound) {
				SoundManager.enable_sound();
			} else {
				SoundManager.disable_sound();
			}
		} else if (px > 0.21f && px < 0.3f && py > 0.81f) {
			if (Game.enable_music) {
				z = false;
			} else {
				z = true;
			}
			Game.enable_music = z;
			if (Game.enable_music) {
				SoundManager.enable_music();
			} else {
				SoundManager.disable_music();
			}
		}
		if (px > 0.82f && py > 0.82f) {
			Settings.save();
			ApparatusApp.backend.exit2();
		}
		Gdx.input.setInputProcessor(this);
		return true;
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override // com.badlogic.gdx.InputProcessor
	public boolean touchUp(int x, int y, int pointer, int btn) {
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
