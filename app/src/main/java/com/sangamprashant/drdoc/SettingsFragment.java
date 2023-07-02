package com.sangamprashant.drdoc;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class SettingsFragment extends Fragment {
    NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_profile) {
                    showProfileDialog();
                    return true;
                }
                if (item.getItemId() == R.id.nav_change_account) {
                    showChangeAccountDialog();
                    return true;
                }
                if (item.getItemId() == R.id.nav_cart) {
                    Intent intent = new Intent(requireActivity(), CartActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.nav_my_order) {
                    Intent intent = new Intent(requireActivity(), MyOrdersActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.nav_dashboard) {
                    Intent intent = new Intent(requireActivity(),SellerDashboardActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.nav_logout) {
                    showLogoutDialog();
                    return true;
                }
                if (item.getItemId() == R.id.nav_about) {
                    showAboutDialog();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void showProfileDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_photo);

        ImageButton ImageToSelect = dialog.findViewById(R.id.ImageToUpload);
        Button UploadSelectedImage = dialog.findViewById(R.id.uploadSelectedImage);
        Button RemoveCurrentPhoto = dialog.findViewById(R.id.removeCurrentProfile);
        Button CanceledUpload = dialog.findViewById(R.id.canceledProfileUpload);
        ImageToSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(),"ImageToSelect is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        UploadSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(requireActivity(),"UploadSelectedImage is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        RemoveCurrentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(requireActivity(),"RemoveCurrentPhoto is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        CanceledUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(requireActivity(),"CanceledUpload is clicked",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void showChangeAccountDialog () {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_account_type);

        LinearLayout RegularAccount = dialog.findViewById(R.id.regularAccount);
        LinearLayout DoctorAccount = dialog.findViewById(R.id.doctorAccount);
        LinearLayout SellerAccount = dialog.findViewById(R.id.sellerAccount);
        Button CanceledAccountChange = dialog.findViewById(R.id.cancelAccountChange);

        RegularAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(),"RegularAccount is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        DoctorAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(),"DoctorAccount is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        SellerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(),"SellerAccount is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        CanceledAccountChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(requireActivity(),"CanceledAccountChange is clicked",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void showLogoutDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout LogOutConfirm = dialog.findViewById(R.id.logOutConfirm);
        LinearLayout CancelLogOut = dialog.findViewById(R.id.cancelLogout);

        LogOutConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(requireActivity(),"LogOutConfirm is clicked",Toast.LENGTH_SHORT).show();
            }
        });

        CancelLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(requireActivity(),"Canceled LogOut.",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private void showAboutDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.about_us);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
