
package com.sp.bookshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageSwitcher imageView;
    private static final String TAG = "ListFragment";
    int PICK_IMAGE_MULTIPLE = 1;
    TextView total;
    ArrayList<Uri> mArrayUri;
    int position = 0;
    int cout=0;
    String groupId="";

    EditText name,category,moduleCode,description;
    RadioButton excellent,good,poor;
    Button select, previous, next,list,map;
    Uri imageurl;

    //Firebase Storage declarations
    FirebaseStorage storage;
    StorageReference storageReference;


    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Inputs
        name=view.findViewById(R.id.item_name);
        category=view.findViewById(R.id.item_category);
        moduleCode=view.findViewById(R.id.item_code);
        description=view.findViewById(R.id.item_description);
        map=view.findViewById(R.id.map_button);
        list=view.findViewById(R.id.chat_btn);
        excellent=view.findViewById(R.id.radio_excellent);
        good=view.findViewById(R.id.radio_good);
        poor=view.findViewById(R.id.radio_poor);

        list.setOnClickListener(onListItem);
        map.setOnClickListener(onMap);

        //Select Image
        select = view.findViewById(R.id.select);
        total = view.findViewById(R.id.text);
        imageView = view.findViewById(R.id.image);
        previous = view.findViewById(R.id.previous);
        mArrayUri = new ArrayList<Uri>();

        // showing all images in imageswitcher
        imageView.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView1 = new ImageView(getActivity().getApplicationContext());
                return imageView1;
            }
        });
        next = view.findViewById(R.id.next);

        // click here to select next image
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < mArrayUri.size() - 1) {
                    // increase the position by 1
                    position++;
                    imageView.setImageURI(mArrayUri.get(position));
                } else {
                    Toast.makeText(getActivity(), "Last Image Already Shown", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // click here to view previous image
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    // decrease the position by 1
                    position--;
                    imageView.setImageURI(mArrayUri.get(position));
                }
            }
        });

        imageView = view.findViewById(R.id.image);

        // click here to select image
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // initialising intent
                Intent intent = new Intent();

                // setting type to select to be image
                intent.setType("image/*");

                // allowing multiple image to be selected
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
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
    public void ItemList(){
        String nameStr = name.getText().toString().trim();
        String categoryStr = category.getText().toString().trim();
        String moduleStr = moduleCode.getText().toString().trim();
        String descriptionStr = description.getText().toString().trim();
        String imageStr="";

        if (nameStr.isEmpty()) {
            name.setError("Name field is required!");
            name.requestFocus();
            return;
        } if (categoryStr.isEmpty()) {
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

        Userdata userData = new Userdata(nameStr, categoryStr, moduleStr,descriptionStr,imageStr);

        groupId =  FirebaseDatabase.getInstance().getReference("Userdata")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

        //Log.d(TAG, "GroupID = "+ groupId);

        FirebaseDatabase.getInstance().getReference("Userdata")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(groupId).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    replaceFragment(new ProfileFragment());
                    Toast.makeText(getActivity(), "You have Successfully listed an item", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),"Failed to list an item", Toast.LENGTH_LONG).show();
                }
            }
        });
        uploadImage();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != data) {
            imageurl = data.getData();
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    imageurl = data.getData();
                    //ClipData().getItemAt(i).getUri();
                    mArrayUri.add(imageurl);

                }
                // setting 1st selected image into image switcher
                imageView.setImageURI(mArrayUri.get(0));
                position = 0;
            } else {
                Uri imageurl = data.getData();
                mArrayUri.add(imageurl);
                imageView.setImageURI(mArrayUri.get(0));
                position = 0;
            }
        } else {
            // show this if no image is selected
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    private void uploadImage()
    {
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
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG,"Group ID:"+groupId);
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
                        public void onFailure(@NonNull Exception e)
                        {
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
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
    }
}



