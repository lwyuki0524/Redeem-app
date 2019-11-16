package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 讀取登入資訊
        String getAccountRecord = getSharedPreferences("record", MODE_PRIVATE)
                .getString("type", "");

        // 屬性屬於「會員」跳轉至「會員主畫面」
        if (getAccountRecord.equals("member")) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this  , MemberMainActivity.class);
            startActivity(intent);
            finish();
        }
        // 屬性屬於「廠商」跳轉至「廠商主畫面」
        if (getAccountRecord.equals("company")) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this  , FirmMainActivity.class);
            startActivity(intent);
            finish();
        }

        // 會員註冊按鈕
        Button member_register = (Button)findViewById(R.id.index_member_register);

        member_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , member_register.class);
                startActivity(intent);
            }
        });

        // 會員登入按鈕
        Button member_login = (Button)findViewById(R.id.index_member_login);

        member_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , member_login.class);
                startActivity(intent);
            }
        });

        // 廠商註冊按鈕
        Button company_register = (Button)findViewById(R.id.index_company_register);

        company_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , FirmSignUp1.class);
                startActivity(intent);
            }
        });

        // 廠商登入按鈕
        Button company_login = (Button)findViewById(R.id.index_company_login);

        company_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , FirmLogin.class);
                startActivity(intent);
            }
        });


    }
}
