package net.mgsx.ld41.utils;

import com.badlogic.gdx.math.MathUtils;

public class FormatUtils {

	public static String distanceToString(float tileDistance){
		return MathUtils.floor(tileDistance) + "m";
	}
}
