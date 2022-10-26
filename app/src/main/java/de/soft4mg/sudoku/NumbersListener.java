package de.soft4mg.sudoku;

public interface NumbersListener {




    public void numberPressed(int number, NumberAction numberAction);

    public void numberPressedLong(int number, NumberAction numberAction);

    public void buttonPressed(NumberAction numberAction);

    public void buttonPressedLong(NumberAction numberAction);

}
