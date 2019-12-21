package com.example.asfalitoapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference usersref;
    FirebaseAuth mAuth;
    EditText desc;
    Location usrlocation;
    FusedLocationProviderClient fusedLocationClient;


    // Folder path for Firebase Storage.
    String Storage_Path = "images";
    Button ChooseButton, UploadButton;

    // Creating ImageView.
    ImageView SelectImage;

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        getLastlocation();
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        desc = (EditText) findViewById(R.id.editText);

        requestStoragePermission();


        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();


        database = FirebaseDatabase.getInstance();
        usersref = database.getReference("images/");

        ChooseButton = (Button) findViewById(R.id.button3);
        UploadButton = (Button) findViewById(R.id.button2);
        SelectImage = (ImageView) findViewById(R.id.imageView);
        progressDialog = new ProgressDialog(MainActivity.this);
        ChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });


        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling method to upload selected image on Firebase storage.
                UploadImageFileToFirebaseStorage();

            }
        });



    }





    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public void getLastlocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener < Location > () {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            usrlocation = location;

                        }
                    }
                });
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        if (FilePathUri != null) {



            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.

             final String id=mAuth.getUid()+System.currentTimeMillis();

            StorageReference storageReference2nd = storageReference.child(Storage_Path + id + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener < UploadTask.TaskSnapshot > () {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();



                            @SuppressWarnings("VisibleForTests")
                            Report imageUploadInfo = new Report(desc.getText().toString(), taskSnapshot.getStorage().getDownloadUrl().toString(), usrlocation.getLatitude(), usrlocation.getLongitude(),mAuth.getUid(),id);

                            // Getting image upload ID.
                            // String ImageUploadId = databaseReference.push().getKey();



                            DatabaseReference imageref = usersref.push();
                            imageref.setValue(imageUploadInfo);

                        }




                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener < UploadTask.TaskSnapshot > () {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        } else {

            Toast.makeText(MainActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
        // Checking whether FilePathUri Is empty or not.

    }




    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            getLastlocation();


                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                getLastlocation();

                                return;
                            }
                        }

                        // check for permanent denial of any permission
                        if (!report.areAllPermissionsGranted()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List < PermissionRequest > permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                SelectImage.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                ChooseButton.setText("Image Selected");

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}