package jk.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jk.aoc.util.InputUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;

public class Ten {
    interface Instruction {
        int cycles();

        void modify(@NonNull Computer comp);

        static Instruction parseInstruction(@NonNull String s) {
            if (s.equals("noop")) {
                return new Noop();
            }

            return new AddX(Integer.parseInt(s.split(" ")[1]));
        }
    }

    @AllArgsConstructor
    static class AddX implements Instruction {
        int value;

        @Override
        public int cycles() {
            return 2;
        }

        @Override
        public void modify(@NonNull Computer comp) {
            comp.x += this.value;
        }
    }

    static class Noop implements Instruction {
        @Override
        public int cycles() {
            return 1;
        }

        @Override
        public void modify(@NonNull Computer comp) {
        }
    }

    static class Computer {
        int clock = 0;
        int x = 1;

        List<Integer> processInstructions(@NonNull List<Instruction> inst,
                @NonNull List<Integer> cyclesToCheck) {
            List<Integer> values = new ArrayList<>();
            int cycleIdx = 0;
            int nextCycle = cyclesToCheck.get(cycleIdx++);

            for (var i : inst) {
                clock += i.cycles();
                if (clock >= nextCycle) {
                    values.add(nextCycle * x);
                    nextCycle = cycleIdx == cyclesToCheck.size() ? Integer.MAX_VALUE : cyclesToCheck.get(cycleIdx++);
                }
                i.modify(this);
            }

            return values;
        }

        void processInstructions(@NonNull List<Instruction> inst) {
            for (var i : inst) {
                for (int cycle = 0; cycle < i.cycles(); cycle++) {
                    draw();
                    clock++;
                }
                i.modify(this);
            }
        }

        void draw() {
            int pixelCheck = clock % 40;
            if (pixelCheck == 0 && clock > 0) {
                System.out.println();
            }
            if (pixelCheck >= x - 1 && pixelCheck <= x + 1) {
                System.out.print("#");
            } else {
                System.out.print(".");
            }
        }
    }

    public static void solve() {
        List<Instruction> inst = new ArrayList<>();
        InputUtil.getInput(Ten.class, s -> s.useDelimiter("\n"), Instruction::parseInstruction, inst::add);
        part1(inst);
        part2(inst);
    }

    static void part1(@NonNull List<Instruction> inst) {
        Computer comp = new Computer();
        var results = comp.processInstructions(inst, Arrays.asList(20, 60, 100, 140, 180, 220));
        System.out.printf("One: %s%n", results.stream().reduce(Integer::sum).get());
    }

    static void part2(@NonNull List<Instruction> inst) {
        Computer comp = new Computer();
        comp.processInstructions(inst);
    }
}
