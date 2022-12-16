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

    public int manhattanDistance(@NonNull Point other) {
        return Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY());
    }

}
