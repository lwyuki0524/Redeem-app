package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import java.util.List;

public class member_register extends AppCompatActivity {

    private ImageView imageview;
    private EditText nickname;
    private EditText account;
    private EditText password;
    private Button upload;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("會員註冊");
        setContentView(R.layout.activity_member_register);

        requestMultiplePermissions();

        imageview = (ImageView) findViewById(R.id.iv);
        nickname = (EditText) findViewById(R.id.nickname);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        upload = (Button) findViewById(R.id.upload);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap bitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
                    Upload(bitmap);
                } catch (Exception e) {
                    Toast.makeText(member_register.this, "尚未選取圖片！", Toast.LENGTH_SHORT).show();
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
                    imageview.setImageBitmap(bitmap); // 將圖片載入 imageview
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(member_register.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) { // 相機
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail); // 將圖片載入 imageview
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

    public String Upload(Bitmap myBitmap) {
        final String nn = nickname.getText().toString();
        final String ac = account.getText().toString();
        final String pw = password.getText().toString();

        // 暱稱、帳號、密碼檢查
        if (nn.equals("") || ac.equals("") || pw.equals("")) {
            Toast.makeText(member_register.this, "有欄位尚未完成", Toast.LENGTH_SHORT).show();
        } else if (2 > nn.length()) {
            Toast.makeText(member_register.this, "暱稱請輸入大於或等於 2 個字元", Toast.LENGTH_SHORT).show();
        } else if (6 > ac.length()) {
            Toast.makeText(member_register.this, "帳號請輸入大於或等於 6 個字元", Toast.LENGTH_SHORT).show();
        } else if (6 > pw.length()) {
            Toast.makeText(member_register.this, "密碼請輸入大於或等於 6 個字元", Toast.LENGTH_SHORT).show();
        } else { // 檢查通過
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            Toast.makeText(getApplicationContext(), "註冊執行中...", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MysqlCon con = new MysqlCon();
                    if (con.insert_member_register_Data(nn, ac, pw, imageString)) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "註冊成功", Toast.LENGTH_SHORT).show();
                                // 跳轉到登入畫面
                                Intent intent = new Intent();
                                intent.setClass(member_register.this, member_login.class);
                                startActivity(intent);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "註冊失敗", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
        return "";
    }

}

