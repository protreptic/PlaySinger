package name.peterbukhal.android.playsinger.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class Fonts {
	
	private static Fonts sInstance;
	
	private Context mContext;
	private Map<String, Typeface> mTypefaces = new HashMap<String, Typeface>();

	private Fonts(Context context) {
		mContext = context;
	}
	
	public static Fonts get(Context context) {
		if (sInstance == null) {
			sInstance = new Fonts(context);
		}
		
		return sInstance;
	}
	
	public Typeface getTypeface(String typeface) {
		Typeface result = mTypefaces.get(typeface);
		
		if (result == null) {
			result = Typeface.createFromAsset(mContext.getAssets(), "font/" + typeface + ".ttf");
			if (result == null) {
				result = Typeface.DEFAULT;
			} else {
				mTypefaces.put(typeface, result);
			}
		}
		
		return result;
	}

}
