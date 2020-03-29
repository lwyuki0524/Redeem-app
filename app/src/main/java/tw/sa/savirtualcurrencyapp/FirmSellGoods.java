package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class FirmSellGoods extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_sell_goods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sell_goods_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(FirmSellGoods.this, FirmMainActivity.class));
                finish();//刪除此activity
            }
        });

        // 生成 QR Code

        ImageView image_view = (ImageView) findViewById(R.id.imageView);
        BarcodeEncoder encoder = new BarcodeEncoder();

        String firmID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("firmID", "");

        final String qrcode_content = "C" + firmID;

        try {
            Bitmap bit = encoder.encodeBitmap(qrcode_content, BarcodeFormat.QR_CODE,
                    250, 250);
            image_view.setImageBitmap(bit);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        /****
         *
         *       若收款成功，需要轉到firm_sell_success的介面(FirmSellSuccess.class)
         *
         ****/
    }
}
