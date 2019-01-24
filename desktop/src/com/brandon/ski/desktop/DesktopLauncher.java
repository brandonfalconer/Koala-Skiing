package com.brandon.ski.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.brandon.ski.SkiGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Koala Skiing";
		cfg.width = 540;
		cfg.height = 960;
		new LwjglApplication(new SkiGame(), cfg);
	}
}
