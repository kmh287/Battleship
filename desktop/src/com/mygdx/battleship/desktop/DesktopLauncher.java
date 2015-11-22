package com.mygdx.battleship.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.battleship.BattleshipRoot;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1000;
		config.height = 950;
		config.resizable  = false;
		config.fullscreen = false; // RETINA MACS DO NOT SUPPORT FULLSCREEN (LWJGL Bug)
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.backgroundFPS = 0;
		config.x = 0;
		config.y = 0;
		new LwjglApplication(new BattleshipRoot(), config);
	}
}
