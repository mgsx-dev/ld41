package net.mgsx.ld41.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

import net.mgsx.ld41.LD41;
import net.mgsx.ld41.parts.Block;
import net.mgsx.ld41.parts.GameWorld;
import net.mgsx.ld41.parts.StaticBlock;
import net.mgsx.ld41.utils.TiledMapStream;
import net.mgsx.ld41.utils.TiledMapUtils;

public class BlockController {
	
	public static final int NB_BLOCKS = 13;
	public static final int TNT_BLOCK_INDEX = 12;
	
	public static final int TID_BOMB = 161;
	private static final int MAXY = 18;
	
	private Array<StaticBlock> staticBlocks = new Array<StaticBlock>();
	private boolean nextTNT;
	
	private TiledMapStream map;
	private TiledMapTileLayer groundLayer, decoLayer, blockGroundLayer, blockDecoLayer, tmpLayer;
	private Block block;
	
	private GameWorld world;
	
	private Array<TiledMap> blocks = new Array<TiledMap>();
	
	private float cdx;
	private TiledMapTileSet tileset;
	
	private TiledMap nextBlock;
	private OrthogonalTiledMapRenderer nextRenderer;
	private Matrix4 nextMatrix = new Matrix4();
	
	public BlockController(GameWorld world, TiledMapStream map, TiledMapTileSet tileset, Block block) {
		super();
		this.world = world;
		this.map = map;
		this.block = block;
		this.tileset = tileset;
		groundLayer = map.getTileLayer("ground");
		decoLayer = map.getTileLayer("deco");
		
		tmpLayer = new TiledMapTileLayer(20, 20, 32, 32); // don't care about tile size ... XXX 20 is enough
		
		// reset block
		for(int i=1 ; i<=NB_BLOCKS ; i++){
			blocks.add(new TmxMapLoader().load("b" + i + ".tmx"));
		}
		
		nextRenderer = new OrthogonalTiledMapRenderer(null);
		
		resetBlock();
	}
	
	private static int TNT_FREQUENCY = 10;
	private int tntCountDown = TNT_FREQUENCY;
	
	private void resetBlock() {
		TiledMap blockMap;
		
		boolean currentTNT = nextTNT;
		blockMap = nextBlock;
		
		int blockIndex;
		int debugBlock = -1;
		if(debugBlock >= 0){
			blockIndex = debugBlock;
		}else{
			tntCountDown--;
			if(tntCountDown <= 0){
				blockIndex = TNT_BLOCK_INDEX;
			}else 
				blockIndex = MathUtils.random(blocks.size-1);
		}
		nextBlock = blocks.get(blockIndex);
		nextTNT = blockIndex == TNT_BLOCK_INDEX;
		
		if(nextTNT){
			tntCountDown = TNT_FREQUENCY;
		}
		
		// first reset (twice)
		if(blockMap == null){
			resetBlock();
			return;
		}
		
		block.isTNT = currentTNT;
		block.map = TiledMapUtils.copy(blockMap);
		blockGroundLayer = (TiledMapTileLayer)block.map.getLayers().get("ground");
		blockDecoLayer = (TiledMapTileLayer)block.map.getLayers().get("deco");
		block.ix = MathUtils.ceil(LD41.WIDTH / GameWorld.TILE_WIDTH) - 7 + world.getOffsetX(); // TODO
		block.iy = MathUtils.ceil(LD41.HEIGHT / GameWorld.TILE_HEIGHT) - 3; // TODO
		block.position.set(block.ix, block.iy).scl(GameWorld.TILE_WIDTH, GameWorld.TILE_HEIGHT);
	}
	
