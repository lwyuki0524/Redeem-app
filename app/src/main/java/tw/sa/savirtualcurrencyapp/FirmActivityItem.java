package tw.sa.savirtualcurrencyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import FirmListCard.FirmActivityList_card;

public class FirmActivityItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_activity_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//刪除此activity(可刪除)
            }
        });

        /*取得layout物件*/
        final TextView activity_name = (TextView)findViewById(R.id.activity_name);
        final TextView activity_address = (TextView)findViewById(R.id.activity_address);
        final TextView activity_content = (TextView)findViewById(R.id.activity_content);
        final TextView activity_money = (TextView)findViewById(R.id.activity_money);
        final TextView goods_name = (TextView)findViewById(R.id.goods_name);
        final TextView goods_price_title = (TextView)findViewById(R.id.goods_price_title);
        final ImageView goods_img = (ImageView) findViewById(R.id.goods_img);
        final TextView maxPeople = (TextView)findViewById(R.id.maxPeople);
        final TextView joinPeople = (TextView)findViewById(R.id.joinPeople);
        final TextView goods_price = (TextView)findViewById(R.id.goods_price);
        final TextView goods_stock = (TextView)findViewById(R.id.goods_stock);
        final TextView dollars = (TextView)findViewById(R.id.dollars);
        final TextView goods_stock_title = (TextView)findViewById(R.id.goods_stock_title);
        Button back_btn = (Button)findViewById(R.id.backToActivityList);
        final Button choose_btn = (Button)findViewById(R.id.choose_btn);
        final RelativeLayout goods_out_layout = (RelativeLayout)findViewById(R.id.goods_out_layout);

        final String firmID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("firmID", "");

        /*取得儲存的資料*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();

                    String act_ID = getSharedPreferences("record", MODE_PRIVATE).getString("activityID", "");


                    // 資料庫處理要求
                    String sql = "SELECT * FROM `activity` WHERE `活動ID` = '" + act_ID + "' ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);


                    while (rs.next()) {
                        final String name = rs.getString("活動名稱");
                        final String address = rs.getString("活動地點");
                        final String content = rs.getString("活動內容");
                        final String money = rs.getString("獎勵金額");
                        final String maxP = rs.getString("活動限制人數");
                        final String joinP = rs.getString("活動已報名人數");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                activity_name.setText(name);
                                activity_address.setText(address);
                                activity_content.setText(content);
                                activity_money.setText(money);
                                maxPeople.setText(maxP);
                                joinPeople.setText(joinP);
                            }
                        });

//                        final String nowActivity =rs.getString("目前活動");
//                        Toast.makeText(getApplicationContext(), "nowActivity="+nowActivity, Toast.LENGTH_SHORT).show();

                        /*活動是否已舉辦，以改變按鈕狀態*/
                        try{
                            if(rs.getString("目前活動").equals("是")){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        choose_btn.setText("取消活動");
                                    }
                                });
                            }
                            else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        choose_btn.setText("舉辦活動");
                                    }
                                });
                            }
                        }
                        catch (NullPointerException e){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    choose_btn.setText("舉辦活動");
                                }
                            });
                        }


                        SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                        record.edit()
                                .putString("activity_name", name)
                                .putString("activity_money", money)
                                .putString("activity_location", address)
                                .putString("activity_maxPerson", maxP)
                                .putString("activity_textarea", content)
                                .apply();
                    }

                    String imageString="";
                    sql = "SELECT * FROM `commodity` WHERE `活動ID` = '" + act_ID + "' ";
                    st = con.createStatement();
                    rs = st.executeQuery(sql);
                    while (rs.next()){
                        imageString = rs.getString("商品照片");
                        final String g_name= rs.getString("商品名稱");
                        final String g_price= rs.getString("商品定價");
                        final String g_stock= rs.getString("庫存");
                        byte[] byteArray= Base64.decode(imageString, Base64.DEFAULT);
                        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
                        final Bitmap bitmap= BitmapFactory.decodeStream(byteArrayInputStream);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                goods_name.setText(g_name);
                                goods_price.setText(g_price);
                                goods_stock.setText(g_stock);
                                goods_img.setImageBitmap(bitmap);
                            }
                        });
                        SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                        record.edit()
                                .putString("commodity_name", g_name)
                                .putString("commodity_money", g_price)
                                .putString("commodity_stock", g_stock)
                                .putString("commodity_img", imageString)
                                .apply();
                    }

                    /*如果沒商品資訊*/
                    rs = st.executeQuery(sql);
                    if(!rs.next()){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                goods_img.setVisibility(View.INVISIBLE);
                                goods_name.setVisibility(View.INVISIBLE);
                                goods_price_title.setVisibility(View.INVISIBLE);
                                goods_price.setVisibility(View.INVISIBLE);
                                dollars.setVisibility(View.INVISIBLE);
                                goods_stock_title.setVisibility(View.INVISIBLE);
                                goods_stock.setVisibility(View.INVISIBLE);
                                TextView nodata = findViewById(R.id.nodata);
                                nodata.setVisibility(View.VISIBLE);
                            }
                        });
                        SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                        record.edit()
                                .putString("commodity_name", "")
                                .putString("commodity_money", "")
                                .putString("commodity_stock", "")
                                .putString("commodity_img", "")
                                .apply();

                    }

                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 生成 MySQL 連線資訊
                            MysqlInfo info = new MysqlInfo();
                            Connection con = info.getCon();

                            String act_ID = getSharedPreferences("record", MODE_PRIVATE).getString("activityID", "");

                            if (choose_btn.getText().equals("取消活動")){
                                /*清除此廠商先前選擇的活動*/
                                String sql = "UPDATE `activity` SET `目前活動`= NULL WHERE `廠商ID` = '"+firmID+"' ";
                                Statement st = con.createStatement();
                                int rs = st.executeUpdate(sql);
                                if (rs>0){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            // 提示訊息
                                            Toast.makeText(getApplicationContext(), "取消活動成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            // 提示訊息
                                            Toast.makeText(getApplicationContext(), "取消活動失敗", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            else {
                                // 資料庫處理要求
                                /*清除此廠商先前選擇的活動*/
                                String sql = "UPDATE `activity` SET `目前活動`= NULL WHERE `廠商ID` = '"+firmID+"' ";
                                Statement st = con.createStatement();
                                int rs = st.executeUpdate(sql);
                                if(rs>0){
                                    sql = "UPDATE `activity` SET `目前活動`='是' WHERE `活動ID` = '" + act_ID + "' ";
                                    rs = st.executeUpdate(sql);
                                    if(rs>0){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                // 提示訊息
                                                Toast.makeText(getApplicationContext(), "選擇活動成功", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                    else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                // 提示訊息
                                                Toast.makeText(getApplicationContext(), "選擇活動失敗", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            // 提示訊息
                                            Toast.makeText(getApplicationContext(), "選擇活動失敗", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                st.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        startActivity(new Intent(FirmActivityItem.this,FirmMainActivity.class));
                        finish();

                    }

                }).start();
            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 刪除活動ID暫存
                SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                record.edit()
                        /*活動部分資料*/
                        .putString("activityID", "")
                        .putString("activity_name", "")
                        .putString("activity_money", "")
                        .putString("activity_location", "")
                        .putString("activity_maxPerson", "")
                        .putString("activity_textarea", "")
                        /*商品部分資料*/
                        .putString("commodity_name", "")
                        .putString("commodity_money", "")
                        .putString("commodity_stock", "")
                        .apply();

                finish();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.firm_activity_item_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_1:
                startActivity(new Intent(FirmActivityItem.this, FirmActivityModify.class));
                Toast.makeText(this, "編輯", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_2:
                new AlertDialog.Builder(FirmActivityItem.this)
                        .setTitle("刪除")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage("確定要刪除嗎?\n 此操作無法復原!")//設定顯示的文字
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })//設定結束的子視窗
                        .setPositiveButton("確認刪除",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteActivity();
//                                Intent intent=new Intent(FirmActivityItem.this,FirmActivityList.class);
//                                startActivity(intent);
//                                finish();
                            }
                        })//設定結束的子視窗
                        .show();//呈現對話視窗
                break;
        }
        return true;
    }

    public void deleteActivity(){

        Toast.makeText(this, "刪除", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();

                    String act_ID = getSharedPreferences("record", MODE_PRIVATE).getString("activityID", "");

                    // 資料庫處理要求
                    String sql = "UPDATE `activity` SET `刪除`='刪除' WHERE `活動ID` = '" + act_ID + "' ";
//                    String sql = "DELETE FROM `activity` WHERE `活動ID` = '" + act_ID + "' ";
                    Statement st = con.createStatement();
                    int deleteActResult= st.executeUpdate(sql);

                    if(deleteActResult>0){
                        Log.v("DB", "成功刪除活動：" + sql);
//                        sql = "DELETE FROM `commodity` WHERE `活動ID` = '" + act_ID + "' ";
//                        st = con.createStatement();
//                        int deleteGoodsResult= st.executeUpdate(sql);
//                        if(deleteGoodsResult>0){
//                            Log.v("DB", "成功刪除商品：" + sql);
                        SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                        record.edit()
                                /*活動部分資料*/
                                .putString("activityID", "")
                                .putString("activity_name", "")
                                .putString("activity_money", "")
                                .putString("activity_location", "")
                                .putString("activity_maxPerson", "")
                                .putString("activity_textarea", "")
                                /*商品部分資料*/
                                .putString("commodity_name", "")
                                .putString("commodity_money", "")
                                .putString("commodity_stock", "")
                                .apply();
                            Intent intent=new Intent(FirmActivityItem.this,FirmActivityList.class);
                            startActivity(intent);
                            finish();
//                        }
//                        else {
//                            Log.e("DB", "刪除商品失敗：" + sql);
//                        }
                    }
                    else {
                        Log.e("DB", "刪除活動失敗：" + sql);
                    }



                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }

}
