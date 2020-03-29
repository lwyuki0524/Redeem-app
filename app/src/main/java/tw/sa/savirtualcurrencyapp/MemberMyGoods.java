package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import MemberGoodsList.MemberGoodsAdapter;
import MemberGoodsList.MemberGoods_card;

public class MemberMyGoods extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_my_goods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.get_goods_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MemberMyGoods.this, MemberMainActivity.class));
                finish();//刪除此activity
            }
        });
        // 讀取會員ID
        final String memberID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("memberID", "");

        /*建一個串列，其形態是我們自訂的java檔，裡面是要填入的卡片資料結構*/
        final List<MemberGoods_card> activityList = new ArrayList<>();
        /*下面是要導入的視圖*/
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final TextView myGoodsCount = (TextView)findViewById(R.id.myGoodsCount);



        /* 顯示商品 */
        new Thread(new Runnable() {
            byte[] byteArray;
            ByteArrayInputStream byteArrayInputStream;
            Bitmap bitmap;

            @Override
            public void run() {

                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();
                    // 資料庫處理要求
                    String sql = "SELECT `商品名稱`,`交易金額`,`商品照片`,COUNT(*) AS `數量` FROM `commodity`,`transaction_record` ";
                    sql += " WHERE `commodity`.`商品ID` = `transaction_record`.`商品ID` AND `transaction_record`.`付款方ID`= 'M"+memberID+"' ";
                    sql +="  GROUP BY `transaction_record`.`商品ID`,`付款方ID`,`交易金額` ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);


                    while (rs.next()){
                        //取得商品照片
                        byteArray = Base64.decode(rs.getString("商品照片"), Base64.DEFAULT);
                        byteArrayInputStream = new ByteArrayInputStream(byteArray);
                        bitmap = BitmapFactory.decodeStream(byteArrayInputStream);

                        //將取得的資料加到自定型態的活動串列中 (FirmActivityList_card.java檔為此卡片的資料結構)
                        activityList.add(new MemberGoods_card(rs.getString("商品名稱"),"$"+rs.getString("交易金額"),rs.getString("數量"),bitmap));
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //設定視圖樣式
                            recyclerView.setLayoutManager(new LinearLayoutManager(MemberMyGoods.this,LinearLayoutManager.VERTICAL,false));
                        }
                    });

                    rs = st.executeQuery(sql);
                    if(!rs.next()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                LinearLayout nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
                                nodata_layout.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    sql ="SELECT COUNT(*) AS `總數量` FROM `commodity`,`transaction_record` ";
                    sql+=" WHERE `commodity`.`商品ID` = `transaction_record`.`商品ID` AND `transaction_record`.`付款方ID`= 'M"+memberID+"' ";
                    st = con.createStatement();
                    rs = st.executeQuery(sql);

                    while (rs.next()){
                        final String Total_amount = rs.getString("總數量");
                            runOnUiThread(new Runnable() {
                            public void run() {
                                myGoodsCount.setText(Total_amount);
                            }
                        });
                    }



                    st.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        recyclerView.setAdapter(new MemberGoodsAdapter(MemberMyGoods.this, activityList));


    }

}
