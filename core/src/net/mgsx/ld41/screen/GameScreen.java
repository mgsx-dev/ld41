package net.mgsx.ld41.screen;

import com.badlogic.gdx.ScreenAdapter;

import net.mgsx.ld41.parts.GameWorld;

public class GameScreen extends ScreenAdapter
{
	private GameWorld world;
	
	public GameScreen() {
		world = new GameWorld();
	}
	
	@Override
	public void render(float delta) {
		world.update(delta);
		world.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		world.resize(width, height);
	}
}
