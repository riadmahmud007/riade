package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Products;
import ViewHolder.ProductViewHolder;

public class SearchProductsActivity extends AppCompatActivity {
    private Button SearchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String SearchInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        inputText=findViewById(R.id.search_product_name);
        SearchBtn=findViewById(R.id.search_btn);
        searchList=findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchProductsActivity.this));

        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchInput=inputText.getText().toString();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Products");


        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(SearchInput),Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=new
                FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {

                        productViewHolder.txtProductName.setText(products.getPname());
                        productViewHolder.txtProductDescription.setText(products.getDescription());
                        productViewHolder.txtProductPrice.setText("Price = "+products.getPrice()+" TAKA ");
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(SearchProductsActivity.this,ProductDetailsActivity.class);
                                intent.putExtra("pid",products.getPid());
                                startActivity(intent);
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
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}
