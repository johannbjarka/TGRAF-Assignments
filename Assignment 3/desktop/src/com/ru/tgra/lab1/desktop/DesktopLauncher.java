package com.ru.tgra.lab1.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ru.tgra.shapes.MazeGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "MazeGame!"; // or whatever you like
		config.width = 720;  //experiment with
		config.height = 640;  //the window size
		config.x = config.width / 2;
		config.y = 0;
		//config.fullscreen = true;

		new LwjglApplication(new MazeGame(), config);
	}
}
