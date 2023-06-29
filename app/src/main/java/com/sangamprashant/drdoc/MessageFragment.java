package com.sangamprashant.drdoc;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessageFragment extends Fragment {
    ScrollView containerForUsers;
    LinearLayout isEmptyUser,ContainerToPopulate;
    CurrentUser currentUser = CurrentUser.getInstance();
    String userId = currentUser.getUserId();
    String name = currentUser.getName();
    String email = currentUser.getEmail();
    String userName = currentUser.getUserName();
    String account = currentUser.getAccount();
    String token = currentUser.getToken();
    private int totalUsers = 0;
    String loggedUserAccount = (account.equals("doctor") ? "regular" : "doctor");
    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        containerForUsers = view.findViewById(R.id.containerForUsers);
        isEmptyUser = view.findViewById(R.id.ifEmptyUser);
        ContainerToPopulate = view.findViewById(R.id.containerToPopulate);

        makeToast(loggedUserAccount);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation); // Assign the bottomNavigationView variable
        setBottomNavigationVisibility(View.VISIBLE); // Set BottomNavigationView visible

        methodToGetUsers();
        return view;
    }

    void makeToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
    public interface BottomNavigationVisibilityListener {
        void setBottomNavigationVisibility(int visibility);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BottomNavigationVisibilityListener) {
            BottomNavigationVisibilityListener listener = (BottomNavigationVisibilityListener) context;
            listener.setBottomNavigationVisibility(View.GONE);
        }
    }
    public void setBottomNavigationVisibility(int visibility) {
        bottomNavigationView.setVisibility(visibility);
    }

    public void methodToGetUsers() {
        // Replace API_ENDPOINT with the actual endpoint URL
        String API_ENDPOINT = "https://drdoc-sangamprashant.vercel.app/message/" + loggedUserAccount;
        // Replace TOKEN with your authentication token if required
        String TOKEN = token;

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(API_ENDPOINT)
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
                            totalUsers = jsonArray.length();
                            if (totalUsers > 0) {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        containerForUsers.setVisibility(View.VISIBLE);
                                        isEmptyUser.setVisibility(View.GONE);
                                    }
                                });
                            }
                            // Loop through the JSON array and create user items
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject userObject = jsonArray.getJSONObject(i);
                                String userName = userObject.getString("name");
                                String userPhoto = userObject.optString("Photo", "");
                                String userUserName = userObject.getString("userName");

                                // Add other required data

                                // Inflate the user_layout.xml layout for each user item
                                final View userItemView = getLayoutInflater().inflate(R.layout.user_layout, null);
                                // Set data for the user item
                                final TextView nameTextView = userItemView.findViewById(R.id.nameOfUser);
                                final CircleImageView imageViewUser = userItemView.findViewById(R.id.userImage);
                                final TextView userNameTextView = userItemView.findViewById(R.id.userName);
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!TextUtils.isEmpty(userPhoto)) {
                                            Picasso.get()
                                                    .load(userPhoto)
                                                    .placeholder(R.drawable.user)
                                                    .error(R.drawable.user)
                                                    .into(imageViewUser, new Callback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            // Photo loaded successfully
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            // Handle error while loading photo
                                                        }
                                                    });
                                        }else {
                                            imageViewUser.setImageResource(R.drawable.user);
                                            // You can also set a different drawable or handle the case as per your requirements
                                        }
                                    }
                                });
                                nameTextView.setText(userName);
                                userNameTextView.setText(userUserName);
                                // Set other data for the user item

                                // Add the user item to the LinearLayout
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ContainerToPopulate.addView(userItemView);
                                        ContainerToPopulate.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (bottomNavigationView != null) {
                                                    int visibility = bottomNavigationView.getVisibility();
                                                    if (visibility == View.VISIBLE) {
                                                        setBottomNavigationVisibility(View.GONE);
                                                    } else {
                                                        setBottomNavigationVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Handle error
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireActivity(), "Failed to fetch users", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(requireActivity(), "Failed to fetch users", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
