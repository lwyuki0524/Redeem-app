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
import RoundPic.ToRoundImage;

public class MemberBoughtSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_bought_success);

        ImageView imageview = findViewById(R.id.goods_circle_pic);
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
        TextView p_name = (TextView) findViewById(R.id.p_name);
        TextView c_name = (TextView) findViewById(R.id.c_name);
        TextView date = (TextView) findViewById(R.id.date);
        TextView m_transaction_amount = (TextView) findViewById(R.id.m_transaction_amount);

        // 取得交易成功資訊
        String succ_p_name = getSharedPreferences("success", MODE_PRIVATE)
                .getString("succ_p_name", "");
        String succ_c_name = getSharedPreferences("success", MODE_PRIVATE)
                .getString("succ_c_name", "");
        String succ_date = getSharedPreferences("success", MODE_PRIVATE)
                .getString("succ_date", "");
        String succ_m_transaction_amount = getSharedPreferences("success", MODE_PRIVATE)
                .getString("succ_m_transaction_amount", "");

        // 顯示交易成功資訊
        p_name.setText(succ_p_name);
        c_name.setText(succ_c_name);
        date.setText(succ_date);
        m_transaction_amount.setText("$ " + succ_m_transaction_amount);

        /*  回主畫面  */
        Button home_btn = findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清空交易成功資訊
                SharedPreferences record = getSharedPreferences("success", MODE_PRIVATE);
                record.edit()
                        .putString("succ_p_name", "")
                        .putString("succ_c_name", "")
                        .putString("succ_date", "")
                        .putString("succ_m_transaction_amount", "")
                        .apply();
                // 跳轉至主畫面
                Intent intent=new Intent(MemberBoughtSuccess.this,MemberMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
