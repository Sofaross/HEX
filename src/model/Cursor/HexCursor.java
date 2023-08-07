package model.Cursor;

public class HexCursor {
    private final Position position;

    public HexCursor() {
        position = new Position();
    }

    public void setCursorPosition(int row, int column) {
        position.setRow(row);
        position.setColumn(column);
    }

    public Position getPosition() {
        return position;
    }

    public void moveRight(int maxLength) {
        int row = position.getRow();
        int column = position.getColumn();

        if (column < maxLength - 1) {
            position.setColumn(column + 1);
        } else if (row < maxLength - 1) {
            position.setRow(row + 1);
            position.setColumn(0);
        }
    }
}