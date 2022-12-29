package jk.aoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jk.aoc.util.InputUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

public class TwentyOne {
    @ToString
    @Getter
    static class MonkeyOperation {
        private final @NonNull String a;
        private @NonNull String op;
        private final @NonNull String b;

        public MonkeyOperation(@NonNull String[] s) {
            this.a = s[0];
            this.op = s[1];
            this.b = s[2];
        }
    }

    @ToString
    static class Yell {
        private @NonNull Optional<Long> number = Optional.empty();
        private @NonNull Optional<MonkeyOperation> operation = Optional.empty();
        private boolean shouldReset = false;

        public Yell(long number) {
            this.number = Optional.of(number);
        }

        public Yell(@NonNull MonkeyOperation operation) {
            this.operation = Optional.of(operation);
            this.shouldReset = true;
        }

        public void reset() {
            if (this.shouldReset) {
                this.number = Optional.empty();
            }
        }

        public long resolve(@NonNull MonkeyResolver resolver) {
            if (number.isPresent()) {
                return number.get();
            }

            var op = operation.get();
            var left = resolver.resolve(op.getA());
            var right = resolver.resolve(op.getB());
            long result;
            if (op.getOp().equals("+")) {
                result = left + right;
            } else if (op.getOp().equals("-")) {
                result = left - right;
            } else if (op.getOp().equals("*")) {
                result = left * right;
            } else if (op.getOp().equals("/")) {
                result = left / right;
            } else {
                result = Long.compare(left, right);
            }

            number = Optional.of(result);
            return result;
        }
    }

    @Getter
    @ToString
    static class Monkey {
        private final @NonNull String id;
        private final @NonNull Yell yell;

        public Monkey(@NonNull String id, long number) {
            this.id = id;
            this.yell = new Yell(number);
        }

        public Monkey(@NonNull String id, @NonNull String[] s) {
            this.id = id;
            this.yell = new Yell(new MonkeyOperation(s));
        }

        public static Monkey parseMonkey(@NonNull String s) {
            var id = s.substring(0, 4);
            var split = s.substring(6).split(" ");

            if (split.length == 1) {
                return new Monkey(id, Long.parseLong(split[0]));
            }

            return new Monkey(id, split);
        }
    }

    public static void solve() {
        List<Monkey> monkeys = new ArrayList<>();
        InputUtil.getInput(TwentyOne.class, s -> s.useDelimiter("\n"), Monkey::parseMonkey, monkeys::add);
        part1(monkeys);
        part2Brute();
    }

    static class MonkeyResolver {
        private Map<String, Monkey> monkeys = new HashMap<>();

        public MonkeyResolver(@NonNull List<Monkey> monkeys) {
            for (var m : monkeys) {
                this.monkeys.put(m.getId(), m);
            }
        }

        public long resolve(@NonNull String id) {
            return this.monkeys.get(id).getYell().resolve(this);
        }
    }

    static void part1(@NonNull List<Monkey> monkeys) {
        var resolver = new MonkeyResolver(monkeys);
        System.out.printf("One: %s%n", resolver.resolve("root"));
    }

    static void part2Brute() {
        List<Monkey> monkeys = new ArrayList<>();
        InputUtil.getInput(TwentyOne.class, s -> s.useDelimiter("\n"),
                Monkey::parseMonkey, monkeys::add);
        var resolver = new MonkeyResolver(monkeys);
        resolver.monkeys.get("root").yell.operation.get().op = "=";

        long attempt = 0;
        long result = -1;
        do {
            resolver.monkeys.get("humn").yell.number = Optional.of(attempt++);
            result = resolver.resolve("root");
            monkeys.forEach(m -> m.yell.reset());
        } while (result != 0);

        System.out.printf("Two: %s%n", attempt);

        // 3032671800353
    }
}
