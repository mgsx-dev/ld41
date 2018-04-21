package net.mgsx.ld41.parts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.ld41.LD41;
import net.mgsx.ld41.gfx.PixelDistoFX;
import net.mgsx.ld41.logic.BlockController;
import net.mgsx.ld41.logic.HeroController;
import net.mgsx.ld41.utils.TiledMapLink;
import net.mgsx.ld41.utils.TiledMapStream;
import net.mgsx.ld41.utils.TiledMapUtils;

public class GameWorld {

	public static final float TILE_WIDTH = 32f;
	public static final float TILE_HEIGHT = 32f;
	
	private boolean gfx = true;
	
	private TiledMapStream mapStream;
	private OrthogonalTiledMapRenderer mapRenderer;
	OrthographicCamera camera;
	private SpriteBatch batch;
	private HeroController heroControl;
	private Block block;
	private BlockController blockControl;
	
	public Hero hero;
	
	private boolean isOver;
	private float overtime;
	
	private PixelDistoFX pixelDistoFX;
	
	public GameWorld() {
		TiledMap mapBase = new TmxMapLoader().load("map.tmx");
		mapStream = new TiledMapStream(mapBase, MathUtils.ceil(LD41.WIDTH / TILE_WIDTH) + 1);
		
		TiledMapLink link = mapStream.appendMap(mapBase);
		link.nextMap = link;
		
		mapRenderer = new OrthogonalTiledMapRenderer(mapStream.getMap());
		
		camera = new OrthographicCamera();
		
		batch = new SpriteBatch();
		
		hero = new Hero(mapBase.getTileSets().getTileSet(0));
		GridPoint2 p = TiledMapUtils.findAndRemoveCell(mapBase, Hero.TID_HERO);
		hero.setGridPosition(p.x, p.y, TILE_WIDTH, TILE_HEIGHT);
		
		heroControl = new HeroController(this, mapStream, mapBase.getTileSets().getTileSet(0), hero);
		
		
		block = new Block(this);
		blockControl = new BlockController(this, mapStream, mapBase.getTileSets().getTileSet(0), block);
		
		pixelDistoFX = new PixelDistoFX();
	}
	
	public void update(float delta) {
		if(isOver){
			overtime += delta;
			if(overtime > 2){
				LD41.i().gameOver();
			}
			return;
		}
		
		pixelDistoFX.update(delta);
		
		mapStream.update(camera.position.x - camera.viewportWidth/2);
		
		heroControl.update(delta);
		hero.update(delta);
		
		blockControl.update(camera, delta);
		block.update(delta);
		
		if(camera.position.x < hero.position.x)
			camera.position.x = MathUtils.lerp(camera.position.x, hero.position.x, .1f);
	}

	public void draw() {
		Gdx.gl.glClearColor(.5f, .8f, 1f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(gfx){
			drawGFX();
		}else{
			drawNoGFX();
		}
	}
	
	private void drawNoGFX(){
		camera.update();

		mapStream.begin(camera);
		mapRenderer.setView(camera);
		mapRenderer.render();
		mapStream.end(camera);

		block.draw();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		hero.draw(batch);
		batch.end();
		
		// finaliy the nextBlock
		blockControl.draw();
	}
	private void drawGFX(){
		camera.update();

		pixelDistoFX.beginNormal(camera);
		pixelDistoFX.drawSphere(hero.position.x- 64 + 16, hero.position.y - 64 + 16 - 8, 128, 128);
		
		// XXX waves working but need to separate water from other pixels ...
		if(false){
			mapStream.begin(camera);
			pixelDistoFX.drawWaves(camera, mapStream.getMap());
			mapStream.end(camera);
		}
		
		pixelDistoFX.endNormal();
		
		pixelDistoFX.beginFrame();
		
		mapStream.begin(camera);
		mapRenderer.setView(camera);
		mapRenderer.render();
		mapStream.end(camera);

		pixelDistoFX.endFrame();
		
		pixelDistoFX.drawComposed();
		
		block.draw();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		hero.draw(batch);
		batch.end();
		
		// finaliy the nextBlock
		blockControl.draw();
	}

	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		
		pixelDistoFX.resize(width, height);
	}

	public void moveRight() {
		// camera.position.x += TILE_WIDTH * 6; // TODO 6 is block width ...
	}

	public int getOffsetX() {
		return MathUtils.floor((camera.position.x - camera.viewportWidth/2) / TILE_WIDTH);
	}

	public void gameOver() 
	{
		if(!isOver){
			
			isOver = true;
			overtime = 0;
		}
		
	}
	
}

	
