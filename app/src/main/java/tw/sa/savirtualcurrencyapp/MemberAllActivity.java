package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import MemberAllActivityCard.MemberAllActivityAdapter;
import MemberAllActivityCard.MemberAllActivity_card;

public class MemberAllActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_all_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.all_activity_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MemberAllActivity.this, MemberMainActivity.class));
                finish();//刪除此activity
            }
        });

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MemberAllActivity.this, MemberMainActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        final String memberID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("memberID", "");

        /*建一個串列，其形態是我們自訂的java檔，裡面是要填入的卡片資料結構*/
        final List<MemberAllActivity_card> activityList = new ArrayList<>();
        /*下面是要導入的視圖*/
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

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
                    String sql = "SELECT `廠商ID`,`活動ID`,`活動名稱`,`活動地點` FROM `activity` WHERE `刪除`IS NULL ORDER BY `活動ID` DESC";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);


                    while (rs.next()){
                        String company_name="未知";
                        String sql2 = "SELECT `公司名稱` FROM `company` WHERE `廠商ID` = '"+rs.getString(1)+"' ";
                        st = con.createStatement();
                        ResultSet rs2 = st.executeQuery(sql2);
                        if(rs2.next()){
                            company_name=rs2.getString(1);
                        }

//                        count++;//用來計算多少筆資料
                        //將取得的資料加到自定型態的活動串列中 (FirmActivityList_card.java檔為此卡片的資料結構)
                        activityList.add(new MemberAllActivity_card(company_name+"公司",
                                rs.getString("活動名稱"),
                                "地點: "+rs.getString("活動地點"),
                                rs.getInt("活動ID")));
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            //設定視圖樣式
//                                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.HORIZONTAL));
                            recyclerView.setLayoutManager(new LinearLayoutManager(MemberAllActivity.this,LinearLayoutManager.VERTICAL,false));
                        }
                    });

                    rs = st.executeQuery(sql);
                    if(!rs.next()){
//                        activityList.add(new FirmActivityList_card("目前無活動",0));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                LinearLayout nodata_layout = (LinearLayout)findViewById(R.id.nodata_layout);
                                nodata_layout.setVisibility(View.VISIBLE);
                                TextView nodata_text = new TextView(MemberAllActivity.this);
                                nodata_text.setText("目前無活動");
                                nodata_text.setTextSize(16);
                                nodata_layout.addView(nodata_text);

                                Toast.makeText(MemberAllActivity.this, "目前無活動:" , Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
//                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.HORIZONTAL));

                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        //將 activityList(從資料庫取得的資料) 利用自訂的FirmActivityAdapter適配器填入 recyclerView 中
        recyclerView.setAdapter(new MemberAllActivityAdapter(this, activityList));

//        TextView all_activity_btn = (TextView) findViewById(R.id.all_activity_btn);
//        all_activity_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MemberAllActivity.this,MemberActivityInfo.class);
//                startActivity(intent);
//            }
//        });

    }
}
