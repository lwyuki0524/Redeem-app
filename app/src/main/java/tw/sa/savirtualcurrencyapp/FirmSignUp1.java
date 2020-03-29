package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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
import android.view.KeyEvent;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import RoundPic.CircleBitmapByShader;
import RoundPic.ToRoundImage;


public class FirmSignUp1 extends AppCompatActivity {
    private EditText company_name;
    private EditText account;
    private EditText password;
    private ImageView firm_logo;
    private ImageView firm_logo_base;

    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private boolean checkAC = true;//檢查帳號是否重複 false:不重複 true:重複
    private String cn = "";
    private String ac = "";
    private String pw = "";
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_sign_up1);

        requestMultiplePermissions();

        firm_logo = (ImageView) findViewById(R.id.firm_pic);
        firm_logo_base = (ImageView) findViewById(R.id.firm_pic_base);
        company_name = (EditText) findViewById(R.id.company_name);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);

        // 取得欄位暫存檔
        try {
            String sp_company_name = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("company_name", "");
            if (!sp_company_name.equals("")) {
                company_name.setText(sp_company_name);
            }
            String sp_account = getSharedPreferences("firm_record", MODE_PRIVATE)
                    .getString("account", "");
            if (!sp_account.equals("")) {
                account.setText(sp_account);
            }
        } catch (Exception e) {
            Log.e("SP", "欄位暫存檔取得錯誤");
            e.printStackTrace();
        }

        firm_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });


        Button button = findViewById(R.id.nextbtn);
        button.setOnClickListener(new Button.OnClickListener() {  //到註冊2
            @Override
            public void onClick(View v) {

                cn = company_name.getText().toString();
                ac = account.getText().toString();
                pw = password.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {//檢查帳號是否重複
                        try {
                            // 生成 MySQL 連線資訊
                            MysqlInfo info = new MysqlInfo();
                            Connection con = info.getCon();
                            // 資料庫處理要求
                            String sql="SELECT * FROM `company` WHERE `帳號` = '"+ac+"' ";
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(sql);
                            if (rs.next()) {
                                    checkAC = true;//帳號重複
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(FirmSignUp1.this, "已有此帳號!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                checkAC=false;
                            }
                            rs.close();


                            // 暱稱、帳號、密碼檢查
                            if (cn.equals("") || ac.equals("") || pw.equals("")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(FirmSignUp1.this, "有欄位尚未完成", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (2 > cn.length()) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(FirmSignUp1.this, "暱稱請輸入大於或等於 2 個字元", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (6 > ac.length()) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(FirmSignUp1.this, "帳號請輸入大於或等於 6 個字元", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (6 > pw.length()) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(FirmSignUp1.this, "密碼請輸入大於或等於 6 個字元", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else if (checkAC){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(FirmSignUp1.this, "請輸入其他帳號", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else { // 檢查通過

                                // 儲存第一步資訊
                                SharedPreferences record = getSharedPreferences("firm_record", MODE_PRIVATE);

                                try {
                                    bitmap = ((BitmapDrawable) firm_logo.getDrawable()).getBitmap();

                                    //將Bitmap壓縮至字結數組輸出流ByteArrayOutputStream
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                                    //利用Base64 字結束組輸出流中的數據轉換成字符串String
                                    byte[] byteArray = bos.toByteArray();
                                    String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));

                                    //將String 存至SharedPreferences
                                    record.edit()
                                            .putString("company_name", cn)
                                            .putString("firm_logo", imageString)
                                            .putString("account", ac)
                                            .putString("password", pw)
                                            .apply();
                                    Intent intent = new Intent(FirmSignUp1.this, FirmSignUp2.class);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(FirmSignUp1.this, "尚未選取圖片！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }



                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "帳號新增失敗", Toast.LENGTH_SHORT).show();
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                }).start();

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
//                    bitmap= CircleBitmapByShader.circleBitmapByShader(bitmap,150,50);
                    firm_logo.setImageBitmap(bitmap); // 將圖片載入 imageview
                    firm_logo_base.setImageDrawable(null);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(FirmSignUp1.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) { // 相機
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            firm_logo.setImageBitmap(thumbnail); // 將圖片載入 imageview
            firm_logo_base.setImageDrawable(null);
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

    @Override
    //安卓重寫返回鍵事件
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(FirmSignUp1.this)
                    .setTitle("確定要返回嗎?")//設定視窗標題
                    .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                    .setMessage("如果返回上一頁，所有的資訊將不會儲存。")//設定顯示的文字
                    .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })//設定結束的子視窗
                    .setPositiveButton("確認返回",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences record = getSharedPreferences("firm_record", MODE_PRIVATE);
                            //將String 存至SharedPreferences
                            record.edit()
                                    .putString("company_name", "")
                                    .putString("firm_logo", "")
                                    .putString("account", "")
                                    .putString("password", "")
                                    .putString("activity_name", "")
                                    .putString("activity_money", "")
                                    .putString("activity_location", "")
                                    .putString("activity_maxPerson", "")
                                    .putString("activity_textarea", "")
                                    .putString("commodity_name", "")
                                    .putString("commodity_money", "")
                                    .putString("commodity_amount", "")
                                    .apply();
                            finish();
                        }
                    })//設定結束的子視窗
                    .show();//呈現對話視窗
        }
        return true;
    }

}
