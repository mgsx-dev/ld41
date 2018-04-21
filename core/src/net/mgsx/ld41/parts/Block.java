package net.mgsx.ld41.parts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class Block {

	public int ix, iy;
	public Vector2 position = new Vector2();
	public TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private GameWorld world;
	private Matrix4 transform = new Matrix4();
	
	public Block(GameWorld world) {
		renderer = new OrthogonalTiledMapRenderer(null);
		camera = new OrthographicCamera();
		this.world = world;
	}
	
	public void update(float delta){
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = position.x;
		camera.position.y = -position.y;
		camera.update();
		transform.set(world.camera.combined).translate(position.x, position.y, 0);
	}
	
	public void draw(){
		if(map != null){
			renderer.setMap(map);
			renderer.setView(transform, 0, 0, 200, 200); // TODO (just relative full map ?)
			renderer.render();
		}
	}
}
