package jk.aoc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

public final class Graph {
    @Getter
    public static final class Edge {
        private final @NonNull Integer connection;
        private final @NonNull Optional<Integer> weight;

        public Edge(@NonNull Integer connection) {
            this.connection = connection;
            this.weight = Optional.empty();
        }

        @NonNull
        public Edge(Integer connection, Integer weight) {
            this.connection = connection;
            this.weight = Optional.of(weight);
        }
    }

    @NoArgsConstructor
    @Getter
    public static final class Vertex {
        private final @NonNull List<Edge> edges = new ArrayList<>();

        public void addEdge(@NonNull Edge edge) {
            edges.add(edge);
        }
    }

    private @NonNull Matrix<Vertex> matrix;

    @NonNull
    public <T> Graph(Matrix<T> matrix, BiFunction<Matrix<T>, Integer, Vertex> converter) {
        List<Vertex> vertices = IntStream.range(0, matrix.size()).mapToObj(i -> converter.apply(matrix, i))
                .collect(Collectors.toList());
        this.matrix = new Matrix<>(matrix.getNumRows(), vertices);
    }

    @NonNull
    public Integer minDistance(Predicate<Edge> pred, Integer start, Integer end) {
        Map<Integer, Integer> distances = new HashMap<>();
        Set<Integer> unvisited = new HashSet<>(IntStream.range(0, matrix.size()).mapToObj(Integer::valueOf).toList());
        var current = Optional.of(start);
        distances.put(start, 0);
        while (current.isPresent()) {
            var currentIdx = current.get();
            var length = distances.get(currentIdx) + 1;
            var currentVertex = matrix.get(currentIdx);
            unvisited.remove(currentIdx);
            for (var edge : currentVertex.getEdges()) {
                if (pred.test(edge) && unvisited.contains(edge.getConnection())) {
                    var known = distances.get(edge.getConnection());
                    if (known == null || length < known) {
                        distances.put(edge.getConnection(), length);
                    }
                }
            }

            current = distances.entrySet().stream()
                    .filter(e -> unvisited.contains(e.getKey()))
                    .min((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()))
                    .map(e -> e.getKey());
        }

        return distances.get(end);
    }

    public void printMatrix() {
        matrix.print();
    }
}
