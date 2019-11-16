package tw.sa.savirtualcurrencyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirmMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_main_activity);

        final TextView company_name = (TextView) findViewById(R.id.company_name);
        final TextView company_ID = (TextView) findViewById(R.id.company_ID);
        final TextView company_money = (TextView) findViewById(R.id.company_money);

        Toolbar toolbar = findViewById(R.id.toolbar); //上方工具欄
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout); //側滑選單
        NavigationView navigationView = findViewById(R.id.nav_view); //側邊欄
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        /* 抓資料庫資訊回來 */

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
                    String sql = "SELECT * FROM `company` WHERE `帳號` = '" + account + "' ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    while (rs.next()) {
                        final String c_name = rs.getString("公司名稱");
                        final String c_ID = rs.getString("廠商ID");
                        final String c_money = rs.getString("餘額");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                company_name.setText(c_name);
                                company_ID.setText(c_ID);
                                company_money.setText("$ " + c_money);
                            }
                        });
                    }
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /*  選擇活動  */
        Button activity_btn = findViewById(R.id.choose_activity_btn);
        activity_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirmMainActivity.this, FirmActivityList.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });


        /*  到支付紅包  */
        ImageButton paid_money = findViewById(R.id.paid_red_envelope);
        paid_money.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirmMainActivity.this, FirmPaidMoney.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });


        /*  到賣東西  */
        ImageButton sell_goods = findViewById(R.id.sell_goods_btn);
        sell_goods.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirmMainActivity.this, FirmSellGoods.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });

        /*  到日記簿  */
        ImageButton account_book = findViewById(R.id.account_book_btn);
        account_book.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirmMainActivity.this,FirmAccountBook.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            // 清空登入資訊
            SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
            record.edit()
                    .putString("type", "")
                    .putString("account", "")
                    .apply();
            // 提示訊息
            Toast.makeText(getApplicationContext(), "成功登出，感謝使用", Toast.LENGTH_SHORT).show();
            // 跳轉至 APP 主畫面
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
