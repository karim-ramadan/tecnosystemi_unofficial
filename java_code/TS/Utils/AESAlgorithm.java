package it.tecnosystemi.TS.Utils;

import com.scottyab.aescrypt.AESCrypt;
import java.security.GeneralSecurityException;
import javax.crypto.spec.SecretKeySpec;

public class AESAlgorithm {
    public static final String Encrypt(String str, String str2) {
        try {
            return AESCrypt.encrypt(str, str2);
        } catch (GeneralSecurityException unused) {
            return null;
        }
    }

    public static final byte[] Encrypt(byte[] bArr, SecretKeySpec secretKeySpec, byte[] bArr2) {
        try {
            return AESCrypt.encrypt(secretKeySpec, bArr2, bArr);
        } catch (GeneralSecurityException unused) {
            return null;
        }
    }

    public static final String Decrypt(String str, String str2) {
        try {
            return AESCrypt.decrypt(str, str2);
        } catch (GeneralSecurityException unused) {
            return null;
        }
    }

    public static final byte[] Decrypt(SecretKeySpec secretKeySpec, byte[] bArr, byte[] bArr2) {
        try {
            return AESCrypt.decrypt(secretKeySpec, bArr, bArr2);
        } catch (GeneralSecurityException unused) {
            return null;
        }
    }
}
