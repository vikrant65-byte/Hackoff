package com.example.stree;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    FirebaseUser user;


    private ImageView avatarIv, coverIv;
    private TextView emailTv, nameTv, phoneTv;
    private FloatingActionButton fab,Gmap;
    private ProgressDialog loadingBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileIimageRef;

    ProgressDialog progressDialog;

    String currentUserID;
    final  static int Gallery_pick =1;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("currentUserID");
        UserProfileIimageRef = FirebaseStorage.getInstance().getReference().child("profile Images");

        avatarIv =view.findViewById(R.id.avatarIv);
        coverIv =view.findViewById(R.id.coverIv);
        nameTv =view.findViewById(R.id.nameTv);
        emailTv =view.findViewById(R.id.emailTv);
        phoneTv =view.findViewById(R.id.phoneTv);
        fab =view.findViewById(R.id.fab);
        Gmap =view.findViewById(R.id.Gmap);



        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/**");
                startActivityForResult(galleryIntent,Gallery_pick);
            }
        });

        Gmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),UserMapsActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {

        if (requestCode==Gallery_pick && resultCode == RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(getActivity());
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
             CropImage.ActivityResult result = CropImage.getActivityResult(data);

             if (resultCode== RESULT_OK)
             {

                 loadingBar.setTitle("Profile Image");
                 loadingBar.setMessage("PLease wait,while we are updating your new profile image...");
                 loadingBar.show();
                 loadingBar.setCanceledOnTouchOutside(true);

                 Uri resultUri = result.getUri();

                 StorageReference filepath = UserProfileIimageRef.child(currentUserID+ ".jpg");

                 filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                         if (task.isSuccessful())
                         {
                             Toast.makeText(getActivity(),"Profile Image stores in FireBase storage succesfully",Toast.LENGTH_SHORT).show();

                             //To det link of the image in FireBase DataBase

                             final  String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                             UsersRef.child("ProfileImage").setValue(downloadUrl)
                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             {

                                                 if (task.isSuccessful())
                                                 {

                                                     Toast.makeText(getActivity(),"Profile link saved in FireBase database",Toast.LENGTH_SHORT).show();
                                                     loadingBar.dismiss();
                                                 }
                                                 else
                                                 {
                                                     String message = task.getException().getMessage();
                                                     Toast.makeText(getActivity(),"Error occurred",Toast.LENGTH_SHORT).show();
                                                     loadingBar.dismiss();

                                                 }
                                             }

                                         }
                                     });
                         }

                     }
                 });

             }
             else
             {
                 Toast.makeText(getActivity(),"Error occurred: Image couldn't be cropped",Toast.LENGTH_SHORT).show();
                 loadingBar.dismiss();
             }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            //mProfilleTv.setText(user.getEmail());
            // mProfileTv.setText(user.getEmail());
        }
        else {

            startActivity(new Intent(getActivity(),  MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }

}




