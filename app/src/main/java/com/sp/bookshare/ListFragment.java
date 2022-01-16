package com.sp.bookshare;

import android.app.Activity;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

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
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    TextView total;
    ArrayList<Uri> mArrayUri;
    int position = 0;
    List<String> imagesEncodedList;

    EditText name,category,moduleCode,description;
    RadioButton excellent,good,poor;
    Button select, previous, next,list,map;

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

        //Inputs
        name=view.findViewById(R.id.item_name);
        category=view.findViewById(R.id.item_category);
        moduleCode=view.findViewById(R.id.item_code);
        description=view.findViewById(R.id.item_description);
        map=view.findViewById(R.id.map_button);
        list=view.findViewById(R.id.list_button);
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
            replaceFragment(new ProfileFragment());
            Toast.makeText(getActivity(),"You have successfully listed your item!", Toast.LENGTH_LONG).show();
        }
    };
    private View.OnClickListener onMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), Maps.class);
            startActivity(intent);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
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
}



