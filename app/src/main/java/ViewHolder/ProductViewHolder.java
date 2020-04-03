package ViewHolder;

import android.content.ClipData;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;

import Interface.ItemClickListner;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
public TextView txtProductName,txtProductDescription,txtProductPrice;
public ImageView imageView;
public ItemClickListner listner;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.product_image);
        txtProductName=itemView.findViewById(R.id.product_name);
        txtProductPrice=itemView.findViewById(R.id.product_price);
        txtProductDescription=itemView.findViewById(R.id.product_description);
    }

public  void setItemClickLister(ItemClickListner listner){

this.listner=listner;
}

    @Override
    public void onClick(View view) {
        listner.onClick(view,getAdapterPosition(),false);

    }

}
