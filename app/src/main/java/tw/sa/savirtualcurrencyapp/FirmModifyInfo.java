package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import RoundPic.AdjustBitmap;

public class FirmModifyInfo extends AppCompatActivity {

    private ImageView company_img_circle;
    private TextView company_nickname;
    private ImageButton edit_company_nickname;
    private TextView company_ID;
    private TextView company_account;
    private Button modify_btn;
    private int GALLERY = 1, CAMERA = 2;
    String new_imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_modify_info);

        requestMultiplePermissions();

        Toolbar toolbar = (Toolbar) findViewById(R.id.all_activity_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(FirmModifyInfo.this, FirmMainActivity.class));
                finish();//刪除此activity(可刪除)
            }
        });

        company_img_circle = findViewById(R.id.company_img_circle);
        company_nickname = findViewById(R.id.company_nickname);
        edit_company_nickname = findViewById(R.id.edit_company_nickname);
        company_ID = findViewById(R.id.company_ID);
        company_account = findViewById(R.id.company_account);
        modify_btn = findViewById(R.id.modify_btn);

        // 讀取廠商資訊
        final String firmID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("firmID", "");

        /* 抓資料庫資訊回來 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();
                    // 資料庫處理要求
                    String sql = "SELECT * FROM `company` WHERE `廠商ID` = '" + firmID + "' ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    while (rs.next()) {
                        final String c_name = rs.getString("公司名稱");
                        final String c_account = rs.getString("帳號");
                        final String c_photo = rs.getString("商標");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                byte[] byteArray = Base64.decode(c_photo, Base64.DEFAULT);
                                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                                Bitmap c_photo_bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                                c_photo_bitmap = AdjustBitmap.getCircleBitmap(c_photo_bitmap);

                                company_nickname.setText(c_name);
                                company_ID.setText(firmID);
                                company_account.setText(c_account);
                                company_img_circle.setImageBitmap(c_photo_bitmap);

                            }
                        });
                    }
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 修改暱稱(介面上修改)
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        edit_company_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setTitle("暱稱修改");

                // 設定輸入
                Context context = getApplicationContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("請輸入新的暱稱");
                layout.addView(input);

                builder.setView(layout);

                // 設定按鈕
                builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        company_nickname.setText(input.getText().toString());
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();

            }
        });

        // 修改圖片(介面上修改)
        company_img_circle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPictureDialog();
            }

        });

        // 確認修改按鈕
        modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Bitmap bitmap = ((BitmapDrawable) company_img_circle.getDrawable()).getBitmap();
                    //將Bitmap壓縮至字結數組輸出流ByteArrayOutputStream
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                    //利用Base64 字結束組輸出流中的數據轉換成字符串String
                    byte[] byteArray = bos.toByteArray();
                    new_imageview = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "選擇圖片失敗", Toast.LENGTH_SHORT).show();
                }

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            // 生成 MySQL 連線資訊
                            MysqlInfo info = new MysqlInfo();
                            Connection con = info.getCon();
                            // 資料庫處理要求
                            String sql = "UPDATE `company` SET `公司名稱`='" + company_nickname.getText() + "',`商標`='" + new_imageview + "' WHERE `廠商ID` = '" + firmID + "'";
                            Log.v("DB", sql);
                            Statement st = con.createStatement();

                            int rs = st.executeUpdate(sql);
                            if (rs > 0) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                        // 跳轉回主畫面
                                        startActivity(new Intent(FirmModifyInfo.this, FirmMainActivity.class));
                                    }
                                });
                                finish();
                                Log.v("DB", "更新成功");
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "修改失敗", Toast.LENGTH_SHORT).show();
                                        // 跳轉回主畫面
                                        startActivity(new Intent(FirmModifyInfo.this, FirmMainActivity.class));
                                    }
                                });
                                finish();
                                Log.v("DB", "更新失敗");
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
        });

        Button mPassword = (Button) findViewById(R.id.modifyPassword);
        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(FirmModifyInfo.this, FirmModifyPassword.class);
                startActivity(intent);
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
                    Bitmap bm = AdjustBitmap.getCircleBitmap(bitmap);
                    company_img_circle.setImageBitmap(bm);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) { // 相機
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Bitmap thumb = AdjustBitmap.getCircleBitmap(thumbnail);
            company_img_circle.setImageBitmap(thumb);
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

