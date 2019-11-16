package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class company extends AppCompatActivity {

    // 右上選單實作

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 按下「刪除記錄」後的動作
        if (id == R.id.logout) {
            // 清空登入資訊
            SharedPreferences record = getSharedPreferences("record", MODE_PRIVATE);
            record.edit()
                    .putString("type", "")
                    .putString("account", "")
                    .apply();
            // 提示訊息
            Toast.makeText(getApplicationContext(), "成功登出，感謝使用", Toast.LENGTH_SHORT).show();
            // 跳轉至 APP 主畫面
            Intent intent = new Intent();
            intent.setClass(company.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("廠商主畫面");
        setContentView(R.layout.activity_company);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
