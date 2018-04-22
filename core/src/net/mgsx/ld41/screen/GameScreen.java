package net.mgsx.ld41.screen;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.mgsx.ld41.assets.GameAssets;
import net.mgsx.ld41.parts.GameWorld;
import net.mgsx.ld41.utils.StageScreen;

public class GameScreen extends StageScreen
{
	
	public static final int CHERRY_PER_LEVEL = 3;
	
	private GameWorld world;
	
	private int cherryCount;
	private Table cherryTable;
	private Skin skin;
	private Label levelLabel;
	private Label gameOverLabel;
	
	private int level = 0;
	
	public GameScreen() {
		super();
		world = new GameWorld();
		
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		
		skin = GameAssets.i().skin;
		
		Table main = new Table(skin);
		root.add(main).expand().fillX().top();
		
		cherryTable = new Table();
		
		levelLabel = new Label("Level 1", skin);
		
		main.add(levelLabel).expandX().left().padLeft(200);
		main.add(cherryTable);
		

	}
	
	@Override
	public void render(float delta) {
		world.update(delta);
		updateGUI();
		world.draw();
		super.render(delta);
	}
	
	private void updateGUI() 
	{
		if(world.isOver && gameOverLabel == null){
			gameOverLabel = new Label("Game Over", skin);
			stage.addActor(gameOverLabel);
			gameOverLabel.setFillParent(true);
			gameOverLabel.setAlignment(Align.center);
		}
		
		if(world.cherryCount > cherryCount){
			cherryCount = world.cherryCount;
			
			int level = cherryCount / CHERRY_PER_LEVEL;
			if(level > this.level){
				this.level = level;
				for(Actor child : cherryTable.getChildren()){
					child.addAction(Actions.sequence(Actions.scaleBy(0, 0, .5f, Interpolation.linear), Actions.removeActor()));
				}
				levelLabel.setText("Level " + (this.level + 1));
				world.setLevel(this.level);
			}
			Image img = new Image(skin, "cherry");
			cherryTable.add(img);
			img.setScale(0);
			
			img.addAction(Actions.scaleTo(1, 1, 2f, Interpolation.bounceOut));
		}
		
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		world.resize(width, height);
	}
}
