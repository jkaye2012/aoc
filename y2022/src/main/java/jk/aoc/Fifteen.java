package jk.aoc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jk.aoc.util.InputUtil;
import jk.aoc.util.Line;
import jk.aoc.util.Point;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

public class Fifteen {
    static Point parsePoint(@NonNull String s) {
        var split = s.split(", ");
        return new Point(Integer.parseInt(split[0].substring(2)), Integer.parseInt(split[1].substring(2)));
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    static class Sensor {
        Point location;
        Point closestBeacon;

        static Sensor parseSensor(@NonNull String s) {
            var split = s.split(": ");
            return new Sensor(parsePoint(split[0].substring(10)), parsePoint(split[1].substring(21)));
        }

        Optional<Line> coveredAlong(int y) {
            var distance = location.manhattanDistance(closestBeacon);
            var yDiff = Math.abs(y - location.getY());
            var rem = distance - yDiff;
            if (rem < 0) {
                return Optional.empty();
            }

            return Optional.of(new Line(new Point(location.getX() - rem, y), new Point(location.getX() + rem, y)));
        }
    }

    @Getter
    static class Network {
        Set<Sensor> sensors = new HashSet<>();

        public void addSensor(@NonNull Sensor sensor) {
            this.sensors.add(sensor);
        }
    }

    static class ExtensibleField {
        List<Line> lines = new ArrayList<>();

        public void extend(@NonNull Line line) {
            for (var existing : lines) {
                if (existing.tryExtendHorizontal(line)) {
                    subsume(existing);
                    return;
                }
            }

            lines.add(line);
            lines.sort((l1, l2) -> Integer.compare(l1.getA().getX(), l1.getB().getX()));
        }

        private void subsume(@NonNull Line extended) {
            List<Line> toRemove = new ArrayList<>();
            for (var line : lines) {
                if (line == extended) {
                    continue;
                }

                if (extended.tryExtendHorizontal(line)) {
                    toRemove.add(line);
                }
            }

            for (var line : toRemove) {
                lines.remove(line);
            }
        }

        public int totalSize() {
            return lines.stream().map(l -> l.mahattanLength()).reduce(Integer::sum).orElseGet(() -> 0);
        }

        public void clip(int bound) {
            if (lines.get(0).getA().getX() < 0) {
                lines.get(0).getA().setX(0);
            }
            if (lines.get(lines.size() - 1).getB().getX() > bound) {
                lines.get(lines.size() - 1).getB().setX(bound);
            }
        }

        public long firstDiscontinuity() {
            int curr = lines.get(0).getB().getX();
            for (var line : lines.subList(1, lines.size())) {
                if (curr + 1 < line.getA().getX()) {
                    return curr + 1;
                }
                curr = line.getB().getX();
            }
            return -1;
        }
    }

    public static void solve() {
        Network network = new Network();
        InputUtil.getInput(Fifteen.class, s -> s.useDelimiter("\n"), Sensor::parseSensor, network::addSensor);
        part1(network);
        part2(network, 4000000);
    }

    static void part1(@NonNull Network network) {
        ExtensibleField covered = new ExtensibleField();
        for (var sensor : network.getSensors()) {
            sensor.coveredAlong(2000000).ifPresent(covered::extend);
        }
        System.out.printf("One: %s%n", covered.totalSize());
    }

    static void part2(@NonNull Network network, int bound) {
        for (int y = 0; y <= bound; y++) {
            ExtensibleField covered = new ExtensibleField();
            for (var sensor : network.getSensors()) {
                sensor.coveredAlong(y).ifPresent(covered::extend);
            }
            covered.clip(bound);
            if (covered.totalSize() < bound) {
                System.out.printf("Two: %s%n", 4000000 * covered.firstDiscontinuity() + y);
            }
        }
    }
}
