package io.kvineet.sysconfigurator.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;


public class EncryptionUtil {

  public static SecretKeySpec constructKey(String myKey)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    byte[] key = null;

    MessageDigest sha = null;
    key = myKey.getBytes("UTF-8");
    sha = MessageDigest.getInstance("SHA-1");
    key = sha.digest(key);
    key = Arrays.copyOf(key, 16);
    byte[] decodedKey = Base64.decodeBase64(myKey);
    return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
  }

  public static String encrypt(String strToEncrypt, String secret) {
    try {
      SecretKeySpec secretKey = constructKey(secret);
      final byte[] plaintext = strToEncrypt.getBytes(UTF_8);

      final ByteArrayOutputStream baos = new ByteArrayOutputStream();

      final Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
      final IvParameterSpec ivForCBC =
          createIV(aesCBC.getBlockSize(), Optional.of(new SecureRandom()));

      aesCBC.init(Cipher.ENCRYPT_MODE, secretKey, ivForCBC);
      baos.write(ivForCBC.getIV());

      try (final CipherOutputStream cos = new CipherOutputStream(baos, aesCBC)) {
        cos.write(plaintext);
      }
      byte[] ciphertext = baos.toByteArray();
      return Base64.encodeBase64String(ciphertext);
    } catch (Exception e) {
      System.out.println("Error while encrypting: " + e.toString());
    }
    return "******************";
  }

  private static IvParameterSpec createIV(int blockSize, Optional<SecureRandom> rng) {
    final byte[] iv = new byte[blockSize];
    final SecureRandom theRNG = rng.orElse(new SecureRandom());
    theRNG.nextBytes(iv);
    return new IvParameterSpec(iv);
  }

  public static String decrypt(String strToDecrypt, String secret) {

    try {
      SecretKeySpec secretKey = constructKey(secret);
      final byte[] decrypted;

      final ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(strToDecrypt));

      final Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
      final IvParameterSpec ivForCBC = readIV(aesCBC.getBlockSize(), bais);
      aesCBC.init(Cipher.DECRYPT_MODE, secretKey, ivForCBC);

      final byte[] buf = new byte[1_024];
      try (final CipherInputStream cis = new CipherInputStream(bais, aesCBC);
          final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        int read;
        while ((read = cis.read(buf)) != -1) {
          baos.write(buf, 0, read);
        }
        decrypted = baos.toByteArray();
      }

      return new String(decrypted, UTF_8);
    } catch (Exception e) {
      System.out.println("Error while decrypting: " + e.toString());
    }
    return "******************";
  }

  private static IvParameterSpec readIV(int blockSize, final InputStream is) throws IOException {
    final byte[] iv = new byte[blockSize];
    int offset = 0;
    while (offset < blockSize) {
      final int read = is.read(iv, offset, blockSize - offset);
      if (read == -1) {
        throw new IOException("Too few bytes for IV in input stream");
      }
      offset += read;
    }
    return new IvParameterSpec(iv);
  }
}
