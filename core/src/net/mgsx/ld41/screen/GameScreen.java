package net.mgsx.ld41.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.ld41.parts.GameWorld;

public class GameScreen extends ScreenAdapter
{
	private GameWorld world;
	
	public GameScreen() {
		world = new GameWorld();
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		world.update(delta);
		world.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		world.resize(width, height);
	}
}
