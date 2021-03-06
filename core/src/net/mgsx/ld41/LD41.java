package net.mgsx.ld41;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import net.mgsx.ld41.screen.EndScreen;
import net.mgsx.ld41.screen.GameScreen;
import net.mgsx.ld41.screen.MenuScreen;

public class LD41 extends Game {
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	public static final boolean debug = false;
	public static boolean allowPause = false;
	private boolean paused;
	
	public static LD41 i(){
		return (LD41)Gdx.app.getApplicationListener();
	}
	
	@Override
	public void create () {
//		setScreen(new GameScreen());
//		return;
		
		
		if(debug) setScreen(new GameScreen());
		else{
			// TODO transition
			setScreen(new MenuScreen());
		}
	}
	
	@Override
	public void render() {
		if(allowPause && Gdx.input.isKeyJustPressed(Input.Keys.P)){
			paused = !paused;
		}
		if(!paused)
			super.render();
	}


	public void gameOver() {
		if(debug) setScreen(new GameScreen());
		else{
			// TODO transition
			GameScreen gameScreen = (GameScreen)screen;
			setScreen(new EndScreen(gameScreen.world));
		}
	}


	public void gameStart() 
	{
		if(debug) setScreen(new GameScreen());
		else{
			// TODO transition
			setScreen(new GameScreen());
		}
	}


	public void gameMenu() {
		if(debug) setScreen(new MenuScreen());
		else{
			// TODO transition
			setScreen(new MenuScreen());
		}
	}

	
}
