package jk.aoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

import jk.aoc.Five.Cargo.CargoBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public class Five {
    @Getter
    static class Pallet {
        private Stack<Character> stack = new Stack<>();

        public Pallet(@NonNull List<Character> chars) {
            for (var c : chars) {
                stack.push(c);
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (var c : stack) {
                sb.append(c);
            }
            return sb.toString();
        }
    }

    @AllArgsConstructor
    @Getter
    static class Move {
        private int num;
        private int from;
        private int to;

        static Move parseMove(@NonNull String s) {
            var split = s.split(" ");
            return new Move(Integer.parseInt(split[1]),
                    Integer.parseInt(split[3]) - 1,
                    Integer.parseInt(split[5]) - 1);
        }

        void apply9000(@NonNull List<Pallet> pallets) {
            var fromP = pallets.get(from);
            var toP = pallets.get(to);

            for (int i = 0; i < num; i++) {
                toP.getStack().push(fromP.getStack().pop());
            }
        }

        void apply9001(@NonNull List<Pallet> pallets) {
            var fromP = pallets.get(from);
            var toP = pallets.get(to);
            List<Character> mover = new ArrayList<>();

            for (int i = 0; i < num; i++) {
                mover.add(fromP.getStack().pop());
            }
            for (int i = mover.size() - 1; i >= 0; i--) {
                toP.getStack().push(mover.get(i));
            }
        }
    }

    @AllArgsConstructor
    @NonNull
    @Getter
    static class Cargo {
        private List<Pallet> pallets;
        private List<Move> moves;

        void applyMoves9000() {
            for (var move : getMoves()) {
                move.apply9000(getPallets());
            }
        }

        void applyMoves9001() {
            for (var move : getMoves()) {
                move.apply9001(getPallets());
            }
        }

        static class CargoBuilder {
            private List<List<Character>> palletBuilders = new ArrayList<>();
            private List<Move> moves = new ArrayList<>();
            private boolean built = false;

            void consume(@NonNull String s) {
                if (s.startsWith("move")) {
                    moves.add(Move.parseMove(s));
                } else if (s.contains("[")) {
                    for (int i = 1; i < s.length(); i += 4) {
                        int idx = i / 4;
                        if (palletBuilders.size() <= idx) {
                            palletBuilders.add(new ArrayList<>());
                        }
                        var c = s.charAt(i);
                        if (c != ' ') {
                            palletBuilders.get(idx).add(c);
                        }
                    }
                }

            }

            Cargo build() {
                List<Pallet> pallets = new ArrayList<>();
                for (var builder : palletBuilders) {
                    if (!built) {
                        Collections.reverse(builder);
                    }
                    pallets.add(new Pallet(builder));
                }
                built = true;

                return new Cargo(pallets, moves);
            }

        }
    }

    static void solve() {
        var builder = new CargoBuilder();
        InputUtil.getInput(Five.class, s -> s.useDelimiter("\n"), Function.identity(), builder::consume);
        part1(builder.build());
        part2(builder.build());
    }

    static void part1(@NonNull Cargo cargo) {
        cargo.applyMoves9000();
        System.out.print("One: ");
        for (var p : cargo.getPallets()) {
            System.out.print(p.getStack().peek());
        }
        System.out.println();
    }

    static void part2(@NonNull Cargo cargo) {
        cargo.applyMoves9001();
        System.out.print("Two: ");
        for (var p : cargo.getPallets()) {
            System.out.print(p.getStack().peek());
        }
        System.out.println();

    }
}
