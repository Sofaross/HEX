package model;

public class ByteSearch {
    public static int search(byte[] array, byte[] sequence) {
        int arrayLength = array.length;
        int sequenceLength = sequence.length;

        for (int i = 0; i <= arrayLength - sequenceLength; i++) {
            int j;
            for (j = 0; j < sequenceLength; j++) {
                if (array[i + j] != sequence[j]) {
                    break;
                }
            }
            if (j == sequenceLength) {
                return i;
            }
        }
        return -1;
    }
}