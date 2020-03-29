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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import MemberActivityRecommend.RecommendActivityAdapter;
import MemberActivityRecommend.RecommendActivity_card;
import RoundPic.AdjustBitmap;

public class MemberMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {
    private ConvenientBanner convenientBanner;
    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    private boolean isExit = false;
    private Timer timer;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_main_activity);
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);

        /*建一個串列，其形態是我們自訂的java檔，裡面是要填入的卡片資料結構*/
        final List<RecommendActivity_card> activityList = new ArrayList<>();
        /*下面是要導入的視圖*/
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //获取本地的图片
        for (int position = 0; position < 7; position++) {
            localImages.add(getResId("ic_test_" + position, R.drawable.class));
        }

        //开始自动翻页
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new MemberMainActivity.LocalImageHolderView();
            }
        }, localImages)
                //设置指示器是否可见
                .setPointViewVisible(true)
                //设置自动切换（同时设置了切换时间间隔）
                .startTurning(2000)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                //设置指示器的方向（左、中、右）
                //.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                //设置点击监听事件
                .setOnItemClickListener(this)
                //设置手动影响（设置了该项无法手动切换）
                .setManualPageable(true);
        final TextView member_nickname = (TextView) findViewById(R.id.member_nickname);
        final TextView member_ID = (TextView) findViewById(R.id.member_ID);
        final TextView member_money = (TextView) findViewById(R.id.member_money);
        final ImageView member_photo = (ImageView) findViewById(R.id.member_photo);


        Toolbar toolbar = findViewById(R.id.toolbar); //上方工具欄
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout); //側滑選單

        // 側邊欄位
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        final TextView member_nav_nickname = (TextView) header.findViewById(R.id.member_nav_nickname);
        final TextView member_nav_money = (TextView) header.findViewById(R.id.member_nav_money);
        final ImageView member_nav_photo = (ImageView) header.findViewById(R.id.member_nav_photo);

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
                    int count = 0;

                    @Override
                    public void run() {
                        if (haveInternet()) {
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
                                    final String m_photo = rs.getString("照片");

                                    // 儲存會員ID
                                    SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                                    record.edit()
                                            .putString("memberID", m_ID)
                                            .commit();

                                    runOnUiThread(new Runnable() {
                                        public void run() {
//                                        Bitmap m_photo_bitmap = decodeToImage(m_photo);
                                            byte[] byteArray = Base64.decode(m_photo, Base64.DEFAULT);
                                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                                            Bitmap m_photo_bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                                            m_photo_bitmap = AdjustBitmap.getCircleBitmap(m_photo_bitmap);

                                            // member_main_content.xml
                                            member_nickname.setText(m_name);
                                            member_ID.setText(m_ID);
                                            member_money.setText("$ " + m_money);
                                            member_photo.setImageBitmap(m_photo_bitmap);

                                            // member_main_nav_header.xml
                                            member_nav_nickname.setText(m_name);
                                            member_nav_money.setText("目前金額: " + m_money + "元");
                                            member_nav_photo.setImageBitmap(m_photo_bitmap);
                                        }
                                    });
                                }

//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        // 提示訊息
//                                        Toast.makeText(getApplicationContext(), "count:"+count, Toast.LENGTH_SHORT).show();
//                                        //設定視圖樣式
//                                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count, StaggeredGridLayoutManager.VERTICAL));
////                                            LinearLayoutManager layoutManager=new LinearLayoutManager(MemberMainActivity.this,LinearLayoutManager.HORIZONTAL,false);
////                                            recyclerView.setLayoutManager(layoutManager);
//                                    }
//                                });

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
                                    new AlertDialog.Builder(MemberMainActivity.this)
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

                /* 顯示活動 */
                new Thread(new Runnable() {
                    byte[] byteArray;
                    ByteArrayInputStream byteArrayInputStream;
                    Bitmap bitmap;

                    //            int count=0;
                    @Override
                    public void run() {

                        try {
                            // 生成 MySQL 連線資訊
                            MysqlInfo info = new MysqlInfo();
                            Connection con = info.getCon();
                            // 資料庫處理要求
                            String sql2 = "SELECT `活動名稱`,`活動ID`,`商標` FROM `activity`,`company` ";
                            sql2 += " WHERE `activity`.`廠商ID` = `company`.`廠商ID` ORDER BY RAND() LIMIT 5";
                            Statement st2 = con.createStatement();
                            ResultSet rs2 = st2.executeQuery(sql2);


                            while (rs2.next()) {
//                        count++;//用來計算多少筆資料
                                //取得商標
                                byteArray = Base64.decode(rs2.getString("商標"), Base64.DEFAULT);
                                byteArrayInputStream = new ByteArrayInputStream(byteArray);
                                bitmap = BitmapFactory.decodeStream(byteArrayInputStream);

                                final String tt = rs2.getString("活動名稱");
                                //將取得的資料加到自定型態的活動串列中 (FirmActivityList_card.java檔為此卡片的資料結構)
                                activityList.add(new RecommendActivity_card(rs2.getString("活動名稱"), rs2.getInt("活動ID"), bitmap));

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        //設定視圖樣式
//                            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.HORIZONTAL));
                                        recyclerView.setLayoutManager(new LinearLayoutManager(MemberMainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                    }
                                });
                            }

                            rs2 = st2.executeQuery(sql2);
                            if (!rs2.next()) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        LinearLayout nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
                                        nodata_layout.setVisibility(View.VISIBLE);
                                        TextView nodata_text = new TextView(MemberMainActivity.this);
                                        nodata_text.setText("目前無活動");
                                        nodata_text.setTextSize(16);
                                        nodata_layout.addView(nodata_text);
                                    }
                                });
//                        activityList.add(new FirmActivityList_card("目前無活動",0));

//                                      }
//                    else {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
////                                LinearLayout nodata_Linear = new LinearLayout(FirmActivityList.this);
////                                TextView nodata_text = new TextView(FirmActivityList.this);
////                                nodata_text.setText("目前無活動");
////                                nodata_text.setTextSize(18);
////                                nodata_Linear.addView(nodata_text);
////                                nodata.addView(nodata_Linear);
////                                nodata.setVisibility(View.VISIBLE);
//                            }
//                        });
                            }

