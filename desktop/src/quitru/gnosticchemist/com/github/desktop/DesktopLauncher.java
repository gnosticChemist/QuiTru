package quitru.gnosticchemist.com.github.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import quitru.gnosticchemist.com.github.Model.Starter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width=360;
		config.height=640;
		config.resizable=false;
		new LwjglApplication(new Starter(), config);
	}
}
