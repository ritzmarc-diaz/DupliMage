package com.example.duplimage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final int REQ_PERM = 0;
    public static final int PICK_IMAGE = 1;
    //    public static final int PICK_IMAGE2 = 2;
    public static final int CHOOSE_FOLDER = 2;

    int spanCount = 3;

    //initialize view image module
    ImageView imgGallery;
    //initialize text view module
    //TextView MatchResult;
    //global Results variable
    public static String resultsText;
    //coordinator layout for snackbar
    CoordinatorLayout coordinatorLayout;
    //initialize mat for images
    Uri imageUri1;
    Uri imageUri2;

    //initialize directory path
    DocumentFile pickedDir;

    //initialize file path for images
    String imagefile = "/sdcard/DCIM/heh.jpg";
    String imagefile2 = "/sdcard/DCIM/heh mcdo.jpg";
    ArrayList<String> filePathList = new ArrayList<>();
    ArrayList<Integer> deleteIndex = new ArrayList<>();
    ArrayList<String> filePaths = new ArrayList<>();

    //initialize matched image threshold counter
    int fileSize;

    //initialize results array
    ArrayList<Double> results = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                    REQ_PERM);
        }



        //Initializes textview and imageview
        imgGallery = findViewById(R.id.imageView);
        ProgressBar loadingProgressBar = findViewById(R.id.loadingProgressBar);
        RecyclerView galleryLayout = findViewById(R.id.galleryLayout);
//        imgGallery2 = findViewById(R.id.imageView2);
        //MatchResult = findViewById(R.id.matchResult);
//        recyclerView = findViewById(R.id.imageView);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        //Initialize choose_image buttons
        Button btn_choose_image = findViewById(R.id.btn_choose_image);
        Button btn_choose_folder = findViewById(R.id.btn_choose_folder);
//        Button btn_choose_image2 = findViewById(R.id.btn_choose_image2);
        Button btn_start_matching = findViewById(R.id.btn_match_images);
        Button btn_view_image = findViewById(R.id.btn_view_images);
        ImageButton btn_back = findViewById(R.id.btn_back);
        Button btn_delete_image = findViewById(R.id.btn_delete_image);

        coordinatorLayout = findViewById(R.id.main_coordinator);

        btn_choose_folder.setEnabled(false);
        btn_start_matching.setEnabled(false);
        btn_view_image.setEnabled(false);
        btn_delete_image.setEnabled(false);

        //Button Click Listener
        btn_choose_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loadingProgressBar.setProgress(0);
                filePathList.clear();
                deleteIndex.clear();
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/jpeg");
                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(pickIntent, PICK_IMAGE);
                btn_choose_folder.setEnabled(true);
            }
        });

//        btn_choose_image2.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/jpeg");
//                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//                startActivityForResult(pickIntent, PICK_IMAGE2);
//            }
//        });

        btn_choose_folder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loadingProgressBar.setProgress(0);
                filePathList.clear();
                deleteIndex.clear();
                btn_delete_image.setEnabled(false);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, CHOOSE_FOLDER);
                btn_start_matching.setEnabled(true);
                loadingProgressBar.setVisibility(View.VISIBLE);
            }
        });

        //Match Images Button
        btn_start_matching.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //startActivity(new Intent (MainActivity.this, Results.class));
                deleteIndex.clear();
                results.clear();
                fileSize = filePathList.size();
                loadingProgressBar.setMax(fileSize);
                for(int i=0; i < fileSize; i++){
                    imagefile2 = filePathList.get(i);
                    results.add(SiftSurfAlgorithm());
                    if (imagefile2.equals(imagefile)){
                    } else {
                        if (results.get(i) >= 96.09) {
                            deleteIndex.add(i);
                            btn_delete_image.setEnabled(true);
                            btn_view_image.setEnabled(true);
                        }
                    }
                }
                loadingProgressBar.setVisibility(View.GONE);
