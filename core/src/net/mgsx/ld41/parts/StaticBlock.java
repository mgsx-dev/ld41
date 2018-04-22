package net.mgsx.ld41.parts;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class StaticBlock {
	public Array<GridPoint2> groundTiles = new Array<GridPoint2>(36);
	public Array<GridPoint2> decoTiles = new Array<GridPoint2>(36);
	public int xMax;
}
