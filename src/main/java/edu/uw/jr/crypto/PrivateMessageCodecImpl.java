package edu.uw.jr.crypto;

import edu.uw.ext.framework.crypto.PrivateMessageTriple;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Implementation of PrivateMessageCodec that uses a 128 bit AES key to encrypt the data. All keystores are of the
 * PKCS12 type.
 *
 * @author Jesse Ruth
 */
public class PrivateMessageCodecImpl implements edu.uw.ext.framework.crypto.PrivateMessageCodec {
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
        return null;
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
        return new byte[0];
    }
}
