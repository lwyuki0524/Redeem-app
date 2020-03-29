package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestMultiplePermissions();

        // 讀取登入資訊
        String getAccountRecord = getSharedPreferences("record", MODE_PRIVATE)
                .getString("type", "");

        // 屬性屬於「會員」跳轉至「會員主畫面」
        if (getAccountRecord.equals("member")) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this  , MemberMainActivity.class);
            startActivity(intent);
            finish();
        }
        // 屬性屬於「廠商」跳轉至「廠商主畫面」
        if (getAccountRecord.equals("company")) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this  , FirmMainActivity.class);
            startActivity(intent);
            finish();
        }

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

        // 會員註冊按鈕
        Button member_register = (Button)findViewById(R.id.index_member_register);

        member_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , MemberRegister.class);
                startActivity(intent);
            }
        });

        // 會員登入按鈕
        Button member_login = (Button)findViewById(R.id.index_member_login);

        member_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , MemberLogin.class);
                startActivity(intent);
            }
        });

        // 廠商註冊按鈕
        Button company_register = (Button)findViewById(R.id.index_company_register);

        company_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , FirmSignUp1.class);
                startActivity(intent);
            }
        });

        // 廠商登入按鈕
        Button company_login = (Button)findViewById(R.id.index_company_login);

        company_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this  , FirmLogin.class);
                startActivity(intent);
            }
        });


    }

    // 禁用返回鍵
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
