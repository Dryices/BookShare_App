package com.sp.bookshare;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";
    private static final int GalleryPick = 1;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int PICK_Camera_IMAGE = 2;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView imageView;
    File Destination;
    String groupId = "";
    String seller = "";
    String cameraPermission[];
    String storagePermission[];
    EditText name, price, category, moduleCode, description;
    Button select, list, map;
    Uri imageurl;
    Bitmap bitmap;
    //Firebase Storage declarations
    FirebaseStorage storage;
    StorageReference storageReference;
    private View.OnClickListener onListItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemList();
        }
    };

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
        list = view.findViewById(R.id.list_btn);

        list.setOnClickListener(onListItem);

        //Select Image
        select = view.findViewById(R.id.select);
        imageView = view.findViewById(R.id.item_image);


        // click here to select image
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showImagePicDialog();

                ImagePicker.with(ListFragment.this)
                        .crop(16f, 16f)                //Crop image(Optional), Check Customization for more option
                        .compress(508)            //Final image size will be less than 0.5 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                seller = ds.child("name").getValue(String.class);
                Log.d(TAG, "USERID= " + seller);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "An error has occurred!", Toast.LENGTH_LONG).show();
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageurl = data.getData();
            Log.d("LOG1", " data.getdata ");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageurl);
                //bitmap = Bitmap.createScaledBitmap(bitmap,  600,392,true);
                Log.d("LOG1", " uri to bitmap ");

            } catch (IOException e) {
                Log.d("LOG1", " ioexception e");
                e.printStackTrace();
            }
            if (imageurl != null) {

                Log.d("LOG1", " there is a uri ");

                imageView.setImageURI(imageurl);
                imageView.setVisibility(View.VISIBLE);

            } else {
                Log.d("LOG1", " there is no uri ");
            }
        } else {
            Log.d("LOG1", " result code not ok ");
        }
    }

    public void ItemList() {
        String priceStr = price.getText().toString().trim();
        String nameStr = name.getText().toString().trim();
        String categoryStr = category.getText().toString().trim();
        String moduleStr = moduleCode.getText().toString().trim();
        String descriptionStr = description.getText().toString().trim();
        String imageStr = "";
        String userID = "";
        String sellerStr;


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

        userID = FirebaseDatabase.getInstance().getReference("Userdata").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getKey();


        Log.d(TAG, "USERID= " + seller);
        sellerStr = seller;

        Userdata userData = new Userdata(nameStr, priceStr, categoryStr, moduleStr, descriptionStr, imageStr, userID, sellerStr);

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
        Log.d(TAG, "uploadImage: sd");
        if (imageurl != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            ref.putFile(imageurl)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "Group ID:" + imageurl);
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



