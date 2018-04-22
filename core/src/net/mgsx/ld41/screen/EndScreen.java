package net.mgsx.ld41.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

import net.mgsx.ld41.LD41;
import net.mgsx.ld41.assets.GameAssets;
import net.mgsx.ld41.parts.GameWorld;
import net.mgsx.ld41.utils.FormatUtils;
import net.mgsx.ld41.utils.StageScreen;

public class EndScreen extends StageScreen
{
	public EndScreen(GameWorld world) {
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		
		Skin skin = GameAssets.i().skin;
		
		Table full = new Table();
		
		Table main = new Table(skin);
		main.defaults().padBottom(10);
		
		main.add("SCORE").padBottom(40).row();
		
		Image img = new Image(GameAssets.i().textureEnd);
		img.setScaling(Scaling.none);
		
		main.add("Level " + (world.getLevel() + 1)).row();
		main.add("Distance " + FormatUtils.distanceToString(world.distanceTile)).row();
		main.add("Blocks " + world.usedBlocks).row();
		
		Label label = new Label("Press any key", skin);
		label.setColor(Color.BLACK);
		main.add(label).padTop(50).row();
		
		full.add(img);
		full.add(main);
		
		root.add(full).expand().fill();
		
		label.addAction(Actions.forever(Actions.sequence(Actions.alpha(.5f, 1), Actions.alpha(1))));
	}
	
	@Override
	public void render(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
			LD41.i().gameMenu();
		}
		
		Gdx.gl.glClearColor(.5f, .8f, 1f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render(delta);
	}
}
