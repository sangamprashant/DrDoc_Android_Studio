package com.sangamprashant.drdoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogInActivity extends AppCompatActivity {
    CurrentUser currentUser = CurrentUser.getInstance();
    TextView gotoSignUp;
    Button signInButton;

    EditText userEmailInput, userPasswordInput;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // Hide the action bar
        getSupportActionBar().hide();
        gotoSignUp = findViewById(R.id.goToSignUp);
        signInButton = findViewById(R.id.sign_in_button);
        userEmailInput = findViewById(R.id.InputUserEmail);
        userPasswordInput = findViewById(R.id.InputUserPassword);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            // User is already signed in, navigate to MainActivity
            currentUser.setUser(
                    userId,
                    sharedPreferences.getString("name", null),
                    sharedPreferences.getString("email", null),
                    sharedPreferences.getString("userName", null),
                    sharedPreferences.getString("account", null),
                    sharedPreferences.getString("token", null)
            );
            navigateToMainActivity();
        }

        gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmailInput.getText().toString();
                String password = userPasswordInput.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LogInActivity.this, "Please enter all the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    SignInRequest signInRequest = new SignInRequest();
                    signInRequest.sendSignInDetails(email, password);
                }
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity so that the user cannot go back to the login screen using the back button
    }

    public class SignInRequest {

        private static final String API_URL = "https://drdoc-sangamprashant.vercel.app/api/signin";

        public void sendSignInDetails(String email, String password) {
            new AsyncTask<Void, Void, Response>() {
                @Override
                protected Response doInBackground(Void... voids) {
                    try {
                        // Create JSON object with signIn details
                        JSONObject signInData = new JSONObject();
                        signInData.put("email", email);
                        signInData.put("password", password);

                        // Create OkHttp client
                        OkHttpClient client = new OkHttpClient();

                        // Create request body with JSON data
                        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                        RequestBody requestBody = RequestBody.create(mediaType, signInData.toString());

                        // Create POST request with the API URL and request body
                        Request request = new Request.Builder()
                                .url(API_URL)
                                .post(requestBody)
                                .build();

                        // Execute the request and get the response
                        Response response = client.newCall(request).execute();

                        return response;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Response response) {
                    if (response != null && response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject responseJson = new JSONObject(responseBody);

                            // Extract the token and user data from the response
                            String token = responseJson.getString("token");
                            JSONObject userJson = responseJson.getJSONObject("user");

                            // Extract the required user information
                            String userId = userJson.getString("_id");
                            String name = userJson.getString("name");
                            String userEmail = userJson.getString("email");
                            String userName = userJson.getString("userName");
                            String account = userJson.getString("account");
                            // Add any other required user information

                            // Inside the successful signIn response handling code
                            currentUser.setUser(userId, name, userEmail, userName, account, token);
                            // Set any other required user information

                            // Save the user details in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", userId);
                            editor.putString("name", name);
                            editor.putString("email", userEmail);
                            editor.putString("userName", userName);
                            editor.putString("account", account);
                            editor.putString("token", token);
                            editor.apply();

                            // Handle the success response
                            // e.g., navigate to the main activity
                            navigateToMainActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Display error message
                        Toast.makeText(LogInActivity.this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }
}
