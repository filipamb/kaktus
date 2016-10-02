package pl.atendesoftware.amimobile.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pl.atendesoftware.amimobile.R;
import pl.atendesoftware.amimobile.helpers.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity {
    private TextView mUsernameField = null;
    private TextView mPasswordField = null;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        mUsernameField = (TextView) findViewById(R.id.username_field);
        mPasswordField = (TextView) findViewById(R.id.password_field);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUsernameField.getText().toString().equals("admin") && mPasswordField.getText().toString().equals("admin")) {
                    SharedPreferencesHelper.putValue(mContext, SharedPreferencesHelper.app_user_login_key, true);
                    SharedPreferencesHelper.putValue(mContext, SharedPreferencesHelper.app_user_username_key, mUsernameField.getText().toString());
                    SharedPreferencesHelper.putValue(mContext, SharedPreferencesHelper.app_user_name_key, "Atende Software");
                    SharedPreferencesHelper.putValue(mContext, SharedPreferencesHelper.app_user_email_key, "atendesoftware@amimobile.pl");

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(mContext, "Niepoprawne dane logowania", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SharedPreferencesHelper.getBoolean(mContext, SharedPreferencesHelper.app_user_login_key, false)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            mUsernameField.setText(SharedPreferencesHelper.getString(mContext, SharedPreferencesHelper.app_user_username_key, ""));
        }
    }
}