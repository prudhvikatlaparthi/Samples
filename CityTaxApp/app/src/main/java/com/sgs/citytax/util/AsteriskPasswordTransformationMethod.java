package com.sgs.citytax.util;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {

    private char DOT = '\u2022';

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private final CharSequence mSource;
        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }
        public char charAt(int index) {
            return DOT; // This is the important part
        }
        public int length() {
            return mSource.length(); // Return default
        }
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
};
