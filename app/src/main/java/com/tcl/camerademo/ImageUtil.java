package com.tcl.camerademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by arena on 2017/2/14.
 */

public class ImageUtil {
    public static Bitmap[] bitmap = new Bitmap[2];
    public static void init(Context context){
        bitmap[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.edge_1);
        bitmap[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_1);
    }
}
