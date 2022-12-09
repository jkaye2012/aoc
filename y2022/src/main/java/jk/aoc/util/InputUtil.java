package jk.aoc.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import lombok.NonNull;

public class InputUtil {
    private InputUtil() {
    }

    @NonNull
    public static <T, I> void getInput(Class<T> cls, UnaryOperator<Scanner> scanner,
            Function<String, I> parser, Consumer<I> consumer) {
        System.out.println(cls.getSimpleName() + ".txt");
        InputStream stream = cls.getResourceAsStream("/" + cls.getSimpleName() + ".txt");
        try (Scanner sc = scanner.apply(new Scanner(stream, StandardCharsets.UTF_8))) {
            while (sc.hasNext()) {
                I input = parser.apply(sc.next());
                consumer.accept(input);
            }
        }
    }
}
