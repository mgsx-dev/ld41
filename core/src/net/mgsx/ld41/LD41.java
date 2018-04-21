package net.mgsx.ld41;

import com.badlogic.gdx.Game;

import net.mgsx.ld41.screen.GameScreen;

public class LD41 extends Game {
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	@Override
	public void create () {
		setScreen(new GameScreen());
	}

	
}
