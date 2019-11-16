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

public class MemberMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_main_activity);

        final TextView member_nickname = (TextView) findViewById(R.id.member_nickname);
        final TextView member_ID = (TextView) findViewById(R.id.member_ID);
        final TextView member_money = (TextView) findViewById(R.id.member_money);

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
                    String sql = "SELECT * FROM `member` WHERE `帳號` = '" + account + "' ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    while (rs.next()) {
                        final String m_name = rs.getString("會員暱稱");
                        final String m_ID = rs.getString("會員ID");
                        final String m_money = rs.getString("餘額");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                member_nickname.setText(m_name);
                                member_ID.setText(m_ID);
                                member_money.setText("$ " + m_money);
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
        TextView activity_btn = findViewById(R.id.all_activity_btn);
        activity_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        /*  到收紅包  */
        ImageButton receive_money = findViewById(R.id.receive_money_btn);
        receive_money.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MemberMainActivity.this,MemberReceivedMoney.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });


        /*  到買東西  */
        ImageButton buy_goods = findViewById(R.id.buy_goods_btn);
        buy_goods.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MemberMainActivity.this,MemberBoughtGoods.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });

        /*  到日記簿  */
        ImageButton account_book = findViewById(R.id.member_account_book_btn);
        account_book.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MemberMainActivity.this,MemberAccountBook.class);
                /*
                    存資料
                 */
                startActivity(intent);
            }
        });


        /*  活動1  */
        ImageButton activity1 = findViewById(R.id.activity1);
        activity1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

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

