package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirmSignUp3 extends AppCompatActivity {

    private EditText commodity_name;
    private EditText commodity_money;
    private EditText commodity_amount;
    private ImageView commodity_pic;
    private ImageView commodity_pic_base;

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/demonuts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_sign_up3);

        commodity_pic = (ImageView) findViewById(R.id.commodity_pic);
        commodity_name = (EditText) findViewById(R.id.commodity_name);
        commodity_money = (EditText) findViewById(R.id.commodity_money);
        commodity_amount = (EditText) findViewById(R.id.commodity_amount);
        commodity_pic_base = (ImageView) findViewById(R.id.commodity_pic_base);

        // 取得欄位暫存檔
        try {
            String sp_commodity_name = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("commodity_name", "");
            if (!sp_commodity_name.equals("")) {
                commodity_name.setText(sp_commodity_name);
            }
            String sp_commodity_money = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("commodity_money", "");
            if (!sp_commodity_money.equals("")) {
                commodity_money.setText(sp_commodity_money);
            }
            String sp_commodity_amount = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("commodity_amount", "");
            if (!sp_commodity_amount.equals("")) {
                commodity_amount.setText(sp_commodity_amount);
            }
        } catch (Exception e) {
            Log.e("SP", "欄位暫存檔取得錯誤");
            e.printStackTrace();
        }

        commodity_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });


        Button lastbtn = findViewById(R.id.lastbtn);
        lastbtn.setOnClickListener(new Button.OnClickListener() {  //到註冊2
            @Override
            public void onClick(View v) {
                final String comm_n = commodity_name.getText().toString();
                final String comm_cm = commodity_money.getText().toString();
                final String comm_ca = commodity_amount.getText().toString();

                // 儲存步驟三的資料至暫存
                SharedPreferences record = getSharedPreferences("firm_record", MODE_PRIVATE);
                record.edit()
                        .putString("commodity_name", comm_n)
                        .putString("commodity_money", comm_cm)
                        .putString("commodity_amount", comm_ca)
                        .apply();

//                Intent intent = new Intent(FirmSignUp3.this, FirmSignUp2.class);
//                startActivity(intent);
                finish();
            }
        });

        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new Button.OnClickListener() {  //確認註冊，到註冊4(登入)
            @Override
            public void onClick(View v) {
                final Bitmap bitmap;
                final String comm_n = commodity_name.getText().toString();
                final String comm_cm = commodity_money.getText().toString();
                final String comm_ca = commodity_amount.getText().toString();

                if (comm_n.equals("") || comm_cm.equals("") || comm_ca.equals("")) {
                    Toast.makeText(FirmSignUp3.this, "有欄位尚未完成", Toast.LENGTH_SHORT).show();
                } else { // 檢查通過

                    // 廠商資料
                    final String company_name = getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("company_name", "");
                    final String account = getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("account", "");
                    final String password = getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("password", "");
                    final String firm_logo = getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("firm_logo", "");

                    // 活動
                    final String activity_name = getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("activity_name", "");
                    final int activity_money = Integer.valueOf(getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("activity_money", ""));
                    final String activity_location = getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("activity_location", "");
                    final int activity_maxPerson = Integer.valueOf(getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("activity_maxPerson", ""));
                    final String activity_textarea = getSharedPreferences("firm_record", MODE_PRIVATE)
                            .getString("activity_textarea", "");

                    // 商品
                    final String com_n = comm_n;
                    final int com_cm = Integer.valueOf(comm_cm);
                    final int com_ca = Integer.valueOf(comm_ca);

                    // 商品圖
                    try {
                        bitmap = ((BitmapDrawable) commodity_pic.getDrawable()).getBitmap();

                        //將Bitmap壓縮至字結數組輸出流ByteArrayOutputStream
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                        //利用Base64 字結束組輸出流中的數據轉換成字符串String
                        byte[] byteArray = bos.toByteArray();
                        final String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // 註冊寫入階段的暫存值
                                    String firmID = null;
                                    String activityID = null;

                                    // 生成 MySQL 連線資訊
                                    MysqlInfo info = new MysqlInfo();
                                    Connection con = info.getCon();
                                    // 資料庫處理要求

                                    // 一、建立廠商帳號
                                    Statement st = con.createStatement();
                                    String sql1 = "INSERT INTO `company` (`帳號`, `密碼`, `餘額`, `公司名稱`, `商標`) VALUES ('" + account + "', '" + password + "', '1000', '" + company_name + "', '" + firm_logo + "');";
                                    st.executeUpdate(sql1);

                                    // 二、取得廠商帳號 ID
                                    String sql_getcid = "SELECT * FROM `company` WHERE `帳號` = '" + account + "' ";
                                    ResultSet rs_getcid = st.executeQuery(sql_getcid);
                                    while (rs_getcid.next()) {
                                        final String c_ID = rs_getcid.getString("廠商ID");

                                        // 二之一、儲存廠商ID
                                        firmID = c_ID;

                                    }

                                    // 三、建立活動
                                    String sql2 = "INSERT INTO `activity` (`廠商ID`, `活動名稱`, `活動地點`, `活動內容`, `獎勵金額`, `活動限制人數`) VALUES ('" + firmID + "', '" + activity_name + "', '" + activity_location + "', '" + activity_textarea + "', '" + activity_money + "', '" + activity_maxPerson + "');";
                                    st.executeUpdate(sql2);

                                    // 四、取得活動 ID
                                    String sql_getaid = "SELECT * FROM `activity` WHERE `廠商ID` = '" + firmID + "' ";
                                    ResultSet rs_getaid = st.executeQuery(sql_getaid);
                                    while (rs_getaid.next()) {
                                        final String a_ID = rs_getaid.getString("活動ID");

                                        // 四之一、儲存活動ID
                                        activityID = a_ID;

                                    }

                                    // 五、建立商品
                                    String sql3 = "INSERT INTO `commodity` (`活動ID`, `廠商ID`, `商品名稱`, `商品定價`, `數量`, `商品照片`, `庫存`) VALUES ('" + activityID + "','" + firmID + "', '" + com_n + "', '" + com_cm + "', '" + com_ca + "', '" + imageString + "', '" + com_ca + "');";
                                    st.executeUpdate(sql3);

                                    st.close();
                                    Log.v("DB", "成功寫入資料：" + sql1 + sql2 + sql3);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "註冊成功", Toast.LENGTH_SHORT).show();
                                            // 清空註冊時的暫存資料
                                            SharedPreferences record = getSharedPreferences("firm_record", MODE_PRIVATE);
                                            record.edit()
                                                    // 第一步
                                                    .putString("company_name", "")
                                                    .putString("firm_logo", "")
                                                    .putString("account", "")
                                                    .putString("password", "")
                                                    // 第二步
                                                    .putString("activity_name", "")
                                                    .putString("activity_money", "")
                                                    .putString("activity_location", "")
                                                    .putString("activity_maxPerson", "")
                                                    .putString("activity_textarea", "")
                                                    // 第三步
                                                    .putString("commodity_name", "")
                                                    .putString("commodity_money", "")
                                                    .putString("commodity_amount", "")
                                                    .apply();
                                            Intent intent = new Intent(FirmSignUp3.this, FirmSignUp4.class);
                                            startActivity(intent);
                                        }
                                    });
                                } catch (
                                        SQLException e) {
                                    Log.e("DB", "寫入資料失敗");
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "註冊失敗", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(FirmSignUp3.this, FirmSignUp1.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        }).start();


                    } catch (Exception e) {
                        Toast.makeText(FirmSignUp3.this, "尚未選取圖片！", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "從圖庫選擇圖檔",
                "用相機拍攝影像"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // 取消
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) { // 圖庫
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    commodity_pic_base.setImageDrawable(null);
                    commodity_pic.setImageBitmap(bitmap); // 將圖片載入 imageview
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(FirmSignUp3.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) { // 相機
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            commodity_pic_base.setImageDrawable(null);
            commodity_pic.setImageBitmap(thumbnail); // 將圖片載入 imageview
        }
    }

}
