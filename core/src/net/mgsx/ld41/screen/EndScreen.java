package net.mgsx.ld41.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.ld41.LD41;
import net.mgsx.ld41.assets.GameAssets;
import net.mgsx.ld41.utils.StageScreen;

public class EndScreen extends StageScreen
{
	public EndScreen() {
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		
		
		Table main = new Table(GameAssets.i().skin);
		main.defaults().padBottom(30);
		
		main.add("GAME OVER").row();
		
		main.add("Level 4").row();
		main.add("Distance 356m").row();
		main.add("Blocks 56").row();
		
		main.add("Press any key").row();
		
		root.add(main);
	}
	
	@Override
	public void render(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
			LD41.i().gameMenu();
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render(delta);
	}
}
