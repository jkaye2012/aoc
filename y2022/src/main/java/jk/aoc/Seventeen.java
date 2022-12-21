package jk.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jk.aoc.util.InfiniteIterator;
import jk.aoc.util.InputUtil;
import jk.aoc.util.Point;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

public class Seventeen {

    enum Direction {
        LEFT,
        RIGHT;

        static Direction parseDirection(char d) {
            return d == '<' ? LEFT : RIGHT;
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Shape {
        private @NonNull Set<Point> points;

        public static Shape horizontalLine(@NonNull Point init) {
            var points = new HashSet<Point>(Arrays.asList(
                    init,
                    init.moveX(1),
                    init.moveX(2),
                    init.moveX(3)));
            return new Shape(points);
        }

        public static Shape plus(@NonNull Point init) {
            var points = new HashSet<Point>(Arrays.asList(
                    init.moveY(1),
                    init.moveX(1),
                    init.move(1, 1),
                    init.move(1, 2),
                    init.move(2, 1)));
            return new Shape(points);
        }

        public static Shape corner(@NonNull Point init) {
            var points = new HashSet<Point>(Arrays.asList(
                    init,
                    init.moveX(1),
                    init.moveX(2),
                    init.move(2, 1),
                    init.move(2, 2)));
            return new Shape(points);
        }

        public static Shape verticalLine(@NonNull Point init) {
            var points = new HashSet<Point>(Arrays.asList(
                    init,
                    init.moveY(1),
                    init.moveY(2),
                    init.moveY(3)));
            return new Shape(points);
        }

        public static Shape square(@NonNull Point init) {
            var points = new HashSet<Point>(Arrays.asList(
                    init,
                    init.moveX(1),
                    init.moveY(1),
                    init.move(1, 1)));
            return new Shape(points);
        }

        public static Shape room() {
            var points = IntStream.range(0, 7).mapToObj(x -> new Point(x, 0)).collect(Collectors.toSet());
            return new Shape(points);
        }

        public boolean containsAny(@NonNull Set<Point> ps) {
            return ps.stream().anyMatch(p -> points.contains(p));
        }

        public int left() {
            return points.stream().map(p -> p.getX()).min(Integer::compareTo).get();
        }

        public int right() {
            return points.stream().map(p -> p.getX()).max(Integer::compareTo).get();
        }

        public int bottom() {
            return points.stream().map(p -> p.getY()).min(Integer::compareTo).get();
        }

        public int top() {
            return points.stream().map(p -> p.getY()).max(Integer::compareTo).get();
        }

        public Set<Point> restingPoints() {
            return points.stream().collect(Collectors.groupingBy(p -> p.getX()))
                    .entrySet().stream()
                    .map(kvp -> kvp.getValue().stream().min((p1, p2) -> Integer.compare(p1.getY(), p2.getY())).get())
                    .map(p -> p.moveY(-1))
                    .collect(Collectors.toSet());
        }

        public Set<Point> leftEdge() {
            return points.stream().collect(Collectors.groupingBy(p -> p.getY()))
                    .entrySet().stream()
                    .map(kvp -> kvp.getValue().stream().min((p1, p2) -> Integer.compare(p1.getX(), p2.getX())).get())
                    .collect(Collectors.toSet());
        }

        public Set<Point> rightEdge() {
            return points.stream().collect(Collectors.groupingBy(p -> p.getY()))
                    .entrySet().stream()
                    .map(kvp -> kvp.getValue().stream().max((p1, p2) -> Integer.compare(p1.getX(), p2.getX())).get())
                    .collect(Collectors.toSet());
        }

        public void consume(@NonNull Shape shape) {
            shape.points.forEach(points::add);
        }

        private void translate(int x, int y) {
            points.forEach(p -> p.translate(x, y));
        }

        public boolean move(@NonNull Shape field, @NonNull Direction direction) {
            switch (direction) {
                case LEFT:
                    if (left() != 0 && !field
                            .containsAny(leftEdge().stream().map(p -> p.moveX(-1)).collect(Collectors.toSet()))) {
                        translate(-1, 0);
                    }
                    break;
                default:
                    if (right() != 6 && !field
                            .containsAny(rightEdge().stream().map(p -> p.moveX(1)).collect(Collectors.toSet()))) {
                        translate(1, 0);
                    }
            }

            if (field.containsAny(restingPoints())) {
                return true;
            } else {
                translate(0, -1);
                return false;
            }
        }

        public void print() {
            for (int y = top(); y >= bottom(); y--) {
                for (int x = left(); x <= right(); x++) {
                    if (points.contains(new Point(x, y))) {
                        System.out.print("#");
                    } else {
                        System.out.print(".");
                    }
                }
                System.out.println();
            }
        }
    }

    static final class ShapeGenerator {
        private int current = 0;

        public Shape generate(@NonNull Point entry) {
            var prev = current;
            current = (current + 1) % 5;
            switch (prev) {
                case 0:
                    return Shape.horizontalLine(entry);
                case 1:
                    return Shape.plus(entry);
                case 2:
                    return Shape.corner(entry);
                case 3:
                    return Shape.verticalLine(entry);
                default:
                    return Shape.square(entry);
            }
        }
    }

    public static void solve() {
        List<Direction> directions = new ArrayList<>();
        InputUtil.getInput(Seventeen.class, UnaryOperator.identity(), s -> s.toCharArray(), cs -> {
            for (var c : cs) {
                directions.add(Direction.parseDirection(c));
            }
        });
        part1(directions);
        part2(directions, 1000000000000L);
    }

    private static void part1(@NonNull List<Direction> directions) {
        var room = Shape.room();
        var generator = new ShapeGenerator();
        var directionIterator = new InfiniteIterator<>(directions);
        int numRocks = 0;

        while (numRocks < 2022) {
            var shape = generator.generate(new Point(2, room.top() + 4));
            while (!shape.move(room, directionIterator.next())) {
            }
            room.consume(shape);
            numRocks++;
        }

        System.out.printf("One: %s%n", room.top());
    }

    private static void part2(@NonNull List<Direction> directions, long target) {
        var totalDirections = 0;
        var room = Shape.room();
        var generator = new ShapeGenerator();
        var directionIterator = new InfiniteIterator<>(directions);
        int numRocks = 0;
        var recurrenceFinder = new ArrayList<Point>();
        var prevHeight = 0;
        var heights = new ArrayList<Long>();

        while (true) {
            var point = new Point(totalDirections % directions.size(), numRocks % 5);
            recurrenceFinder.add(point);
            heights.add(Integer.toUnsignedLong(room.top() - prevHeight));
            prevHeight = room.top();

            for (int i = 0; i < recurrenceFinder.size(); i++) {
                if (i == recurrenceFinder.size() - 1 || !recurrenceFinder.get(i).equals(point)) {
                    continue;
                }
                var matched = false;
                int len = recurrenceFinder.size() - (i + 1);
                for (int j = i + 1; j < recurrenceFinder.size(); j++) {
                    if (j < len || !recurrenceFinder.get(j).equals(recurrenceFinder.get(j - len))) {
                        break;
                    }
                    matched = true;
                }

                if (matched) {
                    var preRecurrenceLen = recurrenceFinder.size() - len * 2;
                    var preRecurrenceHeight = heights.subList(0, preRecurrenceLen + 1).stream().reduce(0L, Long::sum);
                    var recurringHeight = heights.stream().skip(heights.size() - len).reduce(0L, Long::sum);
                    var recurrence = heights.subList(heights.size() - len, heights.size());
                    var numRecurrences = (target - preRecurrenceLen) / len;
                    Long remainingLen = (target - preRecurrenceLen + 1) % len;
                    var remainingHeight = recurrence.subList(0, remainingLen.intValue()).stream().reduce(0L, Long::sum);
                    System.out.printf("Two: %s%n",
                            remainingHeight + preRecurrenceHeight + recurringHeight * numRecurrences);
                    return;
                }
            }

            var shape = generator.generate(new Point(2, room.top() + 4));
            while (!shape.move(room, directionIterator.next())) {
                totalDirections++;
            }
            totalDirections++;
            room.consume(shape);
            numRocks++;
        }
    }
}
