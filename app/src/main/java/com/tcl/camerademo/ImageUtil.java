package com.tcl.camerademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by arena on 2017/2/14.
 */

public class ImageUtil {
    public static Bitmap[] bitmap = new Bitmap[4];
    public static void init(Context context){
        bitmap[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.border_1);
        bitmap[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hefe_a);
        bitmap[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hefe_b);
        bitmap[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hefe_c);
    }
}
