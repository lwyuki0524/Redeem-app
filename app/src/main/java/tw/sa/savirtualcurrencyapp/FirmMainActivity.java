package tw.sa.savirtualcurrencyapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import RoundPic.AdjustBitmap;
import RoundPic.ToRoundImage;

public class FirmMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isExit = false;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_main_activity);

        final TextView company_name = (TextView) findViewById(R.id.company_name);
        final TextView company_ID = (TextView) findViewById(R.id.company_ID);
        final TextView company_money = (TextView) findViewById(R.id.company_money);
        final ImageView company_photo = (ImageView) findViewById(R.id.company_photo);
        final TextView activity_name = (TextView) findViewById(R.id.activity_name);


        Toolbar toolbar = findViewById(R.id.toolbar); //上方工具欄
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout); //側滑選單

        // 側邊欄位
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        final TextView company_nav_nickname = (TextView) header.findViewById(R.id.company_nav_nickname);
        final TextView company_nav_money = (TextView) header.findViewById(R.id.company_nav_money);
        final ImageView company_nav_photo = (ImageView) header.findViewById(R.id.company_nav_photo);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        final String account = getSharedPreferences("record", MODE_PRIVATE)
                .getString("account", "");

        /* 抓資料庫資訊回來 */
        class Loading implements Runnable {

            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (haveInternet()) {
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
                                    final String c_photo = rs.getString("商標");

                                    // 儲存廠商ID
                                    SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                                    record.edit()
                                            .putString("firmID", c_ID)
                                            .commit();

                                    runOnUiThread(new Runnable() {
                                        public void run() {
//                                        Bitmap c_photo_bitmap = decodeToImage(c_photo);

                                            byte[] byteArray = Base64.decode(c_photo, Base64.DEFAULT);
                                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                                            Bitmap c_photo_bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                                            c_photo_bitmap = AdjustBitmap.getCircleBitmap(c_photo_bitmap);

                                            // firm_main_content.xml
                                            company_name.setText(c_name);
                                            company_ID.setText(c_ID);
                                            company_money.setText("$ " + c_money);
                                            company_photo.setImageBitmap(c_photo_bitmap);

                                            // firm_main_nav_header.xml
                                            company_nav_nickname.setText(c_name);
                                            company_nav_money.setText("目前金額: " + c_money + "元");

                                            company_nav_photo.setImageBitmap(c_photo_bitmap);
                                        }
                                    });
                                }
                                String firmID = getSharedPreferences("record", MODE_PRIVATE)
                                        .getString("firmID", "");
                                String sql2 = "SELECT * FROM `activity` WHERE `廠商ID` = '" + firmID + "' AND `目前活動`='是' ";
                                ResultSet rs2 = st.executeQuery(sql2);
                                while (rs2.next()) {
                                    final String a_name = rs2.getString("活動名稱");

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            activity_name.setText(a_name);
                                        }
                                    });
                                }

                                rs2 = st.executeQuery(sql2);
                                if (!rs2.next()) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            activity_name.setText("目前無舉辦活動!");
                                        }
                                    });
                                }
                                st.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 提示訊息
                                    Toast.makeText(getApplicationContext(), "網路中斷", Toast.LENGTH_SHORT).show();
                                    // 產生視窗物件
                                    new AlertDialog.Builder(FirmMainActivity.this)
                                            .setTitle("訊息")
                                            .setMessage("目前網路中斷，請檢查網路連線狀態")
                                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                }
                            });

                        }
                    }
                }).start();

                /*  紅框資訊  */
                /* 抓資料庫資訊回來 */

                final TextView f_sold = (TextView) findViewById(R.id.sold);
                final TextView f_inventory = (TextView) findViewById(R.id.inventory);
                final TextView f_member_count = (TextView) findViewById(R.id.member_count);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (haveInternet()) {
                            try {
                                // 生成 MySQL 連線資訊
                                MysqlInfo info = new MysqlInfo();
                                Connection con = info.getCon();
                                // 資料庫處理要求
//                    String sql = "SELECT `數量`,`活動已報名人數`, `庫存` FROM `transaction_record`,`activity`,`commodity` ";
//                    sql+=" WHERE `目前活動`='是' AND transaction_record.活動ID=activity.活動ID ";
//                    sql+="AND activity.活動ID =commodity.活動ID AND transaction_record.屬性='商品'";
//                    Statement st = con.createStatement();
//                    ResultSet rs = st.executeQuery(sql);

                                String firmID = getSharedPreferences("record", MODE_PRIVATE)
                                        .getString("firmID", "");

                                String sql2 = "SELECT `庫存`,`活動已報名人數` FROM `commodity`,`activity` ";
                                sql2 += " WHERE commodity.廠商ID = '" + firmID + "' AND activity.廠商ID = '" + firmID + "'"; //目前廠商辦的活動
                                sql2 += " AND activity.目前活動 = '是'  AND activity.活動ID = commodity.活動ID ";
//                    sql2+=" AND transaction_record.商品ID = commodity.商品ID ";
                                Statement st2 = con.createStatement();
                                ResultSet rs2 = st2.executeQuery(sql2);

                                while (rs2.next()) {
//                        int total = Integer.parseInt(rs2.getString("數量"));
//                        int stock = Integer.parseInt(rs2.getString("庫存"));
//                        final String sold = rs2.getString("賣出數");//String.valueOf(total-stock);//賣出商品數
                                    final String inventory = rs2.getString("庫存");
                                    final String member_count = rs2.getString("活動已報名人數");

                                    runOnUiThread(new Runnable() {
                                        public void run() {
//                                f_sold.setText(sold);
                                            f_inventory.setText(inventory);
                                            f_member_count.setText(member_count);
                                        }
                                    });
                                }
                                st2.close();


                                String sql3 = "SELECT COUNT(transaction_record.商品ID)AS `賣出數` FROM `transaction_record`,`activity`,`commodity` ";
                                sql3 += "WHERE activity.廠商ID = '" + firmID + "' AND activity.目前活動 = '是' ";
                                sql3 += "AND commodity.活動ID =activity.活動ID  AND transaction_record.商品ID = commodity.商品ID";
                                Statement st3 = con.createStatement();
                                ResultSet rs3 = st3.executeQuery(sql3);

                                while (rs3.next()) {
                                    final String sold = rs3.getString("賣出數");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            f_sold.setText(sold);
                                        }
                                    });
                                }
                                st3.close();


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }

        Loading loading = new Loading();
        loading.run();

        /*  選擇活動  */
        Button activity_btn = findViewById(R.id.choose_activity_btn);
        activity_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirmMainActivity.this, FirmActivityList.class);
                startActivity(intent);
            }
        });


        /*  到支付紅包  */
        ImageButton paid_money = findViewById(R.id.paid_red_envelope);
        paid_money.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirmMainActivity.this, FirmPaidMoney.class);
                startActivity(intent);
            }
        });


        /*  到賣東西  */
        ImageButton sell_goods = findViewById(R.id.sell_goods_btn);
        sell_goods.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirmMainActivity.this, FirmSellGoods.class);
                startActivity(intent);
            }
        });

        /*  到日記簿  */
        ImageButton account_book = findViewById(R.id.account_book_btn);
        account_book.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirmMainActivity.this, FirmAccountBook.class);
                startActivity(intent);
            }
        });

        // 下拉重新載入
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                Loading loading = new Loading();
                loading.run();
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

        if (id == R.id.nav_modify_firm_info) {
            // 修改公司資料
            Intent intent = new Intent();
            intent.setClass(this, FirmModifyInfo.class);
            startActivity(intent);

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
            finish();
        } else if (id == R.id.nav_statics) {
            Intent intent = new Intent();
            intent.setClass(this, FirmStatistics.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

            Uri uri = Uri.parse("https://youtu.be/-fLuhwCqqQU");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        } else if (id == R.id.nav_tools) {
            String _sVersion = "1.0";
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                _sVersion = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(FirmMainActivity.this);
            dialog.setTitle("關於");
            //dialog.setIcon();
            dialog.setMessage("@Redeem 中原資管 SA第11組 \n 目前版本：v" + _sVersion);
            dialog.setIcon(R.drawable.logo)
                    .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })//.setNegativeButton("cancel",null).create()
                    .show();
        } else if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "推薦您使用 Redeem，介紹說明影片：https://youtu.be/-fLuhwCqqQU");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 網路是否開啟偵測
    private boolean haveInternet() {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            result = false;
        } else {
            if (!info.isAvailable()) {
                result = false;
            } else {
                result = true;
            }
        }

        return result;
    }

    // Base64 to Image
    public static Bitmap decodeToImage(String encodedImage) {
//        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        byte[] byteArray = Base64.decode(encodedImage, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap decodedByte = BitmapFactory.decodeStream(byteArrayInputStream);
        return decodedByte;
    }

    //重寫onKeyDown()方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //點擊返回鍵調用方法
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return false;
    }

    //點擊返回鍵調用的方法
    private void exit() {
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            //2000ms内按第二次則退出
            finish();
            System.exit(0);
        }
    }


}
