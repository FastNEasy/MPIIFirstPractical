package com.example.practical1_java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "MPII";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String SHARED_PREFS_FILE = "sharedFile";
    List<Uri> imgUri = new ArrayList<>();//for files to  be added to the slider
    //List<String> imgUriStrings = new ArrayList<>();
    String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
        //loadData();//supposed to load data from preferences but created a crash
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "main activity");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "started");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        setContentView(R.layout.activity_main);


    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.audioItem){
            Intent intent = new Intent(MainActivity.this, RecordActivity1.class);
            startActivity(intent);

            return true;
        }
        else if(id == R.id.deleteItem){
            Toast.makeText(this, "pressed delete items", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){//camera opening button
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        dispatchTakePictureIntent();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "button_clicked");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "opening_camera");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void dispatchTakePictureIntent() {//starts the camera intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.practical1_java",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {//makes and saves the image file
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK){
                File f = new File(currentPhotoPath);
                ImageView thumbnail = findViewById(R.id.thumbnail);//image thumbnail
                thumbnail.setImageURI(Uri.fromFile(f));
                Log.d(TAG, "Absolute Url of img is: " + Uri.fromFile(f));
                galleryAddPic(f);
                imgUri.add(Uri.fromFile(f));//adds to list that presents all the uri's in the running app
                //imgUriStrings.add(Uri.fromFile(f).toString());
                ViewPager2 imageViewPager = findViewById(R.id.viewPagerMain);
                imageViewPager.setAdapter(new ViewPagerAdapter(this,imgUri));
                //saveData(); // also caused the app crash, not implemented
            }


        }
    }

    private void saveData() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        //String json = gson.toJson(imgUriStrings);
       // editor.putString("TakenPictures", json);
        editor.apply();
    }
    //cant load data into shared prefs
    private void loadData(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("TakenPictures", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        //imgUriStrings = gson.fromJson(json, type);
//        if (imgUriStrings == null){
//            imgUriStrings = new ArrayList<>();
//        }else{
////            for(String str : imgUriStrings){
////                imgUri.add(Uri.parse(str));
////            }
//            ViewPager2 imageViewPager = findViewById(R.id.viewPagerMain);
//            imageViewPager.setAdapter(new ViewPagerAdapter(this,imgUri));
//        }
    }


    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {
           //if all perms oki

        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}