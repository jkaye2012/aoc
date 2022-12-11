package jk.aoc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import jk.aoc.util.InputUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class Eleven {
    @RequiredArgsConstructor
    @NonNull
    static class Monkey {
        private final Queue<Long> items;
        private final UnaryOperator<Long> operation;
        private final Predicate<Long> test;
        private final Long trueMonkey;
        private final Long falseMonkey;
        @Getter
        private final Long divisor;
        @Getter
        private long numObservations = 0;

        List<Long> throwItem() {
            if (items.isEmpty()) {
                return new ArrayList<>();
            }

            var item = items.remove();
            item = operation.apply(item);
            item /= 3;
            numObservations++;
            return Arrays.asList(test.test(item) ? trueMonkey : falseMonkey, item);
        }

        List<Long> throwItem(Long worryFactor) {
            if (items.isEmpty()) {
                return new ArrayList<>();
            }

            var item = items.remove();
            item = operation.apply(item) % worryFactor;
            numObservations++;
            return Arrays.asList(test.test(item) ? trueMonkey : falseMonkey, item);
        }

        void catchItem(Long item) {
            items.add(item);
        }

        static Monkey parseMonkey(@NonNull String s) {
            var split = s.split("\n");
            var items = split[1].substring(18).split(", ");
            Queue<Long> queue = new ArrayDeque<>();
            for (var item : items) {
                queue.add(Long.parseLong(item));
            }

            var opStr = split[2].substring(23).split(" ");
            UnaryOperator<Long> operation = i -> {
                var operand = opStr[1].equals("old") ? i : Long.parseLong(opStr[1]);
                return opStr[0].equals("+") ? i + operand : i * operand;
            };

            var testSplit = split[3].split(" ");
            var testTarget = Long.parseLong(testSplit[testSplit.length - 1]);
            Predicate<Long> test = i -> i % testTarget == 0;

            var trueSplit = split[4].split(" ");
            var trueMonkey = Long.parseLong(trueSplit[trueSplit.length - 1]);

            var falseSplit = split[5].split(" ");
            var falseMonkey = Long.parseLong(falseSplit[falseSplit.length - 1]);

            return new Monkey(queue, operation, test, trueMonkey, falseMonkey, testTarget);
        }
    }

    public static void solve() {
        List<Monkey> monkeys = new ArrayList<>();
        InputUtil.getInput(Eleven.class, s -> s.useDelimiter("\n\n"), Monkey::parseMonkey, monkeys::add);
        part1(monkeys);

        List<Monkey> monkeys2 = new ArrayList<>();
        InputUtil.getInput(Eleven.class, s -> s.useDelimiter("\n\n"), Monkey::parseMonkey, monkeys2::add);
        part2(monkeys2);
    }

    static void runRound(@NonNull List<Monkey> monkeys) {
        List<Long> target;
        for (var monkey : monkeys) {
            while (!(target = monkey.throwItem()).isEmpty()) {
                monkeys.get(target.get(0).intValue()).catchItem(target.get(1));
            }
        }
    }

    static void runRound(@NonNull List<Monkey> monkeys, Long worryFactor) {
        List<Long> target;
        for (var monkey : monkeys) {
            while (!(target = monkey.throwItem(worryFactor)).isEmpty()) {
                monkeys.get(target.get(0).intValue()).catchItem(target.get(1));
            }
        }
    }

    static void part1(@NonNull List<Monkey> monkeys) {
        for (int i = 0; i < 20; i++) {
            runRound(monkeys);
        }

        var business = monkeys.stream().map(Monkey::getNumObservations).sorted(Comparator.reverseOrder()).toList();
        System.out.printf("One: %s%n", business.get(0) * business.get(1));
    }

    static void part2(@NonNull List<Monkey> monkeys) {
        var worryFactor = monkeys.stream().map(Monkey::getDivisor).reduce((l1, l2) -> {
            return l1 * l2;
        }).get();

        for (int i = 0; i < 10000; i++) {
            runRound(monkeys, worryFactor);
        }

        var business = monkeys.stream().map(Monkey::getNumObservations).sorted(Comparator.reverseOrder()).toList();
        System.out.printf("Two: %s%n", business.get(0) * business.get(1));
    }
}
