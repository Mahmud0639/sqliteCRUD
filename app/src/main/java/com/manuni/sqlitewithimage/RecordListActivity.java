package com.manuni.sqlitewithimage;

import static com.manuni.sqlitewithimage.MainActivity.STORAGE_ACCESS_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.manuni.sqlitewithimage.databinding.ActivityRecordListBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class RecordListActivity extends AppCompatActivity {
    ActivityRecordListBinding binding;
    RecordListAdapter adapter = null;
    ArrayList<Model> list;
    ImageView imageViewIcon;
    private Uri imageUri;

    public static final int STORAGE_ACCESS_CODE = 30;
    public static final int STORAGE_IMAGE_GET = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<>();
        adapter = new RecordListAdapter(RecordListActivity.this, R.layout.row_sample, list);
        binding.listView.setAdapter(adapter);

        //get all data from sqlite
        Cursor cursor = MainActivity.sqliteHelper.getData("SELECT * FROM RECORD");
        list.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String age = cursor.getString(2);
            String phone = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            list.add( new Model(id, name, age, phone, image));
        }
        adapter.notifyDataSetChanged();

        if (list.size() == 0) {
            Toast.makeText(this, "No record is available to show.Please insert.", Toast.LENGTH_SHORT).show();

        }
        binding.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(RecordListActivity.this);
                builder.setTitle("Choose an action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            //update
                            Cursor c = MainActivity.sqliteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrId = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrId.add(c.getInt(0));
                            }
                            //show update dialog
                            showDialogUpdate(RecordListActivity.this,arrId.get(position));

                        }
                        if (i == 1) {
                            //delete
                            Cursor cursorDelete = MainActivity.sqliteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrayId = new ArrayList<Integer>();
                            while (cursorDelete.moveToNext()) {
                                arrayId.add(cursorDelete.getInt(0));
                            }
                            showDialogDelete(arrayId.get(position));
                        }
                    }
                });
                builder.show();


                return true;
            }
        });


    }

    private void showDialogDelete(int integer) {
        AlertDialog.Builder builder=new AlertDialog.Builder(RecordListActivity.this);
        builder.setTitle("Warning!");
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    MainActivity.sqliteHelper.deleteData(integer);
                    Toast.makeText(RecordListActivity.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateRecordList();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    private void showDialogUpdate(Activity activity, int position) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");

        imageViewIcon = dialog.findViewById(R.id.imageUpdate);
        EditText nameET = dialog.findViewById(R.id.nameUpdate);
        EditText ageET = dialog.findViewById(R.id.ageUpdate);
        EditText phoneET = dialog.findViewById(R.id.phoneUpdate);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        //get all data from sqlite
        Cursor cursor = MainActivity.sqliteHelper.getData("SELECT * FROM RECORD WHERE id="+position);
        list.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            nameET.setText(name);
            String age = cursor.getString(2);
            ageET.setText(age);
            String phone = cursor.getString(3);
            phoneET.setText(phone);
            byte[] image = cursor.getBlob(4);
            imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));

            list.add( new Model(id, name, age, phone, image));
        }

        //set width and height of the dialog layout

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.7);

        dialog.getWindow().setLayout(width,height);

        dialog.show();

        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(RecordListActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_ACCESS_CODE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.sqliteHelper.updateData(nameET.getText().toString(),ageET.getText().toString(),phoneET.getText().toString(),MainActivity.imageToByte(imageViewIcon),position);
                    dialog.dismiss();
                    Toast.makeText(activity, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateRecordList();

            }
        });

    }

    private void updateRecordList() {
       Cursor cursor = MainActivity.sqliteHelper.getData("SELECT * FROM RECORD");
        list.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String age = cursor.getString(2);
            String phone = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            list.add(new Model(id,name,age,phone,image));
        }
        adapter.notifyDataSetChanged();

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
                imageViewIcon.setImageURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}