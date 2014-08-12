package com.chuck.relativeschat.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 圆环图片
 * 外层拥有一圈透明的圆环
 * @author QD
 *
 */
public class CircleImageView{
	
	Context mContext;
	private final float pixels = 80.f;
	
	public CircleImageView(Context context){
		this.mContext = context;
	}
	
	public Bitmap toRoundCorner(int resourceId) {
		
		
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),resourceId);
		
//        System.out.println("图片是否变成圆角模式了+++++++++++++");
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
//        System.out.println("pixels+++++++" + pixels);

        return output;
    }
}
