import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import kryptos3dit.crypto.AES256;

/**
 * This class performs tests of AES-256 using the test suite provided by
 * National Institute of Standards and Technology. Details about the test files are at,
 * https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/aes/AESAVS.pdf
 * @author Jaideep
 */
public final class TestAES256 {

    /**
     * This inner class helps in building an abstract data type for each
     * data point. 
     */
    static final class TestData {

        char[] key;
        char[] input;
        char[] output;

        /**
         * @param key       The 256-bit key stored as char[32].
         * @param input     The input 16 byte data block stored as char[16]. 
         * @param output    The expected output 16 byte data block stored as char[16].
         */
        public TestData(char[] key, char[] input, char[] output) {
            this.key = key;
            this.input = input;
            this.output = output;
        }

        /**
         * @return The key
         */
        public char[] getKey() {
            return key;
        }

        /**
         * @return The input data block
         */
        public char[] getInput() {
            return input;
        }

        /**
         * @return The output data block
         */
        public char[] getOutput() {
            return output;
        }
    }

    /**
     * Converts a hexadecimal string into bytes stored as char[]
     * The length of the input string should be even.
     * @param inputHex A string consisting of characters [0-9] or [a-f].
     * @return An array of type char of half the size of the input string.
     */
    public static char[] toCharSequence(String inputHex) {

        if (inputHex.length() % 2 != 0) {
            return null;
        }

        char[] result = new char[inputHex.length() / 2];

        for (int i = 0; i < result.length; ++i) {
            result[i] = toCharElement(inputHex.substring(2 * i, 2 * i + 2));
        }
        return result;
    }

    /**
     * Converts a hexadecimal string of size 2 into a byte stored as char.
     * @param inputHex
     * @return The equivalent char, after performing the conversion.
     */
    public static char toCharElement(String inputHex) {

        if (inputHex.length() > 2) {
            return ' ';
        }

        int decimalValue = (16 * toDecimal(inputHex.charAt(0))) + toDecimal(inputHex.charAt(1));
        char result = (char) decimalValue;
        return result;
    }

    /**
     * Converts a single hexadecimal input to its equivalent decimal value.
     * @param inputHex A hexadecimal character.
     * @return         The equivalent decimal value. 
     */
    private static int toDecimal(char inputHex) {

        if (inputHex >= 97 && inputHex <= 102) {
            return (inputHex - 87);
        }
        return (inputHex - 48);
    }

    /**
     * Reads the test file and returns the data points.
     * @param filePath  Path to the test file, should be of CSV format, without headers.
     * @return          An ArrayList of type {@code TestData}
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static ArrayList<TestData> readTestFile(String filePath) throws FileNotFoundException, IOException {

        ArrayList<TestData> result = new ArrayList<TestData>();

        // Initialise reader
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // Iterate through all the lines
        String line = "";
        while ((line = reader.readLine()) != null) {

            // Split the line using "," as a delimiter
            String[] data = line.split(",");

            // Make a new object of type TestData
            TestData dataPoint = new TestData(toCharSequence(data[0]), toCharSequence(data[1]), toCharSequence(data[2]));

            // Add to the list
            result.add(dataPoint);
        } 
        
        reader.close();
        return result;
    }

    /**
     * Performs a deep copy of the input array of type char.
     * @param arr
     * @return An array of type char, made using the input array.
     */
    public static char[] deepCopy(char[] arr) {

        char[] result = new char[arr.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = arr[i];
        }
        return result;
    }

    /**
     * Runs the tests and prints the output to the console.
     * @param args None required
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        // Setup the data points using the test file
        ArrayList<TestData> dataPoints = readTestFile("TEST_DATA.csv");
    

        System.out.println("RUNNING TESTS...");
        int count = 0;
        
        // Iterate through the data points
        for (int i = 0; i < dataPoints.size(); ++i) {

            // Get a deep copy of the input
            char[] temp = deepCopy(dataPoints.get(i).getInput());
    
            // Encrypt the deep copy of the input
            AES256.encrypt(temp, dataPoints.get(i).getKey());

            // Check if it matches with the expected output
            if (Arrays.equals(temp, dataPoints.get(i).getOutput())) {
                ++count;
            }
        }

        System.out.println("TOTAL: " + Integer.toString(dataPoints.size()));
        System.out.println("PASSED: " + Integer.toString(count));
    }
}