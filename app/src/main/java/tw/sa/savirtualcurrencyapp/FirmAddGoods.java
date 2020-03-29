package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class FirmAddGoods extends AppCompatActivity {

    private int GALLERY = 1, CAMERA = 2;
    private ImageView goods_img;
    private EditText goods_name;
    private EditText goods_price;
    private EditText goods_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_add_goods);

//        requestMultiplePermissions();
        getPermissionsCamera();

        /*取得layout物件*/
        goods_img = (ImageView)findViewById(R.id.goods_img);
        goods_name = (EditText)findViewById(R.id.goods_name);
        goods_price = (EditText)findViewById(R.id.goods_price);
        goods_amount = (EditText)findViewById(R.id.goods_amount);


        /*選擇圖片*/
        goods_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });




        /*  返回上一頁(新增活動)  */
        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*刪除這頁資訊*/

                new AlertDialog.Builder(FirmAddGoods.this)
                        .setTitle("確定要返回嗎?")//設定視窗標題
                        .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                        .setMessage("如果返回上一頁，這一頁的資訊將不會儲存。")//設定顯示的文字
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })//設定結束的子視窗
                        .setPositiveButton("確認返回",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })//設定結束的子視窗
                        .show();//呈現對話視窗
            }
        });


        /*  確定新增商品  */
        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Bitmap bitmap = ((BitmapDrawable) goods_img.getDrawable()).getBitmap();
                    Upload(bitmap);
                } catch (Exception e) {
                    Toast.makeText(FirmAddGoods.this, "尚未選取圖片！", Toast.LENGTH_SHORT).show();
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
                    goods_img.setImageBitmap(bitmap); // 將圖片載入 imageview
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(FirmAddGoods.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    public void Upload(Bitmap myBitmap) {
        final String name = goods_name.getText().toString();
        final String price = goods_price.getText().toString();
        final String amount = goods_amount.getText().toString();

        /*取得上頁資訊*/
        final String firmID = getSharedPreferences("record", MODE_PRIVATE).getString("firmID", "");
        final String act_name = getSharedPreferences("record", MODE_PRIVATE).getString("act_name", "");
        final String act_money = getSharedPreferences("record", MODE_PRIVATE).getString("act_money", "");
        final String act_address = getSharedPreferences("record", MODE_PRIVATE).getString("act_address", "");
        final String act_maxPerson = getSharedPreferences("record", MODE_PRIVATE).getString("act_maxPerson", "");
        final String act_content = getSharedPreferences("record", MODE_PRIVATE).getString("act_content", "");


        Toast.makeText(FirmAddGoods.this, "act_content="+act_content, Toast.LENGTH_SHORT).show();

        // 資料檢查
        if (name.equals("") || price.equals("") || amount.equals("")) {
            Toast.makeText(FirmAddGoods.this, "有欄位尚未完成", Toast.LENGTH_SHORT).show();
        }
        else { // 檢查通過
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 生成 MySQL 連線資訊
                        MysqlInfo info = new MysqlInfo();
                        Connection con = info.getCon();

                        // 資料庫處理要求
                        /*新增活動*/
                        String sql = "INSERT INTO `activity` (`廠商ID`, `活動名稱`, `活動地點`, `活動內容`,`獎勵金額`,`活動限制人數`) ";
                        sql+=" VALUES ('"+firmID+"','"+act_name+"','"+act_address+"','"+act_content+"','"+act_money+"','"+act_maxPerson+"' ); ";
                        Statement st = con.createStatement();
                        int insertNum = st.executeUpdate(sql);
                        if(insertNum>0){
                            Log.v("DB", "成功寫入資料：" + sql);
                        }
                        else {
                            Log.e("DB", "寫入資料失敗：" + sql);
                        }

                        /*取得活動ID*/
                        String activityID="";
                        sql = "SELECT MAX(`活動ID`) FROM `activity` ;";
                        st = con.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        if(rs.next()){
                            activityID = rs.getString(1);
                        }

                        /*新增商品*/
                        sql = "INSERT INTO `commodity` (`活動ID`, `廠商ID`, `商品名稱`,`商品定價`,`數量`,`商品照片`,`庫存`) ";
                        sql+=" VALUES ('"+activityID+"','"+firmID+"','"+name+"','"+price+"','"+amount+"','"+imageString+"','"+amount+"')";
                        st = con.createStatement();
                        st.executeUpdate(sql);
                        st.close();
                        Log.v("DB", "成功寫入資料：" + sql);
                    } catch (SQLException e) {
                        Log.e("DB", "寫入資料失敗");
                        e.printStackTrace();
                    }
                }
            }).start();


            /*刪除活動資訊*/
            SharedPreferences sharedPreferences = getSharedPreferences("record", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove("act_name")
                    .remove("act_money")
                    .remove("act_address")
                    .remove("act_maxPerson")
                    .remove("act_content");
            edit.commit();

            Intent intent=new Intent(FirmAddGoods.this,FirmActivityList.class);
            startActivity(intent);
            finish();
        }

    }

    // 取得相機權限
    public void getPermissionsCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

}


