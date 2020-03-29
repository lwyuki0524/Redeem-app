package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemberBoughtProcess extends AppCompatActivity {

    private int payment = 0; // 付款金額

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_bought_process);

        // 讀取會員資訊
        final String memberID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("memberID", "");

        // 讀取交易資訊
        final String transaction = getSharedPreferences("record", MODE_PRIVATE)
                .getString("transaction", "");

        // 檢查交易資訊
        if (transaction.substring(0, 1).equals("C")) { // 來自廠商的交易事件
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

                        PreparedStatement ps = con.prepareStatement("SELECT * FROM `member` WHERE `會員ID` = ?");
                        ps.setString(1, memberID);
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

                        ps = con.prepareStatement("SELECT * FROM `company` WHERE `廠商ID` = ?");
                        ps.setString(1, transaction.substring(1));
                        Log.v("DB ", "廠商ID="+transaction.substring(1));
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

                        String sql=" SELECT `*` FROM `commodity` WHERE `commodity`.`廠商ID` = ? AND `commodity`.`活動ID` IN ( ";
                        sql+=" SELECT `活動ID`   FROM `activity`  WHERE  `activity`. `廠商ID` = ? AND  `activity`.`目前活動`='是') ";

                        ps = con.prepareStatement(sql);
                        ps.setString(1, transaction.substring(1));
                        ps.setString(2, transaction.substring(1));
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            p_ID = rs.getString("商品ID");
                            p_name = rs.getString("商品名稱");
                            p_money = rs.getInt("商品定價");
                            p_amount = rs.getInt("庫存");
                            Log.v("DB", "廠商的商品資料正確");
                            final String finalP_ID = p_ID;
                            final String finalP_name = p_name;


                            check3 = true;
                        }

                        rs = ps.executeQuery();
                        if (!rs.next()){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "交易失敗:商品資料錯誤", Toast.LENGTH_SHORT).show();
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
                        int new_p_amount = p_amount - 1; // 新的商品庫存 = 原有商品庫存 - 1
                        int new_m_money = m_money - p_money; // 新的會員餘額 = 原有會員餘額 - 商品定價
                        int new_c_money = c_money + p_money; // 新的廠商餘額 = 原有廠商餘額 + 商品定價
                        int m_transaction_amount = 0 - p_money; // 會員的交易金額是負的

                        if (new_p_amount >= 0 && new_m_money >= 0) { // 新的商品庫存以及新的會員餘額不得小於 0

                            // 5 交易時間
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            final Date date = new Date();

                            if (check1 && check2 && check3) {

                                // 6 寫入所有交易資料至資料庫
                                String sql1 = "UPDATE `member` SET `餘額` = '" + new_m_money + "' WHERE `member`.`會員ID` = '" + memberID + "'";
                                String sql2 = "UPDATE `company` SET `餘額` = '" + new_c_money + "' WHERE `company`.`廠商ID` = '" + transaction.substring(1) + "'";
                                String sql3 = "UPDATE `commodity` SET `庫存` = '" + new_p_amount + "' WHERE `commodity`.`商品ID` = '" + p_ID + "'";
                                String sql4 = "INSERT INTO `transaction_record` (`付款方ID`, `收款方ID`, `屬性`, `交易項目`, `交易時間`, `交易金額`, `商品ID`) VALUES ('" + "M" + memberID + "', '" + transaction + "', '商品', '" + p_name + "', '" + dateFormat.format(date) + "', '" + p_money + "', '" + p_ID + "');";
                                Statement st = con.createStatement();
                                st.executeUpdate(sql1);
                                st.executeUpdate(sql2);
                                st.executeUpdate(sql3);
                                st.executeUpdate(sql4);
                                st.close();
                                Log.v("DB", "成功寫入資料：" + sql1 + sql2 + sql3 + sql4);

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
                                // 結束
                                finish();
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "交易失敗：庫存或餘額不足", Toast.LENGTH_SHORT).show();
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
        } else if (transaction.substring(0, 1).equals("M")) { // 來自會員的交易事件

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("會員間交易資訊");

            // 設定輸入
            Context context = this;
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("請輸入付款金額");
            layout.addView(input);

            final EditText input2 = new EditText(context);
            input2.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input2.setHint("請輸入交易訊息");
            layout.addView(input2);

            builder.setView(layout);

            // 設定按鈕
            builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    payment = Integer.valueOf(input.getText().toString());
                    if (payment > 0) { // 付款金額正確(大於0元)
                        Log.v("$", input.getText().toString());

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

                                    // 資料庫處理要求

                                    // 1 取得我方會員資料
                                    String m_name = "";
                                    int m_money = 0;

                                    PreparedStatement ps = con.prepareStatement("SELECT * FROM `member` WHERE `會員ID` = ?");
                                    ps.setString(1, memberID);
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
                                        Log.v("DB", "我方會員資料錯誤");
                                        // 結束
                                        finish();
                                    }
                                    rs.close();

                                    // 2 取得對方會員資料
                                    String m2_name = "";
                                    int m2_money = 0;

                                    ps = con.prepareStatement("SELECT * FROM `member` WHERE `會員ID` = ?");
                                    ps.setString(1, transaction.substring(1));
                                    rs = ps.executeQuery();
                                    if (rs.next()) {
                                        m2_name = rs.getString("會員暱稱");
                                        m2_money = rs.getInt("餘額");
                                        Log.v("DB", "會員資料正確");
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
                                        Log.v("DB", "我方會員資料錯誤");
                                        // 結束
                                        finish();
                                    }
                                    rs.close();

                                    // 3 進行金額的運算
                                    int new_m_money = m_money - payment; // 新的我方會員餘額 = 原有我方會員餘額 - 付款金額
                                    int new_m2_money = m2_money + payment; // 新的對方會員餘額 = 原有對方會員餘額 + 付款金額
                                    int m_transaction_amount = 0 - payment; // 交易金額是負的

                                    if (new_m_money >= 0) { // 新的我方會員餘額不得小於 0

                                        // 4 交易時間
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        final Date date = new Date();

                                        if (check1 && check2) {

                                            // 5 寫入所有交易資料至資料庫
                                            String sql1 = "UPDATE `member` SET `餘額` = '" + new_m_money + "' WHERE `member`.`會員ID` = '" + memberID + "'";
                                            String sql2 = "UPDATE `member` SET `餘額` = '" + new_m2_money + "' WHERE `member`.`會員ID` = '" + transaction.substring(1) + "'";
                                            String sql3 = "INSERT INTO `transaction_record` (`付款方ID`, `收款方ID`, `屬性`, `交易項目`, `交易時間`, `交易金額`, `交易信息`) VALUES ('" + "M" + memberID + "', '" + transaction + "', '會員', '" + "會員間交易" + "', '" + dateFormat.format(date) + "', '" + payment + "', '" + input2.getText().toString() + "');";
                                            Statement st = con.createStatement();
                                            st.executeUpdate(sql1);
                                            st.executeUpdate(sql2);
                                            st.executeUpdate(sql3);
                                            st.close();
                                            Log.v("DB", "成功寫入資料：" + sql1 + sql2 + sql3);

                                            // 6 給交易成功畫面的資訊
                                            final String succ_p_name = "會員間交易";
                                            final String succ_c_name = m2_name;
                                            final String succ_m_transaction_amount = Integer.toString(m_transaction_amount);

                                            // 7 交易成功處理
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
                                            // 結束
                                            finish();
                                        }

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "交易失敗：餘額不足", Toast.LENGTH_SHORT).show();
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

                    } else {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "付款金額輸入錯誤，請輸入大於0的金額", Toast.LENGTH_SHORT).show();
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
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "取消交易", Toast.LENGTH_SHORT).show();
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
                }
            });

            builder.show();

        } else {
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
        }

    }
}
