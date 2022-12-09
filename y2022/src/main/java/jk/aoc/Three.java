package jk.aoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jk.aoc.util.InputUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;

public class Three {
    static int priority(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a' + 1;
        }

        return c - 'A' + 27;
    }

    static Set<Character> toChars(@NonNull String s) {
        return s.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
    }

    @AllArgsConstructor
    @NonNull
    static class Rucksack {
        String contents;

        static Rucksack parseRucksack(@NonNull String s) {
            return new Rucksack(s);
        }

        Set<Character> compartment1() {
            return toChars(contents.substring(0, contents.length() / 2));
        }

        Set<Character> compartment2() {
            return toChars(contents.substring(contents.length() / 2));
        }

        Set<Character> contents() {
            return toChars(contents);
        }

        char commonWithinCompartments() {
            Set<Character> one = compartment1();
            one.retainAll(compartment2());
            return one.iterator().next();
        }

        static char commonWithinGroup(@NonNull List<Rucksack> group) {
            Set<Character> common = group.get(0).contents();
            group.stream().skip(1).forEach(r -> common.retainAll(r.contents()));
            return common.iterator().next();
        }

    }

    public static void solve() {
        List<Rucksack> sacks = new ArrayList<>();
        InputUtil.getInput(Three.class, s -> s.useDelimiter("\n"), Rucksack::parseRucksack, sacks::add);
        part1(sacks);
        part2(sacks);
    }

    static void part1(@NonNull List<Rucksack> sacks) {
        System.out.printf("One: %s%n",
                sacks.stream().map(r -> Three.priority(r.commonWithinCompartments())).reduce(0, Integer::sum));
    }

    static void part2(@NonNull List<Rucksack> sacks) {
        List<List<Rucksack>> groups = new ArrayList<>();
        List<Rucksack> current = null;
        for (int i = 0; i < sacks.size(); i++) {
            if (i % 3 == 0) {
                current = new ArrayList<>();
                groups.add(current);
            }
            current.add(sacks.get(i));
        }
        System.out.printf("Two: %s%n",
                groups.stream().map(r -> Three.priority(Rucksack.commonWithinGroup(r))).reduce(0, Integer::sum));
    }
}
