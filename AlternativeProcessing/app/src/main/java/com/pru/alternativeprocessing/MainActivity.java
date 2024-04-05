package com.pru.alternativeprocessing;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pru.alternativeprocessing.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading");
        progressDialog.setMessage("Please be patient");
        /*GetAsyncTask asyncTask = new GetAsyncTask(binding.imgView, progressDialog);
        asyncTask.execute();*/
        RxTask rxTask = new RxTask();
        rxTask.execute(binding.imgView, progressDialog);
    }
}