package net.mgsx.ld41.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.ld41.assets.GameAssets;

public class PixelDistoFX {

	private FrameBuffer fboNormals, fboFrame;
	private SpriteBatch batch;
	private ShapeRenderer renderer, rendererWave;
	private OrthogonalTiledMapRenderer mapRenderer;
	private SpriteBatch mapBatch;
	private Vector3 waveScale = new Vector3();
	private float time;
	
	public PixelDistoFX() {
		batch = new SpriteBatch(4, GameAssets.i().distortion);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		renderer = new ShapeRenderer(20, GameAssets.i().normalSphere);
		mapBatch = new SpriteBatch();
		mapRenderer = new OrthogonalTiledMapRenderer(null, mapBatch);
		rendererWave = new ShapeRenderer(20, GameAssets.i().normalWaves);
	}
	
	public void resize(int width, int height) {
		if(fboNormals != null) fboNormals.dispose();
		fboNormals = new FrameBuffer(Format.RGBA8888, width, height, false);
		if(fboFrame != null) fboFrame.dispose();
		fboFrame = new FrameBuffer(Format.RGBA8888, width, height, false);
	}

	public void beginNormal(Camera camera) {
		fboNormals.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		renderer.setProjectionMatrix(camera.combined);
		rendererWave.setProjectionMatrix(camera.combined);
		
	}

	public void drawSphere(float x, float y, float w, float h) 
	{
		renderer.begin(ShapeType.Filled);
		renderer.rect(x, y, w, h, 
				Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN);
		renderer.end();
	}
	public void drawSphereWave(float x, float y, float w, float h) 
	{
		rendererWave.begin(ShapeType.Filled);
		ShaderProgram shader = GameAssets.i().normalWaves;
		float noiseFreq = 1012f;
		float noiseSpeed = 300f;
		float normalZ = 0f; // XXX 100
		shader.begin();
		shader.setUniformf("u_scale", waveScale.set(noiseFreq / Gdx.graphics.getWidth(), noiseFreq / Gdx.graphics.getHeight(), normalZ));
		shader.setUniformf("u_offset", waveScale.set(time * noiseSpeed, time * noiseSpeed, time * 100000000));
		rendererWave.rect(x, y, w, h, 
				Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN);
		rendererWave.end();
	}
	
	public void update(float delta){
		time += delta;
	}
	
	public void drawWaves(OrthographicCamera camera, TiledMap map) {
		ShaderProgram shader = GameAssets.i().normalWaves;
		mapRenderer.setMap(map);
		mapRenderer.setView(camera);
		mapBatch.setShader(shader);
		shader.begin();
		float noiseFreq = 100f;
		float noiseSpeed = 5f;
		float normalZ = .1f; // XXX 100
		shader.setUniformf("u_scale", waveScale.set(noiseFreq / Gdx.graphics.getWidth(), noiseFreq / Gdx.graphics.getHeight(), normalZ));
		shader.setUniformf("u_offset", waveScale.set(time * noiseSpeed, time * noiseSpeed, time * 20));
		mapRenderer.render();
	}
	
//	public void drawWavesCircle(Camera camera, float x, float y, float r) 
//	{
//		ShaderProgram shader = GameAssets.i().normalWaves;
//		mapBatch.setProjectionMatrix(camera.combined);
//		mapBatch.setShader(shader);
//		mapBatch.begin();
//		float noiseFreq = 10f;
//		shader.setUniformf("u_scale", waveScale.set(noiseFreq / Gdx.graphics.getWidth(), noiseFreq / Gdx.graphics.getHeight(), 100f));
//		shader.setUniformf("u_offset", waveScale.set(time * 5, time * 5, time * 20));
//		mapBatch.draw(GameAssets.i().circleTexture, x + r/2, y + r/2, r, r);
//		mapBatch.end();
//	}
	
	public void endNormal() {
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		fboNormals.end();
	}
	
	public void beginFrame() {
		fboFrame.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public void endFrame() {
		fboFrame.end();
	}
	
	public void drawComposed(){
		ShaderProgram shader = GameAssets.i().distortion;
		Texture normalMap = fboNormals.getColorBufferTexture();
		Texture colorMap = fboFrame.getColorBufferTexture();
		
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		batch.enableBlending();
		batch.begin();
		shader.setUniformi("u_texture1", 1);
		shader.setUniformf("u_amount", 1f / 60f);
		normalMap.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		batch.draw(colorMap, 0, 0, 1, 1, 0, 0, 1, 1);
		batch.end();
	}

}
