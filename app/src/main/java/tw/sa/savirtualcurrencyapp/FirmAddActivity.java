package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FirmAddActivity extends AppCompatActivity implements View.OnTouchListener{


    private EditText multitext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_add_activity);


        multitext = (EditText)findViewById(R.id.textarea);
        multitext.setOnTouchListener(this);//設置多行文本可滑動


        Toolbar toolbar = (Toolbar) findViewById(R.id.firm_add_activity_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirmAddActivity.this, FirmActivityList.class));
                finish();
            }
        });


        /*  取消按鈕  */
        Button cancel = findViewById(R.id.cancelbtn);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent=new Intent(FirmAddActivity.this,FirmAddGoods.class);
                /*
                    存資料
                */
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //觸碰的是EditText並且當前EditText可以滾動則將事件交给EditText處理；否則將事件交由其父類處理
        if ((view.getId() == R.id.textarea && canVerticalScroll(multitext))) {
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
