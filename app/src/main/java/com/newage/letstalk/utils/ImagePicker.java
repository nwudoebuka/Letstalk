package com.newage.letstalk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImagePicker {
    private static final String TAG = ImagePicker.class.getSimpleName();

    private static final int DEFAULT_REQUEST_CODE = 234;
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;        // min pixels
    private static final String TEMP_IMAGE_NAME = "tempImage";

    private static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;
    private static int minHeightQuality = DEFAULT_MIN_HEIGHT_QUALITY;

    private static final String PICK_IMAGE_TEXT = "Choose an Image";

    private static String mChooserTitle;
    private static int mPickImageRequestCode; // = DEFAULT_REQUEST_CODE;
    private static boolean mGalleryOnly = false;

    private ImagePicker() {
        // not called
    }


    /**
     * Launch a dialog to pick an image from camera/gallery apps with custom request code.
     *
     * @param activity which will launch the dialog.
     */
    public static void pickImage(Activity activity) {
        pickImage(activity, PICK_IMAGE_TEXT, DEFAULT_REQUEST_CODE, false);
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps.
     *
     * @param activity     which will launch the dialog.
     * @param chooserTitle will appear on the picker dialog.
     */
    public static void pickImage(Activity activity, String chooserTitle) {
        pickImage(activity, chooserTitle, DEFAULT_REQUEST_CODE, false);
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps with custom request code.
     *
     * @param activity    which will launch the dialog.
     * @param requestCode request code that will be returned in result.
     */
    public static void pickImage(Activity activity, int requestCode) {
        pickImage(activity, PICK_IMAGE_TEXT, requestCode, false);
    }

    /**
     * Launch a dialog to pick an image from gallery apps only with custom request code.
     *
     * @param activity    which will launch the dialog.
     * @param requestCode request code that will be returned in result.
     */
    public static void pickImageGalleryOnly(Activity activity, int requestCode) {
        pickImage(activity, PICK_IMAGE_TEXT, requestCode, true);
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps with custom request code.
     *
     * @param fragment which will launch the dialog.
     */
    public static void pickImage(Fragment fragment) {
        pickImage(fragment, PICK_IMAGE_TEXT, DEFAULT_REQUEST_CODE, false);
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps.
     *
     * @param fragment     which will launch the dialog and will get the result in
     *                     onActivityResult()
     * @param chooserTitle will appear on the picker dialog.
     */
    public static void pickImage(Fragment fragment, String chooserTitle) {
        pickImage(fragment, chooserTitle, DEFAULT_REQUEST_CODE, false);
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps with custom request code.
     *
     * @param fragment    which will launch the dialog.
     * @param requestCode request code that will be returned in result.
     */
    public static void pickImage(Fragment fragment, int requestCode) {
        pickImage(fragment, PICK_IMAGE_TEXT, requestCode, false);
    }

    /**
     * Launch a dialog to pick an image from gallery apps only with custom request code.
     *
     * @param fragment    which will launch the dialog.
     * @param requestCode request code that will be returned in result.
     */
    public static void pickImageGalleryOnly(Fragment fragment, int requestCode) {
        pickImage(fragment, PICK_IMAGE_TEXT, requestCode, true);
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps.
     *
     * @param activity     which will launch the dialog and will get the result in
     *                     onActivityResult()
     * @param chooserTitle will appear on the picker dialog.
     */
    public static void pickImage(Activity activity, String chooserTitle, int requestCode, boolean galleryOnly) {
        mGalleryOnly = galleryOnly;
        mPickImageRequestCode = requestCode;
        mChooserTitle = chooserTitle;
        startChooser(activity);
    }

    /**
     * Launch a dialog to pick an image from camera/gallery apps.
     *
     * @param fragment     which will launch the dialog and will get the result in
     *                     onActivityResult()
     * @param chooserTitle will appear on the picker dialog.
     * @param requestCode  request code that will be returned in result.
     */
    public static void pickImage(Fragment fragment, String chooserTitle, int requestCode, boolean galleryOnly) {
        mGalleryOnly = galleryOnly;
        mPickImageRequestCode = requestCode;
        mChooserTitle = chooserTitle;
        startChooser(fragment);
    }

    private static void startChooser(Fragment fragmentContext) {
        Intent chooseImageIntent = getPickImageIntent(fragmentContext.getContext(), mChooserTitle);
        fragmentContext.startActivityForResult(chooseImageIntent, mPickImageRequestCode);
    }

    private static void startChooser(Activity activityContext) {
        Intent chooseImageIntent = getPickImageIntent(activityContext, mChooserTitle);
        activityContext.startActivityForResult(chooseImageIntent, mPickImageRequestCode);
    }

    /**
     * Get an Intent which will launch a dialog to pick an image from camera/gallery apps.
     *
     * @param context      context.
     * @param chooserTitle will appear on the picker dialog.
     * @return intent launcher.
     */
    public static Intent getPickImageIntent(Context context, String chooserTitle) {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentList = addIntentsToList(context, intentList, pickIntent);

        // Check is we want gallery apps only
        if (!mGalleryOnly) {
            // Camera action will fail if the app does not have permission, check before adding intent.
            // We only need to add the camera intent if the app does not use the CAMERA permission
            // in the androidmanifest.xml
            // Or if the user has granted access to the camera.
            // See https://developer.android.com/reference/android/provider/MediaStore.html#ACTION_IMAGE_CAPTURE
            if (!appManifestContainsPermission(context, Manifest.permission.CAMERA) || hasCameraAccess(context)) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhotoIntent.putExtra("return-data", true);

//                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
// FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
   //                             ImageUtils.getTemporalFile(context, String.valueOf(mPickImageRequestCode))));
                //Uri.fromFile(ImageUtils.getTemporalFile(context, String.valueOf(mPickImageRequestCode))));


                Uri contentUri = getUriForFile(context, getTemporalFile(context, String.valueOf(mPickImageRequestCode)));
                takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                intentList = addIntentsToList(context, intentList, takePhotoIntent);
            }
        }

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[intentList.size()]));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        Log.i(TAG, "Adding intents of type: " + intent.getAction());
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.i(TAG, "App package: " + packageName);
        }
        return list;
    }

    /**
     * Checks if the current context has permission to access the camera.
     * @param context             context.
     */
    private static boolean hasCameraAccess(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks if the androidmanifest.xml contains the given permission.
     * @param context             context.
     * @return Boolean, indicating if the permission is present.
     */
    private static boolean appManifestContainsPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions;
            }
            if (requestedPermissions == null) {
                return false;
            }

            if (requestedPermissions.length > 0) {
                List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
                return requestedPermissionsList.contains(permission);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Called after launching the picker with the same values of Activity.getImageFromResult
     * in order to resolve the result and get the image.
     *
     * @param context             context.
     * @param requestCode         used to identify the pick image action.
     * @param resultCode          -1 means the result is OK.
     * @param imageReturnedIntent returned intent where is the image data.
     * @return image.
     */
    @Nullable
    public static Bitmap getImageFromResult(Context context, int requestCode, int resultCode, Intent imageReturnedIntent) {
        Log.i(TAG, "getImageFromResult() called with: " + "resultCode = [" + resultCode + "]");
        Bitmap bm = null;
        if (resultCode == Activity.RESULT_OK && requestCode == mPickImageRequestCode) {
            File imageFile = getTemporalFile(context, String.valueOf(mPickImageRequestCode));
            Uri selectedImage;
            boolean isCamera = (imageReturnedIntent == null
                    || imageReturnedIntent.getData() == null
                    || imageReturnedIntent.getData().toString().contains(imageFile.toString()));
            if (isCamera) {     /** CAMERA **/
                //selectedImage = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFile); //TODO
                selectedImage = getUriForFile(context, imageFile); //TODO 2
            } else {            /** ALBUM **/
                //selectedImage = imageReturnedIntent.getData(); //TODO
                selectedImage = getUriForFile(context, new File(getRealPathFromURI(context, imageReturnedIntent.getData()))); //TODO 2
            }
            Log.i(TAG, "selectedImage: " + selectedImage);

            bm = decodeBitmap(context, selectedImage);
            int rotation = getRotation(context, selectedImage, isCamera);
            bm = rotate(bm, rotation);
        }
        return bm;
    }

    /**
     * Called after launching the picker with the same values of Activity.getImageFromResult
     * in order to resolve the result and get the input stream for the image.
     *
     * @param context             context.
     * @param requestCode         used to identify the pick image action.
     * @param resultCode          -1 means the result is OK.
     * @param imageReturnedIntent returned intent where is the image data.
     * @return stream.
     */
    public static InputStream getInputStreamFromResult(Context context, int requestCode, int resultCode, Intent imageReturnedIntent) {
        Log.i(TAG, "getFileFromResult() called with: " + "resultCode = [" + resultCode + "]");
        if (resultCode == Activity.RESULT_OK && requestCode == mPickImageRequestCode) {
            File imageFile = getTemporalFile(context, String.valueOf(mPickImageRequestCode));
            Uri selectedImage;
            boolean isCamera = (imageReturnedIntent == null
                    || imageReturnedIntent.getData() == null
                    || imageReturnedIntent.getData().toString().contains(imageFile.toString()));
            if (isCamera) {     /** CAMERA **/
                //selectedImage = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFile);
                selectedImage = getUriForFile(context, imageFile);
            } else {            /** ALBUM **/
               // selectedImage = imageReturnedIntent.getData();
                selectedImage = getUriForFile(context, new File(getRealPathFromURI(context, imageReturnedIntent.getData())));
            }
            Log.i(TAG, "selectedImage: " + selectedImage);

            try {
                if (isCamera) {
                    // We can just open the temporary file stream and return it
                    return new FileInputStream(imageFile);
                } else {
                    // Otherwise use the ContentResolver
                    return context.getContentResolver().openInputStream(selectedImage);
                }
            } catch (FileNotFoundException ex) {
                Log.e(TAG, "Could not open input stream for: " + selectedImage);
                return null;
            }
        }
        return null;
    }

    /**
     * Called after launching the picker with the same values of Activity.getImageFromResult
     * in order to resolve the result and get the image path.
     *
     * @param context             context.
     * @param requestCode         used to identify the pick image action.
     * @param resultCode          -1 means the result is OK.
     * @param imageReturnedIntent returned intent where is the image data.
     * @return path to the saved image.
     */
    @Nullable
    public static String getImagePathFromResult(Context context, int requestCode, int resultCode, Intent imageReturnedIntent) {
        Log.i(TAG, "getImagePathFromResult() called with: " + "resultCode = [" + resultCode + "]");
        Uri selectedImage = null;
        if (resultCode == Activity.RESULT_OK && requestCode == mPickImageRequestCode) {
            File imageFile = getTemporalFile(context, String.valueOf(mPickImageRequestCode));
            boolean isCamera = (imageReturnedIntent == null
                    || imageReturnedIntent.getData() == null
                    || imageReturnedIntent.getData().toString().contains(imageFile.toString()));
            if (isCamera) {
                return imageFile.getAbsolutePath();
            } else {
                selectedImage = imageReturnedIntent.getData();
            }
            Log.i(TAG, "selectedImage: " + selectedImage);
        }
        if (selectedImage == null) {
            return null;
        }
        return getFilePathFromUri(context, selectedImage);
    }




    /**
     * Loads a bitmap and avoids using too much memory loading big images (e.g.: 2560*1920)
     */
    private static Bitmap decodeBitmap(Context context, Uri theUri) {
        Bitmap outputBitmap = null;
        AssetFileDescriptor fileDescriptor = null;

        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");

            // Get size of bitmap file
            BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
            boundsOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, boundsOptions);

            // Get desired sample size. Note that these must be powers-of-two.
            int[] sampleSizes = new int[]{8, 4, 2, 1};
            int targetWidth;
            int targetHeight;

            int i = 0;
            do {
                targetWidth = boundsOptions.outWidth / sampleSizes[i];
                targetHeight = boundsOptions.outHeight / sampleSizes[i];
                i++;
            }
            while (i < sampleSizes.length && (targetWidth < minWidthQuality || targetHeight < minHeightQuality));

            // Decode bitmap at desired size
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inSampleSize = sampleSizes[i - 1];
            outputBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, decodeOptions);
            if (outputBitmap != null) {
                Log.i(TAG, "Loaded image with sample size " + decodeOptions.inSampleSize + "\t\t"
                        + "Bitmap width: " + outputBitmap.getWidth()
                        + "\theight: " + outputBitmap.getHeight());
            }
            fileDescriptor.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputBitmap;
    }


    private static File getTemporalFile(Context context, String payload) {
        return new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME + payload);
    }

    /*
    GETTERS AND SETTERS
     */
    public static void setMinQuality(int minWidthQuality, int minHeightQuality) {
        ImagePicker.minWidthQuality = minWidthQuality;
        ImagePicker.minHeightQuality = minHeightQuality;
    }

    /*
    Uri helper for Deprecated Url methods
    https://medium.com/@ali.muzaffar/what-is-android-os-fileuriexposedexception-
    and-what-you-can-do-about-it-70b9eb17c6d0
     */
    private static Uri getUriForFile(Context mContext, File file) {
        Uri contentUri = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileProvider", file);
            } else {
                contentUri = Uri.fromFile(file);
            }
        } catch (Exception e) {

        }
        return contentUri;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /**
     * Get stream, save the picture to the temp file and return path.
     *
     * @param context context
     * @param uri uri of the incoming file
     * @return path to the saved image.
     */
    private static String getFilePathFromUri(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return savePicture(context, bmp, String.valueOf(uri.getPath().hashCode()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String savePicture(Context context, Bitmap bitmap, String imageSuffix) {
        File savedImage = getTemporalFile(context, imageSuffix + ".jpeg");
        FileOutputStream fos = null;
        if (savedImage.exists()) {
            savedImage.delete();
        }
        try {
            fos = new FileOutputStream(savedImage.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return savedImage.getAbsolutePath();
    }

    /**
     * Get rotation degrees of the selected image. E.g.: 0º, 90º, 180º, 240º.
     *
     * @param context    context.
     * @param imageUri   URI of image which will be analyzed.
     * @param fromCamera true if the image was taken from camera,
     *                   false if it was selected from the gallery.
     * @return degrees of rotation.
     */
    private static int getRotation(Context context, Uri imageUri, boolean fromCamera) {
        int rotation;
        if (fromCamera) {
            rotation = getRotationFromCamera(context, imageUri);
        } else {
            rotation = getRotationFromGallery(context, imageUri);
        }
        Log.i(TAG, "Image rotation: " + rotation);
        return rotation;
    }


    private static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageFile, null);
            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                default:
                    rotate = 0;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception e) {
            //Do nothing
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        } //End of try-catch block
        return result;
    }

    /**
     * Rotate image X degrees.
     */
    private static Bitmap rotate(Bitmap bitmap, int degrees) {
        if (bitmap != null && degrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

}