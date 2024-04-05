package com.pru.alternativeprocessing;

import static com.pru.alternativeprocessing.Utils.getBitmap;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxTask {

    public void execute(ImageView img, ProgressDialog progressDialog) {
        progressDialog.show();
        Observable.fromCallable(Utils::getBitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        img.setImageBitmap(bitmap);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("TAG", "onError: "+e);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
