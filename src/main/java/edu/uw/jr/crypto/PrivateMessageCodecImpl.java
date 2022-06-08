package edu.uw.jr.crypto;

import edu.uw.ext.framework.crypto.PrivateMessageCodec;
import edu.uw.ext.framework.crypto.PrivateMessageTriple;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Implementation of PrivateMessageCodec that uses a 128 bit AES key to encrypt the data. All keystores are of the
 * PKCS12 type.
 *
 * @author Jesse Ruth
 */
public class PrivateMessageCodecImpl implements edu.uw.ext.framework.crypto.PrivateMessageCodec {
    private static final String KEY_STORE_TYPE = "PKCS12";
    private static final String CERT_TYPE = "X.509";
    private static final String AES_KEY_TYPE = "AES";
    private static final String AES_CYPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int AES_KEY_LENGTH = 32;
    private static final int AES_KEY_SIZE = 256;
    private static final String RSA_CYPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String ROOT_PATH = "/";
    private static final String SIGN_ALGORITHM = "SHA256withRSA";


    /**
     * Enciphers the provided data. Key stores will be accessed as resources, i.e. on the classpath.
     *
     * @param plaintext            the data to be encrypted
     * @param senderKeyStoreName   the name of the sender's key store resource
     * @param senderKeyStorePasswd the sender's key store password
     * @param senderKeyName        the alias of the sender's private key
     * @param recipientCertFile    the file containing the recipient's public key certificate, as a resource path
     * @return message containing the ciphertext, key and signature
     * @throws GeneralSecurityException if any cryptographic operations fail
     * @throws IOException              if unable to write either of the files
     */
    @Override
    public PrivateMessageTriple encipher(byte[] plaintext, String senderKeyStoreName, char[] senderKeyStorePasswd, String senderKeyName, String recipientCertFile) throws GeneralSecurityException, IOException {
        final KeyGenerator generator = KeyGenerator.getInstance(AES_KEY_TYPE);
        generator.init(AES_KEY_SIZE);
        final SecretKey sharedSecretKey = generator.generateKey();
        final byte[] cipherText = encrypt(sharedSecretKey, AES_CYPHER_TRANSFORMATION, plaintext);
        final byte[] sharedSecretKeyBytes = sharedSecretKey.getEncoded();
        final PublicKey publicKey = getPublicKeyFromResource(recipientCertFile);
        final byte[] encipheredSharedKey = encrypt(publicKey, RSA_CYPHER_TRANSFORMATION, sharedSecretKeyBytes);
        final PrivateKey clientPrivateKey = getPrivateKeyFromStore(senderKeyStoreName, senderKeyStorePasswd, senderKeyName);

        byte[] signature;
        try {
            Signature sign = Signature.getInstance(SIGN_ALGORITHM);
            sign.initSign(clientPrivateKey);
            sign.update(plaintext);
            signature = sign.sign();
        } catch (final InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            throw new GeneralSecurityException("error signing", e);
        }

        return new PrivateMessageTriple(encipheredSharedKey, cipherText, signature);
    }

