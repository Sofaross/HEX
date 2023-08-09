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

    public void moveRight(int columnCount) {
        int row = position.getRow();
        int column = position.getColumn();

        if (column < columnCount - 1) {
            position.setColumn(column + 1);
        } else if (row < columnCount - 1) {
            position.setRow(row + 1);
            position.setColumn(0);
        }
    }
}