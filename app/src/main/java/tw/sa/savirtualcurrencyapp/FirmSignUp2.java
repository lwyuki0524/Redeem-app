package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirmSignUp2 extends AppCompatActivity implements View.OnTouchListener {

    private EditText multitext;
    private EditText activity_name;
    private EditText activity_money;
    private EditText activity_location;
    private EditText activity_maxPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_sign_up2);

        activity_name = (EditText) findViewById(R.id.activity_name);
        activity_money = (EditText) findViewById(R.id.activity_money);
        activity_location = (EditText) findViewById(R.id.activity_location);
        activity_maxPerson = (EditText) findViewById(R.id.activity_maxPerson);
        multitext = (EditText) findViewById(R.id.activity_textarea);
        multitext.setOnTouchListener(this);//設置多行文本可滑動

        // 取得欄位暫存檔
        try {
            final String sp_activity_name = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("activity_name", "");
            if (!sp_activity_name.equals("")) {
                activity_name.setText(sp_activity_name);
            }
            final String sp_activity_money = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("activity_money", "");
            if (!sp_activity_money.equals("")) {
                activity_money.setText(sp_activity_money);
            }
            final String sp_activity_location = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("activity_location", "");
            if (!sp_activity_location.equals("")) {
                activity_location.setText(sp_activity_location);
            }
            final String sp_activity_maxPerson = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("activity_maxPerson", "");
            if (!sp_activity_maxPerson.equals("")) {
                activity_maxPerson.setText(sp_activity_maxPerson);
            }
            final String sp_activity_textarea = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("activity_textarea", "");
            if (!sp_activity_textarea.equals("")) {
                multitext.setText(sp_activity_textarea);
            }
        } catch (Exception e) {
            Log.e("SP", "欄位暫存檔取得錯誤");
            e.printStackTrace();
        }


        /*  回註冊1 */
        Button lastbtn = findViewById(R.id.lastbtn);
        lastbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String an = activity_name.getText().toString();
                final String am = activity_money.getText().toString();
                final String al = activity_location.getText().toString();
                final String amp = activity_maxPerson.getText().toString();
                final String at = multitext.getText().toString();
                SharedPreferences record = getSharedPreferences("firm_record", MODE_PRIVATE);
                record.edit()
                        .putString("activity_name", an)
                        .putString("activity_money", am)
                        .putString("activity_location", al)
                        .putString("activity_maxPerson", amp)
                        .putString("activity_textarea", at)
                        .apply();
//                Intent intent = new Intent(FirmSignUp2.this, FirmSignUp1.class);
//                startActivity(intent);
                finish();
            }
        });


        /* 到註冊3 */
        Button nextbtn = findViewById(R.id.nextbtn);
        nextbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String an = activity_name.getText().toString();
                final String am = activity_money.getText().toString();
                final String al = activity_location.getText().toString();
                final String amp = activity_maxPerson.getText().toString();
                final String at = multitext.getText().toString();
                if (an.equals("") || am.equals("") || al.equals("") || at.equals("")) {
                    Toast.makeText(FirmSignUp2.this, "有欄位尚未完成", Toast.LENGTH_SHORT).show();
                } else { // 檢查通過
                    SharedPreferences record = getSharedPreferences("firm_record", MODE_PRIVATE);
                    record.edit()
                            .putString("activity_name", an)
                            .putString("activity_money", am)
                            .putString("activity_location", al)
                            .putString("activity_maxPerson", amp)
                            .putString("activity_textarea", at)
                            .apply();
                    Intent intent = new Intent(FirmSignUp2.this, FirmSignUp3.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //觸碰的是EditText並且當前EditText可以滾動則將事件交给EditText處理；否則將事件交由其父類處理
        if ((view.getId() == R.id.activity_textarea && canVerticalScroll(multitext))) {
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
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        //內容總高度與物件實際顯示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }


}
