package com.sangamprashant.drdoc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    String GeneratedOtp;
    boolean a=false, isValidUserName=false,isValidEmail=false;
    CurrentUser currentUser = CurrentUser.getInstance();
    SharedPreferences sharedPreferences;
    String[] AccountTypeOptions = {"Select Account Type","regular", "doctor", "seller"};
    TextView gotoSignIn ,Status, UserNameIsAlreadyUsed, UserNameAvailable, EmailIsAlreadyUsed, EmailAvailable;
    Button signUpButton;
    EditText   userEmailInput,userPasswordInput,userNameInput,userUserNameInput;
    LinearLayout LoadingContainerVisible,VerifyOtpBtnLayout;
    CardView SignUpContainerVisible;
    String UserAccountType;
    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four,otp_textbox_five,otp_textbox_six;
    Button verify_otp;
    LottieAnimationView  SendingOtp, SentOtp, EnterOtp, AccountCreating, AccountLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Spinner AccountTypeSpinner = findViewById(R.id.accountType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AccountTypeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AccountTypeSpinner.setAdapter(adapter);
        AccountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && a) {
                    Toast.makeText(SignUpActivity.this, "Please select an account type.", Toast.LENGTH_SHORT).show();
                } else {
                    UserAccountType = AccountTypeOptions[position];
                    a = true;
                    // Handle the selected option
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle null selection
                Toast.makeText(SignUpActivity.this, "Please select an account type.", Toast.LENGTH_SHORT).show();
            }
        });
        //init
        getSupportActionBar().hide();
        AccountCreating =  findViewById(R.id.accountCreating);
        AccountLogin =  findViewById(R.id.accountLogin);
        SendingOtp = findViewById(R.id.sendingOtp);
        SentOtp = findViewById(R.id.sentOtp);
        EnterOtp = findViewById(R.id.enterOtp);
        gotoSignIn = findViewById(R.id.goToSignIn);
        signUpButton = findViewById(R.id.sign_Up_button);
        userEmailInput= findViewById(R.id.userInputEmail);
        userNameInput= findViewById(R.id.userInputName);
        userPasswordInput = findViewById(R.id.userInputPassword);
        userUserNameInput = findViewById(R.id.userInputUserName);
        LoadingContainerVisible = findViewById(R.id.SignupLoading);
        SignUpContainerVisible = findViewById(R.id.SignupContainer);
        Status = findViewById(R.id.statusOfOtp);
        UserNameIsAlreadyUsed = findViewById(R.id.userNameIsAlreadyUsed);
        UserNameAvailable = findViewById(R.id.userNameAvailable);
        EmailIsAlreadyUsed = findViewById(R.id.emailIsAlreadyUsed);
        EmailAvailable = findViewById(R.id.emailAvailable);
        VerifyOtpBtnLayout=findViewById(R.id.verify_otp_btn_Layout);
        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
        otp_textbox_six = findViewById(R.id.otp_edit_box6);
        verify_otp = findViewById(R.id.verify_otp_btn);
        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four,otp_textbox_five,otp_textbox_six};
        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));

        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Concatenate the entered OTP digits
                String enteredOtp = otp_textbox_one.getText().toString()
                        + otp_textbox_two.getText().toString()
                        + otp_textbox_three.getText().toString()
                        + otp_textbox_four.getText().toString()
                        + otp_textbox_five.getText().toString()
                        + otp_textbox_six.getText().toString();
                // Execute the signup task by passing the signup details
                String email = userEmailInput.getText().toString();
                String password = userPasswordInput.getText().toString();
                String name = userNameInput.getText().toString();
                String userName = userUserNameInput.getText().toString();
                String account = UserAccountType;
                if (Objects.equals(GeneratedOtp, enteredOtp)){
                    Toast.makeText(getApplicationContext(), "Verified successfully.", Toast.LENGTH_SHORT).show();
                    SignUpRequest SignUpRequest = new SignUpRequest();
                    SignUpRequest.sendSignInDetails(email, password, name, userName, account);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Incorrect OTP.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        gotoSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event here
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute the signup task by passing the signup details
                String email = userEmailInput.getText().toString();
                String password = userPasswordInput.getText().toString();
                String name = userNameInput.getText().toString();
                String userName = userUserNameInput.getText().toString();
                String account = UserAccountType;
                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || userName.isEmpty() || account.equals("Select Account Type")) {
                    Toast.makeText(SignUpActivity.this, "Please enter all the fields and select an account type.", Toast.LENGTH_SHORT).show();
                } else {

                    if (isValidUserName&& isValidEmail){
                        LoadingContainerVisible.setVisibility(View.VISIBLE);
                        SignUpContainerVisible.setVisibility(View.GONE);
                        SendingOtpViaEmail SendingOtpViaEmail = new SendingOtpViaEmail();
                        SendingOtpViaEmail.sendDetailsforOtp(email, name);
                        Status.setText("Sending OTP to mail.");
                        SendingOtp.setVisibility(View.VISIBLE);
                        SentOtp.setVisibility(View.GONE);
                        EnterOtp.setVisibility(View.GONE);
                        VerifyOtpBtnLayout.setVisibility(View.GONE);
                        AccountCreating.setVisibility(View.GONE);
                        AccountLogin.setVisibility(View.GONE);
                    } else if (!isValidUserName) {
                        Toast.makeText(SignUpActivity.this, "Please write another UserName.", Toast.LENGTH_SHORT).show();
                    }
                    else if (!isValidEmail) {
                        Toast.makeText(SignUpActivity.this, "Please write another Email.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        userUserNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed when text is changing

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userName = s.toString();
                if (userName.isEmpty()){
                    isValidUserName=false;
                    UserNameAvailable.setVisibility(View.GONE);
                    UserNameIsAlreadyUsed.setVisibility(View.GONE);
                }else {
                    checkUsernameAvailability(userName);
                }
            }
        });
        userEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed when text is changing

            }

            @Override
            public void afterTextChanged(Editable s) {
                String Email = s.toString();
                if (Email.isEmpty()){
                    EmailAvailable.setVisibility(View.GONE);
                    EmailIsAlreadyUsed.setVisibility(View.GONE);
                }else {
                    checkEmailAvailability(Email);
                }
            }
        });
    }
    private void navigateToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public class SignUpRequest {

        private static final String API_URL = "https://drdoc-sangamprashant.vercel.app/api/signup";

        public void sendSignInDetails(String email, String password, String name, String userName, String account ) {
            SendingOtp.setVisibility(View.GONE);
            SentOtp.setVisibility(View.GONE);
            AccountCreating.setVisibility(View.VISIBLE);
            AccountLogin.setVisibility(View.GONE);
            EnterOtp.setVisibility(View.GONE);
            VerifyOtpBtnLayout.setVisibility(View.GONE);
            Status.setText("Creating your Account");
            new AsyncTask<Void, Void, Response>() {
                @Override
                protected Response doInBackground(Void... voids) {
                    try {
                        // Create JSON object with signIn details
                        JSONObject signUpData = new JSONObject();
                        signUpData.put("email", email);
                        signUpData.put("password", password);
                        signUpData.put("name", name);
                        signUpData.put("userName", userName);
                        signUpData.put("account", account);

                        // Create OkHttp client
                        OkHttpClient client = new OkHttpClient();

                        // Create request body with JSON data
                        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                        RequestBody requestBody = RequestBody.create(mediaType, signUpData.toString());

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
                            String message = responseJson.getString("message");
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                            // Login after registration
                            AfterRegistration();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            String responseBody = response.body().string();
                            JSONObject responseJson = new JSONObject(responseBody);
                            String message = responseJson.getString("error");
                            // Display error message
                            LoadingContainerVisible.setVisibility(View.GONE);
                            SignUpContainerVisible.setVisibility(View.VISIBLE);
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }.execute();
        }
    }
    //Logging in after registration
    private  void AfterRegistration(){
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
        String email = userEmailInput.getText().toString();
        String password = userPasswordInput.getText().toString();
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.sendSignInDetails(email, password);

    }
    public class SignInRequest {

        private static final String API_URL = "https://drdoc-sangamprashant.vercel.app/api/signin";

        public void sendSignInDetails(String email, String password) {
            Status.setText("Logging your Account");
            SendingOtp.setVisibility(View.GONE);
            SentOtp.setVisibility(View.GONE);
            AccountCreating.setVisibility(View.GONE);
            AccountLogin.setVisibility(View.VISIBLE);
            EnterOtp.setVisibility(View.GONE);
            VerifyOtpBtnLayout.setVisibility(View.GONE);
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
                            Toast.makeText(SignUpActivity.this, "SignedIn successfully.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SignUpActivity.this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }
    private void checkUsernameAvailability(String userName) {
        // Make an asynchronous request to the server to check username availability
        // You can use any networking library or method of your choice, such as OkHttp or Retrofit

        // Example using OkHttp
        OkHttpClient client = new OkHttpClient();
        String apiUrl = "https://drdoc-sangamprashant.vercel.app/api/check-username";

        // Create a JSON object with the username
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle network failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Parse the response to check if the username is available or not
                    boolean isUsernameAvailable = parseUsernameAvailabilityResponse(responseBody);
                    // Update the UI accordingly (e.g., show an error message if the username is not available)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(userUserNameInput.getText().toString())) {
                                // Username is empty
                            } else {
                                if (isUsernameAvailable) {
                                    // Username is available
                                        UserNameAvailable.setVisibility(View.VISIBLE);
                                        UserNameIsAlreadyUsed.setVisibility(View.GONE);
                                } else {
                                    // Username is not available
                                    UserNameAvailable.setVisibility(View.GONE);
                                    UserNameIsAlreadyUsed.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                } else {
                    // Handle non-successful response
                    Toast.makeText(SignUpActivity.this, "No internet, Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean parseUsernameAvailabilityResponse(String responseBody) {
        // Parse the response body to determine if the username is available or not
        // You can implement your own logic based on the response from your server
        // For example, if the response contains a boolean field indicating availability

        boolean isUsernameAvailable = false;


        try {
            JSONObject responseJson = new JSONObject(responseBody);
            isUsernameAvailable = responseJson.getBoolean("available");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isValidUserName=isUsernameAvailable;
        return isUsernameAvailable;
    }
    private void checkEmailAvailability(String Email) {
        // Make an asynchronous request to the server to check Email availability
        // You can use any networking library or method of your choice, such as OkHttp or Retrofit

        // Example using OkHttp
        OkHttpClient client = new OkHttpClient();
        String apiUrl = "https://drdoc-sangamprashant.vercel.app/api/check-email";

        // Create a JSON object with the username
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("Email", Email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle network failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Parse the response to check if the Email is available or not
                    boolean isEmailAvailable = parseEmailAvailabilityResponse(responseBody);
                    // Update the UI accordingly (e.g., show an error message if the Email is not available)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(userEmailInput.getText().toString())) {
                                // Username is empty
                            } else {
                                if (isEmailAvailable) {
                                    // Email is available
                                    // Update UI or provide feedback to the user
                                    EmailAvailable.setVisibility(View.VISIBLE);
                                    EmailIsAlreadyUsed.setVisibility(View.GONE);
                                } else {
                                    // Email is not available
                                    // Show an error message or provide feedback to the user
                                    EmailAvailable.setVisibility(View.GONE);
                                    EmailIsAlreadyUsed.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                } else {
                    // Handle non-successful response
                    Toast.makeText(SignUpActivity.this, "No internet, Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean parseEmailAvailabilityResponse(String responseBody) {
        // Parse the response body to determine if the Email is available or not
        // You can implement your own logic based on the response from your server
        // For example, if the response contains a boolean field indicating availability

        boolean isEmailAvailable = false;

        try {
            JSONObject responseJson = new JSONObject(responseBody);
            isEmailAvailable = responseJson.getBoolean("available");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isValidEmail=isEmailAvailable;
        return isEmailAvailable;
    }
    public class SendingOtpViaEmail {

        private static final String API_URL = "https://drdoc-sangamprashant.vercel.app/api/sendemail";

        public void sendDetailsforOtp(String email, String name) {
            new AsyncTask<Void, Void, Response>() {
                @Override
                protected Response doInBackground(Void... voids) {
                    try {
                        // Create JSON object with otp details
                        JSONObject otpData = new JSONObject();
                        otpData.put("to", email);
                        otpData.put("name", name);
                        // Create OkHttp client
                        OkHttpClient client = new OkHttpClient();
                        // Create request body with JSON data
                        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                        RequestBody requestBody = RequestBody.create(mediaType, otpData.toString());
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
                            // Extract the otp from the response
                            String otp = responseJson.getString("otp");
                            GeneratedOtp=otp;
                            delayToDisplay();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Display error message
                        AccountCreating.setVisibility(View.GONE);
                        AccountLogin.setVisibility(View.GONE);
                        LoadingContainerVisible.setVisibility(View.GONE);
                        SignUpContainerVisible.setVisibility(View.VISIBLE);
                        SendingOtp.setVisibility(View.GONE);
                        SentOtp.setVisibility(View.GONE);
                        VerifyOtpBtnLayout.setVisibility(View.GONE);
                        EnterOtp.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, "Failed to generate otp. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }
    private void delayToDisplay() {
        Status.setText("OTP sent.");
        AccountCreating.setVisibility(View.GONE);
        AccountLogin.setVisibility(View.GONE);
        SendingOtp.setVisibility(View.GONE);
        SentOtp.setVisibility(View.VISIBLE);
        EnterOtp.setVisibility(View.GONE);
        VerifyOtpBtnLayout.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This code will run after the specified delay
                Status.setText("Enter OTP.");
                SendingOtp.setVisibility(View.GONE);
                SentOtp.setVisibility(View.GONE);
                AccountCreating.setVisibility(View.GONE);
                AccountLogin.setVisibility(View.GONE);
                EnterOtp.setVisibility(View.VISIBLE);
                VerifyOtpBtnLayout.setVisibility(View.VISIBLE);
            }
        }, 4000);
    }
}