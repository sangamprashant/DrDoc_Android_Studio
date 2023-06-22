package com.sangamprashant.drdoc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StoreFragment extends Fragment {
    private int totalPosts = 0;
    private EditText SearchInput;
    private ImageButton menuButton;
    LinearLayout RootOfProduct,ProductCard,ForRepeatInVertical;
    TextView TotalNumberOfProducts,IfEmptyProduct;
    ScrollView IfProductAvailable;
    String type ="all";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        SearchInput = view.findViewById(R.id.searchInput);
        menuButton = view.findViewById(R.id.menuButton);
        RootOfProduct = view.findViewById(R.id.rootProductContainerOfTwo);
        ForRepeatInVertical = view.findViewById(R.id.forRepeatInVertical);
        TotalNumberOfProducts = view.findViewById(R.id.totalNumberOfProducts);
        IfProductAvailable = view.findViewById(R.id.ifProductAvailable);
        IfEmptyProduct = view.findViewById(R.id.ifEmptyProduct);

        // Set click listener for the menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu
                PopupMenu popupMenu = new PopupMenu(requireContext(), menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.store_menu, popupMenu.getMenu());

                // Set click listener for menu item selection
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        // Handle menu item selection
                        if (item.getItemId() == R.id.option1) {
                            showToast("Option 1 selected");
                            return true;
                        } else if (item.getItemId() == R.id.option2) {
                            showToast("Option 2 selected");
                            return true;
                        }
                        return true;

                    }
                });

                // Show the PopupMenu
                popupMenu.show();
            }
        });

        fetchAllProducts();
        methodToGetPost();

        return view;
    }

    private void fetchAllProducts() {
        showToast("Product run");
    }

    public void methodToGetPost() {
        // Replace API_ENDPOINT with the actual endpoint URL
        String API_ENDPOINT = "https://drdoc-sangamprashant.vercel.app/api/product/store/" + type;
        // Replace TOKEN with your authentication token if required

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(API_ENDPOINT)
                .get();

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
                                        TotalNumberOfProducts.setText(String.valueOf(totalPosts) + " Products");
                                        IfProductAvailable.setVisibility(View.VISIBLE);
                                        IfEmptyProduct.setVisibility(View.GONE);
                                    }
                                });
                            }

                            // Loop through the JSON array and create product card items

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Create a new LinearLayout for the first column
                                    LinearLayout firstColumnLayout = new LinearLayout(requireActivity());
                                    firstColumnLayout.setOrientation(LinearLayout.VERTICAL);

                                    // Loop through the JSON array and create product card items
                                    for (int i = 0; i < jsonArray.length(); i += 2) {
                                        try {
                                            // Get the first product item in the pair
                                            JSONObject firstPostObject = jsonArray.getJSONObject(i);
                                            String firstPostTitle = firstPostObject.getString("title");
                                            String firstPostPic = firstPostObject.getString("imageUrl");
                                            String firstPostDate = firstPostObject.getString("type");

                                            // Inflate the product card layout for the first item
                                            View firstPostItemView = getLayoutInflater().inflate(R.layout.product_card, null);

                                            // Set data for the first product card
                                            TextView firstTitleTextView = firstPostItemView.findViewById(R.id.productTitle);
                                            ImageView firstImageViewPost = firstPostItemView.findViewById(R.id.productImage);
                                            TextView firstDateTextView = firstPostItemView.findViewById(R.id.productType);
                                            Picasso.get()
                                                    .load(firstPostPic)
                                                    .placeholder(R.drawable.file)
                                                    .error(R.drawable.file)
                                                    .into(firstImageViewPost);
                                            firstTitleTextView.setText(firstPostTitle);
                                            firstDateTextView.setText(firstPostDate);

                                            // Check if there is a second product item in the pair
                                            if (i + 1 < jsonArray.length()) {
                                                // Get the second product item in the pair
                                                JSONObject secondPostObject = jsonArray.getJSONObject(i + 1);
                                                String secondPostTitle = secondPostObject.getString("title");
                                                String secondPostPic = secondPostObject.getString("imageUrl");
                                                String secondPostDate = secondPostObject.getString("type");

                                                // Inflate the product card layout for the second item
                                                View secondPostItemView = getLayoutInflater().inflate(R.layout.product_card, null);

                                                // Set data for the second product card
                                                TextView secondTitleTextView = secondPostItemView.findViewById(R.id.productTitle);
                                                ImageView secondImageViewPost = secondPostItemView.findViewById(R.id.productImage);
                                                TextView secondDateTextView = secondPostItemView.findViewById(R.id.productType);
                                                Picasso.get()
                                                        .load(secondPostPic)
                                                        .placeholder(R.drawable.file)
                                                        .error(R.drawable.file)
                                                        .into(secondImageViewPost);
                                                secondTitleTextView.setText(secondPostTitle);
                                                secondDateTextView.setText(secondPostDate);

                                                // Create a horizontal LinearLayout to contain the pair of product cards
                                                LinearLayout pairContainer = new LinearLayout(requireActivity());
                                                pairContainer.setOrientation(LinearLayout.HORIZONTAL);
                                                pairContainer.addView(firstPostItemView);
                                                pairContainer.addView(secondPostItemView);

                                                // Add the pair container to the first column LinearLayout
                                                firstColumnLayout.addView(pairContainer);
                                            } else {
                                                // Only one product item left, add it individually to the first column LinearLayout
                                                firstColumnLayout.addView(firstPostItemView);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    // Add the first column LinearLayout to the rootProductContainerOfTwo LinearLayout
                                    LinearLayout rootProductContainerOfTwo = getView().findViewById(R.id.rootProductContainerOfTwo);
                                    rootProductContainerOfTwo.addView(firstColumnLayout);
                                }
                            });


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



    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
