package net.mgsx.ld41.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class TiledMapUtils {

	public static TextureRegion findRegion(TiledMap map, int tileID){
		return map.getTileSets().getTile(tileID + 1).getTextureRegion();
	}

	public static GridPoint2 findCell(TiledMap map, int tileID) 
	{
		for(MapLayer layer : map.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer tileLayer = ((TiledMapTileLayer) layer);
				for(int y=0 ; y<tileLayer.getHeight() ; y++){
					for(int x=0 ; x<tileLayer.getWidth() ; x++){
						Cell cell = tileLayer.getCell(x, y);
						if(cell != null && cell.getTile() != null && cell.getTile().getId() == tileID + 1)
							return new GridPoint2(x, y);
					}
				}
			}
		}
		return null;
	}
	public static GridPoint2 findAndRemoveCell(TiledMap map, int tileID) 
	{
		for(MapLayer layer : map.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer tileLayer = ((TiledMapTileLayer) layer);
				for(int y=0 ; y<tileLayer.getHeight() ; y++){
					for(int x=0 ; x<tileLayer.getWidth() ; x++){
						Cell cell = tileLayer.getCell(x, y);
						if(cell != null && cell.getTile() != null && cell.getTile().getId() == tileID + 1){
							tileLayer.setCell(x, y, null);
							return new GridPoint2(x, y);
						}
					}
				}
			}
		}
		return null;
	}
	
	public static void paint(TiledMap map, TiledMap newMap, float offsetX, float offsetY) {
		resize(map, newMap, offsetX, offsetY);
		
		for(int i=0, j=0 ; i<map.getLayers().getCount() && j<newMap.getLayers().getCount() ; ){
			MapLayer layer = map.getLayers().get(i);
			MapLayer newLayer = newMap.getLayers().get(j);
			if(!(layer instanceof TiledMapTileLayer)){
				i++;
			}else if(!(newLayer instanceof TiledMapTileLayer)){
				j++;
			}else{
				paint((TiledMapTileLayer)layer, (TiledMapTileLayer)newLayer, offsetX, offsetY);
				i++;
				j++;
			}
		}
		
	}
	
	public static void paint(TiledMapTileLayer layer, TiledMapTileLayer newLayer, float offsetX, float offsetY) {
		int ox = (int)(offsetX/newLayer.getTileWidth());
		int oy = (int)(offsetY/newLayer.getTileHeight());
		
		for(int y=0 ; y<newLayer.getHeight() ; y++){
			for(int x=0 ; x<newLayer.getWidth() ; x++){
				Cell newCell = newLayer.getCell(x, y);
				Cell cell = null;
				if(newCell != null){
					cell = new Cell();
					cell.setTile(newCell.getTile());
				}
				layer.setCell(x + ox, y + oy, cell);
			}
		}
	}
	
	/**
	 * simple copy without cell creation
	 * @param dst
	 * @param src
	 */
	public static void copy(TiledMapTileLayer dst, TiledMapTileLayer src) {
		for(int y=0 ; y<src.getHeight() ; y++){
			for(int x=0 ; x<src.getWidth() ; x++){
				dst.setCell(x, y, src.getCell(x, y));
			}
		}
	}


	public static void resize(TiledMap map, TiledMap newMap, float offsetX, float offsetY) {
		
		Rectangle mapBounds = getBounds(new Rectangle(), map);
		Rectangle newMapBounds = getBounds(new Rectangle(offsetX, offsetY, 0, 0), newMap);
		
		mapBounds.merge(newMapBounds);
		
		Array<MapLayer> newLayers = new Array<MapLayer>();
		for(MapLayer layer : map.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer newLayer = resize((TiledMapTileLayer)layer, mapBounds);
				newLayers.add(newLayer);
			}
		}
		while(map.getLayers().getCount()>0) map.getLayers().remove(map.getLayers().getCount()-1);
		for(MapLayer layer : newLayers){
			map.getLayers().add(layer);
		}
		
	}
	
	private static TiledMapTileLayer resize(TiledMapTileLayer layer, Rectangle bounds) {
		int width = MathUtils.ceil(bounds.getWidth() / layer.getTileWidth());
		int height = MathUtils.ceil(bounds.getHeight() / layer.getTileHeight());
		TiledMapTileLayer newLayer = new TiledMapTileLayer(width, height, (int)layer.getTileWidth(), (int)layer.getTileHeight());
		for(int y=0 ; y<layer.getHeight() ; y++){
			for(int x=0 ; x<layer.getWidth() ; x++){
				newLayer.setCell(x, y, layer.getCell(x, y));
			}
		}
		return newLayer;
	}

	public static Rectangle getBounds(Rectangle bounds, TiledMap map){
		bounds.width = 0;
		bounds.height = 0;
		for(MapLayer layer : map.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				getBounds(bounds, (TiledMapTileLayer)layer);
			}
		}
		return bounds;
	}
	public static Rectangle getBounds(Rectangle bounds, TiledMapTileLayer tileLayer){
		bounds.width = Math.max(bounds.width, tileLayer.getWidth() * tileLayer.getTileWidth());
		bounds.height = Math.max(bounds.height, tileLayer.getHeight() * tileLayer.getTileHeight());
		return bounds;
	}

	public static TiledMap copy(TiledMap src) {
		TiledMap dst = new TiledMap();
		for(MapLayer layer : src.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer srcLayer = (TiledMapTileLayer)layer;
				TiledMapTileLayer newLayer = new TiledMapTileLayer(srcLayer.getWidth(), srcLayer.getHeight(), (int)srcLayer.getTileWidth(), (int)srcLayer.getTileHeight());
				for(int y=0 ; y<srcLayer.getHeight() ; y++){
					for(int x=0 ; x<srcLayer.getWidth() ; x++){
						Cell srcCell = srcLayer.getCell(x, y);
						if(srcCell != null && srcCell.getTile() != null){
							Cell dstCell = new Cell();
							dstCell.setTile(srcCell.getTile());
							newLayer.setCell(x, y, dstCell);
						}
					}
				}
				newLayer.setName(srcLayer.getName());
				dst.getLayers().add(newLayer);
			}
		}
		return dst;
	}

	
}
