package net.mgsx.ld41.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssets {
	private static GameAssets i;
	public static GameAssets i(){
		return i== null ? i = new GameAssets() : i;
	}
	
	public Skin skin;
	
	public GameAssets() {
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
	}
}
