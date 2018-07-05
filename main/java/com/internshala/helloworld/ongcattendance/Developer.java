package com.internshala.helloworld.ongcattendance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Developer extends AppCompatActivity {
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        Bitmap bm = BitmapFactory.decodeResource(getResources(),
                R.drawable.pp);
        Bitmap bm1 = BitmapFactory.decodeResource(getResources(),
                R.drawable.abcd);
        Bitmap bm2= BitmapFactory.decodeResource(getResources(),
                R.drawable.abcde);
        Bitmap bm3 = BitmapFactory.decodeResource(getResources(),
                R.drawable.abc1);
        // set circle bitmap
        ImageView mImage = (ImageView) findViewById(R.id.imageView2);
        mImage.setImageBitmap(getCircleBitmap(bm));
       ImageView mImage1 = (ImageView) findViewById(R.id.imageView4);
        mImage1.setImageBitmap(getCircleBitmap(bm1));
        ImageView mImage2 = (ImageView) findViewById(R.id.imageView5);
        mImage2.setImageBitmap(getCircleBitmap(bm2));
        ImageView mImage3 = (ImageView) findViewById(R.id.imageView3);
        mImage3.setImageBitmap(getCircleBitmap(bm3));


    }
}
