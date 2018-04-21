package net.mgsx.ld41.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.ld41.LD41;
import net.mgsx.ld41.parts.Block;
import net.mgsx.ld41.parts.GameWorld;
import net.mgsx.ld41.utils.TiledMapStream;
import net.mgsx.ld41.utils.TiledMapUtils;

public class BlockController {
	private TiledMapStream map;
	private TiledMapTileLayer groundLayer, decoLayer, blockGroundLayer, blockDecoLayer, tmpLayer;
	private Block block;
	
	private GameWorld world;
	
	private Array<TiledMap> blocks = new Array<TiledMap>();
	
	private float cdx;
	private TiledMapTileSet tileset;
	
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
		for(int i=1 ; i<=12 ; i++){
			blocks.add(new TmxMapLoader().load("b" + i + ".tmx"));
		}
		
		resetBlock();
	}
	
	private void resetBlock() {
		TiledMap blockMap;
		int debugBlock = -1;
		if(debugBlock >= 0){
			blockMap = blocks.get(debugBlock);
		}else{
			blockMap = blocks.get(MathUtils.random(blocks.size-1));
		}
		
		block.map = TiledMapUtils.copy(blockMap);
		blockGroundLayer = (TiledMapTileLayer)block.map.getLayers().get("ground");
		blockDecoLayer = (TiledMapTileLayer)block.map.getLayers().get("deco");
		block.ix = MathUtils.ceil(LD41.WIDTH / GameWorld.TILE_WIDTH)/2 + world.getOffsetX(); // TODO
		block.iy = MathUtils.ceil(LD41.HEIGHT / GameWorld.TILE_HEIGHT) - 3; // TODO
		block.position.set(block.ix, block.iy).scl(GameWorld.TILE_WIDTH, GameWorld.TILE_HEIGHT);
	}
	
	public void update(float delta)
	{
		if(block.map == null) return;
		
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
				
				paint();
				
				explode();
				
				// reset block
				resetBlock();
				
				// TODO if collide after reset, what to do : loose
				
				world.moveRight();
			}
		}
	}

	public static final int TID_BOMB = 161;
	
	private void explode() {
		for(int y=0 ; y<blockGroundLayer.getHeight() ; y++){
			for(int x=0 ; x<blockGroundLayer.getWidth() ; x++){
				Cell blockCell = blockDecoLayer.getCell(x, y);
				if(blockCell != null && blockCell.getTile() != null && blockCell.getTile().getId() == TID_BOMB){
					explodeMap(x + block.ix - map.getOffsetX(), y + block.iy);
				}
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

	private void paint() 
	{
		for(int y=0 ; y<blockGroundLayer.getHeight() ; y++){
			for(int x=0 ; x<blockGroundLayer.getWidth() ; x++){
				Cell blockCell = blockGroundLayer.getCell(x, y);
				if(blockCell != null && blockCell.getTile() != null){
					groundLayer.setCell(x + block.ix - map.getOffsetX(), y + block.iy, blockGroundLayer.getCell(x, y));
				}
			}
		}
		
		for(int y=0 ; y<blockDecoLayer.getHeight() ; y++){
			for(int x=0 ; x<blockDecoLayer.getWidth() ; x++){
				Cell blockCell = blockDecoLayer.getCell(x, y);
				if(blockCell != null && blockCell.getTile() != null){
					decoLayer.setCell(x + block.ix - map.getOffsetX(), y + block.iy, blockCell);
				}
			}
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
