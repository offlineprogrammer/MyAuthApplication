package com.offlineprogrammer.myauthapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageAccessLevel;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.options.StorageGetUrlOptions;
import com.amplifyframework.storage.options.StorageListOptions;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    AWSCognitoAuthSession cognitoAuthSession;
    ImageUrlsAdapter dataAdapter;
    private ImageView imageView;
    private Button camera_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        imageView = findViewById(R.id.imageView);
        recyclerView = findViewById(R.id.recyclerView);
        camera_button = findViewById(R.id.camera_button);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(GalleryActivity.this).returnMode(ReturnMode.ALL)
                        .folderMode(true).includeVideo(false).limit(1).theme(R.style.AppTheme_NoActionBar).single().start();
            }
        });

        Amplify.Auth.fetchAuthSession(
                result -> {
                    cognitoAuthSession = (AWSCognitoAuthSession) result;
                    switch (cognitoAuthSession.getIdentityId().getType()) {
                        case SUCCESS:
                            Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityId().getValue());
                            ArrayList imageUrlList = new ArrayList<>();

                            dataAdapter = new ImageUrlsAdapter(getApplicationContext(), imageUrlList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(dataAdapter);
                                }
                            });
                            prepareData();


                            break;
                        case FAILURE:
                            Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityId().getError().toString());
                    }
                },
                error -> Log.e("AuthQuickStart", error.toString())
        );


    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (ImagePicker.shouldHandle(i, i2, intent)) {
            Image firstImageOrNull = ImagePicker.getFirstImageOrNull(intent);
            if (firstImageOrNull != null) {
                uploadImage(firstImageOrNull.getPath());

            }
        }


    }

    private void uploadImage(String path) {
        if (path != null) {

            StorageUploadFileOptions options =
                    StorageUploadFileOptions.builder()
                            .accessLevel(StorageAccessLevel.PROTECTED)
                            .targetIdentityId(cognitoAuthSession.getIdentityId().getValue())
                            .build();


            File exampleFile = new File(path);


            Amplify.Storage.uploadFile(
                    UUID.randomUUID().toString(),
                    exampleFile,
                    result -> {
                        Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                        Amplify.Storage.getUrl(result.getKey(),
                                resultUrl -> {

                                    Log.i("MyAmplifyApp", "Url: " + resultUrl.getUrl());

                                    ImageUrl imageUrl = new ImageUrl();
                                    imageUrl.setImageUrl(resultUrl.getUrl().toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dataAdapter.add(imageUrl, 0);
                                        }
                                    });


                                },
                                errorURl -> Log.e(TAG, "prepareData: ", errorURl));
                    },
                    storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
            );


            // Code for showing progressDialog while uploading
          /*  progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();*/


        } else {


        }

    }


    private void prepareData() {

        StorageListOptions options = StorageListOptions.builder()
                .accessLevel(StorageAccessLevel.PROTECTED)
                .targetIdentityId(cognitoAuthSession.getIdentityId().getValue())
                .build();


        StorageGetUrlOptions sopp = StorageGetUrlOptions.builder()
                .build();


        ArrayList imageUrlList = new ArrayList<>();

        Amplify.Storage.list(
                "",

                result -> {
                    for (StorageItem item : result.getItems()) {
                        Log.i("MyAmplifyApp", "Item: " + item.getKey());

                        Amplify.Storage.getUrl(item.getKey(),
                                resultUrl -> {

                                    Log.i("MyAmplifyApp", "Url: " + resultUrl.getUrl());

                                    ImageUrl imageUrl = new ImageUrl();
                                    imageUrl.setImageUrl(resultUrl.getUrl().toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dataAdapter.add(imageUrl, 0);
                                        }
                                    });


                                },
                                errorURl -> Log.e(TAG, "prepareData: ", errorURl));

                    }

                },
                error -> Log.e("MyAmplifyApp", "List failure", error)
        );


//        return imageUrlList;
    }
}