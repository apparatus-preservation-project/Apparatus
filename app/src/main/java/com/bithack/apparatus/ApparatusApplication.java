package com.bithack.apparatus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.bithack.apparatus.ApparatusApp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ApparatusApplication extends AndroidApplication implements ApparatusApp.Backend {
	protected static final int AUTOSAVE_CHALLENGE_DIALOG = 22;
	protected static final int AUTOSAVE_DIALOG = 21;
	public static final int BG_CHOOSER = 23;
	protected static final int CONTROLS_DIALOG = 18;
	protected static final int GRAPHICS_DIALOG = 16;
	protected static final int INFO_DIALOG = 15;
	private static final int INGAME_BACK_COMMUNITY_DIALOG = 10;
	protected static final int INGAME_BACK_DIALOG = 8;
	static final int INGAME_MENU = 0;
	protected static final int INGAME_SANDBOX_BACK_DIALOG = 7;
	static final int INGAME_SANDBOX_MENU = 1;
	public static final int LOADING_DIALOG = 9;
	public static final int LOGIN_DIALOG = 11;
	public static final int OPEN_DIALOG = 6;
	public static final int PACK_CHOOSER = 24;
	protected static final int PHYSICS_DIALOG = 19;
	public static final int PLAY_OPTIONS_DIALOG = 4;
	public static final int PUBLISHING_DIALOG = 3;
	static final int PUBLISH_DIALOG = 2;
	public static final int REGISTERING_DIALOG = 13;
	public static final int REGISTER_DIALOG = 12;
	public static final int SANDBOX_INFO = 14;
	public static final int SAVE_DIALOG = 5;
	protected static final int SETTINGS_DIALOG = 17;
	protected static final int WELCOME_DIALOG = 20;
	public static String changelog = "Thanks for downloading the unofficial Apparatus 1.2.2 update! This update brings various quality-of-life improvements to Apparatus.\n\nWhat's new in version 1.2.2 Beta 1?\n* Restored music and some sounds to those pre-Christmas update\n* Initial support for apparatus-web\n* Removed Principia Google Play link\n* Removed Facebook integration\n* Various bugfixes and tweaks, general maintenance to provide a more pleasant experience";
	private int autosave_id;
	private ControlsDialog controls_dialog;
	private GraphicsDialog graphics_dialog;
	private Dialog info_dialog = null;
	protected String msg;
	private PhysicsDialog physics_dialog;
	private PublishDialog publish_dialog;
	private SaveDialog save_dialog;
	ApparatusApplication self;
	String version = "1.2.2 Beta 1";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.self = this;
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.stencil = 1;
		config.depth = 16;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useGL20 = false;
		config.useWakelock = false;
		ApparatusApp.backend = this;
		Settings.set_adapter(new AndroidSettingsAdapter(this));
		initialize(new ApparatusApp(), config);
		Game.fix_bottombar = false;
		if ((getIntent() == null || getIntent().getScheme() == null) && !Settings.get("welcome").equals(this.version)) {
			open_welcome();
			Settings.set("welcome", this.version);
		}
		Settings.set("c_url", "");
		handle_intent(getIntent());
	}

	private void handle_intent(Intent i) {
		int id;
		Gdx.app.log("apparatus", "handle intent");
		if (i != null) {
			int id2 = i.getIntExtra("id", -1);
			if (id2 != -1) {
				new LoadCommunityLevelTask().execute(new Integer(id2));
			} else if (i.getScheme() != null && i.getScheme().equals("apparatus")) {
				try {
					id = Integer.parseInt(i.getData().getHost());
				} catch (Exception e) {
					id = -1;
				}
				if (id > 0) {
					new LoadCommunityLevelTask().execute(new Integer(id));
					return;
				}
				Settings.msg(L.get("invalid_id"));
			}
		}
	}

	public void onNewIntent(Intent i) {
		super.onNewIntent(i);
		handle_intent(i);
	}

	@Override // com.badlogic.gdx.backends.android.AndroidApplication
	public void onPause() {
		Gdx.app.log("onPause()", "onPause");
		run_on_gl_thread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass1 */

			public void run() {
				if (ApparatusApp.current == ApparatusApp.game) {
					if (Game.sandbox && Game.mode != 3) {
						Gdx.app.log("apparatus", "Autosaving sandbox!");
						ApparatusApp.game.autosave();
					}
					if (!Game.sandbox) {
						if (Game.mode == 3) {
							ApparatusApp.game.pause_world();
						}
						Gdx.app.log("apparatus", "Autosaving challenge!");
						ApparatusApp.game.autosave_challenge();
					}
				}
			}
		});
		SoundManager.stop_music();
		super.onPause();
	}

	@Override // com.badlogic.gdx.backends.android.AndroidApplication
	public void onResume() {
		Gdx.app.log("onResume()", "onResume");
		super.onResume();
		SoundManager.play_music();
	}

	@Override // com.badlogic.gdx.backends.android.AndroidApplication
	public void onDestroy() {
		Gdx.app.log("onDestroy()", "onDestroy");
		Settings.save();
		super.onDestroy();
		System.runFinalizersOnExit(true);
		System.exit(0);
	}

	public void run_on_gl_thread(Runnable r) {
		ApparatusApp.schedule(r);
	}

	/* access modifiers changed from: protected */
	public Dialog onCreateDialog(int id) {
		Dialog ret = null;
		switch (id) {
			case 0:
				AlertDialog.Builder bld = new AlertDialog.Builder(this);
				CharSequence[] items = {L.get("load_saved_solution"), L.get("mainmenu"), L.get("levelselect"), L.get("graphicssettings"), L.get("controlssettings"), L.get("physicssettings"), L.get("backtogame")};
				bld.setTitle(L.get("paused"));
				bld.setItems(items, new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass3 */

					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								final File f = new File(String.valueOf(Gdx.files.getExternalStoragePath()) + "/ApparatusLevels/" + (".lvl" + ApparatusApp.game.level_n + (ApparatusApp.game.level_category != 0 ? "_" + ApparatusApp.game.level_category : "")) + ".jar");
								final int n = ApparatusApp.game.level_n;
								if (!f.exists()) {
									Settings.msg(L.get("no_saved_solution_found"));
									break;
								} else {
									ApparatusApplication.this.run_on_gl_thread(new Runnable() {
										/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass3.AnonymousClass1 */

										public void run() {
											ApparatusApp.game.open_solution(f, LevelMenu.category, n);
											Game.level_type = 1;
											ApparatusApp.game.level_n = n;
											Game.sandbox = false;
											Game.from_community = false;
											Game.from_sandbox = false;
										}
									});
									break;
								}
							case 1:
								ApparatusApplication.this.run_on_gl_thread(new Runnable() {
									/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass3.AnonymousClass2 */

									public void run() {
										ApparatusApp.instance.open_mainmenu();
									}
								});
								break;
							case 2:
								ApparatusApplication.this.run_on_gl_thread(new Runnable() {
									/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass3.AnonymousClass3 */

									public void run() {
										ApparatusApp.instance.open_levelmenu();
									}
								});
								break;
							case 3:
								ApparatusApplication.this.showDialog(16);
								break;
							case 4:
								ApparatusApplication.this.showDialog(18);
								break;
							case 5:
								ApparatusApplication.this.showDialog(19);
								break;
						}
						dialog.dismiss();
					}
				});
				ret = bld.create();
				break;
			case 1:
				AlertDialog.Builder bld2 = new AlertDialog.Builder(this);
				CharSequence[] sbitems = {L.get("new_apparatus"), L.get("new_challenge"), L.get("set_background"), L.get("publish_as_community"), L.get("save"), String.valueOf(L.get("open")) + "...", L.get("graphicssettings"), L.get("controlssettings"), L.get("physicssettings"), L.get("backtomainmenu")};
				bld2.setTitle(L.get("menu"));
				bld2.setItems(sbitems, new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass4 */

					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								ApparatusApplication.this.run_on_gl_thread(new Runnable() {
									/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass4.AnonymousClass1 */

									public void run() {
										ApparatusApp.game.create_level(0);
									}
								});
								break;
							case 1:
								ApparatusApplication.this.run_on_gl_thread(new Runnable() {
									/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass4.AnonymousClass2 */

									public void run() {
										ApparatusApp.game.create_level(1);
									}
								});
								break;
							case 2:
								ApparatusApplication.this.self.showDialog(23);
								break;
							case 3:
								ApparatusApplication.this.run_on_gl_thread(new Runnable() {
									/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass4.AnonymousClass3 */

									public void run() {
										if (Game.mode == 3) {
											ApparatusApp.game.pause_world();
										}
										ApparatusApp.game.end_challenge_testing();
										ApparatusApp.game.set_mode(4);
									}
								});
								String token = Settings.get("community-token");
								if (token != null && !token.equals("")) {
									ApparatusApplication.this.self.showDialog(2);
									break;
								} else {
									Toast.makeText(ApparatusApplication.this.self, L.get("log_in_required"), 0).show();
									ApparatusApplication.this.self.showDialog(11);
									break;
								}
							case 4:
								ApparatusApplication.this.run_on_gl_thread(new Runnable() {
									/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass4.AnonymousClass4 */

									public void run() {
										if (Game.mode == 3) {
											ApparatusApp.game.pause_world();
											ApparatusApp.game.set_mode(4);
										}
									}
								});
								ApparatusApplication.this.showDialog(5);
								break;
							case 5:
								ApparatusApplication.this.open_level_list();
								break;
							case 6:
								ApparatusApplication.this.showDialog(16);
								break;
							case 7:
								ApparatusApplication.this.showDialog(18);
								break;
							case 8:
								ApparatusApplication.this.showDialog(19);
								break;
							case 9:
								ApparatusApplication.this.run_on_gl_thread(new Runnable() {
									/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass4.AnonymousClass5 */

									public void run() {
										ApparatusApp.instance.open_mainmenu();
									}
								});
								break;
						}
						dialog.dismiss();
					}
				});
				ret = bld2.create();
				break;
			case 2:
				PublishDialog publishDialog = new PublishDialog(this);
				this.publish_dialog = publishDialog;
				ret = publishDialog.get_dialog();
				break;
			case 3:
				ret = ProgressDialog.show(this, "", L.get("publishinglevel"), true, false);
				break;
			case 4:
				AlertDialog.Builder bld3 = new AlertDialog.Builder(this);
				bld3.setMessage(L.get("play_challenge")).setCancelable(true).setPositiveButton(L.get("testplay"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass9 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass9.AnonymousClass1 */

							public void run() {
								ApparatusApp.game.begin_challenge_testing();
							}
						});
					}
				}).setNegativeButton(L.get("simulate"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass10 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass10.AnonymousClass1 */

							public void run() {
								ApparatusApp.game.resume_world();
							}
						});
					}
				});
				ret = bld3.create();
				break;
			case 5:
				SaveDialog saveDialog = new SaveDialog(this);
				this.save_dialog = saveDialog;
				ret = saveDialog.get_dialog();
				break;
			case 6:
				ret = new OpenDialog(this).get_dialog();
				break;
			case 7:
				AlertDialog.Builder bld4 = new AlertDialog.Builder(this);
				bld4.setMessage(L.get("save_changes_to_level")).setCancelable(true).setPositiveButton(L.get("yes"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass11 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass11.AnonymousClass1 */

							public void run() {
								if (ApparatusApp.game.level_filename == null) {
									ApparatusApp.backend.open_save_dialog();
								} else {
									ApparatusApp.game.save();
								}
								ApparatusApp.instance.open_mainmenu();
								FileHandle h = Gdx.files.getFileHandle("/ApparatusLevels/.autosave.jar", Files.FileType.External);
								if (h.exists()) {
									h.delete();
								}
							}
						});
					}
				}).setNegativeButton(L.get("no"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass12 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass12.AnonymousClass1 */

							public void run() {
								ApparatusApp.instance.open_mainmenu();
								FileHandle h = Gdx.files.getFileHandle("/ApparatusLevels/.autosave.jar", Files.FileType.External);
								if (h.exists()) {
									h.delete();
								}
							}
						});
					}
				});
				ret = bld4.create();
				break;
			case 8:
				AlertDialog.Builder bld5 = new AlertDialog.Builder(this);
				bld5.setMessage(L.get("exit_game_level_menu")).setCancelable(true).setPositiveButton(L.get("yes"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass16 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass16.AnonymousClass1 */

							public void run() {
								ApparatusApp.instance.open_levelmenu();
							}
						});
					}
				}).setNegativeButton(L.get("cancel"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass17 */

					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ret = bld5.create();
				break;
			case 9:
				ret = ProgressDialog.show(this, "", L.get("loading"), true, false);
				break;
			case 10:
				AlertDialog.Builder bld6 = new AlertDialog.Builder(this);
				bld6.setMessage(L.get("exit_level")).setCancelable(true).setPositiveButton(L.get("yes_back_community"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass13 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApp.backend.open_community();
					}
				}).setNeutralButton(L.get("yes_back_main_menu"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass14 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass14.AnonymousClass1 */

							public void run() {
								ApparatusApp.instance.open_mainmenu();
							}
						});
					}
				}).setNegativeButton(L.get("cancel"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass15 */

					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ret = bld6.create();
				break;
			case 11:
				ret = new LoginDialog(this).get_dialog();
				break;
			case 12:
				ret = new RegisterDialog(this).get_dialog();
				break;
			case 13:
				ret = ProgressDialog.show(this, "", L.get("registeringaccount"), true, false);
				break;
			case 14:
				AlertDialog.Builder bld7 = new AlertDialog.Builder(this);
				bld7.setTitle(L.get("sandbox_types"));
				bld7.setMessage(L.get("sandbox_types_txt")).setCancelable(true).setPositiveButton(L.get("ok"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass18 */

					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ret = bld7.create();
				break;
			case 15:
				AlertDialog.Builder bld8 = new AlertDialog.Builder(this);
				bld8.setTitle(L.get("help"));
				bld8.setMessage(this.msg).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass21 */

					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ret = bld8.create();
				this.info_dialog = ret;
				break;
			case 16:
				GraphicsDialog graphicsDialog = new GraphicsDialog(this);
				this.graphics_dialog = graphicsDialog;
				ret = graphicsDialog.get_dialog();
				break;
			case 17:
				AlertDialog.Builder bld9 = new AlertDialog.Builder(this);
				CharSequence[] sitems = {L.get("graphics"), L.get("input"), L.get("physics")};
				bld9.setTitle(L.get("settings"));
				bld9.setItems(sitems, new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass2 */

					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								ApparatusApplication.this.showDialog(16);
								break;
							case 1:
								ApparatusApplication.this.showDialog(18);
								break;
							case 2:
								ApparatusApplication.this.showDialog(19);
								break;
						}
						dialog.dismiss();
					}
				});
				ret = bld9.create();
				break;
			case 18:
				ControlsDialog controlsDialog = new ControlsDialog(this);
				this.controls_dialog = controlsDialog;
				return controlsDialog.get_dialog();
			case 19:
				PhysicsDialog physicsDialog = new PhysicsDialog(this);
				this.physics_dialog = physicsDialog;
				ret = physicsDialog.get_dialog();
				break;
			case 20:
				AlertDialog.Builder bld10 = new AlertDialog.Builder(this);
				bld10.setTitle("Thanks for downloading Apparatus!");
				bld10.setMessage(changelog).setCancelable(true).setPositiveButton("Close", new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass22 */

					public void onClick(DialogInterface dialog, int which) {
					}
				});
				ret = bld10.create();
				this.info_dialog = ret;
				break;
			case 21:
				AlertDialog.Builder bld11 = new AlertDialog.Builder(this);
				bld11.setTitle(L.get("autosave_detected"));
				bld11.setMessage(L.get("autosave_detected_txt")).setCancelable(true).setPositiveButton(L.get("recover"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass5 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass5.AnonymousClass1 */

							public void run() {
								Game.sandbox = true;
								ApparatusApp.game.open(".autosave");
								ApparatusApp.instance.play();
								SoundManager.play_startlevel();
							}
						});
					}
				}).setNegativeButton(L.get("delete"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass6 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass6.AnonymousClass1 */

							public void run() {
								FileHandle h = Gdx.files.getFileHandle("/ApparatusLevels/.autosave.jar", Files.FileType.External);
								if (h.exists()) {
									h.delete();
								}
								SoundManager.play_startlevel();
							}
						});
					}
				});
				ret = bld11.create();
				break;
			case 22:
				Gdx.app.log("MSG", "Autosave challenge dialog opened: " + ApparatusApp.game.level_n);
				AlertDialog.Builder bld12 = new AlertDialog.Builder(this);
				bld12.setTitle(L.get("autosave_detected"));
				bld12.setMessage(L.get("autosave_detected_txt")).setCancelable(true).setPositiveButton(L.get("recover"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass7 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass7.AnonymousClass1 */

							public void run() {
								ApparatusApp.instance.play(1, Game.autosave_id);
								ApparatusApp.instance.load_autosave = true;
								SoundManager.play_startlevel();
							}
						});
					}
				}).setNegativeButton(L.get("delete_play"), new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass8 */

					public void onClick(DialogInterface dialog, int which) {
						ApparatusApplication.this.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass8.AnonymousClass1 */

							public void run() {
								int n = Game.autosave_id;
								FileHandle h = Gdx.files.getFileHandle("/ApparatusLevels/.autosave_" + n + (LevelMenu.category == 2 ? "_2" : "") + ".jar", Files.FileType.External);
								if (h.exists()) {
									h.delete();
								}
								ApparatusApp.instance.play(1, n);
								SoundManager.play_startlevel();
							}
						});
					}
				});
				ret = bld12.create();
				break;
			case 23:
				AlertDialog.Builder bld13 = new AlertDialog.Builder(this);
				bld13.setItems(new CharSequence[]{"Default", "BG 1 (Butterfly 1)", "BG 2 (Stripes 1)", "BG 3 (Flowers)", "BG 4 (Butterfly 2)", "BG 5 (Stripes 2)", "BG 6 (Cats)", "BG 7 (Sun)", "BG 8 (Stripes 3)", "BG 9 (Trees)"}, new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass19 */

					public void onClick(DialogInterface dialog, final int which) {
						ApparatusApplication.this.self.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass19.AnonymousClass1 */

							public void run() {
								ApparatusApp.game.set_bg(which);
							}
						});
					}
				});
				bld13.setTitle(L.get("set_background")).setCancelable(true);
				ret = bld13.create();
				break;
			case 24:
				AlertDialog.Builder bld14 = new AlertDialog.Builder(this);
				bld14.setItems(new CharSequence[]{"Main challenges", "Christmas level pack"}, new DialogInterface.OnClickListener() {
					/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass20 */

					public void onClick(DialogInterface dialog, final int which) {
						ApparatusApplication.this.self.run_on_gl_thread(new Runnable() {
							/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass20.AnonymousClass1 */

							public void run() {
								if (which == 0) {
									LevelMenu.category = 1;
								} else {
									LevelMenu.category = 2;
								}
								ApparatusApp.instance.open_levelmenu();
							}
						});
					}
				});
				bld14.setTitle("Select a level pack").setCancelable(true);
				ret = bld14.create();
				break;
		}
		if (ret == null) {
			return null;
		}
		ret.getWindow().setFlags(1024, 1024);
		return ret;
	}

	public void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
			case 2:
				if (this.publish_dialog != null) {
					this.publish_dialog.prepare();
					return;
				}
				return;
			case 5:
				if (this.save_dialog != null) {
					this.save_dialog.prepare();
					return;
				}
				return;
			case 16:
				if (this.graphics_dialog != null) {
					this.graphics_dialog.prepare();
					return;
				}
				return;
			case 18:
				if (this.controls_dialog != null) {
					this.controls_dialog.prepare();
					return;
				}
				return;
			default:
				return;
		}
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_community() {
		Intent i = new Intent(this, CommunityActivity.class);
		i.setFlags(65536);
		startActivity(i);
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_save_dialog() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass23 */

			public void run() {
				ApparatusApplication.this.showDialog(5);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_ingame_menu() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass24 */

			public void run() {
				ApparatusApplication.this.showDialog(0);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_ingame_sandbox_menu() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass25 */

			public void run() {
				ApparatusApplication.this.showDialog(1);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_sandbox_play_options() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass26 */

			public void run() {
				ApparatusApplication.this.showDialog(4);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_level_list() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass27 */

			public void run() {
				try {
					ApparatusApplication.this.removeDialog(6);
				} catch (Exception e) {
				}
				ApparatusApplication.this.showDialog(6);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_ingame_sandbox_back_menu() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass28 */

			public void run() {
				ApparatusApplication.this.showDialog(7);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_ingame_back_menu() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass29 */

			public void run() {
				ApparatusApplication.this.showDialog(8);
			}
		});
	}

	/* access modifiers changed from: protected */
	public class LoadCommunityLevelTask extends AsyncTask<Integer, String, String> {
		public byte[] level_data = null;
		public int status = 0;

		protected LoadCommunityLevelTask() {
		}

		/* access modifiers changed from: protected */
		/* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
			java.lang.Thread.sleep(1000);
			com.badlogic.gdx.Gdx.app.log("Community Level Loader", "Waiting for GL thread to get ready...");
		 */
		public String doInBackground(Integer... params) {
			final File f = Settings.get_tmp_file();
			try {
				Gdx.app.log("Community Level Loader", "Downloading http://apparatus-web.tk/levels/" + params[0] + ".jar");
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(new HttpGet("http://apparatus-web.tk/levels/" + params[0] + ".jar"));
				OutputStream o = new FileOutputStream(f);
				InputStream i = response.getEntity().getContent();
				byte[] buf = new byte[4096];
				while (true) {
					int read = i.read(buf);
					if (read == -1) {
						break;
					}
					o.write(buf, 0, read);
				}
				o.close();
				client.getConnectionManager().shutdown();
			} catch (Exception e) {
				this.status = 2;
			}
			Gdx.app.log("Community Level Loader", "Level downloaded");
			if (this.status != 2) {
				ApparatusApplication.this.run_on_gl_thread(new Runnable() {
					/* class com.bithack.apparatus.ApparatusApplication.LoadCommunityLevelTask.AnonymousClass1 */

					public void run() {
						int _s;
						Gdx.app.log("Community Level Loader", "Preparing game");
						Game game = ApparatusApp.game;
						Game.sandbox = false;
						Game.from_community = true;
						try {
							game.open(f);
							_s = 1;
						} catch (Exception e) {
							_s = 2;
						}
						synchronized (this) {
							if (_s == 1) {
								//this.status = 1;
							} else {
								//this.status = 2;
							}
						}
					}
				});
				int x = 0;
				while (true) {
					if (x >= 10000) {
						break;
					}
					synchronized (this) {
						if (this.status != 0) {
						}
					}
					x++;
				}
			}
			if (this.status != 1) {
				return L.get("error_occured_while_loading");
			}
			ApparatusApplication.this.run_on_gl_thread(new Runnable() {
				/* class com.bithack.apparatus.ApparatusApplication.LoadCommunityLevelTask.AnonymousClass2 */

				public void run() {
					ApparatusApp.instance.play();
					ApparatusApp.instance.fade = 0.0f;
				}
			});
			return "";
		}

		/* access modifiers changed from: protected */
		public void onPreExecute() {
			if (ApparatusApp.instance != null) {
				ApparatusApp.instance.fading = true;
				ApparatusApp.instance.fade = 0.0f;
				ApparatusApp.instance.fade_dir = -2;
			}
		}

		/* access modifiers changed from: protected */
		public void onPostExecute(String result) {
			if (!result.equals("")) {
				Toast.makeText(ApparatusApplication.this, result, 0).show();
			}
		}
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_autosave_dialog() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass30 */

			public void run() {
				ApparatusApplication.this.showDialog(21);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_autosave_challenge_dialog() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass31 */

			public void run() {
				ApparatusApplication.this.showDialog(22);
			}
		});
	}

	public void open_welcome() {
		showDialog(20);
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_infobox(String msg2) {
		this.msg = msg2;
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass32 */

			public void run() {
				if (ApparatusApplication.this.info_dialog != null) {
					ApparatusApplication.this.removeDialog(15);
				}
				ApparatusApplication.this.showDialog(15);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_sandbox_info() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass33 */

			public void run() {
				ApparatusApplication.this.showDialog(14);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_ingame_back_community_menu() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass34 */

			public void run() {
				ApparatusApplication.this.showDialog(10);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_market() {
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_settings() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass35 */

			public void run() {
				ApparatusApplication.this.showDialog(17);
			}
		});
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_twitter() {
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.twitter.com/bithack")));
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_youtube() {
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.youtube.com/user/bithacked")));
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_facebook() {
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.facebook.com/pages/Bithack-AB/213060255398337")));
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_beginner_help_videos() {
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.youtube.com/playlist?list=PLA3A145BCBAEA502D")));
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void exit2() {
		Gdx.app.log("apparatus", "exit called in backend");
		Settings.save();
		System.runFinalizersOnExit(true);
		System.exit(0);
	}

	@Override // com.bithack.apparatus.ApparatusApp.Backend
	public void open_packchooser() {
		runOnUiThread(new Runnable() {
			/* class com.bithack.apparatus.ApparatusApplication.AnonymousClass36 */

			public void run() {
				ApparatusApplication.this.showDialog(24);
			}
		});
	}
}
