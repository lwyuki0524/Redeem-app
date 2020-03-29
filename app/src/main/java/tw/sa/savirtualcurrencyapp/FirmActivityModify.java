package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class FirmActivityModify extends AppCompatActivity {
    EditText activity_name,activity_address,activity_content,goods_name,goods_stock,goods_price,maxPeople,activity_money;
    Button back_btn,modify_btn;
    ImageView goods_img;
    String act_name,act_address,act_content,cname,cstock,cprice,act_maxPeople,cimg,act_money;
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_activity_modify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//刪除此activity(可刪除)
            }
        });

        init();//找到元件


        /*選擇圖片*/
        goods_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPictureDialog();
            }

        });

        /*修改鍵*/
        modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getModifyInfo();//取得使用者修改資料

                try{
                    Bitmap bitmap = ((BitmapDrawable) goods_img.getDrawable()).getBitmap();
                    //將Bitmap壓縮至字結數組輸出流ByteArrayOutputStream
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                    //利用Base64 字結束組輸出流中的數據轉換成字符串String
                    byte[] byteArray = bos.toByteArray();
                    cimg = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
                }
                catch(Exception e){
                    Toast.makeText(FirmActivityModify.this, "選擇圖片失敗", Toast.LENGTH_SHORT).show();
                }

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            // 生成 MySQL 連線資訊
                            MysqlInfo info = new MysqlInfo();
                            Connection con = info.getCon();

                            String act_ID = getSharedPreferences("record", MODE_PRIVATE).getString("activityID", "");
                            // 資料庫處理要求
                            String sql = "UPDATE `activity` SET `活動名稱`='"+activity_name.getText()+"',`活動地點`='"+activity_address.getText()+"', ";
                            sql+=" `活動內容`='"+activity_content.getText()+"',`獎勵金額`='"+activity_money.getText()+"', ";
                            sql+=" `活動限制人數`='"+maxPeople.getText()+"' WHERE `活動ID` = '" + act_ID + "' ";
                            Statement st = con.createStatement();

                            int rs = st.executeUpdate(sql);
                            if(rs>0){
                                Log.v("DB", "更新成功");
                            }
                            else {
                                Log.v("DB", "更新失敗");
                            }

                            String sql2 = "Update `commodity` SET `商品名稱`='"+goods_name.getText()+"',`商品定價`='"+goods_price.getText()+"', ";
                            sql2+=" `數量`='"+goods_stock.getText()+"',`商品照片`='"+cimg+"', ";
                            sql2+=" `庫存`='"+goods_stock.getText()+"' WHERE `活動ID` = '" + act_ID + "' ";
                            Statement st2 = con.createStatement();
                            int rs2 = st2.executeUpdate(sql2);
                            if(rs2>0){
                                Log.v("DB", "商品更新成功");
                            }
                            else {
                                Log.v("DB", "商品更新失敗");
                            }

                            startActivity(new Intent(FirmActivityModify.this,FirmActivityItem.class));
                            finish();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
        });

        /*返回鍵*/
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

    }

    public void init(){
        activity_name = (EditText)findViewById(R.id.activity_name);
        activity_address = (EditText)findViewById(R.id.activity_address);
        activity_content = (EditText)findViewById(R.id.activity_content);
        maxPeople = (EditText)findViewById(R.id.maxPeople);
        goods_name = (EditText)findViewById(R.id.goods_name);
        goods_stock = (EditText)findViewById(R.id.goods_stock);
        goods_price = (EditText)findViewById(R.id.goods_price);
        activity_money = (EditText)findViewById(R.id.activity_money);

        goods_img =(ImageView)findViewById(R.id.goods_img);

        back_btn = (Button)findViewById(R.id.back_btn);
        modify_btn = (Button)findViewById(R.id.modify_btn);

        act_name = getSharedPreferences("record", MODE_PRIVATE).getString("activity_name", "");
        act_address = getSharedPreferences("record", MODE_PRIVATE).getString("activity_location", "");
        act_content = getSharedPreferences("record", MODE_PRIVATE).getString("activity_textarea", "");
        act_maxPeople = getSharedPreferences("record", MODE_PRIVATE).getString("activity_maxPerson", "");
        act_money  = getSharedPreferences("record", MODE_PRIVATE).getString("activity_money", "");
        cname = getSharedPreferences("record", MODE_PRIVATE).getString("commodity_name", "");
        cstock = getSharedPreferences("record", MODE_PRIVATE).getString("commodity_stock", "");
        cprice = getSharedPreferences("record", MODE_PRIVATE).getString("commodity_money", "");
        cimg = getSharedPreferences("record", MODE_PRIVATE).getString("commodity_img", "");

        if(cname==""){ goods_name.setHint("請輸入商品名稱"); }
        else { goods_name.setText(cname); }

        if(cstock==""){ goods_stock.setHint("請輸入商品庫存"); }
        else { goods_stock.setText(cstock); }

        if(cprice==""){ goods_price.setHint("請輸入商品價格"); }
        else { goods_price.setText(cprice); }

        if(act_name==""){ activity_name.setHint("請輸入活動名稱"); }
        else { activity_name.setText(act_name); }

        if(act_address==""){ activity_address.setHint("請輸入活動地點"); }
        else { activity_address.setText(act_address); }

        if(act_content==""){ activity_content.setHint("請輸入活動內容"); }
        else { activity_content.setText(act_content); }

        if(act_maxPeople==""){ maxPeople.setHint("請輸入限制人數"); }
        else { maxPeople.setText(act_maxPeople); }

        if(act_money==""){ activity_money.setHint("請輸入獎勵金額");}
        else { activity_money.setText(act_money); }

//        activity_name.setText(act_name);
//        activity_address.setText(act_address);
//        activity_content.setText(act_content);
//        maxPeople.setText(act_maxPeople);
//        activity_money.setText(act_money);
//
//        goods_stock.setText(cstock);
//        goods_price.setText(cprice);


        byte[] byteArray= Base64.decode(cimg, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
        final Bitmap bitmap= BitmapFactory.decodeStream(byteArrayInputStream);
        goods_img.setImageBitmap(bitmap);
    }

    public void getModifyInfo(){
        activity_name.getText();
        activity_address.getText();
        activity_content.getText();
        activity_money.getText();
        maxPeople.getText();
        goods_name.getText();
        goods_stock.getText();
        goods_price.getText();
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
//                    bitmap= CircleBitmapByShader.circleBitmapByShader(bitmap,150,50);
                    goods_img.setImageBitmap(bitmap); // 將圖片載入 imageview

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(FirmActivityModify.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) { // 相機
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            goods_img.setImageBitmap(thumbnail); // 將圖片載入 imageview
        }
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // 權限取得成功
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
}
