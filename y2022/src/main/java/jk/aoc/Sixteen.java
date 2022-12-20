package jk.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Sets;

import jk.aoc.util.GraphUtil;
import jk.aoc.util.InputUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class Sixteen {
    @Getter
    @RequiredArgsConstructor
    static class Valve {
        private final int flowRate;
        private final @NonNull List<String> connections;
    }

    @Getter
    @RequiredArgsConstructor
    static class NetworkPartition {
        private final Set<String> a;
        private final Set<String> b;
    }

    static class ValveNetwork {
        private final Map<String, Valve> valves = new HashMap<>();
        private Map<String, Integer> levelGraph;

        public void parseValve(@NonNull String s) {
            var name = s.substring(6, 8);
            var flowRate = Integer.parseInt(s.substring(s.indexOf('=') + 1, s.indexOf(';')));
            var connections = s.split(", ");
            connections[0] = connections[0].substring(connections[0].length() - 2);
            valves.put(name, new Valve(flowRate, Arrays.asList(connections)));
        }

        public void constructLevels() {
            levelGraph = new HashMap<>();
            for (var from : valves.keySet()) {
                for (var to : valves.keySet()) {
                    levelGraph.put(from + to,
                            GraphUtil.shortestPath(valves.keySet(), from,
                                    to,
                                    e -> valves.get(e).getConnections()).get());
                }
            }
        }

        private int distance(@NonNull String from, @NonNull String to) {
            return levelGraph.get(from + to);
        }

        private int choosePath(int time, int flow, @NonNull String from, @NonNull Set<String> closedValves) {
            var openValves = new HashSet<>(valves.keySet());
            openValves.removeAll(closedValves);
            if (closedValves.isEmpty() || time <= 0) {
                return flow;
            }

            int best = Integer.MIN_VALUE;
            for (var candidate : closedValves) {
                var closed = new HashSet<>(closedValves);
                closed.remove(candidate);
                int dist = distance(from, candidate);
                var flowRate = valves.get(candidate).getFlowRate();
                var pathTime = time - (dist + 1);
                int result;
                if (dist > 0 && pathTime > 0) {
                    result = choosePath(pathTime, flow + pathTime * flowRate, candidate, closed);
                } else {
                    result = flow;
                }
                best = Math.max(result, best);
            }

            return best;
        }

        Set<String> initialValves() {
            Set<String> init = new HashSet<>();
            for (var kvp : valves.entrySet()) {
                if (kvp.getValue().getFlowRate() != 0) {
                    init.add(kvp.getKey());
                }
            }

            return init;
        }

        List<NetworkPartition> partition(int size) {
            List<NetworkPartition> partitions = new ArrayList<>();
            for (var a : Sets.combinations(initialValves(), size)) {
                var b = initialValves();
                b.removeAll(a);
                partitions.add(new NetworkPartition(a, b));
            }

            return partitions;
        }

        public int maximizeFlow() {
            return choosePath(30, 0, "AA", initialValves());
        }

        public int maximizeFlowWithBuddy() {
            var closed = initialValves();
            int flow = Integer.MIN_VALUE;
            for (int partitionIdx = 1; partitionIdx <= closed.size() / 2; partitionIdx++) {
                for (var partition : partition(partitionIdx)) {
                    var result = choosePath(26, 0, "AA", partition.getA())
                            + choosePath(26, 0, "AA", partition.getB());
                    flow = Math.max(flow, result);
                }
            }

            return flow;
        }

    }

    public static void solve() {
        var network = new ValveNetwork();
        InputUtil.getInput(Sixteen.class, s -> s.useDelimiter("\n"), Function.identity(), network::parseValve);
        network.constructLevels();
        part1(network);
        part2(network);
    }

    static void part1(@NonNull ValveNetwork network) {
        System.out.printf("One: %s%n", network.maximizeFlow());
    }

    static void part2(@NonNull ValveNetwork network) {
        System.out.printf("One: %s%n", network.maximizeFlowWithBuddy());
    }
}
