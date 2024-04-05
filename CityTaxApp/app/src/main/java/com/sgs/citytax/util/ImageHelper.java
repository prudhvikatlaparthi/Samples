package com.sgs.citytax.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageHelper {

/*
    public static String getBase64String(String filePath) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream inputStream=null;
        try {
            File imageFile = new File(filePath);
            if (imageFile.exists()) {
                 inputStream = new FileInputStream(imageFile);
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            bytes = output.toByteArray();
        } catch (Exception e) {

        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {

            }
        }
        if (bytes != null)
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        else
            return null;
    }
*/

    public static String getBase64String(Bitmap bitmap) {
//        if (bitmap == null)
//            return null;
//
//        byte[] bytes = null;
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        try {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
//            bytes = output.toByteArray();
//        } catch (IllegalArgumentException | NullPointerException e) {
//            LogHelper.writeLog(e,null);
//        }
//        if (bytes != null)
//            return Base64.encodeToString(bytes, Base64.DEFAULT);
//        else
            return getBase64String(bitmap,100);
    }

    public static String getBase64String(Bitmap bitmap, int quality) {
        if (bitmap == null)
            return null;

        byte[] bytes = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output);
            bytes = output.toByteArray();
        } catch (IllegalArgumentException |  NullPointerException e) {
            LogHelper.writeLog(e,null);
        }
        if (bytes != null)
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        else
            return null;
    }
    /*public static Bitmap getBitmapFromFilePath(String filePath) {
        File image = new File(filePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        return Bitmap.createScaledBitmap(bitmap, parent.getWidth(), parent.getHeight(), true);
    }*/

/*
    public static String getBase64FromPath(String path) {
        String base64 = "";
        File file=null;
        FileInputStream fileInputStream=null;
        try {

             file= new File(path);
            fileInputStream =new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length() + 100];
            int length =  fileInputStream.read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            
        }finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {

            }
        }
        return base64;
    }
*/


    public static String getFileExtension(Context context, Uri uri) {
        String extension;
        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        return extension;
    }

    public static String getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }

    public static Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            LogHelper.writeLog(e,null);
        } catch (IOException e) {
            LogHelper.writeLog(e,null);
        }finally {
            try {
                fis.close();
            } catch (IOException e) {
                LogHelper.writeLog(e,null);
            }
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            LogHelper.writeLog(e,null);
        } catch (IOException e) {
            LogHelper.writeLog(e,null);
        }finally {
            try {
                fis.close();
            } catch (IOException e) {
                LogHelper.writeLog(e,null);
            }
        }

        //Log.d("ImageHelper", "Width :" + b.getWidth() + " Height :" + b.getHeight());

        /*destFile = new File(file, "img_" + dateFormatter.format(new Date()).toString() + ".png");
        try {
            FileOutputStream out = new FileOutputStream(destFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            
        }*/
        return b;
    }

}
