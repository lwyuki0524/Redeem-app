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

public class FirmPaidProcess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_paid_process);

        // 讀取廠商資訊
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
            intent.setClass(FirmPaidProcess.this, FirmPaidMoney.class);
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
                        ps.setString(1, transaction);
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
                                    intent.setClass(FirmPaidProcess.this, FirmPaidMoney.class);
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
                        ps.setString(1, account);
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
                                    intent.setClass(FirmPaidProcess.this, FirmPaidMoney.class);
                                    startActivity(intent);
                                }
                            });
                            Log.v("DB", "廠商資料錯誤");
                            // 結束
                            finish();
                        }
                        rs.close();

                        // 3 取得廠商的活動資料
                        String a_ID = "";
                        String a_name = "";
                        int a_money = 0;
                        int a_amount = 0;

                        ps = con.prepareStatement("SELECT * FROM `activity` WHERE `company_account` = ?");
                        ps.setString(1, account);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            a_ID = rs.getString("activity_ID");
                            a_name = rs.getString("activity_name");
                            a_money = rs.getInt("activity_money");
                            Log.v("DB", "廠商的活動資料正確");
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
                                    intent.setClass(FirmPaidProcess.this, FirmPaidMoney.class);
                                    startActivity(intent);
                                }
                            });
                            Log.v("DB", "廠商的商品資料錯誤");
                            // 結束
                            finish();
                        }
                        rs.close();

                        // 4 進行金額的運算
                        int new_m_money = m_money + a_money; // 新的會員餘額 = 原有會員餘額 + 活動紅包
                        int new_c_money = c_money - a_money; // 新的廠商餘額 = 原有廠商餘額 - 活動紅包
                        int c_transaction_amount = 0 - a_money; // 廠商的交易金額是負的

                        // 5 交易時間
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final Date date = new Date();

                        if (check1&&check2&&check3) {

                            // 6 寫入所有交易資料至資料庫
                            String sql1 = "UPDATE `member` SET `餘額` = '" + new_m_money + "' WHERE `member`.`帳號` = '" + transaction + "'";
                            String sql2 = "UPDATE `company` SET `餘額` = '" + new_c_money + "' WHERE `company`.`帳號` = '" + account + "'";
                            String sql3 = "INSERT INTO `member_record` (`會員帳號`, `屬性`, `交易項目`, `交易對象`, `交易時間`, `交易金額`) VALUES ('" + transaction + "', '活動', '" + a_name + "', '" + c_name + "', '" + dateFormat.format(date) + "', '" + a_money + "');";
                            String sql4 = "INSERT INTO `company_record` (`廠商帳號`, `屬性`, `交易項目`, `交易對象`, `交易時間`, `售價金額`) VALUES ('" + account + "', '活動', '" + a_name + "', '" + m_name + "', '" + dateFormat.format(date) + "', '" + c_transaction_amount + "');";
                            Statement st = con.createStatement();
                            st.executeUpdate(sql1);
                            st.executeUpdate(sql2);
                            st.executeUpdate(sql3);
                            st.executeUpdate(sql4);
                            st.close();
                            Log.v("DB", "成功寫入資料：" + sql1 + sql2 + sql3 + sql4);

                            // 7 給交易成功畫面的資訊
                            final String succ_a_name = a_name;
                            final String succ_m_name = m_name;
                            final String succ_c_transaction_amount = Integer.toString(c_transaction_amount);

                            // 8 交易成功處理
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 儲存交易成功資訊
                                    SharedPreferences record = getSharedPreferences("success", MODE_PRIVATE);
                                    record.edit()
                                            .putString("succ_a_name", succ_a_name)
                                            .putString("succ_m_name", succ_m_name)
                                            .putString("succ_date", date.toString())
                                            .putString("succ_c_transaction_amount", succ_c_transaction_amount)
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
                                    intent.setClass(FirmPaidProcess.this, FirmPaidSuccess.class);
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
                            intent.setClass(FirmPaidProcess.this, FirmPaidMoney.class);
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
                                intent.setClass(FirmPaidProcess.this, FirmPaidMoney.class);
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
