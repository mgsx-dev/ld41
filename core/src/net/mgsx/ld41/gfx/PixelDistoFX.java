package net.mgsx.ld41.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.ld41.assets.GameAssets;

public class PixelDistoFX {

	private FrameBuffer fboNormals, fboFrame;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	
	public PixelDistoFX() {
		batch = new SpriteBatch(4, GameAssets.i().distortion);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		renderer = new ShapeRenderer(20, GameAssets.i().normalSphere);
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
		
	}

	public void drawSphere(float x, float y, float w, float h) 
	{
		renderer.begin(ShapeType.Filled);
		renderer.rect(x, y, w, h, 
				Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN);
		renderer.end();
	}
	
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
