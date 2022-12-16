package jk.aoc.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public final class Line {
    @NonNull
    Point a;
    @NonNull
    Point b;

    public int mahattanLength() {
        return a.manhattanDistance(b);
    }

    public boolean tryExtendHorizontal(@NonNull Line line) {
        var left = Math.max(Math.min(line.getA().getX(), line.getB().getX()),
                Math.min(getA().getX(), getB().getX()));
        var right = Math.min(Math.max(line.getA().getX(), line.getB().getX()),
                Math.max(getA().getX(), getB().getX()));
        if (right >= left) {
            getA().setX(Math.min(line.getA().getX(), getA().getX()));
            getB().setX(Math.max(line.getB().getX(), getB().getX()));
            return true;
        }
        return false;
    }
}
