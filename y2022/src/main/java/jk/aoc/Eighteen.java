package jk.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Predicate;

import jk.aoc.util.InputUtil;
import jk.aoc.util.Point3D;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

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

    static List<Point3D> surfaceAir(@NonNull Collection<Point3D> points) {
        List<Point3D> adjacencies = points.stream().flatMap(Eighteen::adjacency).collect(Collectors.toList());
        adjacencies.removeAll(points);
        return adjacencies;
    }

    static int surfaceArea(@NonNull Collection<Point3D> points) {
        var adjacencies = surfaceAir(points);
        return adjacencies.size();
    }

    static void part1(@NonNull List<Point3D> points) {
        System.out.printf("One: %s%n", surfaceArea(points));
    }

    @Getter
    @AllArgsConstructor
    @ToString
    static class BoundingBox {
        int minX;
        int maxX;
        int minY;
        int maxY;
        int minZ;
        int maxZ;

        public boolean onBoundary(@NonNull Point3D point) {
            return point.getX() == minX || point.getX() == maxX || point.getY() == minY || point.getY() == maxY
                    || point.getZ() == minZ || point.getZ() == maxZ;
        }

    }

    static BoundingBox getBoundingBox(@NonNull Collection<Point3D> points) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (var point : points) {
            minX = Math.min(minX, point.getX());
            maxX = Math.max(maxX, point.getX());
            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());
            minZ = Math.min(minZ, point.getZ());
            maxZ = Math.max(maxZ, point.getZ());
        }

        return new BoundingBox(minX, maxX, minY, maxY, minZ, maxZ);
    }

    static boolean incrementalSearchUntil(@NonNull Point3D point,
            @NonNull UnaryOperator<Point3D> update, @NonNull Predicate<Point3D> searchDone,
            @NonNull Predicate<Point3D> searchSuccessful) {
        Point3D searchPoint = point;
        do {
            searchPoint = update.apply(searchPoint);
        } while (!searchDone.test(searchPoint));
        return searchSuccessful.test(searchPoint);
    }

    static void part2(@NonNull List<Point3D> points) {
        var air = surfaceAir(points);
        var airSearch = new HashSet<>(air);
        var bounds = getBoundingBox(airSearch);
        Set<Point3D> exposedAir = new HashSet<>();
        for (var boundaryPoint : air.stream().filter(bounds::onBoundary).collect(Collectors.toList())) {
            exposedAir.add(boundaryPoint);
            airSearch.remove(boundaryPoint);
        }

        Predicate<Point3D> searchDone = p -> points.contains(p) || air.contains(p)
                || bounds.onBoundary(p);
        Predicate<Point3D> searchSuccessful = p -> bounds.onBoundary(p) || exposedAir.contains(p);

        var prevExposed = 0;
        while (prevExposed != exposedAir.size()) {
            prevExposed = exposedAir.size();
            for (var airPoint : airSearch) {
                if (incrementalSearchUntil(airPoint, p -> p.moveX(-1), searchDone, searchSuccessful)
                        || incrementalSearchUntil(airPoint, p -> p.moveX(1), searchDone, searchSuccessful)
                        || incrementalSearchUntil(airPoint, p -> p.moveY(-1), searchDone, searchSuccessful)
                        || incrementalSearchUntil(airPoint, p -> p.moveY(1), searchDone, searchSuccessful)
                        || incrementalSearchUntil(airPoint, p -> p.moveZ(-1), searchDone, searchSuccessful)
                        || incrementalSearchUntil(airPoint, p -> p.moveZ(1), searchDone, searchSuccessful)) {
                    exposedAir.add(airPoint);
                }
            }

            airSearch.removeAll(exposedAir);
        }

        System.out.printf("Two: %s%n", air.stream().filter(exposedAir::contains).count());
    }
}
