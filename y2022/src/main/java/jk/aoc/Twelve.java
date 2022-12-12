package jk.aoc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import jk.aoc.util.Graph;
import jk.aoc.util.InputUtil;
import jk.aoc.util.Matrix;
import lombok.NonNull;

public class Twelve {
    static Character real(Character other) {
        if (other == 'S') {
            return 'a';
        }
        if (other == 'E') {
            return 'z';
        }
        return other;
    }

    public static void solve() {
        List<String> rows = new ArrayList<>();
        InputUtil.getInput(Twelve.class, s -> s.useDelimiter("\n"), Function.identity(), rows::add);
        Function<String, List<Character>> parser = s -> s.chars().mapToObj(i -> (char) i).collect(Collectors.toList());

        var matrix = Matrix.parseMatrix(rows, parser);
        var start = matrix.find('S');
        var end = matrix.find('E');
        var possibleStarts = matrix.findAll(c -> c == 'S' || c == 'a');

        BiFunction<Matrix<Character>, Integer, Graph.Vertex> converter = (m, idx) -> {
            var vertex = new Graph.Vertex();
            var me = real(m.get(idx));
            if (idx / m.getNumCols() > 0) {
                var theirIdx = idx - m.getNumCols();
                vertex.addEdge(new Graph.Edge(theirIdx, real(m.get(theirIdx)) - me));
            }
            if (idx / m.getNumCols() < m.getNumRows() - 1) {
                var theirIdx = idx + m.getNumCols();
                vertex.addEdge(new Graph.Edge(theirIdx, real(m.get(theirIdx)) - me));
            }
            if (idx % m.getNumCols() > 0) {
                var theirIdx = idx - 1;
                vertex.addEdge(new Graph.Edge(theirIdx, real(m.get(theirIdx)) - me));
            }
            if (idx % m.getNumCols() < m.getNumCols() - 1) {
                var theirIdx = idx + 1;
                vertex.addEdge(new Graph.Edge(theirIdx, real(m.get(theirIdx)) - me));
            }
            return vertex;
        };
        var graph = new Graph(matrix, converter);
        part1(graph, start, end);
        part2(graph, possibleStarts, end);
    }

    @NonNull
    private static void part1(Graph graph, Integer start, Integer end) {
        System.out.printf("One: %s%n", graph.minDistance(e -> e.getWeight().get() <= 1, start, end));
    }

    @NonNull
    private static void part2(Graph graph, List<Integer> starts, Integer end) {
        System.out.printf("Two: %s%n",
                starts.stream().map(start -> graph.minDistance(e -> e.getWeight().get() <= 1, start, end))
                        .filter(i -> i != null)
                        .min(Integer::compare).get());
    }
}
