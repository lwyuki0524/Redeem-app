package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MemberBoughtGoods extends AppCompatActivity {


    SurfaceView surfaceView;
    TextView textView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_bought_goods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bought_goods_toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemberBoughtGoods.this, MemberMainActivity.class));
                finish();//刪除此activity(可刪除)
            }
        });


        /*以下QR code請自行修改*/
        /****
         *
         *       若支付成功，需要轉到firm_paid_success的介面(FirmPaidSuccess.class)
         *
         ****/
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        textView = (TextView) findViewById(R.id.textView);

        getPermissionsCamera();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        // 取得手機螢幕大小
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(width, height).setAutoFocusEnabled(true).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                    return;
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }

        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0) {
                    barcodeDetector.release();
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            // 建立提示訊息
                            Toast.makeText(getApplicationContext(), "掃描成功", Toast.LENGTH_SHORT).show();
                            // 儲存交易資訊
                            SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
                            record.edit()
                                    .putString("transaction", qrCodes.valueAt(0).displayValue)
                                    .apply();
                            // 跳轉至交易畫面
                            Intent intent = new Intent();
                            intent.setClass(MemberBoughtGoods.this, MemberBoughtProcess.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

        });

    }

    // 取得相機權限
    public void getPermissionsCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

}

