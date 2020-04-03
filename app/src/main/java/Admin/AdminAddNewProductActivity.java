package Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
private String CategoryName,Description,Price,Pname,saveCurrentDate,saveCurrentTime;
private Button AddNewProductButton;
private EditText InputProductName,InputProductDescrpition,InputProductPrice;
private ImageView InputProductImage;
private static final int GalleryPick=1;
private Uri ImageUri;
private String productRandomKey,downloadimageUrl;
private  StorageReference ProductImageRef;
private DatabaseReference ProductRef;
    private ProgressDialog lodingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        CategoryName=getIntent().getExtras().get("category").toString();
        ProductImageRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductRef=FirebaseDatabase.getInstance().getReference().child("Products");

        AddNewProductButton=findViewById(R.id.add_new_product);
        InputProductImage=findViewById(R.id.select_product_image);
        InputProductName=findViewById(R.id.product_name);
        InputProductDescrpition=findViewById(R.id.product_description);
        InputProductPrice=findViewById(R.id.product_price);
        lodingBar=new ProgressDialog(this);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
                
            }
        });
        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProduct();
            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }
    private void ValidateProduct(){
        Description=InputProductDescrpition.getText().toString();
        Price=InputProductPrice.getText().toString();
        Pname=InputProductName.getText().toString();
        if(ImageUri==null)
        {
            Toast.makeText(AdminAddNewProductActivity.this,"Product Image not select ",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(AdminAddNewProductActivity.this,"Please write Product Description ",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pname))
        {
            Toast.makeText(AdminAddNewProductActivity.this,"Please write Product Name ",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Price))
        {
            Toast.makeText(AdminAddNewProductActivity.this,"Please write Product price",Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductInformation();
        }
    }
    private void StoreProductInformation()
    {
        lodingBar.setTitle("Add New Product");
        lodingBar.setMessage("Please wait, while we are adding the new product");
        lodingBar.setCanceledOnTouchOutside(false);
        lodingBar.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        productRandomKey=saveCurrentDate + saveCurrentTime;

        final StorageReference filePath=ProductImageRef.child(ImageUri.getLastPathSegment()+productRandomKey +".jpg");
        final UploadTask uploadTask=filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AdminAddNewProductActivity.this,"Error"+e,Toast.LENGTH_SHORT).show();
                lodingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this,"Product Image upload successfully",Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        downloadimageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadimageUrl=task.getResult().toString();
                        Toast.makeText(AdminAddNewProductActivity.this,"Product Image save to database successfully",Toast.LENGTH_SHORT).show();
                        SaveProductInfoToDatabase();
                    }}
                });

            }
        });
    }
    private void SaveProductInfoToDatabase(){
        HashMap<String,Object>productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadimageUrl);
        productMap.put("category",CategoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname);

        ProductRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent intent=new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);

                    lodingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this,"Product is added successfully",Toast.LENGTH_SHORT).show();
                }else
                {
                    lodingBar.dismiss();
                    String meggase=task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this,"Error : "+meggase,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
