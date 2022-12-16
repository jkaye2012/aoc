package jk.aoc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import jk.aoc.util.InputUtil;
import jk.aoc.util.Point;
import lombok.NonNull;

public class Fourteen {
    static class Field {
        Set<Point> points = new HashSet<>();

        @NonNull
        void fill(Point from, Point to) {
            if (from.getX() != to.getX()) {
                for (int i = Math.min(from.getX(), to.getX()); i <= Math.max(from.getX(), to.getX()); i++) {
                    points.add(new Point(i, from.getY()));
                }
            } else {
                for (int i = Math.min(from.getY(), to.getY()); i <= Math.max(from.getY(), to.getY()); i++) {
                    points.add(new Point(from.getX(), i));
                }
            }
        }

        int minX() {
            return points.stream().map(Point::getX).min(Integer::compareTo).get();
        }

        int maxX() {
            return points.stream().map(Point::getX).max(Integer::compareTo).get();

        }

        int maxY() {
            return points.stream().map(Point::getY).max(Integer::compareTo).get();
        }

        boolean addGrain(@NonNull Point entry, Predicate<Point> failure) {
            Point current = entry;
            while (!failure.test(current)) {
                if (!points.contains(current.down())) {
                    current = current.down();
                } else if (!points.contains(current.downLeft())) {
                    current = current.downLeft();
                } else if (!points.contains(current.downRight())) {
                    current = current.downRight();
                } else {
                    points.add(current);
                    return true;
                }
            }

            return false;
        }

        boolean addGrain(@NonNull Point entry, int floorDepth) {
            Point current = entry;
            while (!points.contains(entry)) {
                if (current.getY() == floorDepth - 1) {
                    points.add(current);
                    return true;
                }

                if (!points.contains(current.down())) {
                    current = current.down();
                } else if (!points.contains(current.downLeft())) {
                    current = current.downLeft();
                } else if (!points.contains(current.downRight())) {
                    current = current.downRight();
                } else {
                    points.add(current);
                    return true;
                }
            }

            return false;
        }

        int simulate(@NonNull Point entry) {
            int minX = minX();
            int maxX = maxX();
            int maxY = maxY();
            int grains = 0;
            while (addGrain(entry, next -> next.getX() > maxX || next.getX() < minX || next.getY() > maxY)) {
                grains++;
            }
            return grains;
        }

        int simulateWithFloor(@NonNull Point entry) {
            int grains = 0;
            int floorDepth = maxY() + 2;
            while (addGrain(entry, floorDepth)) {
                grains++;
            }
            return grains;
        }
    }

    static List<Point> parsePoints(@NonNull String s) {
        List<Point> points = new ArrayList<>();
        var pointStrings = s.split(" -> ");
        for (int i = 0; i < pointStrings.length; i++) {
            var split = pointStrings[i].split(",");
            points.add(new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
        }
        return points;
    }

    public static void solve() {
        List<List<Point>> points = new ArrayList<>();
        InputUtil.getInput(Fourteen.class, s -> s.useDelimiter("\n"), Fourteen::parsePoints, points::add);
        Field field = new Field();
        for (var shape : points) {
            for (int i = 1; i < shape.size(); i++) {
                field.fill(shape.get(i - 1), shape.get(i));
            }
        }
        part1(field);

        Field field2 = new Field();
        for (var shape : points) {
            for (int i = 1; i < shape.size(); i++) {
                field2.fill(shape.get(i - 1), shape.get(i));
            }
        }
        part2(field2);
    }

    static void part1(@NonNull Field field) {
        System.out.printf("One: %s%n", field.simulate(new Point(500, 0)));
    }

    static void part2(@NonNull Field field) {
        System.out.printf("Two: %s%n", field.simulateWithFloor(new Point(500, 0)));
    }
}
