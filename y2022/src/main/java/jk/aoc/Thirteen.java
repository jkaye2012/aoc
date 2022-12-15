package jk.aoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import jk.aoc.util.InputUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class Thirteen {
    enum Order {
        UNDETERMINED,
        INCORRECT,
        CORRECT
    }

    @AllArgsConstructor
    static class Packet implements Comparable<Packet> {
        private @NonNull List<PacketValue> values;

        Packet(@NonNull Integer value) {
            values = new ArrayList<>();
            values.add(new PacketValue(value));
        }

        Packet(@NonNull PacketValue value) {
            values = new ArrayList<>();
            values.add(value);
        }

        int size() {
            return values.size();
        }

        Order relativeTo(@NonNull Packet other) {
            for (int i = 0; i < values.size(); i++) {
                if (i == other.size()) {
                    return Order.INCORRECT;
                }

                var me = values.get(i);
                var them = other.values.get(i);
                var order = me.relativeTo(them);
                if (order != Order.UNDETERMINED) {
                    return order;
                }
            }

            if (other.size() > size()) {
                return Order.CORRECT;
            }

            return Order.UNDETERMINED;
        }

        static Packet parsePacket(@NonNull String s) {
            Stack<List<PacketValue>> stack = new Stack<>();
            stack.add(new ArrayList<>());
            for (int i = 1; i < s.length() - 1; i++) {
                var c = s.charAt(i);
                switch (c) {
                    case '[':
                        stack.add(new ArrayList<>());
                        break;
                    case ']':
                        var packet = new Packet(stack.pop());
                        stack.peek().add(new PacketValue(packet));
                        break;
                    case ',':
                        break;
                    default:
                        StringBuilder sb = new StringBuilder();
                        sb.append(c);
                        while (Character.isDigit(s.charAt(i + 1))) {
                            sb.append(s.charAt(++i));
                        }
                        stack.peek().add(new PacketValue(Integer.parseInt(sb.toString())));
                        break;
                }
            }

            return new Packet(stack.pop());
        }

        @Override
        public int compareTo(Packet arg0) {
            switch (this.relativeTo(arg0)) {
                case CORRECT:
                    return -1;
                case INCORRECT:
                    return 1;
                default:
                    return 0;
            }
        }
    }

    static class PacketValue {
        Optional<Integer> intValue = Optional.empty();
        Optional<Packet> packetValue = Optional.empty();

        PacketValue(@NonNull Integer intValue) {
            this.intValue = Optional.of(intValue);
        }

        PacketValue(@NonNull Packet packetValue) {
            this.packetValue = Optional.of(packetValue);
        }

        boolean isInt() {
            return intValue.isPresent();
        }

        boolean isPacket() {
            return packetValue.isPresent();
        }

        Packet asPacket() {
            return isPacket() ? packetValue.get() : new Packet(intValue.get());
        }

        Order relativeTo(@NonNull PacketValue other) {
            if (isInt() && other.isInt()) {
                switch (Integer.compare(intValue.get(), other.intValue.get())) {
                    case -1:
                        return Order.CORRECT;
                    case 1:
                        return Order.INCORRECT;
                    default:
                        return Order.UNDETERMINED;
                }
            }

            return asPacket().relativeTo(other.asPacket());
        }
    }

    @RequiredArgsConstructor
    static class PacketPair {
        private final @NonNull Packet left;
        private final @NonNull Packet right;

        boolean isOrderCorrect() {
            return left.relativeTo(right) == Order.CORRECT;
        }

        static PacketPair parsePacketPair(@NonNull String s) {
            var split = s.split("\n");
            return new PacketPair(Packet.parsePacket(split[0]), Packet.parsePacket(split[1]));
        }
    }

    public static void solve() {
        List<PacketPair> pairs = new ArrayList<>();
        InputUtil.getInput(Thirteen.class, s -> s.useDelimiter("\n\n"), PacketPair::parsePacketPair, pairs::add);
        part1(pairs);

        List<Packet> packets = new ArrayList<>();
        InputUtil.getInput(Thirteen.class, s -> s.useDelimiter("\n\n"), PacketPair::parsePacketPair, pair -> {
            packets.add(pair.left);
            packets.add(pair.right);
        });
        part2(packets);
    }

    static void part1(@NonNull List<PacketPair> pairs) {
        int result = 0;
        for (int i = 0; i < pairs.size(); i++) {
            if (pairs.get(i).isOrderCorrect()) {
                result += i + 1;
            }
        }
        System.out.printf("One: %s%n", result);
    }

    static Packet createDelimiter(@NonNull Integer value) {
        return new Packet(new PacketValue(new Packet(value)));
    }

    static void part2(@NonNull List<Packet> packets) {
        var delim2 = createDelimiter(2);
        var delim6 = createDelimiter(6);
        packets.add(delim2);
        packets.add(delim6);
        Collections.sort(packets);

        var idx2 = packets.indexOf(delim2) + 1;
        var idx6 = packets.indexOf(delim6) + 1;
        System.out.printf("Two: %s%n", idx2 * idx6);
    }
}
