package kryptos3dit.crypto;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class implements AES-256 encryption/decryption on an
 * arbitrary sized data using the CTR mode of operation.
 * Details about mode of operation: https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation
 * Details about CTR mode: https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Counter_(CTR)
 * @author  Jaideep
 */
public final class AES256CTR {
    
    private final char[] key;
    private final char[] nonce;

    /**
     * WARNING: This constructor is only for testing purposes.
     * THIS SHOULD NOT BE USED TO IMPLEMENT THE ENCRYPTION
     * @param key
     */
    public AES256CTR(char[] key) {
        super();
        this.key = key;
        this.nonce = new char[] {' '};
    }

    /**
     * THIS CONSTRUCTOR SHOULD BE USED FOR ALL ENCRYTPION/DECRYPTION PURPOSES.
     * @param password  The password with which to encrypt the file.    
     * @throws NoSuchAlgorithmException
     */
    public AES256CTR(String password) throws NoSuchAlgorithmException {
        
        super();
        
        byte[] keyByte = MessageDigest.getInstance("SHA-256").digest(
            password.getBytes(StandardCharsets.UTF_8)
        );

        byte[] nonceByte = MessageDigest.getInstance("SHA-256").digest(
            keyByte
        );
        
        char[] keyChar = new char[keyByte.length];
        for (int i = 0; i < keyByte.length; ++i) {
            keyChar[i] = (char) Byte.toUnsignedInt(keyByte[i]);
        }

        char[] nonceChar = new char[16];
        for (int i = 0; i < nonceChar.length; ++i) {
            nonceChar[i] = (char) Byte.toUnsignedInt(nonceByte[i]);
        }

        this.key = keyChar;
        this.nonce = nonceChar;
    }

    /**
     * This method performs bitwise-XOR on two 128-bit long data.
     * This is required to generate the cyphertext (Output_of_AES256 XOR Plaintext)
     * @param img       The image file loaded in char[].
     * @param state     The encrypted state matrix char[16] from AES-256.
     * @param blockNum  The block number which is being XORed.
     * @return Nothing  The specific block in that image gets encrypted.
     */
    private static void XOR(char[] img, final char[] state, final int blockNum) {
        final int blockStartIndex = state.length * blockNum;
        for (int i = blockStartIndex; i < blockStartIndex + state.length && i < img.length; ++i) {
                img[i] = (char) (img[i] ^ state[i % state.length]);
        }
    }

    /**
     * This method performs bitwise-XOR on two 128-bit long data.
     * This is required to generate input to AES256 (Nonce XOR BlockNumber)
     * @param nonce     First 128-bit data of type char[16].
     * @param blockNum  Second 128-bit data of type char[16].
     * @return          The output string after performing bitwise-XOR
     * @throws  ReadOnlyBufferException
     * @throws  BufferOverflowException
     */
    private static char[] XOR(char[] nonce, int blockNum) throws 
            ReadOnlyBufferException, BufferOverflowException {
        
        ByteBuffer bf = ByteBuffer.allocate(nonce.length);
        bf.order(ByteOrder.BIG_ENDIAN);
        bf.putInt(blockNum);
        byte[] b = bf.array();

        char[] result = new char[nonce.length];
        for (int i = 0; i < nonce.length; ++i) {
            result[i] = (char) (nonce[i] ^ (char) Byte.toUnsignedInt(b[i]));
        }
        return result;
    }

    /**
     * This method performs encryption on the image.
     * @param ob        Instance of AES256CTR which stores the nonce and key.
     * @param filePath  The path to the file which needs to be encrypted.
     * @return Nothing  The file specified by {@code filePath} gets encrypted.
     * @throws IOException
     * @throws OutOfMemoryError
     * @throws SecurityException
     * @throws InvalidPathException
     * @throws ReadOnlyBufferException
     * @throws BufferOverflowException
     */
    public static void encrypt(AES256CTR ob, String filePath) throws 
            IOException, OutOfMemoryError, SecurityException, InvalidPathException,
            ReadOnlyBufferException, BufferOverflowException {

        // Read the file into bytes[], convert into char[]
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));

        char[] img = new char[bytes.length];
        for (int i = 0; i < img.length; ++i) {
            img[i] = (char) Byte.toUnsignedInt(bytes[i]);
        }

        final int NUMBER_OF_BLOCKS = img.length / 16 + (img.length % 16 == 0 ? 0 : 1);
        for (int i = 0; i < NUMBER_OF_BLOCKS; ++i) {
            char[] result = XOR(ob.nonce, i);
            AES256.encrypt(result, ob.key);
            XOR(img, result, i);
        }

        for (int i = 0; i < img.length; ++i) {
            bytes[i] = (byte) img[i];
        }
        Files.write(Paths.get(filePath), bytes);
    }

    /**
     * This method performs decryption on the image. Decryption is equivalent
     * to encryption in case of CTR mode of operation.
     * This method is added to make the codebase more readable for those who are
     * less familiar with the CTR mode of operation.
     * @param ob            Instance of AES256CTR which stores the nonce and key.
     * @param filePath      The path to the file which needs to be decrypted.
     * @return Nothing      The file specified by {@code filePath} gets decrypted.
     * @throws IOException
     * @throws OutOfMemoryError
     * @throws SecurityException
     * @throws InvalidPathException
     * @throws ReadOnlyBufferException
     * @throws BufferOverflowException
     */
    public static void decryption(AES256CTR ob, String filePath) throws 
            IOException, OutOfMemoryError, SecurityException, InvalidPathException,
            ReadOnlyBufferException, BufferOverflowException {
        
        encrypt(ob, filePath);
    }
}
