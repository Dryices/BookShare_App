
package com.sp.bookshare;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class ListFragment extends Fragment {

    ImageView imageView;
    File Destination;
    private static final String TAG = "ListFragment";
    String groupId = "";

    private static final int GalleryPick = 1;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int PICK_Camera_IMAGE = 2;
    String cameraPermission[];
    String storagePermission[];


    EditText name, price, category, moduleCode, description;
    Button select, list, map;
    Uri imageurl;

    //Firebase Storage declarations
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Inputs
        name = view.findViewById(R.id.item_name);
        price = view.findViewById(R.id.item_price);
        category = view.findViewById(R.id.item_category);
        moduleCode = view.findViewById(R.id.item_code);
        description = view.findViewById(R.id.item_description);
        map = view.findViewById(R.id.map_button);
        list = view.findViewById(R.id.chat_btn);

        list.setOnClickListener(onListItem);
        map.setOnClickListener(onMap);

        //Select Image
        select = view.findViewById(R.id.select);
        imageView = view.findViewById(R.id.item_image);


        // click here to select image
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });
    }

    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                        Log.d(TAG, "onClick: 1");
                    } else {
                        Log.d(TAG, "onClick: 2");
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, PICK_Camera_IMAGE);
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                        Log.d(TAG, "onClick:3");
                    } else {
                        Log.d(TAG, "onClick: 4");
                        // initialising intent
                        Intent intent = new Intent();

                        // setting type to select to be image
                        intent.setType("image/*");

                        // allowing multiple image to be selected
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                }
            }
        });
        builder.create().show();
    }

    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requesting  gallery permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // Requesting camera and gallery
    // permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        // initialising intent
        Intent intent = new Intent();

        // setting type to select to be image
        intent.setType("image/*");

        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        Log.d(TAG, "onClick: 5");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onClick: onActivity");
            if (resultCode == RESULT_OK) {
               imageurl=data.getData();
                Picasso.get().load(imageurl).into(imageView);
                Log.d(TAG, "onClick:" + imageurl);
            }
        }

    private View.OnClickListener onListItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemList();
        }
    };
    private View.OnClickListener onMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), Maps.class);
            startActivity(intent);
        }
    };

    public void ItemList() {
        String priceStr = price.getText().toString().trim();
        String nameStr = name.getText().toString().trim();
        String categoryStr = category.getText().toString().trim();
        String moduleStr = moduleCode.getText().toString().trim();
        String descriptionStr = description.getText().toString().trim();
        String imageStr = "";

        if (nameStr.isEmpty()) {
            name.setError("Name field is required!");
            name.requestFocus();
            return;
        }
        if (priceStr.isEmpty()) {
            price.setError("Name field is required!");
            price.requestFocus();
            return;
        }
        if (categoryStr.isEmpty()) {
            category.setError("Name field is required!");
            category.requestFocus();
            return;
        }
        if (moduleStr.isEmpty()) {
            moduleCode.setError("Name field is required!");
            moduleCode.requestFocus();
            return;
        }
        if (descriptionStr.isEmpty()) {
            description.setError("Name field is required!");
            description.requestFocus();
            return;
        }

        Userdata userData = new Userdata(nameStr, priceStr, categoryStr, moduleStr, descriptionStr, imageStr);

        groupId = FirebaseDatabase.getInstance().getReference("Userdata")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

        //Log.d(TAG, "GroupID = "+ groupId);

        FirebaseDatabase.getInstance().getReference("Userdata")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(groupId).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    replaceFragment(new ProfileFragment());
                    Toast.makeText(getActivity(), "You have Successfully listed an item", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to list an item", Toast.LENGTH_LONG).show();
                }
            }
        });
        uploadImage();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    private void uploadImage() {
        if (imageurl != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(imageurl)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "Group ID:" + groupId);
                                            //Adding that URL to Realtime database
                                            FirebaseDatabase.getInstance().getReference("Userdata")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(groupId).child("imageURL")
                                                    .setValue(uri.toString());
                                        }
                                    });

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        }
    }
}



