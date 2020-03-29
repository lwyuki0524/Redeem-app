package tw.sa.savirtualcurrencyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import AccountBookCard.AccountBookAdapter;
import AccountBookCard.AccountBookItem_card;
import RoundPic.AdjustBitmap;
import RoundPic.CircleBitmapByShader;
import RoundPic.ToRoundImage;

public class MemberAccountBook extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_account_book);

        final TextView book_output = (TextView) findViewById(R.id.book_output);
        Toolbar toolbar = (Toolbar) findViewById(R.id.member_account_book_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MemberAccountBook.this, MemberMainActivity.class));
                finish();//刪除此activity(可刪除)
            }
        });

        /*建一個串列，其形態是我們自訂的java檔，裡面是要填入的卡片資料結構*/
        final List<AccountBookItem_card> activityList = new ArrayList<>();
        /*下面是要將 cardView 導入的視圖*/
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        // 讀取日記簿資料
        final String memberID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("memberID", "");

        /* 顯示交易 */
        class Loading implements Runnable {

            @Override
            public void run() {
                new Thread(new Runnable() {
                    byte[] byteArray;
                    ByteArrayInputStream byteArrayInputStream;
                    Bitmap bitmap;
                    int count = 0;

                    @Override
                    public void run() {

                        try {
                            // 生成 MySQL 連線資訊
                            MysqlInfo info = new MysqlInfo();
                            Connection con = info.getCon();
                            // 資料庫處理要求
                            String sql = "SELECT * FROM `transaction_record` WHERE `付款方ID` = 'M" + memberID + "' OR `收款方ID` = 'M" + memberID + "'";
                            sql += " ORDER BY `交易時間` DESC ";
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(sql);


                            while (rs.next()) {
                                final String type = rs.getString("屬性");//有活動、商品、會員
                                String traderName = "未知", Img = "";

                                if (type.equals("商品")) {
                                    String sql2 = "SELECT `公司名稱` FROM `company` WHERE `廠商ID` = '" + rs.getString("收款方ID").substring(1) + "'";
                                    ResultSet rs2 = con.createStatement().executeQuery(sql2);
                                    if (rs2.next()) { //取得交易對象名稱
                                        traderName = rs2.getString(1);
                                    } else {
                                        traderName = "找不到交易對象";
                                    }

                                    sql2 = "SELECT `商品照片` FROM `commodity` WHERE `商品ID` = '" + rs.getString("商品ID") + "'";
                                    rs2 = con.createStatement().executeQuery(sql2);
                                    if (rs2.next()) {   //取得商品照片
                                        Img = rs2.getString(1);
                                    }

                                    byteArray = Base64.decode(Img, Base64.DEFAULT);
                                    byteArrayInputStream = new ByteArrayInputStream(byteArray);
                                    bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                                    count++;
                                    //將取得的資料加到自定型態的活動串列中 (FirmActivityList_card.java檔為此卡片的資料結構)
                                    activityList.add(new AccountBookItem_card(rs.getString("交易時間"), "支出",
                                            "購買商品", rs.getString("交易項目"), traderName, "-" + rs.getString("交易金額"),
                                            bitmap, rs.getInt("交易ID")));

                                } else if (type.equals("活動")) {
                                    String sql2 = "SELECT `公司名稱` FROM `company` WHERE `廠商ID` = '" + rs.getString("付款方ID").substring(1) + "' ";
                                    ResultSet rs2 = con.createStatement().executeQuery(sql2);
                                    if (rs2.next()) { //取得交易對象名稱
                                        traderName = rs2.getString(1);
                                    } else {
                                        traderName = "找不到交易對象";
                                    }

                                    sql2 = "SELECT `商標` FROM `company` WHERE `廠商ID` = '" + rs.getString("付款方ID").substring(1) + "'";
                                    rs2 = con.createStatement().executeQuery(sql2);
                                    if (rs2.next()) {
                                        Img = rs2.getString("商標");
                                    }

                                    byteArray = Base64.decode(Img, Base64.DEFAULT);
                                    byteArrayInputStream = new ByteArrayInputStream(byteArray);
                                    bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                                    bitmap = AdjustBitmap.getCircleBitmap(bitmap);
//                            bitmap = CircleBitmapByShader.circleBitmapByShader(bitmap, 50, 25);
                                    count++;
                                    //將取得的資料加到自定型態的活動串列中 (FirmActivityList_card.java檔為此卡片的資料結構)
                                    activityList.add(new AccountBookItem_card(rs.getString("交易時間"), "收入",
                                            "活動名稱", rs.getString("交易項目"), traderName, "+" + rs.getString("交易金額"),
                                            bitmap, rs.getInt("交易ID")));
                                } else if (type.equals("會員")) {

                                    String sql2 = "";
                                    String IorO = "";
                                    ResultSet rs2;
                                    if (rs.getString("收款方ID").substring(1).equals(memberID)) {//如果我是收款方
                                        IorO = "收入";
                                        sql2 = "SELECT `會員暱稱` FROM `member` WHERE `會員ID` = '" + rs.getString("付款方ID").substring(1) + "' ";//對象就是付款方
                                        rs2 = con.createStatement().executeQuery(sql2);
                                        if (rs2.next()) { //取得交易對象名稱
                                            traderName = rs2.getString(1);
                                        } else {
                                            traderName = "找不到交易對象";
                                        }

                                        sql2 = "SELECT `照片` FROM `member` WHERE `會員ID` = '" + rs.getString("付款方ID").substring(1) + "'";
                                        rs2 = con.createStatement().executeQuery(sql2);
                                        if (rs2.next()) {
                                            Img = rs2.getString("照片");
                                        }
                                    } else {//如果我是付款方
                                        IorO = "支出";
                                        sql2 = "SELECT `會員暱稱` FROM `member` WHERE `會員ID` = '" + rs.getString("收款方ID").substring(1) + "' ";//對象就是收款方
                                        rs2 = con.createStatement().executeQuery(sql2);
                                        if (rs2.next()) { //取得交易對象名稱
                                            traderName = rs2.getString(1);
                                        } else {
                                            traderName = "找不到交易對象";
                                        }

                                        sql2 = "SELECT `照片` FROM `member` WHERE `會員ID` = '" + rs.getString("收款方ID").substring(1) + "'";
                                        rs2 = con.createStatement().executeQuery(sql2);
                                        if (rs2.next()) {
                                            Img = rs2.getString("照片");
                                        }
                                    }

                                    byteArray = Base64.decode(Img, Base64.DEFAULT);
                                    byteArrayInputStream = new ByteArrayInputStream(byteArray);
                                    bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                                    bitmap = AdjustBitmap.getCircleBitmap(bitmap);
//                            bitmap = CircleBitmapByShader.circleBitmapByShader(bitmap, 50, 25);
                                    count++;
                                    //將取得的資料加到自定型態的活動串列中 (FirmActivityList_card.java檔為此卡片的資料結構)
                                    if (IorO.equals("支出")) {
                                        activityList.add(new AccountBookItem_card(rs.getString("交易時間"), IorO,
                                                "交易信息", rs.getString("交易信息"), traderName, "-" + rs.getString("交易金額"),
                                                bitmap, rs.getInt("交易ID")));
                                    } else if (IorO.equals("收入")) {
                                        activityList.add(new AccountBookItem_card(rs.getString("交易時間"), IorO,
                                                "交易信息", rs.getString("交易信息"), traderName, "+" + rs.getString("交易金額"),
                                                bitmap, rs.getInt("交易ID")));
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "無資料", Toast.LENGTH_SHORT).show();
                                }

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        //設定視圖樣式
                                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(count, StaggeredGridLayoutManager.HORIZONTAL));
                                    }
                                });

                            }
                            rs = st.executeQuery(sql);
                            if(!rs.next()){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        book_output.setText("無交易資料");
                                    }
                                });
                            }
                            st.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    book_output.setText("無交易資料");
                                }
                            });
                        }
                    }
                }).start();

            }
        }

        Loading loading = new Loading();
        loading.run();

        recyclerView.setAdapter(new AccountBookAdapter(this, activityList));

        // 下拉重新載入
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                activityList.clear();
                Loading loading = new Loading();
                loading.run();
                recyclerView.setAdapter(new AccountBookAdapter(getApplicationContext(), activityList));
            }
        });


    }
}
