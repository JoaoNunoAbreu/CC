package Encryption;

import javax.crypto.Cipher;
import java.security.*;
import javax.crypto.spec.SecretKeySpec;

public class AESencrp {

    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't','S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };

    public static byte[] encrypt(byte[] Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(Data);
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);

        return c.doFinal(encryptedData);
    }

    private static Key generateKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA");
        String passphrase = "blahbl blahbla blah";
        digest.update(passphrase.getBytes());
        return new SecretKeySpec(digest.digest(), 0, 16, "AES");
    }
}