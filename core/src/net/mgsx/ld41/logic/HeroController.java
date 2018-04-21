package net.mgsx.ld41.logic;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

import net.mgsx.ld41.parts.Hero;
import net.mgsx.ld41.utils.TiledMapStream;

public class HeroController {
	private TiledMapStream map;
	private TiledMapTileLayer groundLayer;
	private Hero hero;
	
	public HeroController(TiledMapStream map, Hero hero) {
		super();
		this.map = map;
		this.hero = hero;
		groundLayer = map.getTileLayer("ground");
	}

	public void update(float delta)
	{
		if(!hero.idle) return;
		
		// find next cell
		
		int dirx = hero.rightToleft ? -1 : 1;
		
		// look if can move forward
		int nextY = getNextY(hero.ix + dirx, hero.iy);
		// gap or wall : change direction
		int maxJump = 4;
		if(nextY < 0 || nextY - hero.iy > maxJump){
			hero.rightToleft = !hero.rightToleft;
		}else{
			hero.makeMoveTo(hero.ix + dirx, nextY);
		}
		
	}

	private int getNextY(int ix, int iy) 
	{
		if(canMoveTo(ix, iy)){
			for(int y=iy ; y>=-1 ; y--){
				if(!canMoveTo(ix, y)){
					return y+1;
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
		return cell == null || cell.getTile() == null;
	}
}
