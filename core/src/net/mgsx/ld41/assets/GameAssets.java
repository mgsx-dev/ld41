package net.mgsx.ld41.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GameAssets {
	private static GameAssets i;
	public static GameAssets i(){
		return i== null ? i = new GameAssets() : i;
	}
	
	public Skin skin;
	
	public ShaderProgram normalSphere, normalWaves, distortion;
	
	public TiledMap firstMap, blockTNT;
	public Array<Array<TiledMap>> levelMaps = new Array<Array<TiledMap>>();
	public Array<Array<TiledMap>> blockMaps = new Array<Array<TiledMap>>();
	
	
	public GameAssets() {
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
		
		normalSphere = newShader("normal-sphere");
		normalWaves = newShader("normal-waves");
		distortion = newShader("pixel-disto");
		
		firstMap = new TmxMapLoader().load("map.tmx");
		
		loadLevelMap(1, 3);
		loadLevelMap(2, 2);
		loadLevelMap(3, 2);
		loadLevelMap(4, 2);
		loadLevelMap(5, 1);

		// load other levels here
		
		blockTNT = new TmxMapLoader().load("b-tnt.tmx");
		
		loadBlockMap(1, 12);
		
		// load other block maps
	}

	private void loadBlockMap(int level, int count) 
	{
		Array<TiledMap> maps = new Array<TiledMap>();
		for(int i=1 ; i<=count ; i++){
			maps.add(new TmxMapLoader().load("b" + i + ".tmx"));
		}
		blockMaps.add(maps);
	}

	private void loadLevelMap(int level, int count) {
		Array<TiledMap> maps = new Array<TiledMap>();
		for(int i=1 ; i<=count ; i++){
			maps.add(new TmxMapLoader().load("map" + level + "-" + i + ".tmx"));
		}
		levelMaps.add(maps);
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
