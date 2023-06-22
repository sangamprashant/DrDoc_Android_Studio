package com.sangamprashant.drdoc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    TextView NameOfUser, UserNameOfUser, NoOfPostOfUser;
    CircleImageView UserImageBox;
    LinearLayout PostLinearLayout, IfEmptyPost;
    CurrentUser currentUser = CurrentUser.getInstance();
    String userId = currentUser.getUserId();
    String name = currentUser.getName();
    String email = currentUser.getEmail();
    String userName = currentUser.getUserName();
    String account = currentUser.getAccount();
    String Token = currentUser.getToken();
    String PhotoFromUser = currentUser.getPhoto();
    SharedPreferences sharedPreferences;

    // Pagination variables
    private int currentPage = 1;
    private int totalPosts = 0;
    private boolean isLoading = false;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        NameOfUser = view.findViewById(R.id.Name_of_user);
        UserNameOfUser = view.findViewById(R.id.Username_of_user);
        UserImageBox = view.findViewById(R.id.LoggedUserImage);
        PostLinearLayout = view.findViewById(R.id.rootContainerOfPost);
        NoOfPostOfUser = view.findViewById(R.id.noOfPost_of_user);
        IfEmptyPost = view.findViewById(R.id.ifEmptyPost);
        NameOfUser.setText(name);
        UserNameOfUser.setText(userName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Enable cleartext traffic for HTTP requests
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }
        RunOnOnCreate();
        methodToGetPost();
        GetProfileRequest GetProfileRequest = new GetProfileRequest();
        GetProfileRequest.sendIdDetails();

        // Set up the scroll listener for pagination
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PostLinearLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End of the list reached, load more posts
                        currentPage++;
                        methodToGetPost();
                    }
                }
            });
        }

        return view;
    }

    public void RunOnOnCreate() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        // Retrieve the photo from SharedPreferences
        String storedPhoto = sharedPreferences.getString("photo", null);
        Picasso.get()
                .load(storedPhoto)
                .placeholder(R.drawable.user) // Set a placeholder image while loading
                .error(R.drawable.user) // Set an error image if loading fails
                .into(UserImageBox, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Photo loaded successfully
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle error while loading photo
                    }
                });
    }

    public void methodToGetPost() {
        // Replace API_ENDPOINT with the actual endpoint URL
        String API_ENDPOINT = "https://drdoc-sangamprashant.vercel.app/api/posts";
        // Replace TOKEN with your authentication token if required
        String TOKEN = Token;

        isLoading = true;

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(API_ENDPOINT + "?page=" + currentPage)
                .get();

        // Add authorization header if required
        requestBuilder.addHeader("Authorization", "Bearer " + TOKEN);

        Request request = requestBuilder.build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseBody);
                            totalPosts = jsonArray.length();
                            if (totalPosts > 0) {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        NoOfPostOfUser.setText(String.valueOf(totalPosts) + " Prescriptions");
                                        PostLinearLayout.setVisibility(View.VISIBLE);
                                        IfEmptyPost.setVisibility(View.GONE);
                                    }
                                });
                            }

                            // Loop through the JSON array and create post items
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject postObject = jsonArray.getJSONObject(i);
                                String postTitle = postObject.getString("body");
                                String postPic = postObject.getString("photo");
                                String postDate = postObject.getString("createdAt");

                                // Add other required data

                                // Inflate the item_post.xml layout for each post
                                final View postItemView = getLayoutInflater().inflate(R.layout.post_layout, null);

                                // Set data for the post item
                                final TextView titleTextView = postItemView.findViewById(R.id.postTitle);
                                final ImageFilterView ImageViewPost = postItemView.findViewById(R.id.postImage);
                                final TextView dateTextView = postItemView.findViewById(R.id.postTime);
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Picasso.get()
                                                .load(postPic)
                                                .placeholder(R.drawable.file)
                                                .error(R.drawable.file)
                                                .into(ImageViewPost, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        // Photo loaded successfully
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        // Handle error while loading photo
                                                    }
                                                });
                                    }
                                });
                                titleTextView.setText(postTitle);
                                dateTextView.setText(postDate);
                                // Set other data for the post item

                                // Add the post item to the LinearLayout
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PostLinearLayout.addView(postItemView);
                                    }
                                });
                            }

                            isLoading = false;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Handle error
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireActivity(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                // Handle error
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public class GetProfileRequest {

        private final String API_URL = "https://drdoc-sangamprashant.vercel.app/api/user/" + userId + "/profilephoto";

        public void sendIdDetails() {
            new AsyncTask<Void, Void, Response>() {
                @Override
                protected Response doInBackground(Void... voids) {
                    try {
                        // Create OkHttp client
                        OkHttpClient client = new OkHttpClient();

                        // Create GET request with the API URL
                        Request request = new Request.Builder()
                                .url(API_URL)
                                .get()
                                .build();

                        // Execute the request and get the response
                        Response response = client.newCall(request).execute();

                        return response;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void onPostExecute(Response response) {
                    if (response != null && response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject responseJson = new JSONObject(responseBody);

                            // Extract the token and user data from the response
                            String photo = responseJson.getString("photo");
                            if (photo != null && !photo.isEmpty()) {
                                currentUser.setProfile(photo);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("photo", photo);


                                Picasso.get()
                                        .load(photo)
                                        .placeholder(R.drawable.user)
                                        .error(R.drawable.user)
                                        .into(UserImageBox, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                // Photo loaded successfully
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                // Handle error while loading photo
                                            }
                                        });
                            } else {
                                UserImageBox.setImageResource(R.drawable.user);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Display error message
                        Toast.makeText(requireActivity(), "Failed to get Profile Photo. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }
}
