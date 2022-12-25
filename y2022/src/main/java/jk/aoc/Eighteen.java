package jk.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jk.aoc.util.InputUtil;
import jk.aoc.util.Point3D;
import lombok.NonNull;

public class Eighteen {
    public static void solve() {
        List<Point3D> points = new ArrayList<>();
        InputUtil.getInput(Eighteen.class, s -> s.useDelimiter("\n"), Point3D::parsePoint3D, points::add);
        var before = System.currentTimeMillis();
        part1(points);
        var p1 = System.currentTimeMillis();
        System.out.printf("%s ms%n", p1 - before);
        part2(points);
        var p2 = System.currentTimeMillis();
        System.out.printf("%s ms%n", p2 - p1);
    }

    static Stream<Point3D> adjacency(@NonNull Point3D point) {
        return Arrays.asList(point.moveX(1), point.moveY(1), point.moveZ(1),
                point.moveX(-1), point.moveY(-1), point.moveZ(-1)).stream();
    }

    static int surfaceArea(@NonNull Collection<Point3D> points) {
        List<Point3D> adjacencies = points.stream().flatMap(Eighteen::adjacency).collect(Collectors.toList());
        adjacencies.removeAll(points);
        return adjacencies.size();
    }

    static void part1(@NonNull List<Point3D> points) {
        System.out.printf("One: %s%n", surfaceArea(points));
    }

    static void part2(@NonNull List<Point3D> points) {
        System.out.printf("Two: %s%n", surfaceArea(points));
    }
}
