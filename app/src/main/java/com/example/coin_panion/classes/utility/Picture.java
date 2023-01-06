package com.example.coin_panion.classes.utility;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

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

    public static Blob getBlobFromDB(int pictureID){
        AtomicReference<Blob> blobAtomicReference = new AtomicReference<>(null);
        Thread pictureThread = new Thread(() -> {
            try{
                Connection connection = Line.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT picture_data FROM picture WHERE picture_id = ?");
                preparedStatement.setInt(1, pictureID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while(resultSet.next()){
                    blobAtomicReference.set(resultSet.getBlob(1));
                    return;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ThreadStatic.run(pictureThread);

        return blobAtomicReference.get();
    }

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
}
