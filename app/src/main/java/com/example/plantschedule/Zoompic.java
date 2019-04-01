package com.example.plantschedule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Zoompic {
    public static Bitmap adjustImage(String absolutePath,Bitmap bm) {
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inJustDecodeBounds = true; // isjustdecodebounds is important!
        bm = BitmapFactory.decodeFile(absolutePath, opt);

        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        int screenWidth = 700;
        int screenHeight = 1300;

        opt.inSampleSize = 2;           //compression ratio

        if (picWidth > picHeight) {     // calculate scaling magnification
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        } else {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight;
        }

        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(absolutePath, opt);
        return bm;                   // construct the pixel
    }
    public static Bitmap adjustImage2(String absolutePath,Bitmap bm) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        bm = BitmapFactory.decodeFile(absolutePath, opt);

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度

        int screenWidth = 450;
        int screenHeight = 800;

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 4;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        } else {
            if (picHeight > screenHeight)

                opt.inSampleSize = picHeight / screenHeight;
        }

        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(absolutePath, opt);
        return bm;
    }
}
