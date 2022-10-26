package de.soft4mg.sudoku;

public interface ControlViewListener {

    public void newGameRequested(int dimension, GameLevel gameLevel);

    public void undoRequested();

    public void repaintRequested();

    public void initCandidatesRequested();

    public void clearMarkerRequested();

    public void showCandidatesRequested(boolean show);

    public void showHelpRequested();
}
