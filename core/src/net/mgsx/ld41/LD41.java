package net.mgsx.ld41;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import net.mgsx.ld41.screen.GameScreen;

public class LD41 extends Game {
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	public static LD41 i(){
		return (LD41)Gdx.app.getApplicationListener();
	}
	
	
	@Override
	public void create () {
		setScreen(new GameScreen());
	}


	public void gameOver() {
		// TODO set game over screen
		setScreen(new GameScreen());
	}

	
}
