package com.pru.alternativeprocessing;

import static com.pru.alternativeprocessing.Utils.getBitmap;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class GetAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView img;
    private ProgressDialog progressDialog;

    public GetAsyncTask(ImageView img, ProgressDialog progressDialog) {
        this.img = img;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return getBitmap();
        } catch (Exception e) {
            Log.e("pru", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        img.setImageBitmap(result);
        progressDialog.dismiss();
    }
}
