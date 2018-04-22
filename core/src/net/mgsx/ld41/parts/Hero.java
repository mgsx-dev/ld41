package net.mgsx.ld41.parts;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.ld41.utils.TiledMapUtils;

public class Hero 
{
	public static final int TID_HERO = 448;
	public static final int TID_HERO_EAT = 449;
	public static final int TID_HERO_DEAD = 480;

	public int ix, iy;
	public Vector2 position = new Vector2(), positionA = new Vector2(), positionB = new Vector2();
	public Sprite sprite;
	public boolean rightToleft;
	private TextureRegion region;
	private boolean moving, eating;
	public float moveTime, eatTime;
	private TextureRegion regionEat, regionDead;
	private boolean waterMode;
	private boolean dying;
	
	public Hero(TiledMapTileSet tileSet) {
		region = TiledMapUtils.findRegion(tileSet, TID_HERO);
		regionEat = TiledMapUtils.findRegion(tileSet, TID_HERO_EAT);
		regionDead = TiledMapUtils.findRegion(tileSet, TID_HERO_DEAD);
		sprite = new Sprite(region);
	}
	
	public void update(float delta){
		if(moving){
			moveTime += delta * 5 * (waterMode ? .2f : 1);
			if(moveTime >= 1){
				moveTime = 1;
				moving = false;
				eating = false;
				sprite.setRegion(region);
			}
			Interpolation interpolation = (waterMode ? Interpolation.sine : Interpolation.pow2);
			position.x = MathUtils.lerp(positionA.x, positionB.x, interpolation.apply(moveTime));
			position.y = MathUtils.lerp(positionA.y, positionB.y, interpolation.apply(moveTime));
		}else if(eating){
			eatTime += delta * 3;
			if(eatTime > 1){
				eating = false;
			}
		}
		
		// update sprite
		sprite.setFlip(rightToleft, false);
		sprite.setBounds(position.x, position.y - 4, region.getRegionWidth(), region.getRegionHeight());
	}
	
	public void draw(Batch batch){
		sprite.draw(batch);
	}

	public void setGridPosition(int x, int y, float tileWidth, float tileHeight) 
	{
		ix = x;
		iy = y;
		position.set(ix * tileWidth, iy * tileHeight);
		
	}

	public void makeMoveTo(int tx, int ty, boolean waterMode) 
	{
		moving = true;
		moveTime = 0;
		positionA.set(ix, iy).scl(GameWorld.TILE_WIDTH, GameWorld.TILE_HEIGHT);
		positionB.set(tx, ty).scl(GameWorld.TILE_WIDTH, GameWorld.TILE_HEIGHT);
		ix = tx;
		iy = ty;
		this.waterMode = waterMode;
	}

	public void eat() {
		eating = true;
		eatTime = 0;
		sprite.setRegion(regionEat);
	}

	public boolean idle() {
		return !moving && !eating && !dying;
	}

	public void setDead() {
		dying = true;
		sprite.setRegion(regionDead);
	}
}
