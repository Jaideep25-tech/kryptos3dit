package kryptos3dit.crypto;

/**
 * This class implements AES-256 encryption on a single 128-bit data block.
 * Details at: https://en.wikipedia.org/wiki/Advanced_Encryption_Standard
 * @author  Jaideep
 */
public final class AES256 {

    private static final int ROUND_COUNT = 14;
    
    /**
     * This method performs AES-256 encryption on 16 bytes of data.
     * @param state      The 16 byte data block stored as char[16].
     * @param key       The 256-bit key stored as char[32].
     * @return Nothing  The data gets encrypted.
     */
    public static void encrypt(char[] state, char[] key) {
        
        // Step 1: Key Expansion
        char[][] keys = Helpers.keyExpansion(key);

        // Step 2: Intial Round Key Addtion
        Helpers.addRoundKey(state, keys[0]);

        // Step 3: 13 rounds
        for (int i = 1; i < ROUND_COUNT; ++i) {
            Helpers.subBytes(state);
            Helpers.shiftRows(state);
            Helpers.mixColumns(state);
            Helpers.addRoundKey(state, keys[i]);
        }

        // Step 4: Final round
        Helpers.subBytes(state);
        Helpers.shiftRows(state);
        Helpers.addRoundKey(state, keys[ROUND_COUNT]);
    }
}