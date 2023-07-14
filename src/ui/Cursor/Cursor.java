package ui.Cursor;

public class Cursor {
    private Position position; // Текущая позиция курсора
    private int length; // Общая длина данных, на которые указывает курсор

    public Cursor() {
        this.length = 1;
        position=new Position();
    }
    public void setCursorPosition(int row,int column){
        position.setRow(row);
        position.setColumn(column);

    }
    public Position getPosition(){
        return position;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
//        this.length = length;
//        if (position >= length) {
//            position = length - 1;
//        }
    }

}