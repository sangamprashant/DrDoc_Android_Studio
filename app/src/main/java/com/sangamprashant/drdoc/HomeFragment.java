package com.sangamprashant.drdoc;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    TextView NameOfUser,UserNameOfUser;
    CurrentUser currentUser = CurrentUser.getInstance();
    String userId = currentUser.getUserId();
    String name = currentUser.getName();
    String email = currentUser.getEmail();
    String userName = currentUser.getUserName();
    String account = currentUser.getAccount();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        NameOfUser = view.findViewById(R.id.Name_of_user);
        UserNameOfUser = view.findViewById(R.id.Username_of_user);

        NameOfUser.setText(name);
        UserNameOfUser.setText(userName);

        return view;
    }

}