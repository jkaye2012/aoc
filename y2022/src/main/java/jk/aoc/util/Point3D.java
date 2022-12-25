package jk.aoc.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class Point3D {
    private int x;
    private int y;
    private int z;

    public static Point3D parsePoint3D(@NonNull String s) {
        var split = s.split(",");
        return new Point3D(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public Point3D moveX(int incr) {
        return move(incr, 0, 0);
    }

    public Point3D moveY(int incr) {
        return move(0, incr, 0);
    }

    public Point3D moveZ(int incr) {
        return move(0, 0, incr);
    }

    public Point3D move(int incrX, int incrY, int incrZ) {
        return new Point3D(x + incrX, y + incrY, z + incrZ);
    }
}