	public void draw(){
		
		if(nextBlock != null){
			nextMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			nextMatrix.translate(10, Gdx.graphics.getHeight() - 32 * 3 - 10, 0);
			nextMatrix.scale(.5f, .5f, .5f);
			
			nextRenderer.setView(nextMatrix, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			nextRenderer.setMap(nextBlock);
			nextRenderer.getBatch().setColor(1,1,1, .8f);
			nextRenderer.render();
		}
		
	}
	
	public void update(Camera camera, float delta)
	{
		if(block.map == null) return;
		
		// remove out screen static blocks
		int xLeft = MathUtils.ceil((camera.position.x - camera.viewportWidth/2) / GameWorld.TILE_WIDTH);
		for(int i=0 ; i<staticBlocks.size ; ){
			StaticBlock sb = staticBlocks.get(i);
			if(sb.xMax < xLeft){
				staticBlocks.removeIndex(i);
			}else{
				i++;
			}
		}
		
		float continuousSpeed = 10;
		
		// player control
		int mx = 0;
		int my = 0;
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){ // TODO WASD and ZQSD
			mx = -1;
			cdx = 0;
		}else if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
			mx = 1;
			cdx = 0;
		}else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){ // TODO WASD and ZQSD
			cdx -= delta * continuousSpeed;
		}else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			cdx += delta * continuousSpeed;
		}else{
			cdx = 0;
		}
		if(cdx > 1){
			mx = 1;
			cdx -= 1;
		}else if(cdx < -1){
			mx = -1;
			cdx += 1;
		}
		
		
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			my = -1;
		}
		int rotate = 0;
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
			rotate = 1;
		}
		
		if(rotate != 0){
			rotateBlock();
		}
		
		// TODO check H collisions ...
		block.ix += mx;
		
		
		// compute block size
		int bxmin = 100, bxmax = -100, bymin = -100, bymax = 100;
		for(int y=0 ; y<blockGroundLayer.getHeight() ; y++){
			for(int x=0 ; x<blockGroundLayer.getWidth() ; x++){
				Cell blockCell = blockGroundLayer.getCell(x, y);
				if(blockCell != null && blockCell.getTile() != null){
					bxmin = Math.min(bxmin, x);
					bxmax = Math.max(bxmax, x);
					bymin = Math.min(bymin, y);
					bymax = Math.max(bymax, y);
				}
			}
		}
		
		
		// check collision with screen
		int xmin = MathUtils.ceil((camera.position.x - camera.viewportWidth/2) / GameWorld.TILE_WIDTH) - bxmin;
		int xmax = MathUtils.floor((camera.position.x + camera.viewportWidth/2 + bxmax) / GameWorld.TILE_WIDTH) - bxmax - 1;
		block.ix = MathUtils.clamp(block.ix, xmin, xmax);
		
		
		
		block.position.y += my * delta * GameWorld.TILE_HEIGHT * 10;
		
		block.position.x = block.ix * GameWorld.TILE_WIDTH;
		block.position.y -= delta * GameWorld.TILE_HEIGHT;
		
		
		
		
		int iy = MathUtils.floor(block.position.y / GameWorld.TILE_HEIGHT);
		if(iy != block.iy){
			block.iy = iy;
			block.position.y += GameWorld.TILE_HEIGHT * (block.iy - iy);
			
			// check collisions
			if(collide()){
				block.iy++;
				
				if(block.isTNT){
					explode();
					// add some camera FX and GFX ...
					world.addTrauma((block.ix + 3) * GameWorld.TILE_WIDTH, (block.iy + 3) * GameWorld.TILE_HEIGHT);
				}else{
					paint();
				}
				
				// reset block
				resetBlock();
				
				// TODO if collide after reset, what to do : loose
				
				world.moveRight();
			}
		}
	}

	private void explodeMap(int cx, int cy) {
		int size = 5;
		for(int y=0 ; y<size ; y++){
			for(int x=0 ; x<size ; x++){
				int tx = cx + x - size/2;
				int ty = cy + y - size/2;
				
				Cell mapCell = groundLayer.getCell(tx, ty);
				if(mapCell != null && mapCell.getTile() != null){
//					int newID = mapCell.getTile().getId() - 3;
//					mapCell.setTile(tileset.getTile(newID));
					
					// XXX remove
					groundLayer.setCell(tx, ty, null);
				}
			}
		}
	}

	private void rotateBlock() 
	{
		for(MapLayer layer : block.map.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer tileLayer = (TiledMapTileLayer)layer;
				TiledMapUtils.copy(tmpLayer, tileLayer);
				for(int y=0 ; y<tileLayer.getHeight() ; y++){
					for(int x=0 ; x<tileLayer.getWidth() ; x++){
						int tx = y;
						int ty = tileLayer.getWidth() - 1 - x;
						Cell srcCell = tmpLayer.getCell(x, y);
						tileLayer.setCell(tx, ty, srcCell);
						if(srcCell != null)
							srcCell.setRotation((srcCell.getRotation() + 3)%4);
					}
				}
			}
		}
	}

	private void explode() 
	{
		// find all static blocks around TNT
		Array<StaticBlock> blockToExplode = new Array<StaticBlock>();
		for(int y=0 ; y<blockGroundLayer.getHeight() ; y++){
			for(int x=0 ; x<blockGroundLayer.getWidth() ; x++){
				Cell blockCell = blockGroundLayer.getCell(x, y);
				if(blockCell != null && blockCell.getTile() != null){
					int tntX = x + block.ix;
					int tntY = y + block.iy;
					for(int i=0 ; i<staticBlocks.size ; ){
						StaticBlock sBlock = staticBlocks.get(i);
						if(isStaticBlockImpacted(sBlock, tntX, tntY)){
							staticBlocks.removeIndex(i);
							blockToExplode.add(sBlock);
						}else{
							i++;
						}
					}
					
				}
			}
		}
		
		// remove static block from map
		for(StaticBlock sBlock : blockToExplode){
			for(GridPoint2 point : sBlock.groundTiles){
				groundLayer.setCell(point.x - map.getOffsetX(), point.y, null);
			}
			for(GridPoint2 point : sBlock.decoTiles){
				decoLayer.setCell(point.x - map.getOffsetX(), point.y, null);
			}
		}
		
		
	}
	
	private boolean isStaticBlockImpacted(StaticBlock sBlock, int tntX, int tntY){
		for(GridPoint2 point : sBlock.groundTiles){
			if(Math.abs(point.x - tntX) < 2 && Math.abs(point.y - tntY) < 2){
				return true;
			}
		}
		return false;
	}
	
	private void paint() 
	{
		StaticBlock sBlock = new StaticBlock();
		
		int maxY = 0;
		for(int y=0 ; y<blockGroundLayer.getHeight() ; y++){
			for(int x=0 ; x<blockGroundLayer.getWidth() ; x++){
				Cell blockCell = blockGroundLayer.getCell(x, y);
				if(blockCell != null && blockCell.getTile() != null){
					maxY = Math.max(maxY, y + block.iy);
					GridPoint2 point = new GridPoint2(x + block.ix, y + block.iy);
					if(sBlock.groundTiles.size > 0) sBlock.xMax = Math.max(sBlock.xMax, point.x);
					else sBlock.xMax = point.x;
					sBlock.groundTiles.add(point);
					groundLayer.setCell(x + block.ix - map.getOffsetX(), y + block.iy, blockGroundLayer.getCell(x, y));
				}
			}
		}
		
		for(int y=0 ; y<blockDecoLayer.getHeight() ; y++){
			for(int x=0 ; x<blockDecoLayer.getWidth() ; x++){
				Cell blockCell = blockDecoLayer.getCell(x, y);
				if(blockCell != null && blockCell.getTile() != null){
					GridPoint2 point = new GridPoint2(x + block.ix, y + block.iy);
					sBlock.decoTiles.add(point);
					decoLayer.setCell(x + block.ix - map.getOffsetX(), y + block.iy, blockCell);
				}
			}
		}
		
		staticBlocks.add(sBlock);
		
		if(maxY >= MAXY){
			world.hero.setDead();
			world.gameOver();
		}
	}
	
	private boolean collide() 
	{
		for(int y=0 ; y<blockGroundLayer.getHeight() ; y++){
			for(int x=0 ; x<blockGroundLayer.getWidth() ; x++){
				Cell blockCell = blockGroundLayer.getCell(x, y);
				if(blockCell != null && blockCell.getTile() != null){
					if(y + block.iy < -1) return true; // XXX fill the gaps !
					Cell mapCell = groundLayer.getCell(x + block.ix - map.getOffsetX(), y + block.iy);
					if(mapCell != null && mapCell.getTile() != null){
						return true;
					}
				}
			}
		}
		
		return false;
	}

}
