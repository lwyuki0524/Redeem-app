package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirmActivityList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.firm_activity_list_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirmActivityList.this, FirmMainActivity.class));
                finish();//刪除此activity
            }
        });


        /*  選擇活動項目  */
        CardView activity_item = findViewById(R.id.list_activity1);
        activity_item.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirmActivityList.this,FirmActivityItem.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });


        /*  新增活動項目  */
        CardView add_activity_item = findViewById(R.id.add_activity_btn);
        add_activity_item.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirmActivityList.this,FirmAddActivity.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });

    }


}
