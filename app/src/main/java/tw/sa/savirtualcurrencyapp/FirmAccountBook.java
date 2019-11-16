package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirmAccountBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_account_book);

        final TextView book_output = (TextView) findViewById(R.id.book_output);
        Toolbar toolbar = (Toolbar) findViewById(R.id.firm_account_book_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirmAccountBook.this, FirmMainActivity.class));
                finish();//刪除此activity(可刪除)
            }
        });

        // 讀取日記簿資料

        final String account = getSharedPreferences("record", MODE_PRIVATE)
                .getString("account", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();
                    // 資料庫處理要求
                    String sql = "SELECT * FROM `company_record` WHERE `廠商帳號` = '" + account + "' ORDER BY `交易時間` DESC ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        final String type = rs.getString("屬性");

                        if (type.equals("商品")) {

                            final String tran_name = rs.getString("交易項目");
                            final String tran_object = rs.getString("交易對象");
                            final String tran_time = rs.getString("交易時間");
                            final String tran_money = rs.getString("售價金額");

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    book_output.append("【收入】\n賣出商品：" + tran_name + "\n交易對象：" + tran_object + "\n交易時間：" + tran_time + "\n交易金額：" + tran_money + "\n\n");
                                }
                            });

                        } else if (type.equals("活動")) {

                            final String tran_name = rs.getString("交易項目");
                            final String tran_object = rs.getString("交易對象");
                            final String tran_time = rs.getString("交易時間");
                            final String tran_money = rs.getString("售價金額");

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    book_output.append("【支出】\n活動名稱：" + tran_name + "\n交易對象：" + tran_object + "\n交易時間：" + tran_time + "\n交易金額：" + tran_money + "\n\n");
                                }
                            });

                        }
                    }
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            book_output.setText("無資料");
                        }
                    });
                }
            }
        }).start();

    }
}
