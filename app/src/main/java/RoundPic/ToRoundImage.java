package RoundPic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

public class ToRoundImage {
    public static  Bitmap toRoundBitmap(Bitmap bitmap) {
//圓形圖片寬高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
//正方形的邊長
        int r = 0;
//取最短邊做邊長
        if(width > height) {
            r = height;
        } else {
            r = width;
        }
//構建一個bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
//new一個Canvas,在backgroundBmp上畫圖
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
//設定邊緣光滑,去掉鋸齒
        paint.setAntiAlias(true);
//寬高相等,即正方形
        RectF rect = new RectF(0, 0, r, r);
//通過制定的rect畫一個圓角矩形,當圓角X軸方向的半徑等於Y軸方向的半徑時,
//且都等於r/2時,畫出來的圓角矩形就是圓形
        canvas.drawRoundRect(rect, r/2, r/2, paint);
//設定當兩個圖形相交時的模式,SRC_IN為取SRC圖形相交的部分,多餘的將被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//canvas將bitmap畫在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
//返回已經繪畫好的backgroundBmp
        if(bitmap.getWidth()>300||bitmap.getHeight()>300){
            return small(backgroundBmp);
        }
        else
            return big(backgroundBmp);
    }


    /**Bitmap放大的方法*/
    private static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1.5f,1.5f); //放大比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    /**Bitmap缩小的方法*/

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.3f,0.3f); //缩小比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
}
