package com.od.twins.absoftmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.od.twins.absoftmanager.Application;
import com.od.twins.absoftmanager.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText nickname_view = findViewById(R.id.nickname_view);

        Button nextView = findViewById(R.id.next_view);
        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nickname_view.getText().length() > 3) {
                    Application app = (Application) getApplication();
                    app.setNickName(nickname_view.getText().toString());
                    showMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Enter nickname.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
