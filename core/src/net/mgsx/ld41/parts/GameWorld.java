package net.mgsx.ld41.parts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.ld41.LD41;
import net.mgsx.ld41.assets.GameAssets;
import net.mgsx.ld41.gfx.PixelDistoFX;
import net.mgsx.ld41.logic.BlockController;
import net.mgsx.ld41.logic.HeroController;
import net.mgsx.ld41.utils.TiledMapLink;
import net.mgsx.ld41.utils.TiledMapStream;

public class GameWorld {

	public static final int HERO_START_X = 3;
	public static final int HERO_START_Y = 2;
	
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
	
	private int level;
	
	public int cherryCount;
	
	private float trauma;
	private Vector2 traumaPosition = new Vector2();
	
	public Hero hero;
	
	public boolean isOver;
	private float overtime;
	
	private PixelDistoFX pixelDistoFX;
	
	private float animTime;
	public float distanceTile;
	public int usedBlocks;
	
	public GameWorld() {
		TiledMap mapBase = GameAssets.i().firstMap;
		mapStream = new TiledMapStream(mapBase, MathUtils.ceil(LD41.WIDTH / TILE_WIDTH) + 1, true);
		
		createLinkedMaps(0);
		
		mapRenderer = new OrthogonalTiledMapRenderer(mapStream.getMap());
		
		camera = new OrthographicCamera();
		
		batch = new SpriteBatch();
		
		hero = new Hero(mapBase.getTileSets().getTileSet(0));
		
		hero.setGridPosition(HERO_START_X, HERO_START_Y, TILE_WIDTH, TILE_HEIGHT);
		
		heroControl = new HeroController(this, mapStream, mapBase.getTileSets().getTileSet(0), hero);
		
		
		block = new Block(this);
		blockControl = new BlockController(this, mapStream, mapBase.getTileSets().getTileSet(0), block);
		
		pixelDistoFX = new PixelDistoFX();
	}
	
	private void createLinkedMaps(int level){
		TiledMapLink previousMap = null;
		if(mapStream.currentMap() == null){
			previousMap = mapStream.appendMap(GameAssets.i().firstMap);
		}else{
			previousMap = mapStream.currentMap();
		}
		
		// debug
		if(level < 0){
			previousMap.nextMap = previousMap;
			return;
		}
		
		if(level >= GameAssets.i().levelMaps.size) return;
		
		TiledMapLink first = null, last = null;
		for(TiledMap map : GameAssets.i().levelMaps.get(level)){
			TiledMapLink next = mapStream.appendMap(map);
			if(first == null) first = next;
			previousMap.nextMap = next;
			next.nextMap = previousMap;
			last = next;
		}
		last.nextMap = first;
	}
	
	public void update(float delta) {
		if(isOver){
			overtime += delta;
			if(overtime > 2){
				LD41.i().gameOver();
			}
			return;
		}
		
		animTime += delta;
		
		trauma = Math.max(0, trauma - delta);
		
		pixelDistoFX.update(delta);
		
		mapStream.update(camera.position.x - camera.viewportWidth/2);
		
		heroControl.update(delta);
		hero.update(delta);
		
		blockControl.update(camera, delta);
		block.update(delta);
		
		if(camera.position.x < hero.position.x)
			camera.position.x = MathUtils.lerp(camera.position.x, hero.position.x, .1f);
		
		distanceTile = (camera.position.x - camera.viewportWidth/2) / TILE_WIDTH;
	}

	public void draw() {
		Gdx.gl.glClearColor(.5f, .8f, 1f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float scrolling;
		Texture bgTex = GameAssets.i().textureBackground;
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		scrolling = .2f * camera.position.x / camera.viewportWidth;
		batch.draw(bgTex, 
				camera.position.x - camera.viewportWidth / 2, 
				camera.position.y - camera.viewportHeight / 2, 
				bgTex.getWidth(), 
				bgTex.getHeight()/2, 
				scrolling, .5f, 1 + scrolling, 0);
		scrolling = .5f * camera.position.x / camera.viewportWidth;
		batch.draw(bgTex, 
				camera.position.x - camera.viewportWidth / 2, 
				camera.position.y - camera.viewportHeight / 2, 
				bgTex.getWidth(), 
				bgTex.getHeight()/2, 
				scrolling, 1, 1 + scrolling, .5f);
		batch.end();
		
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
		
		// XXX
		//trauma = 1;
		//traumaPosition.set(100, 100);
		if(trauma > 0){
			float traumaSize = 1024* trauma * (MathUtils.sin(animTime * 30) * 0.5f + 0.5f);
			pixelDistoFX.drawSphere(traumaPosition.x - traumaSize/2, traumaPosition.y - traumaSize/2, traumaSize, traumaSize);
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

	public void addTrauma(float x, float y) {
		trauma = 1;
		traumaPosition.set(x,y);
	}

	public void setLevel(int level) 
	{
		this.level = level;
		createLinkedMaps(level);
	}

	public int getLevel() {
		return level;
	}
	
}

	