//                System.out.println(filePathList + " & " + deleteIndex);

                //update Result string value
                resultsText = String.valueOf(deleteIndex.size());
                startActivity(new Intent(MainActivity.this, Results.class));

                //makes sure match button isn't redundant
                btn_start_matching.setEnabled(false);
            }
        });

        btn_view_image.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                galleryLayout.setVisibility(View.VISIBLE);
                btn_back.setVisibility(View.VISIBLE);

                btn_choose_image.setVisibility(View.GONE);
                btn_choose_folder.setVisibility(View.GONE);
                btn_view_image.setVisibility(View.GONE);
                btn_delete_image.setVisibility(View.GONE);

                btn_delete_image.setEnabled(true);

                // Retrieve the ArrayList of file paths
                filePaths = retrieveFilePaths();

                galleryLayout.setLayoutManager(new GridLayoutManager(MainActivity.this, spanCount));

                //Create and set the Image Adapter
                final ImageAdapter imageAdapter = new ImageAdapter(filePaths, this::onClick);
                galleryLayout.setAdapter(imageAdapter);
            }
        });

        //Back Button for View Gallery
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLayout.setVisibility(View.GONE);
                btn_back.setVisibility(View.GONE);

                btn_choose_image.setVisibility(View.VISIBLE);
                btn_choose_folder.setVisibility(View.VISIBLE);
                btn_delete_image.setVisibility(View.VISIBLE);
                btn_view_image.setVisibility(View.VISIBLE);
            }
        });

        //Delete Image Button
        btn_delete_image.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v){
                deleteImage();
                Snackbar deleted = Snackbar.make(coordinatorLayout, deleteIndex.size() + " Image/s deleted successfully.", Snackbar.LENGTH_LONG);
                deleted.show();
                btn_delete_image.setEnabled(false);
                btn_start_matching.setEnabled(true);
                filePathList.clear();
                deleteIndex.clear();

                galleryLayout.setVisibility(View.GONE);
                btn_delete_image.setVisibility(View.GONE);

                btn_choose_image.setVisibility(View.VISIBLE);
                btn_choose_folder.setVisibility(View.VISIBLE);
                btn_view_image.setVisibility(View.VISIBLE);
            }
        });
    }

    public double SiftSurfAlgorithm(){
        Mat image1 = Imgcodecs.imread(imagefile, Imgcodecs.IMREAD_GRAYSCALE);
        Mat image2 = Imgcodecs.imread(imagefile2, Imgcodecs.IMREAD_GRAYSCALE);
//        System.out.println(imagefile + " && " + imagefile2);
        //Initialize ORB Algorithm
        ORB detector = ORB.create(200);
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

//        //Print out keypoints results
//        List<KeyPoint> keypoints1List = keypoints1.toList();
//        List<KeyPoint> keypoints2List = keypoints2.toList();
//        for(int i=0; i < keypoints1List.size(); i++){
//            System.out.println("Image1 Keypoints: " + i + "_" + keypoints1List.get(i));
//        }
//        System.out.println("------------------------------------------------------");
//        for(int i=0; i < keypoints1List.size(); i++){
//            System.out.println("Image2 Keypoints: " + i + "_" + keypoints2List.get(i));
//        }
//        System.out.println("------------------------------------------------------");
//        //Print out descriptors results
//        for(int i=0; i < descriptor1.rows(); i++){
//            for(int j=0; j < descriptor1.cols(); j++){
//                System.out.print("Image1 Descriptors: " + i + Arrays.toString(descriptor1.get(i,j)));
//            }
//        }
//        System.out.println("------------------------------------------------------");
//        for(int i=0; i < descriptor2.rows(); i++){
//            for(int j=0; j < descriptor2.cols(); j++){
//                System.out.print("Image2 Descriptors: " + i + Arrays.toString(descriptor2.get(i,j)));
//            }
//        }
//        System.out.println("------------------------------------------------------");
//        //Print out match results
//        List<DMatch> listofMatches = matches.toList();
//        for(int i=0; i<listofMatches.size();i++){
//            System.out.println("List of Matches: " + i + listofMatches.get(i));
//        }

        //Compute Results
        double compute_keypoint1 = keypoints1.rows();
        double compute_keypoint2 = keypoints2.rows();
        double total_keypoints = (compute_keypoint1 + compute_keypoint2) / 2;
        double compute_matches = matches.rows();
        double results = (compute_matches / total_keypoints) * 100;
        //Print Results
//        System.out.println(results);

        return results;
    }

    //Delete Image
    public void deleteImage() {

        for(int i=0; i < filePathList.size(); i++){
            imagefile2 = filePathList.get(i);
            String file_dj_path = imagefile2;
            File fdelete = new File(file_dj_path);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Log.e("-->", "file deleted :" + file_dj_path);
                    callBroadCast();
                } else {
                    Log.e("-->", "file not deleted :" + file_dj_path);
                }
            } else {
                Log.e("-->", "File does not exist.");
            }
        }
    }

    public ArrayList<String> retrieveFilePaths(){
        ArrayList<String> filePaths = new ArrayList<>();

        for(int i=0; i < deleteIndex.size(); i++){
            filePaths.add(filePathList.get(deleteIndex.get(i)));
        }

        return filePaths;
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
//            if (requestCode==PICK_IMAGE2){
//                // for gallery
//                imgGallery2.setImageURI(data.getData());
//                imageUri2 = data.getData();
//
//                //Find image file path
//                String[] projection = { MediaStore.Images.Media.DATA };
//                Cursor cursor = getContentResolver().query(imageUri2, projection, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                imagefile2 = cursor.getString(column_index);
//                cursor.close();
//            }
            if (requestCode==CHOOSE_FOLDER){
                //Reads the data to determine the folder chosen
                Uri treeUri = data.getData();
                pickedDir = DocumentFile.fromTreeUri(this, treeUri);

                //Initialize list of file paths
                List<String> filePaths = new ArrayList<>();

                //Makes sure the files are of .jpeg and stores them inside the filePaths arraylist
                for (DocumentFile file : pickedDir.listFiles()) {
                    if (file.getName().endsWith(".jpg")){
                        String uriString = file.getUri().toString();
                        imageUri2 = Uri.parse(uriString);

                        String decodedUriString = null;
                        try {
                            decodedUriString = URLDecoder.decode(uriString, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        String filePath = Uri.parse(decodedUriString).getPath();
                        filePath = filePath.replace("%3A", ":").replace("%2F", "/");
                        int lastIndex = filePath.lastIndexOf(":");
                        String substring = filePath.substring(lastIndex + 1);
                        filePath = "/storage/emulated/0/" + substring;
                        filePaths.add(filePath);
                    }
                    if (file.getName().endsWith(".jpeg")){
                        String uriString = file.getUri().toString();
                        imageUri2 = Uri.parse(uriString);

                        String decodedUriString = null;
                        try {
                            decodedUriString = URLDecoder.decode(uriString, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        String filePath = Uri.parse(decodedUriString).getPath();
                        filePath = filePath.replace("%3A", ":").replace("%2F", "/");
                        int lastIndex = filePath.lastIndexOf(":");
                        String substring = filePath.substring(lastIndex + 1);
                        filePath = "/storage/emulated/0/" + substring;
                        filePaths.add(filePath);
                    }
                }

                //Store all the file path in an array
                filePathList = new ArrayList<>(Arrays.asList(filePaths.toArray(new String[0])));
//                System.out.println(filePathList);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERM) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
            } else {
                // Permissions denied
            }
        }
    }

    public void selectedFiles(List<String> selectedImages) {
        filePathList = (ArrayList<String>) selectedImages;
    }
}