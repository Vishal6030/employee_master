package com.employee.employee_master.encryption;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
@Component
public class AESEncryption {

    public SecretKey generateSecretKey() throws Exception {
        // Use KeyGenerator to generate a secret key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // You can choose different key sizes (128, 192, or 256 bits)
        return keyGenerator.generateKey();
    }

    public byte[] encrypt(String originalText, Key key) throws Exception {
        // Create Cipher instance and initialize it for encryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Perform encryption
        return cipher.doFinal(originalText.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(byte[] encryptedText, Key key) throws Exception {
        // Create Cipher instance and initialize it for decryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Perform decryption
        byte[] decryptedBytes = cipher.doFinal(encryptedText);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
