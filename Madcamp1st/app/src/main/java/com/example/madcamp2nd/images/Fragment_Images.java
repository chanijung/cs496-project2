package com.example.madcamp2nd.images;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madcamp2nd.R;
import com.example.madcamp2nd.RetrofitAPI;
import com.example.madcamp2nd.RetrofitClient;
import com.example.madcamp2nd.Users;
import com.example.madcamp2nd.contacts.Contact;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.karumi.dexter.listener.single.BasePermissionListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.DIRECTORY_PICTURES;

public class Fragment_Images extends Fragment implements View.OnClickListener {
    private View mView;

    private Animator currentAnimator;
    private int shortAnimationDuration;

    List<String> temp_gallery;

    // Fab 버튼 사용할 때 필요한 변수들
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    FloatingActionButton fab_images,fab_synchronization, fab_camera;

    private String imageFilePath;
    private Uri photoUri;

    private static final int REQUEST_IMAGE_CAPTURE = 672;

    private MediaScanner mMediaScanner; // 사진 저장 시 갤러리 폴더에 바로 반영사항을 업데이트 시켜주려면 이 것이 필요하다(미디어 스캐닝)
    Call<Users> call;
    Users user;

    // Retrofit
    RetrofitAPI apiInterface;
    List<String> phone_images;
    Image[] db_images;

    Bitmap temp_bitmap;

    ByteArrayOutputStream outStream;
    ByteArrayInputStream instream;
    byte[] image;
    String profileImageBase64;

    public Fragment_Images(Users user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.fragment_images, container, false);

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fab_images = mView.findViewById(R.id.floatingActionButton_images);
        fab_synchronization = mView.findViewById(R.id.floatingActionButton_synchronization_images);
        fab_camera = mView.findViewById(R.id.floatingActionButton_camera);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // db에 접근해서 갤러리 가져옴
        apiInterface = RetrofitClient.getApiService();
        call = apiInterface.get_gallery(user);
        Log.e("Fragment_Images", "call");
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Log.e("Fragment_Images", "get response");
                Log.e("Fragment_gallery", "dddd db get ok");
                temp_gallery = response.body().getGallery();
                showImage(temp_gallery);
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.e("Fragment_gallery", "dddd db get fail");
            }
        });

        fab_images.setOnClickListener(this);
        fab_synchronization.setOnClickListener(this);
        fab_camera.setOnClickListener(this);
    }

//    private void onPermissionGranted_images() {
//        showImage();
//        cameraPermission();
//    }

