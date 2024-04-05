package com.sgs.citytax.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.sgs.citytax.R;
import com.sgs.citytax.ui.LoginActivity;
import com.sgs.citytax.ui.fragments.BaseFragment;
import com.sgs.citytax.util.JedisUtil;
import com.sgs.citytax.util.LogOutTimerUtil;
import com.sgs.citytax.util.PrefHelper;

public class BaseActivity extends AppCompatActivity implements LogOutTimerUtil.LogOutListener {

    ProgressDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyApplication.updateLanguage(newBase));
    }
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimerUtil.startLogoutTimer(this, this);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimerUtil.startLogoutTimer(this, this);
    }

    /**
     * Performing idle time logout
     */

    @Override
    public void doLogout() {
        if (!(this instanceof LoginActivity)) {
            JedisUtil.cancelJedis();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    //region Alert Dialog
    public void showAlertDialog(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(null);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.cancel());
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    public void showAlertDialog(String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(null);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(getString(R.string.ok), okListener);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), cancelListener);
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    public void showAlertDialog(String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(null);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(getString(R.string.ok), okListener);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), cancelListener);
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void showAlertDialog(String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, int neutralBtnName, DialogInterface.OnClickListener neutralListener, View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(null);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(getString(R.string.ok), okListener);
        dialogBuilder.setNeutralButton(neutralBtnName, neutralListener);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), cancelListener);
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void showAlertDialog(String msg, int camera, DialogInterface.OnClickListener cameraListener, int gallery, DialogInterface.OnClickListener galleryListener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(null);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(camera, cameraListener);
        dialogBuilder.setNegativeButton(gallery, galleryListener);
        dialogBuilder.setNeutralButton(getString(R.string.cancel), cancelListener);
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    public void showAlertDialog(String msg, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder dialogBuilder =  new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        dialogBuilder.setTitle(null);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(getString(R.string.ok), okListener);
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    /**
     * @param msg message from server
     * @param alter alternet msg ref Id from strings
     */
    //Failure alert Dialog with listener
    public void showAlertDialogFailure(String msg, int alter, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        dialogBuilder.setTitle(null);
        if (!TextUtils.isEmpty(msg)) {
            dialogBuilder.setMessage(msg);
        } else {
            dialogBuilder.setMessage(getString(alter));
        }
        dialogBuilder.setPositiveButton(getString(R.string.ok), okListener);
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    public void showAlertDialog(int message, int positiveButton, View.OnClickListener positiveListener, int negativeButton, View.OnClickListener negativeListener) {
        showAlertDialog(message, positiveButton, positiveListener, 0, null, negativeButton, negativeListener, null);
    }

    public void showAlertDialog(int message, int positiveButton, View.OnClickListener positiveListener, int neutralButton, View.OnClickListener neutralListener, int negativeButton, View.OnClickListener negativeListener) {
        showAlertDialog(message, positiveButton, positiveListener, neutralButton, neutralListener, negativeButton, negativeListener, null);
    }

    public void showAlertDialog(int message, int positiveButton, View.OnClickListener positiveListener, int neutralButton, View.OnClickListener neutralListener, int negativeButton, View.OnClickListener negativeListener, View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(null);
        dialogBuilder.setMessage(getString(message));
        dialogBuilder.setPositiveButton(positiveButton, null);
        if (negativeButton > 0)
            dialogBuilder.setNegativeButton(negativeButton, null);
        if (neutralButton > 0)
            dialogBuilder.setNeutralButton(neutralButton, null);
        if (view != null)
            dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setOnShowListener(dialog -> {
            Button positive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setTag(dialog);
            positive.setOnClickListener(positiveListener);
            if (negativeListener != null) {
                Button negative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setTag(dialog);
                negative.setOnClickListener(negativeListener);
            }
            if (neutralListener != null) {
                Button neutral = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                neutral.setTag(dialog);
                neutral.setOnClickListener(neutralListener);
            }
        });
        alertDialog.show();
    }
    //endregion

    //region Progress Dialog

    public void showProgressDialog() {
        showProgressDialog(R.string.msg_please_wait);
    }

    public void showProgressDialog(int message) {
        showProgressDialog(getString(message));
    }

    public void showProgressDialog(String message) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.setMessage(message);
            dialog.show();
        } else if (dialog == null) {
            dialog = ProgressDialog.show(this, null, message, true, false);
            dialog.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
    //endregion

    //region Toast
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region Toolbar
    public void setToolbar(Toolbar toolBar) {
        setSupportActionBar(toolBar);
    }

    public void setToolbar(Toolbar toolBar, String title) {
        setSupportActionBar(toolBar);
        setTitle(title);
    }

    public void setToolbar(Toolbar toolBar, int title) {
        setSupportActionBar(toolBar);
        setTitle(title);
    }

    public void showToolbarBackButton() {
        showToolbarBackButton(0);
    }

    public void showToolbarBackButton(int title) {
        if (title == 0) {
            title = R.string.app_name;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

    }

    public void showToolbarBackButton(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

    }

    public void hideToolbar() {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
    }
    //endregion

    //region Snackbar
    public void showSnackbarMsg(String msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbarMsg(int msg) {
        showSnackbarMsg(getString(msg));
    }


    //endregion

    //region Keyboard
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
        }
    }
    //endregion

    // region Fragment transactions
    public void replaceFragment(Fragment fragment, Boolean addToBackStack, int frameLayoutId) {
        hideKeyBoard();
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        setCustomAnimation(fragmentTransaction, false);
        if (addToBackStack)
            fragmentTransaction.replace(frameLayoutId, fragment, tag).addToBackStack(tag).commit();
        else
            fragmentTransaction.replace(frameLayoutId, fragment, tag).commit();
    }

    public void addFragment(Fragment fragment, Boolean addToBackStack, int frameLayoutId) {
        hideKeyBoard();
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        setCustomAnimation(fragmentTransaction, false);
        if (addToBackStack)
            fragmentTransaction.add(frameLayoutId, fragment, tag).addToBackStack(tag).commit();
        else
            fragmentTransaction.add(frameLayoutId, fragment, tag).commit();
    }

    public void replaceFragmentWithOutAnimation(Fragment fragment, Boolean needToAddToBackStack, int frameLayoutId) {
        hideKeyBoard();
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (needToAddToBackStack)
            fragmentTransaction.replace(frameLayoutId, fragment, tag).addToBackStack(tag).commit();
        else
            fragmentTransaction.replace(frameLayoutId, fragment, tag).commit();
    }

    public void addFragmentWithOutAnimation(Fragment fragment, Boolean needToAddToBackStack, int frameLayoutId) {
        hideKeyBoard();
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (needToAddToBackStack)
            fragmentTransaction.add(frameLayoutId, fragment, tag).addToBackStack(tag).commit();
        else
            fragmentTransaction.add(frameLayoutId, fragment, tag).commit();
    }

    private void setCustomAnimation(FragmentTransaction ft, Boolean reverseAnimation) {
        if (!reverseAnimation) {
            ft.setCustomAnimations(
                    R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right
            );
        } else {
            ft.setCustomAnimations(
                    R.anim.enter_from_left, R.anim.exit_to_right,
                    R.anim.enter_from_right, R.anim.exit_to_left
            );
        }
    }


    public void popBackStack() {
        hideKeyBoard();
        BaseFragment.sDisableFragmentAnimations = true;
        FragmentManager fragment = getSupportFragmentManager();
        fragment.popBackStackImmediate();
        BaseFragment.sDisableFragmentAnimations = false;
    }

    //endregion


    public PrefHelper getPrefHelper() {
        return MyApplication.getPrefHelper();
    }
}
