package com.sangamprashant.drdoc;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class UploadFragment extends Fragment {
    ImageView UploadImage;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
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

        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(requireActivity())
                        .crop()
                        .cropOval()
                        .cropFreeStyle()
                        .maxResultSize(512, 512, true)
                        .provider(ImageProvider.BOTH)
                        .createIntentFromDialog(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher.launch(it);
                                return null;
                            }
                        });
            }
        });

        return view;
    }
}