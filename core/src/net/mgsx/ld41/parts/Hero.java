package net.mgsx.ld41.parts;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Hero 
{
	public int ix, iy;
	public Vector2 position = new Vector2(), positionA = new Vector2(), positionB = new Vector2();
	public Sprite sprite;
	public boolean rightToleft;
	private TextureRegion region;
	public boolean idle = true;
	private float moveTime;
	
	public Hero(TextureRegion leftToRightRegion) {
		region = leftToRightRegion;
		sprite = new Sprite(leftToRightRegion);
	}
	
	public void update(float delta){
		if(!idle){
			moveTime += delta * 5;
			if(moveTime >= 1){
				moveTime = 1;
				idle = true;
			}
			position.x = MathUtils.lerp(positionA.x, positionB.x, Interpolation.sine.apply(moveTime));
			position.y = MathUtils.lerp(positionA.y, positionB.y, Interpolation.sine.apply(moveTime));
		}
		
		// update sprite
		sprite.setFlip(rightToleft, false);
		sprite.setBounds(position.x, position.y, region.getRegionWidth(), region.getRegionHeight());
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

	public void makeMoveTo(int tx, int ty) 
	{
		idle = false;
		moveTime = 0;
		positionA.set(ix, iy).scl(GameWorld.TILE_WIDTH, GameWorld.TILE_HEIGHT);
		positionB.set(tx, ty).scl(GameWorld.TILE_WIDTH, GameWorld.TILE_HEIGHT);
		ix = tx;
		iy = ty;
	}
}
