package tw.sa.savirtualcurrencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FirmSignUp1 extends AppCompatActivity {
    private EditText company_name;
    private EditText account;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_sign_up1);

        company_name = (EditText) findViewById(R.id.company_name);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);

        Button button = findViewById(R.id.nextbtn);
        button.setOnClickListener(new Button.OnClickListener() {  //到註冊2
            @Override
            public void onClick(View v) {
                // 儲存第一步資訊
                final String cn = company_name.getText().toString();
                final String ac = account.getText().toString();
                final String pw = password.getText().toString();
                SharedPreferences record = getSharedPreferences("firm_record", MODE_PRIVATE);
                record.edit()
                        .putString("company_name", cn)
                        .putString("account", ac)
                        .putString("password", pw)
                        .apply();
                Intent intent = new Intent(FirmSignUp1.this, FirmSignUp2.class);
                startActivity(intent);
            }
        });
    }

}
