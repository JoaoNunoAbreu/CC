package Encryption;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;

public class AESencrp {

    static String IV = "AAAAAAAAAAAAAAAA";
    static String chaveencriptacao = "0123456789abcdef";

    public static byte[] encrypt(byte[] data) throws Exception {
        Cipher encripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(chaveencriptacao.getBytes(StandardCharsets.UTF_8), "AES");
        encripta.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
        return encripta.doFinal(data);
    }

    public static byte[] decrypt(byte[] textoencriptado) throws Exception{
        Cipher decripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(chaveencriptacao.getBytes(StandardCharsets.UTF_8), "AES");
        decripta.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
        return decripta.doFinal(textoencriptado);
    }

}