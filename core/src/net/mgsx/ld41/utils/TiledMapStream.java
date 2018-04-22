package net.mgsx.ld41.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class TiledMapStream {

	private TiledMapLink head, tail;
	private TiledMap map;
	private int offsetX;
	private int sourceOffsetX;
	private int fillX;
	private int sizeX, sizeY;
	private boolean cloneMode;
	
	public TiledMapStream(TiledMap mapBase, int columns, boolean cloneMode) 
	{
		this.cloneMode = cloneMode;
		map = new TiledMap();
		sizeX = columns;
		sizeY = mapBase.getProperties().get("height", Integer.class);
		// copy layers (empty)
		for(MapLayer layer : mapBase.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer baseTileLayer = (TiledMapTileLayer)layer;
				TiledMapTileLayer tileLayer = new TiledMapTileLayer(columns, baseTileLayer.getHeight(), (int)baseTileLayer.getTileWidth(), (int)baseTileLayer.getTileHeight());
				tileLayer.setName(layer.getName());
				map.getLayers().add(tileLayer);
			}
		}
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	public TiledMapLink appendMap(TiledMap map){
		TiledMapLink mapLink = new TiledMapLink();
		mapLink.map = map;
		mapLink.sizeX = map.getProperties().get("width", Integer.class);
		mapLink.previousMap = tail;
		if(tail != null){
			tail.nextMap = mapLink;
		}
		tail = mapLink;
		if(head == null){
			head = tail;
		}
		return mapLink;
	}
	
	public int getOffsetX() {
		return offsetX;
	}
	
	public void update(float worldX){
		
		// remove some tiles (copy map stream)
		int newOffsetX = (int)(worldX / 32f);
		int deltaX = newOffsetX - offsetX;
		
		if(deltaX > 0){
			for(MapLayer layer : map.getLayers()){
				TiledMapTileLayer streamLayer = (TiledMapTileLayer)layer;
				for(int ix = 0 ; ix<sizeX ; ix++){
					int srcX = ix + deltaX;
					int dstX = ix;
					if(srcX < sizeX){
						for(int iy=0 ; iy<sizeY ; iy++){
							streamLayer.setCell(dstX, iy, streamLayer.getCell(srcX, iy));
						}
					}
				}
			}
			offsetX += deltaX;
			fillX -= deltaX;
			
			//System.out.println("offsetX " + offsetX);
		}
		
		// add some tiles (fill map stream)
		while(fillX < sizeX){
			
			TiledMap sourceMap = head.map;
			
			int dstX = fillX; // TODO offset ...
			int srcX = offsetX + fillX + sourceOffsetX; // TODO offset
			
			if(srcX >= head.sizeX){
				sourceOffsetX -= head.sizeX;
				head = head.nextMap;
				srcX = offsetX + fillX + sourceOffsetX;
			}
			
			for(MapLayer layer : map.getLayers()){
				TiledMapTileLayer streamLayer = (TiledMapTileLayer)layer;
				TiledMapTileLayer sourceLayer = (TiledMapTileLayer)sourceMap.getLayers().get(layer.getName());
				
				for(int iy=0 ; iy<sizeY ; iy++){
					Cell srcCell;
					if(cloneMode){
						srcCell = copyCell(sourceLayer.getCell(srcX, iy));
					}else{
						srcCell = sourceLayer.getCell(srcX, iy);
					}
					streamLayer.setCell(dstX, iy, srcCell);
				}
			}
			
			// System.out.println("loaded row " + srcX + " into " + dstX);
			
			fillX++;
		}
	}

	private Cell copyCell(Cell cell) 
	{
		Cell clone = null;
		if(cell != null){
			clone = new Cell();
			clone.setFlipHorizontally(cell.getFlipHorizontally());
			clone.setFlipVertically(cell.getFlipVertically());
			clone.setRotation(cell.getRotation());
			clone.setTile(cell.getTile());
		}
		return clone;
	}

	public TiledMapTileLayer getTileLayer(String name) {
		return (TiledMapTileLayer)map.getLayers().get(name);
	}

	public void begin(Camera camera) {
		camera.position.x -= offsetX * 32;
		camera.update();
	}
	
	public void end(Camera camera) {
		camera.position.x += offsetX * 32;
		camera.update();
	}

	/**
	 * Get cell from a map stream layer
	 * @param layer
	 * @param ix
	 * @param iy
	 * @return
	 */
	public Cell getCell(TiledMapTileLayer layer, int ix, int iy) {
		return layer.getCell(ix - offsetX, iy);
	}

	public int getWidth() {
		return sizeX;
	}

	public TiledMapLink currentMap() {
		return head;
	}
}
