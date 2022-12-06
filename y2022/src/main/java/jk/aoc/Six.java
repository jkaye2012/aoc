package jk.aoc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import lombok.NonNull;

public class Six {
    public static void solve() {
        List<Character> stream = new ArrayList<>();
        InputUtil.getInput(Six.class, UnaryOperator.identity(), Function.identity(),
                s -> stream.addAll(s.chars().mapToObj(i -> (char) i).collect(Collectors.toList())));
        find(stream, 4);
        find(stream, 14);
    }

    static void find(@NonNull List<Character> stream, int conseq) {
        for (int idx = conseq; idx < stream.size(); idx++) {
            Set<Character> set = new HashSet<>(stream.subList(idx - conseq, idx));
            if (set.size() == conseq) {
                System.out.printf("%s%n", idx);
                return;
            }
        }
    }
}
