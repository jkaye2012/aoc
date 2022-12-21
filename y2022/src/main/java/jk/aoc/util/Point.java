package jk.aoc.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public final class Point {
    int x;
    int y;

    public Point down() {
        return new Point(x, y + 1);
    }

    public Point downLeft() {
        return new Point(x - 1, y + 1);
    }

    public Point downRight() {
        return new Point(x + 1, y + 1);
    }

    public Point moveY(int incr) {
        return move(0, incr);
    }

    public Point moveX(int incr) {
        return move(incr, 0);
    }

    public Point move(int incrX, int incrY) {
        return new Point(x + incrX, y + incrY);
    }

    public void translate(int incrX, int incrY) {
        setX(x + incrX);
        setY(y + incrY);
    }

    public int manhattanDistance(@NonNull Point other) {
        return Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY());
    }

}
