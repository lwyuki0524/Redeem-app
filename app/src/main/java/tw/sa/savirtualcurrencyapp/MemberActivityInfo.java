package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import RoundPic.AdjustBitmap;
import RoundPic.CircleBitmapByShader;
import RoundPic.ToRoundImage;

public class MemberActivityInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.firm_add_goods_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MemberActivityInfo.this, MemberAllActivity.class));
                finish();//刪除此activity
            }
        });

        // 讀取會員ID
        final String memberID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("memberID", "");
        //讀取活動資料
        final String activityID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("activityID", "");

        final Button registered_btn = (Button)findViewById(R.id.registered_btn);
        final TextView activity_name = (TextView)findViewById(R.id.add_activity_title);
        final ImageView firm_circle_pic = (ImageView)findViewById(R.id.firm_circle_pic);
        final TextView firm_name = (TextView)findViewById(R.id.firm_name);
        final TextView activity_address = (TextView)findViewById(R.id.activity_address);
        final TextView activity_content = (TextView)findViewById(R.id.activity_content);
        final TextView activity_money_info = (TextView)findViewById(R.id.activity_money);
        final ImageView goods_pic = (ImageView)findViewById(R.id.goods_pic);
        final TextView goods_name_title = (TextView)findViewById(R.id.goods_name_title);
        final TextView goods_name = (TextView)findViewById(R.id.goods_name);
        final TextView price = (TextView)findViewById(R.id.price);
        final TextView join_info = (TextView)findViewById(R.id.activity_join_info);
        final TextView activity_Person = (TextView)findViewById(R.id.activity_Person);

        new Thread(new Runnable() {
            byte[] byteArray,byteArray2;
            ByteArrayInputStream byteArrayInputStream,byteArrayInputStream2;
            Bitmap bitmap,bitmap2;
            @Override
            public void run() {
                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();
                    // 資料庫處理要求
                    String sql = "SELECT * FROM `activity` WHERE `活動ID` = '" + activityID + "' ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

//                  顯示活動細節
                    while (rs.next()) {

                        final String firmID = rs.getString("廠商ID");
                        final String activityName = rs.getString("活動名稱");
                        final String activityAddress = rs.getString("活動地點");
                        final String activityContent = rs.getString("活動內容");
                        final String activitymoney = rs.getString("獎勵金額");
                        final String activityMaxPerson = rs.getString("活動限制人數");
                        final String activityJoinPerson = rs.getString("活動已報名人數");

                        //找到此活動的廠商資料
                        String sql2 = "SELECT * FROM `company` WHERE `廠商ID` = '" + firmID + "' ";
                        st = con.createStatement();
                        ResultSet rs2 = st.executeQuery(sql2);
                        while (rs2.next()){
                            final String name = rs2.getString("公司名稱");
                            final String firm_logo = rs2.getString("商標");

                            //處理圖片
                            byteArray= Base64.decode(firm_logo, Base64.DEFAULT);
                            byteArrayInputStream=new ByteArrayInputStream(byteArray);
                            bitmap= BitmapFactory.decodeStream(byteArrayInputStream);
//                            bitmap = ToRoundImage.toRoundBitmap(bitmap);
//                            bitmap= CircleBitmapByShader.circleBitmapByShader(bitmap,110,110);

                            bitmap = AdjustBitmap.getCircleBitmap(bitmap);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    firm_name.setText(name+" 公司");
                                    firm_circle_pic.setImageBitmap(bitmap);
                                }
                            });
                        }

                        //找到此活動的商品資料
                        String sql3 = "SELECT * FROM `commodity` WHERE `活動ID` = '" + activityID + "' ";
                        st = con.createStatement();
                        ResultSet rs3 = st.executeQuery(sql3);
                        while (rs3.next()){
                            final String name2 = rs3.getString("商品名稱");
                            final String goods_img = rs3.getString("商品照片");
                            final String goods_price = rs3.getString("商品定價");

                            //處理圖片
                            byteArray2= Base64.decode(goods_img, Base64.DEFAULT);
                            byteArrayInputStream2=new ByteArrayInputStream(byteArray2);
                            bitmap2= BitmapFactory.decodeStream(byteArrayInputStream2);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    goods_name.setText(name2);
                                    price.setText(goods_price);
                                    goods_pic.setImageBitmap(bitmap2);
                                }
                            });
                        }

                        rs3 = st.executeQuery(sql3);
                        if (!rs3.next()){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    TextView nodata = (TextView)findViewById(R.id.nodata);
                                    nodata.setVisibility(View.VISIBLE);
                                    goods_pic.setVisibility(View.INVISIBLE);
                                    goods_name.setVisibility(View.INVISIBLE);
                                    price.setVisibility(View.INVISIBLE);
                                    goods_name_title.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                activity_name.setText("活動: "+activityName);
                                activity_address.setText(activityAddress);
                                activity_content.setText(" " + activityContent);
                                activity_money_info.setText(" " + activitymoney);
                                activity_Person.setText( " " + activityMaxPerson+" / "+activityJoinPerson);
                            }
                        });
                    }


                    //顯示下方按鈕為 取消報名 或是 報名活動
                    sql = "SELECT * FROM `activity_registration` WHERE `活動ID`='"+activityID+"' AND `客戶ID`='"+memberID+"';";
                     st = con.createStatement();
                     rs = st.executeQuery(sql);
                    if(rs.next()){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                join_info.setText("已報名活動");
                                registered_btn.setText("取消報名");
                            }
                        });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            public void run() {
                                join_info.setText("尚未報名活動");
                                registered_btn.setText("報名活動");
                            }
                        });
                    }
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();


