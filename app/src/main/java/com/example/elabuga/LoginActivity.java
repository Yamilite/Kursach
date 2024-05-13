package com.example.elabuga;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final String SERVER_URL = "https://android-for-students.ru/coursework/login.php";
    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private TextView resultTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        resultTextView = findViewById(R.id.result);
    }
    public void loginUser(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        HashMap<String, String> postData = new HashMap<>();
        postData.put("lgn", username);
        postData.put("pwd", password);
        postData.put("g", "RIBO-05-22");

        HTTPRunnable httpRunnable = new HTTPRunnable(SERVER_URL, postData);
        Thread thread = new Thread(httpRunnable);
        thread.start();
        try {
            thread.join();
            String response = httpRunnable.getResponceBody();
            processResponse(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void processResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            int resultCode = jsonResponse.getInt("result_code");

            if (resultCode == 1) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                runOnUiThread(() -> resultTextView.setText("Неверный логин, пароль или группа."));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
            resultTextView.setText("Error parsing JSON response");
        }
    }
}