    /**
     * Gets a key from the private store provided
     *
     * @param keyStoreName   store name
     * @param keyStorePasswd store password
     * @param keyName        key name
     * @return The private key
     * @throws KeyStoreException         If key is missing
     * @throws NoSuchAlgorithmException  if algorithm is missing
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     * @throws IOException
     */
    private PrivateKey getPrivateKeyFromStore(final String keyStoreName, final char[] keyStorePasswd, final String keyName) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, IOException {
        try (InputStream inputStream = PrivateMessageCodec.class.getResourceAsStream(ROOT_PATH + keyStoreName)) {
            if (inputStream == null) {
                throw new KeyStoreException("Unable to locate keystore " + keyStoreName);
            }
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(inputStream, keyStorePasswd);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, keyStorePasswd);
            return privateKey;
        }
    }

    /**
     * Gets a public key from the resource directory
     *
     * @param certResource name of pem file
     * @return a PublicKey
     * @throws CertificateException
     * @throws IOException
     */
    private PublicKey getPublicKeyFromResource(final String certResource) throws CertificateException, IOException {
        try (InputStream inputStream = PrivateMessageCodec.class.getResourceAsStream(ROOT_PATH + certResource)) {
            CertificateFactory factory = CertificateFactory.getInstance(CERT_TYPE);
            X509Certificate x509Certificate = (X509Certificate) factory.generateCertificate(inputStream);
            PublicKey publicKey = x509Certificate.getPublicKey();
            return publicKey;
        }
    }

    /**
     * Cipher the data with the key and algorithm
     *
     * @param cipherKey      key used to encipher
     * @param transformation cipher transformation to use
     * @param encryptMode    ENCRYPT_MODE or DECRYPT_MODE
     * @param data           plaintext or ciphertext depending on the mode
     * @return the enciphered/deciphered data
     * @throws GeneralSecurityException If any exceptions are raised
     */
    private byte[] cipherData(final Key cipherKey, final String transformation, final int encryptMode, byte[] data) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(encryptMode, cipherKey);
            return cipher.doFinal(data);
        } catch (final InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new GeneralSecurityException("Error on data cipher", e);
        }
    }

    /**
     * Convenience function to encrypt
     *
     * @param cipherKey      key used to encipher
     * @param transformation cipher transformation to use
     * @param data           plaintext or ciphertext depending on the mode
     * @return the enciphered data
     * @throws GeneralSecurityException
     */
    private byte[] encrypt(final Key cipherKey, final String transformation, byte[] data) throws GeneralSecurityException {
        return cipherData(cipherKey, transformation, Cipher.ENCRYPT_MODE, data);
    }

    /**
     * Convenience funvtion to decrypt
     *
     * @param cipherKey      key used to encipher
     * @param transformation cipher transformation to use
     * @param data           plaintext or ciphertext depending on the mode
     * @return the deciphered data
     * @throws GeneralSecurityException
     */
    private byte[] decrypt(final Key cipherKey, final String transformation, byte[] data) throws GeneralSecurityException {
        return cipherData(cipherKey, transformation, Cipher.DECRYPT_MODE, data);
    }

    /**
     * Decipher the provided message. Keystores will be accessed as resources, i.e. on the classpath.
     *
     * @param triple                  the message containing the ciphertext, key and signature
     * @param recipientKeyStoreName   the name of the recipient's key store resource
     * @param recipientKeyStorePasswd the recipient's key store password
     * @param recipientKeyName        the alias of the recipient's private key
     * @param signerCertFile          the name of the signer's certificate
     * @return the plaintext from the file
     * @throws GeneralSecurityException if any cryptographic operations fail
     * @throws IOException              if unable to write either of the files
     */
    @Override
    public byte[] decipher(PrivateMessageTriple triple, String recipientKeyStoreName, char[] recipientKeyStorePasswd, String recipientKeyName, String signerCertFile) throws GeneralSecurityException, IOException {
        final PrivateKey privateKey = getPrivateKeyFromStore(recipientKeyStoreName, recipientKeyStorePasswd, recipientKeyName);
        final byte[] decipheredSharedBytes = decrypt(privateKey, RSA_CYPHER_TRANSFORMATION, triple.getEncipheredSharedKey());

        final SecretKey sharedSecretKey = new SecretKeySpec(decipheredSharedBytes, 0, AES_KEY_LENGTH, AES_KEY_TYPE);

        final byte[] plainText = decrypt(sharedSecretKey, AES_CYPHER_TRANSFORMATION, triple.getCiphertext());

        final PublicKey clientPublicKey = getPublicKeyFromResource(signerCertFile);

        boolean verified;

        try {
            Signature verifier = Signature.getInstance(SIGN_ALGORITHM);
            verifier.initVerify(clientPublicKey);
            verifier.update(plainText);
            verified = verifier.verify(triple.getSignature());
        } catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new GeneralSecurityException("Invalid Signing Key");
        }

        if (!verified) throw new GeneralSecurityException("Signature Verification Failed");

        return plainText;
    }
}
