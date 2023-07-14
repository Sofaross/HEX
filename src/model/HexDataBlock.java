package model;

import java.nio.ByteBuffer;

public class HexDataBlock {
    private byte[] data;

    public HexDataBlock(byte[] data) {
        if (data.length != 2 && data.length != 4 && data.length != 8) {
            throw new IllegalArgumentException("Invalid data block length");
        }
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] newData) {
        if (newData.length != 2 && newData.length != 4 && newData.length != 8) {
            throw new IllegalArgumentException("Invalid data block length");
        }
        data = newData;
    }

    public String toHexString() {
        StringBuilder hexString = new StringBuilder();
        for (byte b : data) {
            hexString.append(String.format("%02X ", b));
        }
        return hexString.toString().trim();
    }
    public int toInt() {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        if (data.length == 2) {
            return buffer.getShort();
        } else if (data.length == 4) {
            return buffer.getInt();
        } else if (data.length == 8) {
            return (int) buffer.getLong();
        }

        throw new IllegalArgumentException("Invalid data length");
    }

}