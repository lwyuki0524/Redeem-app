package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class member_login extends AppCompatActivity {

    private EditText account;
    private EditText password;
    private Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("會員登入");
        setContentView(R.layout.activity_member_login);

        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        upload = (Button) findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ac = account.getText().toString();
                final String pw = password.getText().toString();
                // 帳號、密碼檢查
                if (ac.equals("") || pw.equals("")) {
                    Toast.makeText(getApplicationContext(), "有欄位尚未完成", Toast.LENGTH_SHORT).show();
                } else if (6 > ac.length() || 6 > pw.length()) {
                    Toast.makeText(getApplicationContext(), "帳號或密碼輸入錯誤", Toast.LENGTH_SHORT).show();
                } else { // 檢查通過
                    Toast.makeText(getApplicationContext(), "登入執行中...", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 生成 MySQL 連線資訊
                                MysqlInfo info = new MysqlInfo();
                                Connection con = info.getCon();
                                // 資料庫處理要求
                                PreparedStatement ps = con.prepareStatement("SELECT * FROM `member` WHERE `帳號` = ? AND `密碼` = ?");
                                ps.setString(1, ac);
                                ps.setString(2, pw);
                                //ps.execute();
                                ResultSet rs = ps.executeQuery();
                                if (rs.next()) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "成功登入，歡迎使用！", Toast.LENGTH_SHORT).show();
                                            // 儲存登入資訊
                                            SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                                            record.edit()
                                                    .putString("type", "member")
                                                    .putString("account", ac)
                                                    .apply();
                                            // 跳轉至會員主畫面
                                            Intent intent = new Intent();
                                            intent.setClass(member_login.this, MemberMainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    Log.v("DB", "成功登入");
                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "帳號或密碼輸入錯誤", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Log.v("DB", "登入失敗");
                                }
                                rs.close();

                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "登入失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }
}
