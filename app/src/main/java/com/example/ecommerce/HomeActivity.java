package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Admin.AdminMaintainProductsActivity;
import Model.Products;
import Prevalent.Prevalent;
import ViewHolder.ProductViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    NavigationView navigationView;
    DrawerLayout drawer;

    private String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null)
        {
            type=getIntent().getExtras().get("Admin").toString();
        }



        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!type.equals("Admin"))
                {
                    Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                    startActivity(intent);
                }


            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);
        View headerView=navigationView.getHeaderView(0);
        TextView userNameTextView=headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView=headerView.findViewById(R.id.user_profile_image);

        if(!type.equals("Admin"))
        {
            userNameTextView.setText(Prevalent.currentOnlineuser.getName());
            Picasso.get().load(Prevalent.currentOnlineuser.getImage()).placeholder(R.drawable.profile).into(profileImageView);

        }
        recyclerView=findViewById(R.id.recycler_manu);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    protected  void onStart() {

        super.onStart();
        FirebaseRecyclerOptions<Products> options=
                new  FirebaseRecyclerOptions.Builder<Products>().setQuery(ProductsRef,Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                        productViewHolder.txtProductName.setText(products.getPname());
                        productViewHolder.txtProductDescription.setText(products.getDescription());
                        productViewHolder.txtProductPrice.setText("Price = "+products.getPrice()+" TAKA ");
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(type.equals("Admin"))
                                {
                                    Intent intent=new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid",products.getPid());
                                    startActivity(intent);

                                }
                                else
                                {
                                    Intent intent=new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",products.getPid());
                                    startActivity(intent);

                                }

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
                    ProductViewHolder holder=new ProductViewHolder(view);
                    return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void onBackPress(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            if(menuItem.getItemId()==R.id.nav_cart){
                if(!type.equals("Admin"))
                {
                    Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                    startActivity(intent);
                }



            }else if(menuItem.getItemId()==R.id.nav_search){
                if(!type.equals("Admin"))
                {
                    Intent intent=new Intent(HomeActivity.this,SearchProductsActivity.class);
                    startActivity(intent);


                }


            }else if(menuItem.getItemId()==R.id.nav_categories){
                if(!type.equals("Admin"))
                {
                    Toast.makeText(HomeActivity.this,"cart",Toast.LENGTH_SHORT).show();

                }


            }else if(menuItem.getItemId()==R.id.nav_settings){
                if(!type.equals("Admin"))
                {
                    Intent intent=new Intent(HomeActivity.this,SettingActivity.class);
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this,"cart",Toast.LENGTH_SHORT).show();

                }

            }else if(menuItem.getItemId()==R.id.nav_logout){
                if(!type.equals("Admin"))
                {
                    Toast.makeText(HomeActivity.this,"logout",Toast.LENGTH_SHORT).show();
                    Paper.book().destroy();
                    Intent intent=new Intent(HomeActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }

            }
            DrawerLayout drawer=findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;



    }
}
