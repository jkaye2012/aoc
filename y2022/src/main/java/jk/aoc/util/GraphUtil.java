package jk.aoc.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public final class GraphUtil {
    private GraphUtil() {
    }

    @Getter
    @AllArgsConstructor
    public static class ScoredVertex<T> {
        private final @NonNull T vertex;
        private final int score;
    }

    public static <T> Optional<Integer> shortestPath(@NonNull Collection<T> allVertices, @NonNull T start,
            @NonNull T end,
            @NonNull Function<T, List<T>> edgeFunction) {
        return shortestPath(allVertices, start, end, edgeFunction, (v, vv) -> 1);
    }

    public static <T> Optional<Integer> shortestPath(@NonNull Collection<T> allVertices, @NonNull T start,
            @NonNull T end,
            @NonNull Function<T, List<T>> edgeFunction,
            @NonNull BiFunction<T, T, Integer> edgeCostFunction) {
        Map<T, Integer> distances = new HashMap<>();
        Set<T> unvisited = new HashSet<>(allVertices);
        unvisited.remove(start);
        var current = Optional.of(start);
        distances.put(start, 0);

        while (current.isPresent()) {
            var currentVertex = current.get();
            unvisited.remove(currentVertex);
            for (var nextVertex : edgeFunction.apply(currentVertex)) {
                var length = distances.get(currentVertex) + edgeCostFunction.apply(currentVertex, nextVertex);
                if (unvisited.contains(nextVertex)) {
                    var known = distances.get(nextVertex);
                    if (known == null || length < known) {
                        distances.put(nextVertex, length);
                    }
                }
            }

            current = distances.entrySet().stream()
                    .filter(e -> unvisited.contains(e.getKey()))
                    .min((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()))
                    .map(e -> e.getKey());
        }

        return Optional.ofNullable(distances.get(end));
    }
}
