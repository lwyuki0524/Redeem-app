package tw.sa.savirtualcurrencyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MemberAccountBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_account_book);

        final TextView book_output = (TextView) findViewById(R.id.book_output);
        Toolbar toolbar = (Toolbar) findViewById(R.id.member_account_book_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemberAccountBook.this, MemberMainActivity.class));
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
                    String sql = "SELECT * FROM `member_record` WHERE `會員帳號` = '" + account + "' ORDER BY `交易時間` DESC ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        final String type = rs.getString("屬性");

                        if (type.equals("商品")) {

                            final String tran_name = rs.getString("交易項目");
                            final String tran_object = rs.getString("交易對象");
                            final String tran_time = rs.getString("交易時間");
                            final String tran_money = rs.getString("交易金額");

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    book_output.append("【支出】\n買入商品：" + tran_name + "\n交易對象：" + tran_object + "\n交易時間：" + tran_time + "\n交易金額：" + tran_money + "\n\n");
                                }
                            });

                        } else if (type.equals("活動")) {

                            final String tran_name = rs.getString("交易項目");
                            final String tran_object = rs.getString("交易對象");
                            final String tran_time = rs.getString("交易時間");
                            final String tran_money = rs.getString("交易金額");

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    book_output.append("【收入】\n活動名稱：" + tran_name + "\n交易對象：" + tran_object + "\n交易時間：" + tran_time + "\n交易金額：" + tran_money + "\n\n");
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
