package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import FirmListCard.FirmActivityAdapter;
import FirmListCard.FirmActivityList_card;

public class FirmActivityList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.firm_activity_list_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//刪除此activity
            }
        });

        final String firmID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("firmID", "");

        /*建一個串列，其形態是我們自訂的java檔，裡面是要填入的卡片資料結構*/
        final List<FirmActivityList_card> activityList = new ArrayList<>();
        /*下面是要將 cardView 導入的視圖*/
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        final CardView nodata = (CardView)findViewById(R.id.nodata);
//        final LinearLayout nodata_Linear =(LinearLayout)findViewById(R.id.nodata_Linear);
//        final TextView nodata_text =(TextView)findViewById(R.id.nodata_text);

        /* 顯示活動 */
        new Thread(new Runnable() {
//            int count=0;
            @Override
            public void run() {

                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();
                    // 資料庫處理要求
                    String sql = "SELECT `活動名稱`,`活動ID` FROM `activity` WHERE `廠商ID` = '" + firmID + "' AND `刪除`IS NULL ORDER BY `活動ID` DESC";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    while (rs.next()){
//                        count++;//用來計算多少筆資料
                        //將取得的資料加到自定型態的活動串列中 (FirmActivityList_card.java檔為此卡片的資料結構)
                        activityList.add(new FirmActivityList_card(rs.getString(1),rs.getInt(2)));
                    }

                    rs = st.executeQuery(sql);
                    if(rs.next()){
//                        activityList.add(new FirmActivityList_card("目前無活動",0));
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                LinearLayout nodata_Linear = new LinearLayout(FirmActivityList.this);
                                TextView nodata_text = new TextView(FirmActivityList.this);
                                nodata_text.setText("目前無活動");
                                nodata_text.setTextSize(18);
                                nodata_Linear.addView(nodata_text);
                                nodata.addView(nodata_Linear);
                                nodata.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            //設定視圖樣式
//                            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.HORIZONTAL));
                            recyclerView.setLayoutManager(new LinearLayoutManager(FirmActivityList.this,LinearLayoutManager.VERTICAL,false));
                        }
                    });
//                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.HORIZONTAL));

                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        //將 activityList(從資料庫取得的資料) 利用自訂的FirmActivityAdapter適配器填入 recyclerView 中
        recyclerView.setAdapter(new FirmActivityAdapter(this, activityList));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.HORIZONTAL));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        /*  新增活動項目  */
        CardView add_activity_item = findViewById(R.id.add_activity_btn);
        add_activity_item.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirmActivityList.this,FirmAddActivity.class);
                startActivity(intent);
            }
        });

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        final String firmID = getSharedPreferences("record", MODE_PRIVATE)
//                .getString("firmID", "");
//
//        /*建一個串列，其形態是我們自訂的java檔，裡面是要填入的卡片資料結構*/
//        final List<FirmActivityList_card> activityList = new ArrayList<>();
//        /*下面是要將 cardView 導入的視圖*/
//        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//
//
//        final CardView nodata = (CardView)findViewById(R.id.nodata);
//
//        /* 顯示活動 */
//        new Thread(new Runnable() {
//            int count=0;
//            @Override
//            public void run() {
//
//                try {
//                    // 生成 MySQL 連線資訊
//                    MysqlInfo info = new MysqlInfo();
//                    Connection con = info.getCon();
//                    // 資料庫處理要求
//                    String sql = "SELECT `活動名稱`,`活動ID` FROM `activity` WHERE `廠商ID` = '" + firmID + "' ORDER BY `活動ID` DESC";
//                    Statement st = con.createStatement();
//                    ResultSet rs = st.executeQuery(sql);
//
//
//                    while (rs.next()){
//                        count++;//用來計算多少筆資料
//                        //將取得的資料加到自定型態的活動串列中 (FirmActivityList_card.java檔為此卡片的資料結構)
//                        activityList.add(new FirmActivityList_card(rs.getString(1),rs.getInt(2)));
//                    }
//
//                    rs = st.executeQuery(sql);
//                    if(rs.next()){
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                //設定視圖樣式
//                                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.HORIZONTAL));
//                            }
//                        });
//                    }
//                    else {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                LinearLayout nodata_Linear = new LinearLayout(FirmActivityList.this);
//                                TextView nodata_text = new TextView(FirmActivityList.this);
//                                nodata_text.setText("目前無活動");
//                                nodata_text.setTextSize(18);
//                                nodata_Linear.addView(nodata_text);
//                                nodata.addView(nodata_Linear);
//                                nodata.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//
//                    st.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }).start();
//
//        recyclerView.setAdapter(new FirmActivityAdapter(this, activityList));
//    }


}
