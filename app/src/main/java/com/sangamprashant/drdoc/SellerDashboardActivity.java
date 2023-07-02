package com.sangamprashant.drdoc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SellerDashboardActivity extends AppCompatActivity {

    LinearLayout ConfirmedBtn,PackingBtn,ShippedBtn,DeliveredBtn,CanceledBtn;
    LinearLayout ConfirmedContainer,PackingContainer,ShippedContainer,DeliveredContainer,CanceledContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);
        ConfirmedContainer=findViewById(R.id.Confirm);
        PackingContainer=findViewById(R.id.Packing);
        ShippedContainer=findViewById(R.id.Shipped);
        DeliveredContainer=findViewById(R.id.Delivered);
        CanceledContainer=findViewById(R.id.Canceled);
        ConfirmedBtn=findViewById(R.id.ConfirmBtn);
        PackingBtn=findViewById(R.id.PackingBtn);
        ShippedBtn =findViewById(R.id.ShippedBtn);
        DeliveredBtn=findViewById(R.id.DeliveredBtn);
        CanceledBtn=findViewById(R.id.CanceledBtn);
        ContainerToOpen();
    }

    void ContainerToOpen(){
        ConfirmedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmedContainer.setVisibility(View.VISIBLE);
                PackingContainer.setVisibility(View.GONE);
                ShippedContainer.setVisibility(View.GONE);
                DeliveredContainer.setVisibility(View.GONE);
                CanceledContainer.setVisibility(View.GONE);
            }
        });
        PackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmedContainer.setVisibility(View.GONE);
                PackingContainer.setVisibility(View.VISIBLE);
                ShippedContainer.setVisibility(View.GONE);
                DeliveredContainer.setVisibility(View.GONE);
                CanceledContainer.setVisibility(View.GONE);
            }
        });
        ShippedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmedContainer.setVisibility(View.GONE);
                PackingContainer.setVisibility(View.GONE);
                ShippedContainer.setVisibility(View.VISIBLE);
                DeliveredContainer.setVisibility(View.GONE);
                CanceledContainer.setVisibility(View.GONE);
            }
        });
        DeliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmedContainer.setVisibility(View.GONE);
                PackingContainer.setVisibility(View.GONE);
                ShippedContainer.setVisibility(View.GONE);
                DeliveredContainer.setVisibility(View.VISIBLE);
                CanceledContainer.setVisibility(View.GONE);
            }
        });
        CanceledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmedContainer.setVisibility(View.GONE);
                PackingContainer.setVisibility(View.GONE);
                ShippedContainer.setVisibility(View.GONE);
                DeliveredContainer.setVisibility(View.GONE);
                CanceledContainer.setVisibility(View.VISIBLE);
            }
        });
    }
}