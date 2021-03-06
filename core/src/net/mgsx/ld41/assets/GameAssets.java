package net.mgsx.ld41.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
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

	public Texture textureMenu, textureEnd, textureBackground;
	
	
	public GameAssets() {
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
		
		textureMenu = new Texture(Gdx.files.internal("menu.png"));
		textureEnd = new Texture(Gdx.files.internal("end.png"));
		textureBackground = new Texture(Gdx.files.internal("background.png"));
		textureBackground.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		
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
		
		loadBlockSet(1, 14);
		loadBlockSet(2, 7);
		loadBlockSet(3, 5);
		loadBlockSet(4, 4);
		loadBlockSet(5, 3);
		
		// load other block maps
	}

	private void loadBlockSet(int level, int count) 
	{
		Array<TiledMap> maps = new Array<TiledMap>();
		for(int i=1 ; i<=count ; i++){
			maps.add(new TmxMapLoader().load("b" + level + "-" + i + ".tmx"));
		}
		blockMaps.add(maps);
	}
	
	// old version
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
