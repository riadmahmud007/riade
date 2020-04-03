package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Prevalent.Prevalent;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinNowButton,loginButton;
    private ProgressDialog lodingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNowButton=findViewById(R.id.main_join_now_id);
        loginButton=findViewById(R.id.main_login_id);
        lodingBar=new ProgressDialog(this);
        Paper.init(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });
        String UserPhoneKey=Paper.book().read(Prevalent.UserPhonekey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordkey);
        if(UserPhoneKey!="" && UserPasswordKey!=""){
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
               AllowAccess(UserPhoneKey,UserPasswordKey);
                lodingBar.setTitle("Already Login");
                lodingBar.setMessage("Please wait...");
                lodingBar.setCanceledOnTouchOutside(false);
                lodingBar.show();
            }
        }
    }

    private void AllowAccess(final  String phone,final String password) {


        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(phone).exists()){
                    Users usersData=dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this,"Logged in Success",Toast.LENGTH_SHORT).show();
                            lodingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.currentOnlineuser=usersData;
                            startActivity(intent);


                        }else {
                            lodingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Password don't match",Toast.LENGTH_SHORT).show();

                        }

                    }
                }else {
                    Toast.makeText(MainActivity.this,"Account with the "+phone+" number do not exists",Toast.LENGTH_SHORT).show();
                    lodingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
