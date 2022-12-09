package jk.aoc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jk.aoc.util.InputUtil;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

public class Nine {
    enum Direction {
        R, L, U, D;

        static Direction parseDirection(@NonNull String s) {
            if (s.equals("R")) {
                return R;
            }
            if (s.equals("L")) {
                return L;
            }
            if (s.equals("U")) {
                return U;
            }
            if (s.equals("D")) {
                return D;
            }

            throw new IllegalArgumentException(s);
        }
    }

    @NonNull
    @Getter
    @AllArgsConstructor
    static class Instruction {
        Direction direction;
        int distance;

        static Instruction parseInstruction(@NonNull String s) {
            var split = s.split(" ");
            return new Instruction(Direction.parseDirection(split[0]), Integer.parseInt(split[1]));
        }

        @NonNull
        void apply(Point head, Point tail, Set<Point> visited) {
            int dist = distance;
            while (dist-- > 0) {
                head.move(direction);
                tail.moveTowards(head);
                visited.add(tail.copy());
            }
        }

        @NonNull
        void apply(List<Point> rope, Set<Point> visited) {
            int dist = distance;
            while (dist-- > 0) {
                rope.get(0).move(direction);
                for (int idx = 0; idx < rope.size() - 1; idx++) {
                    rope.get(idx + 1).moveTowards(rope.get(idx));
                }
                visited.add(rope.get(rope.size() - 1).copy());
            }
        }
    }

    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    static class Point {
        int x = 0;
        int y = 0;

        Point copy() {
            return new Point(x, y);
        }

        void move(@NonNull Direction direction) {
            switch (direction) {
                case R:
                    x += 1;
                    break;
                case L:
                    x -= 1;
                    break;
                case U:
                    y += 1;
                    break;
                case D:
                    y -= 1;
                    break;
            }
        }

        void moveTowards(@NonNull Point other) {
            if (other.x > this.x && other.x - this.x > 1) {
                this.x++;
                this.y += Integer.compare(other.y, this.y);
            } else if (other.x < this.x && this.x - other.x > 1) {
                this.x--;
                this.y += Integer.compare(other.y, this.y);
            } else if (other.y > this.y && other.y - this.y > 1) {
                this.y++;
                this.x += Integer.compare(other.x, this.x);
            } else if (other.y < this.y && this.y - other.y > 1) {
                this.y--;
                this.x += Integer.compare(other.x, this.x);
            }
        }
    }

    public static void solve() {
        List<Instruction> inst = new ArrayList<>();
        InputUtil.getInput(Nine.class, s -> s.useDelimiter("\n"), Instruction::parseInstruction, inst::add);
        part1(inst);
        part2(inst);
    }

    static void printVisited(@NonNull Set<Point> visited) {
        var minX = visited.stream().map(p -> p.x).min(Integer::compare).get();
        var maxX = visited.stream().map(p -> p.x).max(Integer::compare).get();
        var minY = visited.stream().map(p -> p.y).min(Integer::compare).get();
        var maxY = visited.stream().map(p -> p.y).max(Integer::compare).get();

        for (var y = maxY; y >= minY; y--) {
            for (var x = minX; x <= maxX; x++) {
                var p = new Point(x, y);
                if (visited.contains(p)) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

    static void part1(@NonNull List<Instruction> inst) {
        Point head = new Point();
        Point tail = new Point();
        Set<Point> visited = new HashSet<>();
        visited.add(tail.copy());
        inst.forEach(i -> i.apply(head, tail, visited));
        System.out.printf("One: %s%n", visited.size());
    }

    static void part2(@NonNull List<Instruction> inst) {
        List<Point> rope = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rope.add(new Point());
        }
        Set<Point> visited = new HashSet<>();
        visited.add(new Point());
        inst.forEach(i -> i.apply(rope, visited));
        // printVisited(visited);
        System.out.printf("Two: %s%n", visited.size());
    }
}
