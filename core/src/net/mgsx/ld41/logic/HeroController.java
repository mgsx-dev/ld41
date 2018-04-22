package net.mgsx.ld41.logic;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

import net.mgsx.ld41.parts.GameWorld;
import net.mgsx.ld41.parts.Hero;
import net.mgsx.ld41.utils.TiledMapStream;

public class HeroController {
	private TiledMapStream map;
	private TiledMapTileLayer groundLayer, decoLayer;
	private Hero hero;
	
	public static final int TID_CHERRY = 162;

	
	
	private boolean [] solidIDs;
	private boolean [] waterIDs;
	private boolean [] killIDs;
	private TiledMapTileSet tileset;
	private GameWorld world;
	private float flipTimeout;
	
	public HeroController(GameWorld world, TiledMapStream map, TiledMapTileSet tileset, Hero hero) {
		super();
		this.world = world;
		this.map = map;
		this.hero = hero;
		this.tileset = tileset;
		groundLayer = map.getTileLayer("ground");
		decoLayer = map.getTileLayer("deco");
		
		solidIDs = new boolean[1024];
		int [] ids = new int[]{3,4,5,35,36,37,67,68,69,99,100,101,131,132,133,6,7,8,38,40,70,71,72,103,104,135,136, 
				9,10,11,41,43,73,74,75,106,107,138,139}; // water borders
		for(int i=0;i<ids.length;i++){
			solidIDs[ids[i]] = true;
		}
		
		int [] wids = new int[]{42, 105, 137};
		waterIDs = new boolean[1024];
		for(int i=0;i<wids.length;i++){
			waterIDs[wids[i]] = true;
		}
		
		int [] kids = new int[]{163, 195};
		killIDs = new boolean[1024];
		for(int i=0;i<kids.length;i++){
			killIDs[kids[i]] = true;
		}
	}

	public void update(float delta)
	{
		if(!hero.idle()) return;
		
		// find next cell
		
		int dirx = hero.rightToleft ? -1 : 1;
		
		Cell currentCell = map.getCell(groundLayer, hero.ix, hero.iy);
		
		int nextY;
		
		// spike case
		if(currentCell != null && currentCell.getTile() != null && killIDs[currentCell.getTile().getId()-1])
		{
			hero.setDead();
			world.gameOver();
			return;
		}
		
		// water case
		if(currentCell != null && currentCell.getTile() != null && waterIDs[currentCell.getTile().getId()-1])
		{
			// will try to swing as high as possible !
			Cell aCell;
			
			// try top
			aCell = map.getCell(groundLayer, hero.ix, hero.iy + 1);
			if(aCell == null || aCell.getTile() == null || waterIDs[aCell.getTile().getId()-1]){
				hero.makeMoveTo(hero.ix, hero.iy+1, true);
				return;
			}
			
			// then try right
			aCell = map.getCell(groundLayer, hero.ix + 1, hero.iy);
			if(aCell == null || aCell.getTile() == null || waterIDs[aCell.getTile().getId()-1]){
				hero.makeMoveTo(hero.ix + 1, hero.iy, true);
				hero.rightToleft = false;
				return;
			}
//			aCell = map.getCell(groundLayer, hero.ix - dirx, hero.iy);
//			if(aCell == null || aCell.getTile() == null || waterIDs[aCell.getTile().getId()-1]){
//				hero.rightToleft = !hero.rightToleft;
//				hero.makeMoveTo(hero.ix - dirx, hero.iy, true);
//				return;
//			}
			
		}
		
		// look if can move forward
		nextY = getNextY(hero.ix + dirx, hero.iy);
		
		// gap or wall : change direction
		int maxJump = 4;
		if(nextY < 0 || nextY - hero.iy > maxJump){
			if(flipTimeout > 0){
				flipTimeout -= delta;
			}else{
				hero.rightToleft = !hero.rightToleft;
				flipTimeout = .3f;
			}
		}else{
			// check cherry
			Cell decoCell = map.getCell(decoLayer, hero.ix, hero.iy);
			if(decoCell != null && decoCell.getTile() != null && decoCell.getTile().getId() == TID_CHERRY){
				hero.eat();
				world.cherryCount++;
				decoCell.setTile(tileset.getTile(TID_CHERRY + 1));
			}
			else{
				hero.makeMoveTo(hero.ix + dirx, nextY, false);
			}
			
		}
	}

	private int getNextY(int ix, int iy) 
	{
		if(canMoveTo(ix, iy)){
			for(int y=iy ; y>=-1 ; y--){
				if(!canMoveTo(ix, y)){
					return y+1;
				}else{
					Cell cell = groundLayer.getCell(ix - map.getOffsetX(), y);
					if(cell != null && cell.getTile() != null && waterIDs[cell.getTile().getId()-1]){
						return y;
					}
				}
			}
			return -1;
		}else{
			for(int y=iy ; y<30 ; y++){ // TODO what is the high limit ?
				if(canMoveTo(ix, y)){
					return y;
				}
			}
			return -1;
		}
	}

	private boolean canMoveTo(int x, int y) {
		Cell cell = groundLayer.getCell(x - map.getOffsetX(), y);
		return cell == null || cell.getTile() == null || !solidIDs[cell.getTile().getId()-1];
	}
}
