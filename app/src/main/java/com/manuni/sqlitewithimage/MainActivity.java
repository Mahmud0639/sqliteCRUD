package com.manuni.sqlitewithimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.manuni.sqlitewithimage.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public static final int STORAGE_ACCESS_CODE = 30;
    public static final int STORAGE_IMAGE_GET = 40;
    private Uri imageUri;

    public static SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //creating database
        sqliteHelper = new SqliteHelper(MainActivity.this,"RECORDDB.sqlite",null,1);
        //insert columns
        sqliteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR,age VARCHAR,phone VARCHAR,image BLOB)");

        binding.imageForUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_ACCESS_CODE);
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sqliteHelper.insertData(binding.nameEt.getText().toString(),binding.ageEt.getText().toString(),binding.phoneEt.getText().toString(),imageToByte(binding.imageForUpload));
                    Toast.makeText(MainActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                    binding.nameEt.setText("");
                    binding.ageEt.setText("");
                    binding.phoneEt.setText("");
                    binding.imageForUpload.setImageResource(R.drawable.ic_add_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RecordListActivity.class));
            }
        });




    }

    //ekhane byte automatic houyar karon holo insertData parameter a byte send korte hobe
    public static byte[] imageToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] byteImage = baos.toByteArray();

        return byteImage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, STORAGE_IMAGE_GET);
        }else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_IMAGE_GET && resultCode == RESULT_OK){
           imageUri = data.getData();

            try {
                binding.imageForUpload.setImageURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}