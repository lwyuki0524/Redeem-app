package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import RoundPic.AdjustBitmap;
import RoundPic.CircleBitmapByShader;
import RoundPic.ToRoundImage;

public class FirmPaidSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_paid_success);

        ImageView imageview = findViewById(R.id.member_circle_pic);
        // 以資料流的方式讀取bitmap資源
        Resources r = this.getResources();
        @SuppressLint("ResourceType") InputStream is = r.openRawResource(R.drawable.check_icon);
        BitmapDrawable bmpDraw = new BitmapDrawable(r, is);
        Bitmap bmp = bmpDraw.getBitmap();

        // 將圖片轉換成圓形圖片
        Bitmap bm = AdjustBitmap.getCircleBitmap(bmp);
//        Bitmap bm = ToRoundImage.toRoundBitmap(bmp);

        //傳給imagview進行顯示
        imageview.setImageBitmap(bm);

        // 宣告文字方塊
        TextView a_name = (TextView) findViewById(R.id.a_name);
        TextView m_name = (TextView) findViewById(R.id.m_name);
        TextView date = (TextView) findViewById(R.id.date);
        TextView c_transaction_amount = (TextView) findViewById(R.id.c_transaction_amount);

        // 取得交易成功資訊
        String succ_a_name = getSharedPreferences("success", MODE_PRIVATE)
                .getString("succ_a_name", "");
        String succ_m_name = getSharedPreferences("success", MODE_PRIVATE)
                .getString("succ_m_name", "");
        String succ_date = getSharedPreferences("success", MODE_PRIVATE)
                .getString("succ_date", "");
        String succ_c_transaction_amount = getSharedPreferences("success", MODE_PRIVATE)
                .getString("succ_c_transaction_amount", "");

        // 顯示交易成功資訊
        a_name.setText(succ_a_name);
        m_name.setText(succ_m_name);
        date.setText(succ_date);
        c_transaction_amount.setText("$ " + succ_c_transaction_amount);

        /*  回主畫面  */
        Button home_btn = findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清空交易成功資訊
                SharedPreferences record = getSharedPreferences("success", MODE_PRIVATE);
                record.edit()
                        .putString("succ_a_name", "")
                        .putString("succ_m_name", "")
                        .putString("succ_date", "")
                        .putString("succ_c_transaction_amount", "")
                        .apply();
                // 跳轉至主畫面
                Intent intent = new Intent(FirmPaidSuccess.this, FirmMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
