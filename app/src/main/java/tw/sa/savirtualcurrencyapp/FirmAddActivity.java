package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirmAddActivity extends AppCompatActivity implements View.OnTouchListener{


//    private EditText multitext;
    private EditText act_name;
    private EditText act_money;
    private EditText act_address;
    private EditText act_maxPerson;
    private EditText act_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_add_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.firm_add_activity_toolbar);
        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirmAddActivity.this, FirmActivityList.class));
                finish();
            }
        });


//        multitext = (EditText)findViewById(R.id.act_content);

        act_name= (EditText)findViewById(R.id.act_name);
        act_money= (EditText)findViewById(R.id.act_money);
        act_address= (EditText)findViewById(R.id.act_address);
        act_maxPerson= (EditText)findViewById(R.id.act_maxPerson);
        act_content= (EditText)findViewById(R.id.act_content);
        act_content.setOnTouchListener(this);//設置多行文本可滑動

        String name = getSharedPreferences("record", MODE_PRIVATE).getString("act_name", "");
        String money = getSharedPreferences("record", MODE_PRIVATE).getString("act_money", "");
        String address = getSharedPreferences("record", MODE_PRIVATE).getString("act_address", "");
        String maxPerson = getSharedPreferences("record", MODE_PRIVATE).getString("act_maxPerson", "");
        String content = getSharedPreferences("record", MODE_PRIVATE).getString("act_content", "");

        if(name!=""){
            act_name.setText(name);
        }
        if(money!=""){
            act_money.setText(money);
        }
        if(address!=""){
            act_address.setText(address);
        }
        if(maxPerson!=""){
            act_maxPerson.setText(maxPerson);
        }
        if(content!=""){
            act_content.setText(content);
        }



        /*  取消按鈕  */
        Button cancel = findViewById(R.id.cancelbtn);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("record", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.remove("act_name")
                        .remove("act_money")
                        .remove("act_address")
                        .remove("act_maxPerson")
                        .remove("act_content");
                edit.commit();

                Intent intent=new Intent(FirmAddActivity.this,FirmActivityList.class);
                startActivity(intent);
                finish();
            }
        });


        /*  確認按鈕  */
        Button confirm = findViewById(R.id.confirm_btn);
        confirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String an = act_name.getText().toString();
                final String am = act_money.getText().toString();
                final String ad = act_address.getText().toString();
                final String amP = act_maxPerson.getText().toString();
                final String ac = act_content.getText().toString();

                // 檢查是否都填了
                if (an.equals("") || am.equals("") || ad.equals("")|| amP.equals("")|| ac.equals("")) {
                    Toast.makeText(FirmAddActivity.this, "有欄位尚未完成", Toast.LENGTH_SHORT).show();
                }
                else { // 檢查通過

                    // 儲存資料
                    SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                    record.edit()
                            .putString("act_name", an)
                            .putString("act_money", am)
                            .putString("act_address", ad)
                            .putString("act_maxPerson", amP)
                            .putString("act_content", ac)
                            .commit();

                    Intent intent=new Intent(FirmAddActivity.this,FirmAddGoods.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //觸碰的是EditText並且當前EditText可以滾動則將事件交给EditText處理；否則將事件交由其父類處理
        if ((view.getId() == R.id.act_content && canVerticalScroll(act_content))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    private boolean canVerticalScroll(EditText editText) {
        //滾動的距離
        int scrollY = editText.getScrollY();
        //內容總高度
        int scrollRange = editText.getLayout().getHeight();
        //物件實際顯示高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //內容總高度與物件實際顯示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }


}
