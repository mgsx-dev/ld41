package net.mgsx.ld41;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = LD41.WIDTH;
		config.height = LD41.HEIGHT;
		new LwjglApplication(new LD41(), config);
	}
}
