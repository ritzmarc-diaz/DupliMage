package com.example.duplimage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Loads OpenCV Integration
    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i("OpenCV", "OpenCV loaded successfully");
                //Initialize Mat for OpenCV Integration
                //Mat opencv = new Mat();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    //String file to limit choices to jpg and jpeg files only
    String[] mimeTypes = {"image/jpeg", "image/jpg"};

    //initialize request code for gallery picking
    public static final int PICK_IMAGE = 1;
    public static final int PICK_IMAGE2 = 2;
    public static final int CHOOSE_FOLDER = 3;

    //initialize recyclerview module
    RecyclerView recyclerView;
    //initialize view image module
    ImageView imgGallery;
    ImageView imgGallery2;
    //initialize text view module
    TextView MatchResult;
    //initialize mat for images
    Uri imageUri1;
    Uri imageUri2;
    //initialize file path for images
    String imagefile = "/sdcard/DCIM/heh.jpg";
    String imagefile2 = "/sdcard/DCIM/heh mcdo.jpg";

    //initialize directory path
    DocumentFile pickedDir;

    //initialize integer to count the files in a directory
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 0);
            }
        }
        //Initializes textview and imageview
        imgGallery = findViewById(R.id.imageView);
        recyclerView = findViewById(R.id.recyclerview);
//        imgGallery2 = findViewById(R.id.imageView2);
//        MatchResult = findViewById(R.id.matchResult);

        //Initialize the list of items for recyclerview
        List<Item> items = new ArrayList<Item>();

        //setting up the recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),items));

        //Initialize choose_image buttons
        Button btn_choose_image = findViewById(R.id.btn_choose_image);
        Button btn_choose_folder = findViewById(R.id.btn_choose_folder);
        Button btn_start_matching = findViewById(R.id.btn_match_images);
        Button btn_delete_image = findViewById(R.id.btn_delete_image);

        //Button Click Listener
        btn_choose_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/jpeg");
                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(pickIntent, PICK_IMAGE);
            }
        });

        btn_choose_folder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, CHOOSE_FOLDER);
            }
        });

        //Match Images Button
        btn_start_matching.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SiftSurfAlgorithm();
            }
        });

        //Delete Image Button
        btn_delete_image.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v){
                deleteImage();
            }
        });
    }

    public void SiftSurfAlgorithm(){
        Mat image1 = Imgcodecs.imread(imagefile, Imgcodecs.IMREAD_GRAYSCALE);
        Mat image2 = Imgcodecs.imread(imagefile2, Imgcodecs.IMREAD_GRAYSCALE);
        //Initialize ORB Algorithm
        ORB detector = ORB.create();
        //Initialize Image Keypoints
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        //Initialize Descriptor
        Mat descriptor1 = new Mat();
        Mat descriptor2 = new Mat();
        //keypoint and descriptor detect and compute
        detector.detectAndCompute(image1, new Mat(), keypoints1, descriptor1);
        detector.detectAndCompute(image2, new Mat(), keypoints2, descriptor2);
        //Initialize Brute Force Matcher
        DescriptorMatcher matcher = BFMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING, true);
        //Matching the images
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptor1, descriptor2, matches);
        List<DMatch> listofMatches = matches.toList();
        //Print the List of Matches
        for(int i=0; i<listofMatches.size();i++){
            System.out.println("List of Matches: " + i + listofMatches.get(i));
        }
        //Compute Results
        double compute_keypoint1 = keypoints1.rows();
        double compute_keypoint2 = keypoints2.rows();
        double total_keypoints = (compute_keypoint1 + compute_keypoint2) / 2;
        double compute_matches = matches.rows();
        double results = (compute_matches / total_keypoints) * 100;
        //Print Results
        System.out.println(results);
        System.out.println(descriptor1);

        //update Match Result TextView
        MatchResult.setText("Matching Rate: " + results + "%");
    }

    //Delete Image
    public void deleteImage() {
        String file_dj_path = Environment.getExternalStorageDirectory() + "/DCIM/Judy.jpg";
        File fdelete = new File(file_dj_path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.e("-->", "file deleted :" + file_dj_path);
                callBroadCast();
            } else {
                Log.e("-->", "file not deleted :" + file_dj_path);
            }
        } else{
            Log.e("-->", "File does not exist.");
        }
    }

    //Refreshes the Gallery (showing results of file deletion)
    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            if (requestCode==PICK_IMAGE){
                // for gallery
                imgGallery.setImageURI(data.getData());
                imageUri1 = data.getData();

                //Find image file path
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(imageUri1, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                imagefile = cursor.getString(column_index);
                cursor.close();
            }
            if (requestCode==PICK_IMAGE2){
                // for gallery
                imgGallery2.setImageURI(data.getData());
                imageUri2 = data.getData();

                //Find image file path
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(imageUri2, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                imagefile2 = cursor.getString(column_index);
                cursor.close();
            }
            if (requestCode==CHOOSE_FOLDER){
                Uri treeUri = data.getData();
                pickedDir = DocumentFile.fromTreeUri(this, treeUri);

                for (DocumentFile file : pickedDir.listFiles()) {
                    String type = this.getContentResolver().getType(file.getUri());
                    if (type != null && type.startsWith("image/jpeg")) {
                        count++;
                    }
                }
                System.out.println(count);
            }

        }
    }

    //Checks if OpenCV Ran Successfully
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}