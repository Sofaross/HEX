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
}