//        ImageView imageview = findViewById(R.id.firm_circle_pic);
//        // 以資料流的方式讀取bitmap資源
//        Resources r = this.getResources();
//        @SuppressLint("ResourceType") InputStream is = r.openRawResource(R.drawable.pic_coffee);
//        BitmapDrawable bmpDraw = new BitmapDrawable(r, is);
//        Bitmap bmp = bmpDraw.getBitmap();
//
//        // 將圖片轉換成圓形圖片
//        Bitmap bm = ToRoundImage.toRoundBitmap(bmp);
//        //傳給imagview進行顯示
//        imageview.setImageBitmap(bm);


        // 活動報名or 取消報名
        registered_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    int MaxPerson2;
                    int joinPerson2;
                    @Override
                    public void run() {
                        try {
                            // 生成 MySQL 連線資訊
                            MysqlInfo info = new MysqlInfo();
                            Connection con = info.getCon();
                            // 資料庫處理要求
                            String sql="";

                            //查看此會員是否有報名資料(活動報名檔)
                            sql = "SELECT * FROM `activity_registration` WHERE `活動ID`='"+activityID+"' AND `客戶ID`='"+memberID+"';";
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(sql);

                            if(rs.next()){//若有資料則: 取消報名
                                sql = "DELETE FROM `activity_registration` WHERE `活動ID`='"+activityID+"' AND `客戶ID`='"+memberID+"';";
                                st = con.createStatement();
                                int updatenum = st.executeUpdate(sql);
                                if(updatenum>0){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "取消成功", Toast.LENGTH_SHORT).show();
                                            registered_btn.setText("報名活動");
                                            join_info.setText("尚未報名活動");
                                        }
                                    });

                                    //更新活動報名人數於活動檔中(報名人數減少)
                                    int activity_reg_num=1;
                                    //先找出目前活動報名人數，再減一
                                    sql = "SELECT `活動已報名人數` FROM `activity` WHERE `活動ID`='"+activityID+"';";
                                    rs = con.createStatement().executeQuery(sql);
                                    while (rs.next()){
                                        activity_reg_num = rs.getInt(1);
                                        activity_reg_num--;
                                    }

                                    sql = "UPDATE `activity` SET `活動已報名人數`='"+activity_reg_num+"' WHERE `活動ID`='"+activityID+"';";
                                    st = con.createStatement();
                                    st.executeUpdate(sql);

                                    String sql2 = "SELECT * FROM `activity` WHERE `活動ID` = '" + activityID + "' ";
                                    st = con.createStatement();
                                    ResultSet rs2 = st.executeQuery(sql2);
                                    while (rs2.next()){
                                        MaxPerson2 = rs2.getInt("活動限制人數");
                                        joinPerson2 = rs2.getInt("活動已報名人數");
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                activity_Person.setText(MaxPerson2+" / "+joinPerson2);
                                            }
                                        });
                                    }

                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "取消失敗", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            else {//若無資料，查看若無超過人數限制則報名

                                String sql2 = "SELECT * FROM `activity` WHERE `活動ID` = '" + activityID + "' ";
                                st = con.createStatement();
                                ResultSet rs2 = st.executeQuery(sql2);
                                boolean checkMaxPerson = false;
                                while (rs2.next()){
                                    final int MaxPerson = rs2.getInt("活動限制人數");
                                    final int joinPerson = rs2.getInt("活動已報名人數");
                                    if ( MaxPerson > joinPerson){
                                        checkMaxPerson=true;
                                    }
                                    else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "報名人數已達上限", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                if (checkMaxPerson==true){
                                    sql = "INSERT INTO `activity_registration`(`活動ID`, `客戶ID`, `報到狀態`)";
                                    sql+=" VALUES('"+activityID+"','"+memberID+"','未報到');";
                                    st = con.createStatement();
                                    int updatenum = st.executeUpdate(sql);

                                    if(updatenum>0){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "報名成功", Toast.LENGTH_SHORT).show();
                                                registered_btn.setText("取消報名");
                                                join_info.setText("已報名活動");
                                            }
                                        });

                                        //更新活動報名人數於活動檔中(報名人數增加)
                                        int activity_reg_num=1;
                                        sql = "SELECT `活動已報名人數` FROM `activity` WHERE `活動ID`='"+activityID+"';";
                                        rs = con.createStatement().executeQuery(sql);
                                        while (rs.next()){
                                            activity_reg_num = rs.getInt(1);
                                            activity_reg_num++;
                                        }

                                        sql = "UPDATE `activity` SET `活動已報名人數`='"+activity_reg_num+"' WHERE `活動ID`='"+activityID+"';";
                                        st = con.createStatement();
                                        st.executeUpdate(sql);

                                        rs2 = st.executeQuery(sql2);
                                        while (rs2.next()){
                                            MaxPerson2 = rs2.getInt("活動限制人數");
                                            joinPerson2 = rs2.getInt("活動已報名人數");
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    activity_Person.setText(MaxPerson2+" / "+joinPerson2);
                                                }
                                            });
                                        }

                                    }
                                    else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "報名失敗", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                            }

                            st.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });


    }
}
