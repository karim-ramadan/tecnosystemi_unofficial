package it.tecnosystemi.TS.Utils;

import android.util.Base64;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {
    private final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
    private final SecretKeySpec key;
    private AlgorithmParameterSpec spec;

    public AESCrypt(String str) throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        instance.update(str.getBytes("UTF-8"));
        byte[] bArr = new byte[32];
        System.arraycopy(instance.digest(), 0, bArr, 0, 32);
        this.key = new SecretKeySpec(bArr, "AES");
        this.spec = getIV();
    }

    public AlgorithmParameterSpec getIV() {
        return new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    }

    public String encrypt(String str) throws Exception {
        this.cipher.init(1, this.key, this.spec);
        return new String(Base64.encode(this.cipher.doFinal(str.getBytes("UTF-8")), 0), "UTF-8");
    }

    public String decrypt(String str) throws Exception {
        this.cipher.init(2, this.key, this.spec);
        return new String(this.cipher.doFinal(Base64.decode(str, 0)), "UTF-8");
    }
}
