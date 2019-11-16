package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemberBoughtProcess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_bought_process);

        // 讀取會員資訊
        final String account = getSharedPreferences("record", MODE_PRIVATE)
                .getString("account", "");

        // 讀取交易資訊
        final String transaction = getSharedPreferences("record", MODE_PRIVATE)
                .getString("transaction", "");

        // 檢查交易資訊
        if (transaction.equals("")) {
            Toast.makeText(getApplicationContext(), "交易錯誤", Toast.LENGTH_SHORT).show();
            // 跳轉回掃描畫面
            Intent intent = new Intent();
            intent.setClass(MemberBoughtProcess.this, MemberBoughtGoods.class);
            startActivity(intent);
            // 清空交易資訊
            SharedPreferences clear_record = getSharedPreferences("record", MODE_PRIVATE);
            clear_record.edit()
                    .putString("transaction", "")
                    .apply();
            // 結束
            finish();
        } else { // 檢查通過
            Toast.makeText(getApplicationContext(), "交易處理中", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 生成 MySQL 連線資訊
                        MysqlInfo info = new MysqlInfo();
                        Connection con = info.getCon();

                        // 檢查交易資訊是否正常
                        boolean check1 = false;
                        boolean check2 = false;
                        boolean check3 = false;

                        // 資料庫處理要求

                        // 1 取得會員資料
                        String m_name = "";
                        int m_money = 0;

                        PreparedStatement ps = con.prepareStatement("SELECT * FROM `member` WHERE `帳號` = ?");
                        ps.setString(1, account);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            m_name = rs.getString("會員暱稱");
                            m_money = rs.getInt("餘額");
                            Log.v("DB", "會員資料正確");
                            check1 = true;
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "交易失敗", Toast.LENGTH_SHORT).show();
                                    // 清空交易資訊
                                    SharedPreferences clear_record = getSharedPreferences("record", MODE_PRIVATE);
                                    clear_record.edit()
                                            .putString("transaction", "")
                                            .apply();
                                    // 跳轉回掃描畫面
                                    Intent intent = new Intent();
                                    intent.setClass(MemberBoughtProcess.this, MemberBoughtGoods.class);
                                    startActivity(intent);
                                }
                            });
                            Log.v("DB", "會員資料錯誤");
                            // 結束
                            finish();
                        }
                        rs.close();

                        // 2 取得廠商資料
                        String c_name = "";
                        int c_money = 0;

                        ps = con.prepareStatement("SELECT * FROM `company` WHERE `帳號` = ?");
                        ps.setString(1, transaction);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            c_name = rs.getString("公司名稱");
                            c_money = rs.getInt("餘額");
                            Log.v("DB", "廠商資料正確");
                            check2 = true;
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "交易失敗", Toast.LENGTH_SHORT).show();
                                    // 清空交易資訊
                                    SharedPreferences clear_record = getSharedPreferences("record", MODE_PRIVATE);
                                    clear_record.edit()
                                            .putString("transaction", "")
                                            .apply();
                                    // 跳轉回掃描畫面
                                    Intent intent = new Intent();
                                    intent.setClass(MemberBoughtProcess.this, MemberBoughtGoods.class);
                                    startActivity(intent);
                                }
                            });
                            Log.v("DB", "廠商資料錯誤");
                            // 結束
                            finish();
                        }
                        rs.close();

                        // 3 取得廠商的商品資料
                        String p_ID = "";
                        String p_name = "";
                        int p_money = 0;
                        int p_amount = 0;

                        ps = con.prepareStatement("SELECT * FROM `commodity` WHERE `廠商帳號` = ?");
                        ps.setString(1, transaction);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            p_ID = rs.getString("商品ID");
                            p_name = rs.getString("商品名稱");
                            p_money = rs.getInt("商品定價");
                            p_amount = rs.getInt("商品數量");
                            Log.v("DB", "廠商的商品資料正確");
                            check3 = true;
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "交易失敗", Toast.LENGTH_SHORT).show();
                                    // 清空交易資訊
                                    SharedPreferences clear_record = getSharedPreferences("record", MODE_PRIVATE);
                                    clear_record.edit()
                                            .putString("transaction", "")
                                            .apply();
                                    // 跳轉回掃描畫面
                                    Intent intent = new Intent();
                                    intent.setClass(MemberBoughtProcess.this, MemberBoughtGoods.class);
                                    startActivity(intent);
                                }
                            });
                            Log.v("DB", "廠商的商品資料錯誤");
                            // 結束
                            finish();
                        }
                        rs.close();

                        // 4 進行金額與數量的運算
                        int new_p_amount = p_amount - 1; // 新的商品數量 = 原有商品數量 - 1
                        int new_m_money = m_money - p_money; // 新的會員餘額 = 原有會員餘額 - 商品定價
                        int new_c_money = c_money + p_money; // 新的廠商餘額 = 原有廠商餘額 + 商品定價
                        int m_transaction_amount = 0 - p_money; // 會員的交易金額是負的

                        // 5 交易時間
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final Date date = new Date();

                        if (check1&&check2&&check3) {

                            // 6 寫入所有交易資料至資料庫
                            String sql1 = "UPDATE `member` SET `餘額` = '" + new_m_money + "' WHERE `member`.`帳號` = '" + account + "'";
                            String sql2 = "UPDATE `company` SET `餘額` = '" + new_c_money + "' WHERE `company`.`帳號` = '" + transaction + "'";
                            String sql3 = "UPDATE `commodity` SET `商品數量` = '" + new_p_amount + "' WHERE `commodity`.`商品ID` = '" + p_ID + "'";
                            String sql4 = "INSERT INTO `member_record` (`會員帳號`, `屬性`, `交易項目`, `交易對象`, `交易時間`, `交易金額`) VALUES ('" + account + "', '商品', '" + p_name + "', '" + c_name + "', '" + dateFormat.format(date) + "', '" + m_transaction_amount + "');";
                            String sql5 = "INSERT INTO `company_record` (`廠商帳號`, `屬性`, `交易項目`, `交易對象`, `交易時間`, `售價金額`) VALUES ('" + transaction + "', '商品', '" + p_name + "', '" + m_name + "', '" + dateFormat.format(date) + "', '" + p_money + "');";
                            Statement st = con.createStatement();
                            st.executeUpdate(sql1);
                            st.executeUpdate(sql2);
                            st.executeUpdate(sql3);
                            st.executeUpdate(sql4);
                            st.executeUpdate(sql5);
                            st.close();
                            Log.v("DB", "成功寫入資料：" + sql1 + sql2 + sql3 + sql4 + sql5);

                            // 7 給交易成功畫面的資訊
                            final String succ_p_name = p_name;
                            final String succ_c_name = c_name;
                            final String succ_m_transaction_amount = Integer.toString(m_transaction_amount);

                            // 8 交易成功處理
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 儲存交易成功資訊
                                    SharedPreferences record = getSharedPreferences("success", MODE_PRIVATE);
                                    record.edit()
                                            .putString("succ_p_name", succ_p_name)
                                            .putString("succ_c_name", succ_c_name)
                                            .putString("succ_date", date.toString())
                                            .putString("succ_m_transaction_amount", succ_m_transaction_amount)
                                            .apply();
                                    // 清空交易資訊
                                    SharedPreferences clear_record = getSharedPreferences("record", MODE_PRIVATE);
                                    clear_record.edit()
                                            .putString("transaction", "")
                                            .apply();
                                    // 成功訊息
                                    Toast.makeText(getApplicationContext(), "交易成功", Toast.LENGTH_SHORT).show();
                                    // 跳轉至交易成功畫面
                                    Intent intent = new Intent();
                                    intent.setClass(MemberBoughtProcess.this, MemberBoughtSuccess.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "交易失敗", Toast.LENGTH_SHORT).show();
                            // 清空交易資訊
                            SharedPreferences clear_record = getSharedPreferences("record", MODE_PRIVATE);
                            clear_record.edit()
                                    .putString("transaction", "")
                                    .apply();
                            // 跳轉回掃描畫面
                            Intent intent = new Intent();
                            intent.setClass(MemberBoughtProcess.this, MemberBoughtGoods.class);
                            startActivity(intent);
                            // 結束
                            finish();
                        }


                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "交易錯誤", Toast.LENGTH_SHORT).show();
                                // 清空交易資訊
                                SharedPreferences clear_record = getSharedPreferences("record", MODE_PRIVATE);
                                clear_record.edit()
                                        .putString("transaction", "")
                                        .apply();
                                // 跳轉回掃描畫面
                                Intent intent = new Intent();
                                intent.setClass(MemberBoughtProcess.this, MemberBoughtGoods.class);
                                startActivity(intent);
                            }
                        });
                        e.printStackTrace();
                        // 結束
                        finish();
                    }
                }
            }).start();
        }
    }
}
