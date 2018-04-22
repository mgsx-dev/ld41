package net.mgsx.ld41.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.mgsx.ld41.LD41;
import net.mgsx.ld41.assets.GameAssets;
import net.mgsx.ld41.utils.StageScreen;

public class MenuScreen extends StageScreen
{

	private Label labelStart;

	public MenuScreen() 
	{
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		
		Skin skin = GameAssets.i().skin;
		
		Table main = new Table(skin);
		
		labelStart = new Label("Press any key to start", skin);
		
		labelStart.addAction(Actions.forever(Actions.sequence(Actions.alpha(.5f, 1), Actions.alpha(1))));
		labelStart.setColor(Color.BLACK);
		
		main.add(labelStart).expand().top().padTop(150).row();
		main.setBackground(new TextureRegionDrawable(new TextureRegion(GameAssets.i().textureMenu)));
		
		// TODO add image
		
		// TODO set background ....
		
		root.add(main).expand().fill();
	}
	
	@Override
	public void render(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
			LD41.i().gameStart();
		}
		Gdx.gl.glClearColor(.5f, .8f, 1f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render(delta);
	}
}
