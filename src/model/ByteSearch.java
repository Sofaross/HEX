package model;

public class ByteSearch {
    public static int searchWithMask(byte[] data, byte[] mask, int startIndex) {
        for (int i = startIndex; i <= data.length - mask.length; i++) {
            boolean found = true;
            for (int j = 0; j < mask.length; j++) {
                if (mask[j] != -1 && mask[j] != data[i + j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }
}