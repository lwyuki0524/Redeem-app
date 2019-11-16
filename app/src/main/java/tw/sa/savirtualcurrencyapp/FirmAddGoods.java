package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirmAddGoods extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_add_goods);

        /*  返回上一頁(新增活動)  */
        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirmAddGoods.this,FirmAddActivity.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });


        /*  確定新增商品  */
        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirmAddGoods.this,FirmActivityList.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });

    }


    public void add_good_upload(View view){
        Intent intent=new Intent(this,FirmActivityList.class);
        /*
            要記得存資料
         */
        startActivity(intent);
    }
}
