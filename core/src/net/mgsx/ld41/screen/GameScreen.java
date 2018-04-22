package net.mgsx.ld41.screen;

import com.badlogic.gdx.graphics.Color;
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
import net.mgsx.ld41.utils.FormatUtils;
import net.mgsx.ld41.utils.StageScreen;

public class GameScreen extends StageScreen
{
	
	public static final int CHERRY_PER_LEVEL = 3;
	
	public GameWorld world;
	
	private int cherryCount;
	private Table cherryTable;
	private Skin skin;
	private Label levelLabel;
	private Label distanceLabel;
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
		
		distanceLabel = new Label("", skin);
		distanceLabel.setColor(1, 1, 1, .5f);
		
		main.add(distanceLabel).padLeft(140).width(160);
		main.add(levelLabel).expandX().center();
		main.add(cherryTable).width(32 * CHERRY_PER_LEVEL).right();
		// main.debugAll();

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
			gameOverLabel.setColor(Color.BLACK);
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
			img.setOrigin(Align.center);
			cherryTable.add(img);
			img.setScale(0);
			
			img.addAction(Actions.sequence(
					Actions.scaleTo(3, 3, .3f, Interpolation.pow2Out), 
					Actions.scaleTo(1, 1, .5f, Interpolation.bounceOut)));
		}
		
		distanceLabel.setText(FormatUtils.distanceToString(world.distanceTile));
		
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		world.resize(width, height);
	}
}
