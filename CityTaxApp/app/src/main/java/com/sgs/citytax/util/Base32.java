package com.sgs.citytax.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Base32 {
    public static String byteArrayToBase32(byte[] data) {
        String result = "";
        if (data.length % 5 != 0) {
            return result;
        }

        byte[] bits = new byte[data.length * 8];
        // convert to bits
        for (int i = 0; i < data.length; i++) {
            bits[i * 8] = (byte) ((data[i] & 0x80) >> 7);
            bits[i * 8 + 1] = (byte) ((data[i] & 0x40) >> 6);
            bits[i * 8 + 2] = (byte) ((data[i] & 0x20) >> 5);
            bits[i * 8 + 3] = (byte) ((data[i] & 0x10) >> 4);
            bits[i * 8 + 4] = (byte) ((data[i] & 0x08) >> 3);
            bits[i * 8 + 5] = (byte) ((data[i] & 0x04) >> 2);
            bits[i * 8 + 6] = (byte) ((data[i] & 0x02) >> 1);
            bits[i * 8 + 7] = (byte) ((data[i] & 0x01) >> 0);
        }
        // extract 5 bit values and convert to string
        for (int i = 0; i < data.length / 5 * 8; i++) {
          /*  if (i > 0 && i % 4 == 0) {
                result += '-';
            }*/
            byte value = (byte) (bits[i * 5 + 0] << 4
                    | bits[i * 5 + 1] << 3 | bits[i * 5 + 2] << 2
                    | bits[i * 5 + 3] << 1 | bits[i * 5 + 4] << 0);

            if (value >= 0 && value < 26) {
                result = result + (char) (value + 'A');
            }

            if (value >= 26 && value < 30) {
                result = result + (char) (value - 26 + '2');
            }

            if (value == 30) {
                result = result + '7';
            }

            if (value == 31) {
                result = result + '9';
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String GenerateHashedCode(String accountSecretKey, long iterationNumber, int digits)
            throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
        byte[] key = accountSecretKey.getBytes(StandardCharsets.UTF_8);
        return calculateCode(key, iterationNumber);
    }


    public static String calculateCode(byte[] key, long tm)
    {
        // Allocating an array of bytes to represent the specified instant
        // of time.
        String hmacHashFunction="HmacSHA1"; // 256,384,512
        byte[] data = new byte[8];
        long value = tm;
        int keyModulus=1000000;

        // Converting the instant of time from the long representation to a
        // big-endian array of bytes (RFC4226, 5.2. Description).
        for (int i = 8; i-- > 0; value >>>= 8)
        {
            data[i] = (byte) value;
        }

        // Building the secret key specification for the HmacSHA1 algorithm.
        SecretKeySpec signKey = new SecretKeySpec(key, hmacHashFunction);

        try
        {
            // Getting an HmacSHA1/HmacSHA256 algorithm implementation from the JCE.
            Mac mac = Mac.getInstance(hmacHashFunction);

            // Initializing the MAC algorithm.
            mac.init(signKey);

            // Processing the instant of time and getting the encrypted data.
            byte[] hash = mac.doFinal(data);

            // Building the validation code performing dynamic truncation
            // (RFC4226, 5.3. Generating an HOTP value)
            int offset = hash[hash.length - 1] & 0xF;

            // We are using a long because Java hasn't got an unsigned integer type
            // and we need 32 unsigned bits).
            long truncatedHash = 0;

            for (int i = 0; i < 4; ++i)
            {
                truncatedHash <<= 8;

                // Java bytes are signed but we need an unsigned integer:
                // cleaning off all but the LSB.
                truncatedHash |= (hash[offset + i] & 0xFF);
            }

            // Clean bits higher than the 32nd (inclusive) and calculate the
            // module with the maximum validation code value.
            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= keyModulus;

            //When 0 is at the start
            String code = (int) truncatedHash + "";
            if(code.length()==5){
                code=0+code;
            }

            // Returning the validation code to the caller.
            return code;
        }
        catch (NoSuchAlgorithmException | InvalidKeyException ex)
        {
            LogHelper.writeLog(ex,null);
//            // We're not disclosing internal error details to our clients.
//            throw new Exception("The operation cannot be performed now.");
            return "";
        }
    }
}