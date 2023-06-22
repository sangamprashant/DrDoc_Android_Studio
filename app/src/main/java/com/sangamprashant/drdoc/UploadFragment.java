package com.sangamprashant.drdoc;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadFragment extends Fragment {
    ImageView UploadImage;
    EditText titleInput;
    Button btnUploadProfilePic;
    String imageUrlFromCloudinary;
    Uri uri;
    LinearLayout LoadingContainer;
    ScrollView UploadContainer;
    CurrentUser currentUser = CurrentUser.getInstance();
    String userId = currentUser.getUserId();
    String jwtToken=currentUser.getToken();

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    uri = result.getData().getData();
                    // Use the uri to load the image
                    Glide.with(requireContext())
                            .load(uri)
                            .into(UploadImage); // Load the image into the ImageView
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        UploadImage = view.findViewById(R.id.ImageToChange);
        titleInput = view.findViewById(R.id.titleInput);
        btnUploadProfilePic = view.findViewById(R.id.btnUploadProfilePic);
        LoadingContainer = view.findViewById(R.id.loadingContainer);
        UploadContainer = view.findViewById(R.id.containerForUpload);

        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(requireActivity())
                        .crop()
                        .cropOval()
                        .cropFreeStyle()
                        .maxResultSize(2048, 2048, true)
                        .provider(ImageProvider.BOTH)
                        .createIntentFromDialog(it -> {
                            launcher.launch(it);
                            return null;
                        });
            }
        });

        btnUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();

                if (uri == null && title.isEmpty()) {
                    toastMessage("Please select an image and write a prescription.");
                } else if (uri == null) {
                    toastMessage("Please select an image.");
                } else if (title.isEmpty()) {
                    toastMessage("Please write a prescription detail.");
                } else {
                    UploadContainer.setVisibility(View.GONE);
                    LoadingContainer.setVisibility(View.VISIBLE);
                    // Upload the image to Cloudinary
                    uploadToCloudinary(uri);

                    if (imageUrlFromCloudinary == null) {
                        toastMessage("Something went wrong.");
                        LoadingContainer.setVisibility(View.GONE);
                        UploadContainer.setVisibility(View.VISIBLE);
                    } else {
                        // Get the image URL from Cloudinary and the title from the input field
                        String imageUrl = imageUrlFromCloudinary; // Replace with the Cloudinary image URL from the onSuccess callback
                        // Do something with the image URL and title (e.g., save them to a database)
                        UploadDocumentRequest UploadDocumentRequest = new UploadDocumentRequest();
                        UploadDocumentRequest.sendDocumentDetails(imageUrl, title);
                    }
                }
            }
        });

        return view;
    }


    public class UploadDocumentRequest {

        private static final String API_URL = "https://drdoc-sangamprashant.vercel.app/api/createpost";
        private static final String AUTHORIZATION_HEADER = "Authorization";
        public void sendDocumentDetails(String image, String body) {
            new AsyncTask<Void, Void, Response>() {
                @Override
                protected Response doInBackground(Void... voids) {
                    try {
                        // Create JSON object with post details
                        JSONObject signUpData = new JSONObject();
                        signUpData.put("pic", image);
                        signUpData.put("body", body);
                        // Create OkHttp client
                        OkHttpClient client = new OkHttpClient();

                        // Create request body with JSON data
                        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                        RequestBody requestBody = RequestBody.create(mediaType, signUpData.toString());
                        // Create POST request with the API URL and request body
                        Request.Builder requestBuilder = new Request.Builder()
                                .url(API_URL)
                                .post(requestBody);

                        // Add the JWT token as an authorization header
                        String authorizationValue = "Bearer " + jwtToken;
                        requestBuilder.addHeader(AUTHORIZATION_HEADER, authorizationValue);

                        // Build the request
                        Request request = requestBuilder.build();
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
                            toastMessage(message);
                            LoadingContainer.setVisibility(View.GONE);
                            UploadContainer.setVisibility(View.VISIBLE);
                            clearFields();

                            //
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
                            LoadingContainer.setVisibility(View.GONE);
                            UploadContainer.setVisibility(View.VISIBLE);
                            toastMessage(message);

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
    private void clearFields() {
        titleInput.setText("");
        uri = null;
        imageUrlFromCloudinary = null;
        UploadImage.setImageResource(R.drawable.addimg); // Set a placeholder image or empty image view
    }
    void toastMessage (String Message){
        // Upload the image to Cloudinary
        Toast.makeText(requireActivity(),Message, Toast.LENGTH_SHORT).show();
    }
    private void uploadToCloudinary(Uri uri) {
        try {
            URL url = new URL("https://api.cloudinary.com/v1_1/drdocsocial/image/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // Set request headers
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

            // Create the request body
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            byte[] fileData = readBytes(inputStream);
            byte[] requestBody = buildMultipartFormData(boundary, "file", fileData, "drdocstartuppreset", "drdocsocial");

            // Write the request body
            connection.getOutputStream().write(requestBody);

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = connection.getInputStream();
                String response = convertInputStreamToString(responseStream);
                // Parse the response to get the image URL
                // Example response format: {"url": "https://res.cloudinary.com/drdocsocial/image/upload/v1234567890/sample.jpg"}
                imageUrlFromCloudinary = parseImageUrlFromResponse(response);
                // Do something with the imageUrl (e.g., save it to a database)
            } else {
                // Handle error
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] buildMultipartFormData(String boundary, String fieldName, byte[] fileData, String uploadPreset, String cloudName) {
        StringBuilder requestBodyBuilder = new StringBuilder();
        requestBodyBuilder.append("--").append(boundary).append("\r\n");
        requestBodyBuilder.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"image.jpg\"").append("\r\n");
        requestBodyBuilder.append("Content-Type: image/jpeg").append("\r\n");
        requestBodyBuilder.append("\r\n");
        String requestBodyPart1 = requestBodyBuilder.toString();

        StringBuilder requestBodyPart2Builder = new StringBuilder();
        requestBodyPart2Builder.append("\r\n");
        requestBodyPart2Builder.append("--").append(boundary).append("\r\n");
        requestBodyPart2Builder.append("Content-Disposition: form-data; name=\"upload_preset\"").append("\r\n");
        requestBodyPart2Builder.append("\r\n");
        requestBodyPart2Builder.append(uploadPreset).append("\r\n");
        requestBodyPart2Builder.append("--").append(boundary).append("\r\n");
        requestBodyPart2Builder.append("Content-Disposition: form-data; name=\"cloud_name\"").append("\r\n");
        requestBodyPart2Builder.append("\r\n");
        requestBodyPart2Builder.append(cloudName).append("\r\n");
        requestBodyPart2Builder.append("--").append(boundary).append("--").append("\r\n");
        String requestBodyPart2 = requestBodyPart2Builder.toString();

        byte[] requestBodyPart1Bytes = requestBodyPart1.getBytes();
        byte[] requestBodyPart2Bytes = requestBodyPart2.getBytes();

        byte[] requestBody = new byte[requestBodyPart1Bytes.length + fileData.length + requestBodyPart2Bytes.length];
        System.arraycopy(requestBodyPart1Bytes, 0, requestBody, 0, requestBodyPart1Bytes.length);
        System.arraycopy(fileData, 0, requestBody, requestBodyPart1Bytes.length, fileData.length);
        System.arraycopy(requestBodyPart2Bytes, 0, requestBody, requestBodyPart1Bytes.length + fileData.length, requestBodyPart2Bytes.length);

        return requestBody;
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private String parseImageUrlFromResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.optString("url", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