//                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.HORIZONTAL));

                            st2.close();
//                    }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();


                /* 會員已獲商品數量顯示*/
                final TextView my_goods = (TextView) findViewById(R.id.my_goods_amount);
                final String ID = getSharedPreferences("record", MODE_PRIVATE).getString("memberID", "");
                final String m_ID = "M" + ID;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (haveInternet()) {
                            try {
                                // 生成 MySQL 連線資訊
                                MysqlInfo info = new MysqlInfo();
                                Connection con = info.getCon();
                                // 讀取會員ID
                                final String memberID = getSharedPreferences("record", MODE_PRIVATE)
                                        .getString("memberID", "");
                                // 資料庫處理要求
                                String sql3 = sql3 = "SELECT COUNT(*) AS `總數量` FROM `commodity`,`transaction_record` ";
                                sql3 += " WHERE `commodity`.`商品ID` = `transaction_record`.`商品ID` AND `transaction_record`.`付款方ID`= 'M" + memberID + "' ";
                                Statement st3 = con.createStatement();
                                ResultSet rs3 = st3.executeQuery(sql3);

                                while (rs3.next()) {
                                    final String goods_count = rs3.getString("總數量");
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            my_goods.setText(goods_count);
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

        //將 activityList(從資料庫取得的資料) 利用自訂的 RecommendActivityAdapter 適配器填入 recyclerView 中
//        recyclerView.setLayoutManager(new LinearLayoutManager(MemberMainActivity.this));
        recyclerView.setAdapter(new RecommendActivityAdapter(MemberMainActivity.this, activityList));
        Loading loading = new Loading();
        loading.run();

        /*  選擇活動  */
        TextView activity_btn = findViewById(R.id.all_activity_btn);
        activity_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberMainActivity.this, MemberAllActivity.class);
                startActivity(intent);
            }
        });


        /*  到收紅包  */
        ImageButton receive_money = findViewById(R.id.receive_money_btn);
        receive_money.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberMainActivity.this, MemberReceivedMoney.class);
                startActivity(intent);
            }
        });


        /*  到買東西  */
        ImageButton buy_goods = findViewById(R.id.buy_goods_btn);
        buy_goods.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberMainActivity.this, MemberBoughtGoods.class);
                startActivity(intent);
            }
        });

        /*  到日記簿  */
        ImageButton account_book = findViewById(R.id.member_account_book_btn);
        account_book.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberMainActivity.this, MemberAccountBook.class);
                startActivity(intent);
            }
        });


        final TextView my_goods_btn = (TextView) findViewById(R.id.my_goods_btn);
        my_goods_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberMainActivity.this, MemberMyGoods.class);
                startActivity(intent);
            }
        });


//        /*  活動1  */
//        ImageButton activity1 = findViewById(R.id.activity1);
//        activity1.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        // 下拉重新載入
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                activityList.clear();
                Loading loading = new Loading();
                loading.run();
                recyclerView.setAdapter(new RecommendActivityAdapter(MemberMainActivity.this, activityList));
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

        if (id == R.id.nav_modify_member_info) {
            // 修改會員資料
            Intent intent = new Intent();
            intent.setClass(this, MemberModifyPerson.class);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(MemberMainActivity.this);
            dialog.setTitle("關於");
            //dialog.setIcon();
            dialog.setMessage("@Redeem 中原資管 \n 目前版本：v" + _sVersion);
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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "position:" + position, Toast.LENGTH_SHORT).show();
    }

    //为了方便改写，来实现复杂布局的切换
    private class LocalImageHolderView implements Holder<Integer> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            //你可以通过layout文件来创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            imageView.setImageResource(data);
        }
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param c
     * @return
     */

    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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

//    // Base64 to Image
//    public static Bitmap decodeToImage(String encodedImage) {
//        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        return decodedByte;
//    }

}

