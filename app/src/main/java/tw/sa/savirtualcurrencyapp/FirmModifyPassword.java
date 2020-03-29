package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class FirmModifyPassword extends AppCompatActivity {

    private TextView old_password;
    private TextView new_password;
    private TextView re_new_password;
    private Button confirm_modify;
    private String O_PW, N_PW, R_N_PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_modify_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.all_activity_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(FirmModifyPassword.this, FirmModifyInfo.class));
                finish();//刪除此activity(可刪除)
            }
        });

        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);
        re_new_password = findViewById(R.id.re_new_password);
        confirm_modify = findViewById(R.id.confirm_modify);

        // 讀取廠商資訊
        final String firmID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("firmID", "");

        // 確認修改按鈕
        confirm_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 取得輸入資料
                O_PW = old_password.getText().toString();
                N_PW = new_password.getText().toString();
                R_N_PW = re_new_password.getText().toString();

                if (N_PW.equals(R_N_PW) && N_PW.length() >= 6 && O_PW.length() >= 6) { // 確認新密碼是否正確

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                // 生成 MySQL 連線資訊
                                MysqlInfo info = new MysqlInfo();
                                Connection con = info.getCon();
                                // 資料庫處理要求
                                String sql = "UPDATE `company` SET `密碼`='" + N_PW + "' WHERE `廠商ID` = '" + firmID + "' AND `密碼` = '" + O_PW + "'";
                                Log.v("DB", sql);
                                Statement st = con.createStatement();

                                int rs = st.executeUpdate(sql);
                                if (rs > 0) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    finish();
                                    Log.v("DB", "更新成功");
                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "錯誤，修改失敗", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Log.v("DB", "更新失敗");
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();

                } else {
                    Toast.makeText(getApplicationContext(), "輸入錯誤", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}