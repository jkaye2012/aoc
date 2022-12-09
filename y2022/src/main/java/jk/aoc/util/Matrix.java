package jk.aoc.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class Matrix<T> {
    private int numRows;
    private int numCols;
    private List<T> items;

    @NonNull
    public Matrix(int numRows, List<T> items) {
        if (numRows <= 0) {
            throw new IllegalArgumentException("numRows <= 0");
        }
        if (items.size() % numRows != 0) {
            throw new IllegalArgumentException("numRows must evenly divide items into columns");
        }

        this.numRows = numRows;
        this.numCols = items.size() / numRows;
        this.items = items;
    }

    @NonNull
    public static <T> Matrix<T> parseMatrix(List<String> rows, Function<String, List<T>> rowParser) {
        List<T> items = new ArrayList<>();
        for (var row : rows) {
            items.addAll(rowParser.apply(row));
        }

        return new Matrix<>(rows.size(), items);
    }

    public int size() {
        return getNumCols() * getNumRows();
    }

    private int index(int row, int col) {
        return row * getNumCols() + col;
    }

    public T get(int row, int col) {
        if (row >= getNumRows()) {
            throw new IllegalArgumentException("requested row out of bounds");
        }
        if (col >= getNumCols()) {
            throw new IllegalArgumentException("requested column out of bounds");
        }

        return items.get(index(row, col));
    }

    enum Direction {
        FORWARD,
        REVERSE
    }

    class RowIterator implements Iterator<T> {
        int row;
        Direction direction;
        int idx;

        RowIterator(int row, Direction direction) {
            this.row = row;
            this.direction = direction;

            switch (direction) {
                case FORWARD:
                    this.idx = row * Matrix.this.getNumCols();
                    break;
                case REVERSE:
                    this.idx = (row + 1) * Matrix.this.getNumCols() - 1;
                    break;
            }
        }

        @Override
        public boolean hasNext() {
            switch (direction) {
                case FORWARD:
                    return this.idx < row * Matrix.this.getNumCols() + Matrix.this.getNumCols();
                case REVERSE:
                    return this.idx >= row * Matrix.this.getNumCols();
            }

            return false;
        }

        @Override
        public T next() {
            T item = Matrix.this.items.get(idx);
            switch (direction) {
                case FORWARD:
                    this.idx++;
                    break;
                case REVERSE:
                    this.idx--;
                    break;
            }

            return item;
        }
    }

    class ColumnIterator implements Iterator<T> {
        int col;
        Direction direction;
        int idx;

        ColumnIterator(int col, Direction direction) {
            this.col = col;
            this.direction = direction;

            switch (direction) {
                case FORWARD:
                    this.idx = col;
                    break;
                case REVERSE:
                    this.idx = col + Matrix.this.getNumRows() * Matrix.this.getNumCols() - Matrix.this.getNumCols();
                    break;
            }
        }

        @Override
        public boolean hasNext() {
            switch (direction) {
                case FORWARD:
                    return this.idx < Matrix.this.size();
                case REVERSE:
                    return this.idx >= 0;
            }

            return false;
        }

        @Override
        public T next() {
            T item = Matrix.this.items.get(idx);
            switch (direction) {
                case FORWARD:
                    this.idx += Matrix.this.getNumCols();
                    break;
                case REVERSE:
                    this.idx -= Matrix.this.getNumCols();
                    break;
            }

            return item;
        }
    }

    public Iterator<T> getForwardRowIterator(int row) {
        return new RowIterator(row, Direction.FORWARD);
    }

    public Iterator<T> getReverseRowIterator(int row) {
        return new RowIterator(row, Direction.REVERSE);
    }

    public Iterator<T> getForwardColumnIterator(int col) {
        return new ColumnIterator(col, Direction.FORWARD);
    }

    public Iterator<T> getReverseColumnIterator(int col) {
        return new ColumnIterator(col, Direction.REVERSE);
    }

    public Stream<T> stream() {
        return getItems().stream();
    }
}
