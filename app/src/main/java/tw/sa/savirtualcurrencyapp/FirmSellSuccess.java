package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.InputStream;

import RoundPic.ToRoundImage;

public class FirmSellSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_sell_success);


        ImageView imageview = findViewById(R.id.goods_circle_pic);
        // 以資料流的方式讀取bitmap資源
        Resources r = this.getResources();
        @SuppressLint("ResourceType") InputStream is = r.openRawResource(R.drawable.pic_coffee);
        BitmapDrawable bmpDraw = new BitmapDrawable(r, is);
        Bitmap bmp = bmpDraw.getBitmap();

// 將圖片轉換成圓形圖片
        Bitmap bm = ToRoundImage.toRoundBitmap(bmp);
//傳給imagview進行顯示
        imageview.setImageBitmap(bm);
    }


}
