package jk.aoc;

import java.util.ArrayList;
import java.util.List;

import jk.aoc.util.InputUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

public class Twenty {
    @AllArgsConstructor
    @Getter
    @ToString
    static class EncryptedAtom {
        private long value;
        private int mixerIndex;

        public int steps(int mixerSize) {
            Long result = value % (mixerSize - 1);
            return result.intValue();
        }
    }

    public static void solve() {
        List<Long> values = new ArrayList<>();
        InputUtil.getInput(Twenty.class, s -> s.useDelimiter("\n"), Long::parseLong, values::add);
        List<EncryptedAtom> instructions = new ArrayList<>();
        List<EncryptedAtom> mixer = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            var atom = new EncryptedAtom(values.get(i), i);
            instructions.add(atom);
            mixer.add(atom);
        }
        part1(instructions, mixer, true);
        List<EncryptedAtom> instructions2 = new ArrayList<>();
        List<EncryptedAtom> mixer2 = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            var atom = new EncryptedAtom(values.get(i), i);
            instructions2.add(atom);
            mixer2.add(atom);
        }
        part2(instructions2, mixer2);
    }

    static void part1(@NonNull List<EncryptedAtom> instructions, @NonNull List<EncryptedAtom> mixer,
            boolean print) {
        for (var inst : instructions) {
            if (inst.getValue() == 0) {
                continue;
            }
            var steps = inst.steps(mixer.size());
            int curr = inst.mixerIndex;
            if (steps > 0) {
                for (int i = 0; i < steps; i++) {
                    var next = (curr + 1) % mixer.size();
                    if (next == 0) {
                        var back = mixer.size() - 2;
                        while (back > 0) {
                            var swap = mixer.get(back);
                            swap.mixerIndex = curr;
                            mixer.set(curr, swap);
                            curr = back--;
                        }
                        continue;
                    }
                    var swap = mixer.get(next);
                    swap.mixerIndex = curr;
                    mixer.set(curr, swap);
                    curr = next;
                }
            } else if (steps < 0) {
                for (int i = 0; i > steps; i--) {
                    var next = Math.floorMod(curr - 1, mixer.size());
                    if (next == mixer.size() - 1) {
                        var fwd = 1;
                        while (fwd < mixer.size() - 1) {
                            var swap = mixer.get(fwd);
                            swap.mixerIndex = curr;
                            mixer.set(curr, swap);
                            curr = fwd++;
                        }
                        continue;
                    }
                    var swap = mixer.get(next);
                    swap.mixerIndex = curr;
                    mixer.set(curr, swap);
                    curr = next;
                }
            }
            inst.mixerIndex = curr;
            mixer.set(curr, inst);
        }

        if (print) {
            var decrypted = mixer.stream().map(EncryptedAtom::getValue).toList();
            var zeroIndex = decrypted.indexOf(0L);
            var value = decrypted.get((1000 + zeroIndex) % decrypted.size())
                    + decrypted.get((2000 + zeroIndex) % decrypted.size())
                    + decrypted.get((3000 + zeroIndex) % decrypted.size());
            System.out.printf("One: %s%n", value);
        }
    }

    static void part2(@NonNull List<EncryptedAtom> instructions, @NonNull List<EncryptedAtom> mixer) {
        instructions.forEach(i -> i.value *= 811589153);
        for (int it = 0; it < 10; it++) {
            part1(instructions, mixer, false);
        }

        var decrypted = mixer.stream().map(EncryptedAtom::getValue).toList();
        var zeroIndex = decrypted.indexOf(0L);
        var value = decrypted.get((1000 + zeroIndex) % decrypted.size())
                + decrypted.get((2000 + zeroIndex) % decrypted.size())
                + decrypted.get((3000 + zeroIndex) % decrypted.size());
        System.out.printf("Two: %s%n", value);
    }
}
