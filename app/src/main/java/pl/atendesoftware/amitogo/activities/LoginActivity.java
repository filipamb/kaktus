package pl.atendesoftware.amitogo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pl.atendesoftware.amitogo.R;

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
                    SharedPreferences preferences = mContext.getSharedPreferences("amitogo",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putBoolean("loggedIn",true);
                    editor.putString("username","admin");
                    editor.apply();

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
        if(mContext.getSharedPreferences("amitogo",Context.MODE_PRIVATE).getBoolean("loggedIn",false)){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            mUsernameField.setText(mContext.getSharedPreferences("amitogo",Context.MODE_PRIVATE).getString("username",""));
        }
    }
}