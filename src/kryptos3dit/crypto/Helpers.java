package kryptos3dit.crypto;

/**
 * This class contains all the functions required to perform AES encryption.
 * Details at: https://en.wikipedia.org/wiki/Advanced_Encryption_Standard
 * @author  Jaideep
 */
final class Helpers {

    /**
     * This method performs the SubBytes step on a block of 16 bytes.
     * @param state     The state block of AES encryption.
     * @return Nothing  The value of state gets modified.
     */
    static void subBytes(char[] state) {

        for (int i = 0; i < state.length; ++i) {
            state[i] = Constants.SBOX[state[i]];
        }
    }

    /**
     * This method performs the ShiftRows step on a block of 16 bytes.
     * @param state     The state block of AES encryption (char[16]).
     * @return Nothing  The value of state gets modified.
     */
    static void shiftRows(char[] state) {

        // Row0 <<< 0

        // Row1 <<< 1
        char _temp = state[1];
        state[1] = state[5];
        state[5] = state[9];
        state[9] = state[13];
        state[13] = _temp;

        // Row2 <<< 2
        _temp = state[2];
        state[2] = state[10];
        state[10] = _temp;
        _temp = state[6];
        state[6] = state[14];
        state[14] = _temp;

        // Row3 <<< 3 - equivalent to Row3 >>> 1
        _temp = state[15];
        state[15] = state[11];
        state[11] = state[7];
        state[7] = state[3];
        state[3] = _temp;
    }

    /**
     * This method performs the MixColumns step on a block of 16 bytes.
     * Details at: https://en.wikipedia.org/wiki/Rijndael_MixColumns
     * 
     * Addition is bitwise XOR in GF(2^8)
     * Multiplication is done using lookup tables from Constants.java
     * 
     * @param state     The state block of AES encryption (char[16]).
     * @return Nothing  The value of state gets modified.
     */
    static void mixColumns(char[] state) {

        for (int i = 0; i < 4; ++i) {

            char[] s = new char[4];
            s[0] = state[4 * i];
            s[1] = state[4 * i + 1];
            s[2] = state[4 * i + 2];
            s[3] = state[4 * i + 3];

            state[4 * i + 0] = (char) (Constants.MUL2[s[0]] ^ Constants.MUL3[s[1]] ^ s[2] ^ s[3]);
            state[4 * i + 1] = (char) (s[0] ^ Constants.MUL2[s[1]] ^ Constants.MUL3[s[2]] ^ s[3]);
            state[4 * i + 2] = (char) (s[0] ^ s[1] ^ Constants.MUL2[s[2]] ^ Constants.MUL3[s[3]]);
            state[4 * i + 3] = (char) (Constants.MUL3[s[0]] ^ s[1] ^ s[2] ^ Constants.MUL2[s[3]]);
        }
    }

    /**
     * This method performs the round key addition step of AES.
     * Essentially, it performs bitwiseXOR(Key, State) for all 16 bytes.
     * 
     * @param state     The state block of AES encryption (char[16]).
     * @param key       The round key with which to mask the state.
     * @return Nothing  The value of state gets modified.
     */
    static void addRoundKey(char[] state, final char[] key) {

        for (int i = 0; i < state.length; ++i) {
            state[i] ^= key[i];
        }
    }

    /**
     * This method performs the key expansion for AES-256 encryption.
     * It gives 15 round keys (14 rounds + 1 initial) from the initial key.
     * 
     * Standard letter notations are used from the algorithm on Wikipedia.
     * Details at: https://en.wikipedia.org/wiki/AES_key_schedule
     * 
     * @param K     The initial key (char[32]) to be expanded into round keys.
     * @return W    The final 15 round keys.
     */
    static char[][] keyExpansion(char[] K) {

        /**
         * This inner class contains helper functions for keyExpansion.
         * Details at: https://en.wikipedia.org/wiki/AES_key_schedule#The_key_schedule
         */
        final class KeyExpansionHelpers {

            public char[] bytesToWord(char b0, char b1, char b2, char b3) {
                return new char[] { b0, b1, b2, b3 };
            }

            /**
             * @param W     The resultant round keys.
             * @param word  The word to be stored inside W.
             * @param i     The word number.
             */
            public void wordToBytes(char[][] W, char[] word, int i) {
                W[i / 4][4 * (i % 4) + 0] = word[0];
                W[i / 4][4 * (i % 4) + 1] = word[1];
                W[i / 4][4 * (i % 4) + 2] = word[2];
                W[i / 4][4 * (i % 4) + 3] = word[3];
            }
            
            public char[] rotWord(char[] word) {
                char[] result = new char[word.length];
                result[0] = word[1];
                result[1] = word[2];
                result[2] = word[3];
                result[3] = word[0];
                return result;
            }

            public char[] subWord(char[] word) {
                char[] result = new char[word.length];
                for (int i = 0; i < word.length; ++i) {
                    result[i] = Constants.SBOX[word[i]];
                }
                return result;
            }

            public char[] xorWords(char[] first, char[] second) {
                char[] result = new char[first.length];
                for (int i = 0; i < first.length; ++i) {
                    result[i] = (char) (first[i] ^ second[i]);
                }
                return result;
            }
        }
        KeyExpansionHelpers operations = new KeyExpansionHelpers();
        
        int N = 8;
        int R = 15;
        char[][] W = new char[15][16];
        
        final char[][] rcon = {
            {0x01, 0x00, 0x00, 0x00},
            {0x02, 0x00, 0x00, 0x00},
            {0x04, 0x00, 0x00, 0x00},
            {0x08, 0x00, 0x00, 0x00},
            {0x10, 0x00, 0x00, 0x00},
            {0x20, 0x00, 0x00, 0x00},
            {0x40, 0x00, 0x00, 0x00}
        };

        for (int i = 0; i < 4 * R; ++i) {

            if (i < N) {
                char[] word = operations.bytesToWord(K[4 * i], K[4 * i + 1], K[4 * i + 2], K[4 * i + 3]);
                operations.wordToBytes(W, word, i);
            }
            else {

                // Get (i - 1)th word
                int r = (i - 1) / 4;
                int c = 4 * ((i - 1) % 4);
                char[] prevWord = operations.bytesToWord(W[r][c + 0], W[r][c + 1], W[r][c + 2], W[r][c + 3]);
                
                // Get (i - N)th word
                r = (i - N) / 4;
                c = 4 * ((i - N) % 4);
                char[] prevNWord = operations.bytesToWord(W[r][c + 0], W[r][c + 1], W[r][c + 2], W[r][c + 3]);

                if (i % N == 0) {
                    char[] result = operations.xorWords(
                                        prevNWord, operations.xorWords(
                                            rcon[i / N - 1], operations.subWord(
                                                operations.rotWord(prevWord)
                                    )));
                    operations.wordToBytes(W, result, i);
                }
                else if (i % N == 4) {
                    char[] result = operations.xorWords(
                                        prevNWord, operations.subWord(prevWord)
                                    );
                    operations.wordToBytes(W, result, i);
                }
                else {
                    char[] result = operations.xorWords(prevWord, prevNWord);
                    operations.wordToBytes(W, result, i);
                }
            } 
        }
        
        return W;
    }
}
