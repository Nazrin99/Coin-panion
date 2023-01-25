package com.example.coin_panion.classes.utility;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import kotlinx.coroutines.internal.AtomicOp;

public class Picture implements Serializable{
    private Drawable picture;
    private String firebaseReference;
    private PictureType pictureType;
    public static final String storageUrl = "gs://coinpanion-86b14.appspot.com";

    public Picture(Drawable picture, String firebaseReference, PictureType pictureType) {
        this.picture = picture;
        this.firebaseReference = firebaseReference;
        this.pictureType = pictureType;
    }



    public Picture() {
    }

    public Picture(String firebaseReference) {
        this.firebaseReference = firebaseReference;
        this.pictureType = getPictureType(firebaseReference);
    }

    /**
     * Inserts a new image into the database, returns a Picture object. Insertion into database is asynchronous
     * @param id
     * @param uri
     * @param pictureType
     * @param contentResolver
     * @return
     */
    public static Picture insertIntoDatabase(String id, Uri uri, PictureType pictureType, ContentResolver contentResolver){

        InputStream inputStream = null;
        try{
            inputStream = contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(inputStream == null){
            return null;
        }
        AtomicReference<InputStream> inputStreamAtomicReference = new AtomicReference<>(inputStream);
        Picture picture = new Picture(Drawable.createFromStream(inputStream, uri.toString()), constructImageReference(id, pictureType), pictureType);
        Executors.newSingleThreadExecutor().execute(() -> {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(storageUrl);
            StorageReference imageReference = firebaseStorage.getReference().child(constructImageReference(id, pictureType));
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/png")
                    .build();

            System.out.println(inputStreamAtomicReference.get());
            UploadTask uploadTask = imageReference.putStream(inputStreamAtomicReference.get(), metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Picture added");
                }
            });
        });
        return picture;
    }


    /**
     * Gets picture from database based on the references given. Is asynchronous, hence used while block to wait for data transfer to complete, might not be a good idea
     * @param reference
     * @return
     */
    public void getPictureFromDatabase(String reference){
        Executors.newSingleThreadExecutor().execute(() -> {
            AtomicReference<Drawable> pictureAtomicReference = new AtomicReference<>(null);
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(storageUrl);
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference imageReference = storageReference.child(reference);

            imageReference.getStream().addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                    InputStream inputStream = taskSnapshot.getStream();
                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    pictureAtomicReference.set(drawable);
                }
            });
            while(pictureAtomicReference.get() == null){

            }
            this.setPicture(pictureAtomicReference.get());
        });
    }

    /**
     * Construct Drawable from Uri
     * @param imageUri
     * @param contentResolver
     * @return drawable object
     */
    public static Drawable constructDrawableFromUri(Uri imageUri, ContentResolver contentResolver){
        return Drawable.createFromStream(constructInputStreamFromUri(imageUri, contentResolver), imageUri.toString());
    }

    /**
     * Construct Drawable from Blob
     * @param toDrawable
     * @return Drawable object created from Blob
     */
    public static Drawable constructDrawableFromBlob(Blob toDrawable){
        try{
            byte[] data = toDrawable.getBytes(1, (int) toDrawable.length());
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            Drawable drawable = Drawable.createFromStream(byteArrayInputStream, null);
            return drawable;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert Image Uri to InputStream, can be passed for parameter Blob for SQL
     * @param imageUri
     * @param contentResolver
     * @return InputStream that can be passed to PreparedStatement.setBlob
     */
    public static InputStream constructInputStreamFromUri(Uri imageUri, ContentResolver contentResolver){
        InputStream inputStream = null;
        try{
            inputStream = contentResolver.openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public static PictureType getPictureType(String imageReference) {
        if(imageReference.contains("COVER")){
            return PictureType.COVER;
        }
        else if(imageReference.contains("PROFILE")){
            return PictureType.PROFILE;
        }
        else if(imageReference.contains("QR")){
            return PictureType.QR;
        }
        else if(imageReference.contains("PAYMENT")){
            return PictureType.PAYMENT;
        }
        return PictureType.GENERAL;

    }

    public static String constructImageReference(String id, PictureType pictureType){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id);
        stringBuilder.append("_");
        stringBuilder.append(pictureType.getType());
        stringBuilder.append(".png");
        return stringBuilder.toString();
    }

    public void getPictureFromDatabase(){
        Executors.newSingleThreadExecutor().execute(() -> {
            String reference = this.getFirebaseReference();
            if(reference == null){
                return;
            }

            AtomicReference<Drawable> drawableAtomicReference = new AtomicReference<>(null);
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference().child(reference);

            storageReference.getStream().addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                    InputStream inputStream = taskSnapshot.getStream();
                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    drawableAtomicReference.set(drawable);
                    System.out.println(drawable);
                }
            });
            while(drawableAtomicReference.get() == null){

            }
            this.setPicture(drawableAtomicReference.get());
        });
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
    }

    public static Drawable getRoundedDrawable(Drawable drawable, Resources resources){
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap roundBitmap = getRoundedCroppedBitmap(bitmapDrawable.getBitmap());

        return new BitmapDrawable(resources, roundBitmap);
    }

    public static Drawable cropToSquareAndRound(Drawable drawable, Resources resources){
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        Bitmap circleCropped = getRoundedCroppedBitmap(cropImg);
        Drawable croppedDrawable = new BitmapDrawable(resources, circleCropped);

        return croppedDrawable;
    }

    public static InputStream compressInputStream(InputStream inputStream){
        int quality = 110;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = null;
        do {
            if(quality <= 10){
                quality -= 1;
            }
            else{
                quality -= 10;
            }
            outputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            data = outputStream.toByteArray();
            System.out.println(data.length);
            System.out.println(quality);
        } while(data.length > 1024000);

        return new ByteArrayInputStream(data);
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public String getFirebaseReference() {
        return firebaseReference;
    }

    public void setFirebaseReference(String firebaseReference) {
        this.firebaseReference = firebaseReference;
    }

    public PictureType getPictureType() {
        return pictureType;
    }

    public void setPictureType(PictureType pictureType) {
        this.pictureType = pictureType;
    }
}
