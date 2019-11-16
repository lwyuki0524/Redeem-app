package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class FirmSignUp3 extends AppCompatActivity {

    private EditText commodity_name;
    private EditText commodity_money;
    private EditText commodity_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_sign_up3);

        commodity_name = (EditText) findViewById(R.id.commodity_name);
        commodity_money = (EditText) findViewById(R.id.commodity_money);
        commodity_amount = (EditText) findViewById(R.id.commodity_amount);

        Button lastbtn = findViewById(R.id.lastbtn);
        lastbtn.setOnClickListener(new Button.OnClickListener() {  //到註冊2
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirmSignUp3.this, FirmSignUp2.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });

        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new Button.OnClickListener() {  //確認註冊，到註冊4(登入)
            @Override
            public void onClick(View v) {

                // 廠商資料
                final String company_name = getSharedPreferences("firm_record", MODE_PRIVATE)
                        .getString("company_name", "");
                final String account = getSharedPreferences("firm_record", MODE_PRIVATE)
                        .getString("account", "");
                final String password = getSharedPreferences("firm_record", MODE_PRIVATE)
                        .getString("password", "");

                // 活動
                final String activity_name = getSharedPreferences("firm_record", MODE_PRIVATE)
                        .getString("activity_name", "");
                final int activity_money = Integer.valueOf(getSharedPreferences("firm_record", MODE_PRIVATE)
                        .getString("activity_money", ""));
                final String activity_location = getSharedPreferences("firm_record", MODE_PRIVATE)
                        .getString("activity_location", "");
                final String activity_textarea = getSharedPreferences("firm_record", MODE_PRIVATE)
                        .getString("activity_textarea", "");

                // 商品
                final String com_n = commodity_name.getText().toString();
                final int com_cm = Integer.valueOf(commodity_money.getText().toString());
                final int com_ca = Integer.valueOf(commodity_amount.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 生成 MySQL 連線資訊
                            MysqlInfo info = new MysqlInfo();
                            Connection con = info.getCon();
                            // 資料庫處理要求
                            String sql1 = "INSERT INTO `company` (`帳號`, `密碼`, `餘額`, `公司名稱`, `商標`) VALUES ('" + account + "', '" + password + "', '0', '" + company_name + "', '" + "" + "');";
                            String sql2 = "INSERT INTO `activity` (`company_account`, `activity_money`, `activity_name`, `activity_location`, `activity_textarea`) VALUES ('" + account + "', '" + activity_money + "', '" + activity_name + "', '" + activity_location + "', '" + activity_textarea + "');";
                            String sql3 = "INSERT INTO `commodity` (`廠商帳號`, `商品名稱`, `商品定價`, `商品數量`) VALUES ('" + account + "', '" + com_n + "', '" + com_cm + "', '" + com_ca + "');";
                            Statement st = con.createStatement();
                            st.executeUpdate(sql1);
                            st.executeUpdate(sql2);
                            st.executeUpdate(sql3);
                            st.close();
                            Log.v("DB", "成功寫入資料：" + sql1 + sql2 + sql3);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "註冊成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(FirmSignUp3.this, FirmSignUp4.class);
                                    startActivity(intent);
                                }
                            });
                        } catch (
                                SQLException e) {
                            Log.e("DB", "寫入資料失敗");
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "註冊失敗", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(FirmSignUp3.this, FirmSignUp1.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }).start();



            }
        });
    }
}
