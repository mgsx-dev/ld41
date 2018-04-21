package net.mgsx.ld41.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GameAssets {
	private static GameAssets i;
	public static GameAssets i(){
		return i== null ? i = new GameAssets() : i;
	}
	
	public Skin skin;
	
	public ShaderProgram normalSphere, normalWaves, distortion;
	
	public GameAssets() {
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
		
		normalSphere = newShader("normal-sphere");
		normalWaves = newShader("normal-waves");
		distortion = newShader("pixel-disto");
	}

	private ShaderProgram newShader(String name) {
		ShaderProgram sp = new ShaderProgram(
				Gdx.files.internal("shaders/" + name + ".vs"), 
				Gdx.files.internal("shaders/" + name + ".fs"));
		if(!sp.isCompiled()){
			throw new GdxRuntimeException(sp.getLog());
		}
		return sp;
	}
}
