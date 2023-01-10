package com.example.coin_panion.classes.utility;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAccumulator;

public class Picture {
    private int pictureID;
    private Drawable picture;

    public int getPictureID() {
        return pictureID;
    }

    public void setPictureID(int pictureID) {
        this.pictureID = pictureID;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public Picture(int pictureID, Drawable picture) {
        this.pictureID = pictureID;
        this.picture = picture;
    }

    /**
     * Gets existing image from database
     * @DATABASE
     * @param pictureID
     * @return drawable object associated with the id
     */
    public static Picture getPictureFromDB(int pictureID){
        AtomicReference<Picture> drawableAtomicReference = new AtomicReference<>();
        Thread pictureThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM picture WHERE picture_id = ?");
                preparedStatement.setInt(1, pictureID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    drawableAtomicReference.set(new Picture(resultSet.getInt(1), constructDrawableFromBlob(resultSet.getBlob(2))));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(pictureThread);

        return drawableAtomicReference.get();
    }

    /**
     * Insert new picture into database, returns Picture object
     * @DATABASE
     * @param imageUri
     * @param contentResolver
     * @param dataThread
     * @return picture
     */
    public static Picture insertPicIntoDB(Uri imageUri, ContentResolver contentResolver, Thread dataThread){
        AtomicReference<Picture> atomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO picture(picture_data) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setBlob(1, constructInputStreamFromUri(imageUri, contentResolver));
                preparedStatement.execute();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                atomicReference.set(new Picture(resultSet.getInt(1), constructDrawableFromUri(imageUri, contentResolver)));
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(dataThread);
        return atomicReference.get();
    }

    public static Picture insertPicIntoDB(Drawable drawable, Thread dataThread){
        AtomicReference<Picture> atomicReference = new AtomicReference<>();
        dataThread = new Thread(() -> {
            try(
                    Connection connection = Line.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO picture(picture_data) VALUES(?)", Statement.RETURN_GENERATED_KEYS)
                    ){
                preparedStatement.setBlob(1, constructInputStreamFromDrawable(drawable));
                preparedStatement.execute();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();

                atomicReference.set(new Picture(resultSet.getInt(1), drawable));
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
        ThreadStatic.run(dataThread);
        return atomicReference.get();
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
            Drawable drawable = new BitmapDrawable(byteArrayInputStream);
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

    public static InputStream constructInputStreamFromDrawable(Drawable drawable){
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        System.out.println("........length......" + imageInByte);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        return bis;
    }


}
