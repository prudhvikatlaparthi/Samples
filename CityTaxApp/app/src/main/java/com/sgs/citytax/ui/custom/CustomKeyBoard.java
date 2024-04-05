package com.sgs.citytax.ui.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Timer;
import java.util.TimerTask;


public class CustomKeyBoard {

    private final KeyboardView keyboardView;
    private final Activity hostActivity;
    private final AlertDialog parentView;
    private boolean allowDecimal = false;

    public CustomKeyBoard(Activity host, KeyboardView keyboardView, int layoutID, AlertDialog parentView) {
        hostActivity = host;
        this.keyboardView = keyboardView;
        this.keyboardView.setKeyboard(new Keyboard(hostActivity, layoutID));
        this.keyboardView.setPreviewEnabled(false);
        this.parentView = parentView;
        KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
            final static int CodeDelete = -5;
            final static int CodeCancel = -3;
            final static int CodePrev = 55000;
            final static int CodeAllLeft = 55001;
            final static int CodeLeft = 55002;
            final static int CodeRight = 55003;
            final static int CodeAllRight = 55004;
            final static int CodeNext = 55005;
            final static int CodeClear = 55006;
            final static int CodeDecimal = -23;
            private Timer timerLongPress = null;


            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                View focusCurrent;
                if (CustomKeyBoard.this.parentView != null)
                    focusCurrent = CustomKeyBoard.this.parentView.getCurrentFocus();
                else
                    focusCurrent = hostActivity.getWindow().getCurrentFocus();
                if (focusCurrent == null)
                    return;
                if (focusCurrent.getClass().equals(EditText.class) ||
                        focusCurrent.getClass().equals(AppCompatEditText.class)
                        || focusCurrent.getClass().equals(TextInputEditText.class)) {
                    EditText edittext = (EditText) focusCurrent;
                    Editable editable = edittext.getText();
                    int start = edittext.getSelectionStart();
                    if (primaryCode == CodeCancel) {
                        hideCustomKeyboard();
                    } else if (primaryCode == CodeDelete) {
                        if (editable != null && start > 0) editable.delete(start - 1, start);
                    } else if (primaryCode == CodeClear) {
                        if (editable != null) editable.clear();
                    } else if (primaryCode == CodeLeft) {
                        if (start > 0) edittext.setSelection(start - 1);
                    } else if (primaryCode == CodeRight) {
                        if (start < edittext.length()) edittext.setSelection(start + 1);
                    } else if (primaryCode == CodeAllLeft) {
                        edittext.setSelection(0);
                    } else if (primaryCode == CodeAllRight) {
                        edittext.setSelection(edittext.length());
                    } else if (primaryCode == CodePrev) {
                        View focusNew = edittext.focusSearch(View.FOCUS_LEFT);
                        if (focusNew != null) focusNew.requestFocus();
                    } else if (primaryCode == CodeNext) {
                        View focusNew = edittext.focusSearch(View.FOCUS_RIGHT);
                        if (focusNew != null) focusNew.requestFocus();
                    } else if (primaryCode == CodeDecimal) {
                        if (allowDecimal) {
                            editable.insert(start, ".");
                        }
                    } else {
                        editable.insert(start, Character.toString((char) primaryCode));
                    }
                }
            }

            @Override
            public void onPress(final int arg0) {
                if (arg0 != CodeDelete)
                    return;
                timerLongPress = new Timer();
                timerLongPress.schedule(new TimerTask() {
                    @Override
                    public void run() {
//                        try {
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
//                                    try {
                                        CustomKeyBoard.this.onKeyLongPress();
//                                    } catch (Error e) {
//                                        Log.e(CustomKeyBoard.class.getSimpleName(), "uiHandler.run: " + e.getMessage(), e);
//                                    }
                                }
                            };
                            uiHandler.post(runnable);
//                        } catch (Error e) {
//                            Log.e(CustomKeyBoard.class.getSimpleName(), "Timer.run: " + e.getMessage(), e);
//                        }
                    }
                }, ViewConfiguration.getLongPressTimeout());
            }

            @Override
            public void onRelease(int primaryCode) {
                if (primaryCode == CodeDelete && timerLongPress != null)
                    timerLongPress.cancel();
            }

            @Override
            public void onText(CharSequence text) {
            }

            @Override
            public void swipeDown() {
            }

            @Override
            public void swipeLeft() {
            }

            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeUp() {
            }
        };
        this.keyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        hostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void onKeyLongPress() {
        View focusCurrent;
        if (parentView != null)
            focusCurrent = parentView.getCurrentFocus();
        else
            focusCurrent = hostActivity.getWindow().getCurrentFocus();
        if (focusCurrent == null)
            return;
        if (focusCurrent.getClass().equals(EditText.class) ||
                focusCurrent.getClass().equals(AppCompatEditText.class)
                || focusCurrent.getClass().equals(TextInputEditText.class)) {
            EditText edittext = (EditText) focusCurrent;
            edittext.setText("");
        }
    }

    public void showCustomKeyboard(View v) {
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) hostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    public void registerEditText(final EditText editText) {
        if (editText != null) {
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) showCustomKeyboard(v);
                else hideCustomKeyboard();
            });
            editText.setOnClickListener(v -> {
                showCustomKeyboard(v);
                editText.setSelection(editText.length());
            });
            editText.setOnTouchListener((v, event) -> {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();
                edittext.setInputType(InputType.TYPE_NULL);
                edittext.onTouchEvent(event);
                edittext.setInputType(inType);
                return true;
            });
            editText.setInputType(editText.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
    }

    public void registerEditText(final EditText editText, final boolean allowDecimal) {
        this.allowDecimal = allowDecimal;
        registerEditText(editText);
    }
}