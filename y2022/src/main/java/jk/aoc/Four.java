package jk.aoc;

import java.util.ArrayList;
import java.util.List;

import jk.aoc.util.InputUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public class Four {
    @AllArgsConstructor
    @Getter
    static class Section {
        private int begin;
        private int end;

        static Section parseSection(@NonNull String s) {
            var split = s.split("-");
            return new Section(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }

        int length() {
            return end - begin + 1;
        }
    }

    @AllArgsConstructor
    @Getter
    static class Pair {
        Section first;
        Section second;

        static Pair parsePair(@NonNull String s) {
            var split = s.split(",");
            return new Pair(Section.parseSection(split[0]), Section.parseSection(split[1]));
        }

        int overlapDistance() {
            return Math.max(0,
                    Math.min(first.getEnd(), second.getEnd()) - Math.max(first.getBegin(), second.getBegin()) + 1);
        }

        boolean hasFullOverlap() {
            var dist = overlapDistance();
            return dist == first.length() || dist == second.length();
        }

        boolean hasAnyOverlap() {
            return overlapDistance() > 0;
        }
    }

    static void solve() {
        List<Pair> pairs = new ArrayList<>();
        InputUtil.getInput(Four.class, s -> s.useDelimiter("\n"), Pair::parsePair, pairs::add);
        part1(pairs);
        part2(pairs);
    }

    static void part1(@NonNull List<Pair> pairs) {
        System.out.printf("One: %s%n", pairs.stream().filter(Pair::hasFullOverlap).count());
    }

    static void part2(@NonNull List<Pair> pairs) {
        System.out.printf("Two: %s%n", pairs.stream().filter(Pair::hasAnyOverlap).count());
    }
}
