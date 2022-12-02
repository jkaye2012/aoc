package jk.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

public class One {
    @Getter
    static class Elf {
        List<Integer> food;

        Elf(@NonNull String line) {
            this.food = Arrays.stream(line.split("\n")).map(Integer::parseInt).toList();
        }

        Integer weight() {
            return food.stream().reduce(0, Integer::sum);
        }
    }

    public static void solve() {
        List<Elf> party = new ArrayList<>();
        InputUtil.getInput(One.class, sc -> sc.useDelimiter("\n\n"), s -> new Elf(s),
                party::add);
        part1(party);
        part2(party);
    }

    static void part1(List<Elf> party) {
        System.out.println(
                String.format("One: %s", party.stream().max(Comparator.comparing(Elf::weight)).get().weight()));
    }

    static void part2(List<Elf> party) {
        party.sort(Comparator.comparing(Elf::weight).reversed());
        System.out.println(
                String.format("Two: %s",
                        party.subList(0, 3).stream().reduce(0, (i, e) -> i + e.weight(), Integer::sum)));
    }

}