//    private void cameraPermission() {
//        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
//            onPermissionGranted_camera(false);
//        else {
//            mView.findViewById(R.id.floatingActionButton_camera).setOnClickListener(v -> {
//                Log.e("tag", "camera permission");
//                Dexter.withContext(getContext())
//                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
//                        .withListener(new BaseMultiplePermissionsListener() {
//                            @Override
//                            public void onPermissionsChecked(MultiplePermissionsReport report) {
//                                if (report.areAllPermissionsGranted())
//                                    onPermissionGranted_camera(true);
//                            }
//                        }).check();
//            });
//        }
//    }

    private void onPermissionGranted_camera(boolean doAction) {
        // 사진 저장 후 미디어 스캐닝을 돌려줘야 갤러리에 반영됨.
        mMediaScanner = MediaScanner.getInstance(getActivity().getApplicationContext());
        FloatingActionButton fab = mView.findViewById(R.id.floatingActionButton_camera);
        fab.setOnClickListener(new FABClickListener());

        mView.findViewById(R.id.floatingActionButton_camera).setOnClickListener(v -> {
            Log.e("Fragment_Images", "PermissionGranted");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                }

                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        if(doAction)
            mView.findViewById(R.id.floatingActionButton_camera).performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton_images: // Fab 버튼 닫기, 열기
                Log.e("Fragment_Images", "touch");
                toggleFab();
                // 동기화 작업, db에서 받고, 띄워줘야함.
                break;
            case R.id.floatingActionButton_synchronization_images: // 동기화 버튼 누르면!
                Log.e("Fragment_Images", "images");
//                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
//

//                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Fragment_Images", "get Images");// 동기화 작업, db에서 받고, 띄워줘야함.
                apiInterface = RetrofitClient.getApiService();
                try {
                    phone_images = loadImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                user.setGallery(phone_images);
                call = apiInterface. giveandget_gallery(user);
                call.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        Log.e("Fragment_Images", "dddd sync ok");
                        temp_gallery = response.body().getGallery();
                        showImage(temp_gallery);
                        // db_images
                    }

                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                        Log.e("Fragment_Images", "dddd sync fail");
                    }
                });

                toggleFab();

                break;
            case R.id.floatingActionButton_camera:
                Log.e("Fragment_Images", "Start Camera");
                toggleFab();
                Dexter.withContext(getContext())
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .withListener(new BaseMultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted())
                                    onPermissionGranted_camera(true);
                            }
                        }).check();
                break;
        }
    }

    private void toggleFab() {
        Log.e("Fragment_Images", "toggleFab");
        if (isFabOpen) {
            fab_images.setImageResource(R.drawable.icon_plus);
            fab_synchronization.startAnimation(fab_close);
            fab_synchronization.setClickable(false);
            fab_camera.startAnimation(fab_close);
            fab_camera.setClickable(false);
            isFabOpen = false;
        } else {
            fab_images.setImageResource(R.drawable.icon_x);
            fab_synchronization.startAnimation(fab_open);
            fab_synchronization.setClickable(true);
            fab_camera.startAnimation(fab_open);
            fab_camera.setClickable(true);
            isFabOpen = true;
        }
    }

    class FABClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "Start Camera", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> loadImage() throws IOException {
        ProgressDialog pd;
        ArrayList<Image> images = new ArrayList<Image>();

        pd = ProgressDialog.show(getContext(), "Loading Images", "Please Wait");

        Cursor c = getContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME
                },
                null, null, null);

        int idColumn = c.getColumnIndex(MediaStore.Images.Media._ID);
        int nameColumn = c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

        while(c.moveToNext()) {
            long id = c.getLong(idColumn);
            String name = c.getString(nameColumn);

            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            images.add(new Image(contentUri, name));
        }
        c.close();
        pd.cancel();

        byte[] target;
        Image[] temp_image = images.toArray(new Image[0]);
        List<String> load_gallery = new ArrayList<>();
        for (int x = 0; x < temp_image.length; x++) {
            Log.e("loadimage tag", temp_image[x].uri.toString());
            InputStream is = getContext().getContentResolver().openInputStream(temp_image[x].uri);
            temp_bitmap = BitmapFactory.decodeStream(is);
            outStream = new ByteArrayOutputStream();
//            rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.PNG, 70, fOut);
            temp_bitmap.compress(Bitmap.CompressFormat.PNG, 70, outStream);
            image = outStream.toByteArray();

            profileImageBase64 = Base64.encodeToString(image,0);
            load_gallery.add(profileImageBase64);
        }

        return load_gallery;
    }

    private void showImage(List<String> images) {

        RecyclerView recyclerView = mView.findViewById(R.id.recyclerView_images);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        ImageAdapter mAdapter = new ImageAdapter(this, images);
        recyclerView.setAdapter(mAdapter);
    }




    void zoomImageFromThumb(final View thumbView, Bitmap bm) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = mView.findViewById(
                R.id.imageView_expanded);

        expandedImageView.setImageBitmap(bm);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        mView.findViewById(R.id.frameLayout_images)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Fragment_Images", "OnAcitivty Result");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            //Bitmap bitmap = ((BitmapDrawable) BitmapFactory.decodeFile(imageFilePath).getDrawable()).getBitmap();
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);
            } else {
                exifDegree = 0;
            }

            String result = "";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
            Date curDate = new Date(System.currentTimeMillis());
            String filename = formatter.format(curDate);

            String strFolderName = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "adzv" + File.separator;
            File file = new File(strFolderName);
            if (!file.exists())
                file.mkdirs();

            File f = new File(strFolderName + "/" + filename + ".png");
            result = f.getPath();

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result = "Save Error fOut";
            }

            // 비트맵 사진 폴더 경로에 저장
            Log.e("Fragment_Images", "Finish ready");
            outStream = new ByteArrayOutputStream();
//            rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.PNG, 70, fOut);
            rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.PNG, 70, outStream);
            image = outStream.toByteArray();
            profileImageBase64 = Base64.encodeToString(image, 0);

            // go db
            Log.e("Fragment_Images", "go db");
            Log.e("Fragment_Images", user.getUid());
            temp_gallery.add(profileImageBase64);
            user.setGallery(temp_gallery);
            apiInterface = RetrofitClient.getApiService();
            call = apiInterface.giveandget_gallery(user);
            Log.e("Fragment_Images", "call");
            call.enqueue(new Callback<Users>() {
                @Override
                public void onResponse(Call<Users> call, Response<Users> response) {
                    Log.e("Fragment_Images", "get response");
                    temp_gallery = response.body().getGallery();
                    showImage(temp_gallery);
//                    String data = temp_gallery.get(0);
//                    byte[] bytePlainOrg = Base64.decode(data, 0);
//                    Log.e("Fragment_Images", "with bytearrayinputstream");
//                    ByteArrayInputStream instream =  new ByteArrayInputStream(bytePlainOrg);
//                    Bitmap bm = BitmapFactory.decodeStream(instream);
//                    ImageView ivimage = (ImageView) getActivity().findViewById(R.id.imageView2);
//                    ivimage.setImageBitmap(bm);
//                    ivimage.setVisibility(View.VISIBLE);

                }
                @Override
                public void onFailure(Call<Users> call, Throwable t) {
                    Log.e("Fragment_gallery", "dddd camera fail");
                }
            });

            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                fOut.close();
                // 방금 저장된 사진을 갤러리 폴더 반영 및 최신화
                mMediaScanner.mediaScanning(strFolderName + "/" + filename + ".png");
            } catch (IOException e) {
                e.printStackTrace();
                result = "File close Error";
            }
        }
    }

    private int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}