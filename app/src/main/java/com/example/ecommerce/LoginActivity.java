package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Prevalent.Prevalent;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText InputNumber,InputPassword;
    private Button LoginButton;
    private ProgressDialog lodingBar;
    private  String parentDbname="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton=findViewById(R.id.login_btn);
        InputNumber=findViewById(R.id.login_phone_number_input);
        InputPassword=findViewById(R.id.login_password_input);
        lodingBar=new ProgressDialog(this);



        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });
    }

    private void LoginUser() {
        String phone=InputNumber.getText().toString();
        String password=InputPassword.getText().toString();
         if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(getApplicationContext(),"Please Write your phone number... ",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(),"Please Write your password... ",Toast.LENGTH_SHORT).show();
        }else {
             lodingBar.setTitle("Login Account");
             lodingBar.setMessage("Please wait, while we are checking the credentials");
             lodingBar.setCanceledOnTouchOutside(false);
             lodingBar.show();
             AllowAccessToAccount(phone,password);


         }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child(parentDbname).child(phone).exists()){
                   Users usersData=dataSnapshot.child(parentDbname).child(phone).getValue(Users.class);
                   if(usersData.getPhone().equals(phone))
                   {
                       if(usersData.getPassword().equals(password))
                       {
                           Toast.makeText(LoginActivity.this,"Logged in Success",Toast.LENGTH_SHORT).show();
                           lodingBar.dismiss();
                           Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                           startActivity(intent);


                       }else {
                           lodingBar.dismiss();
                           Toast.makeText(LoginActivity.this,"Password don't match",Toast.LENGTH_SHORT).show();

                       }

                   }
               }else {
                   Toast.makeText(LoginActivity.this,"Account with the "+phone+" number do not exists",Toast.LENGTH_SHORT).show();
                   lodingBar.dismiss();
                   Toast.makeText(LoginActivity.this,"you need to create Account",Toast.LENGTH_SHORT).show();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
