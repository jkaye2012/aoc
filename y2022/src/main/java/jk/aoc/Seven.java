package jk.aoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class Seven {
    @NonNull
    @AllArgsConstructor
    @Getter
    static class Command {
        public enum Type {
            CD,
            LS
        }

        Type type;
        Optional<String> args;
        List<String> result;

        static List<Command> parseCommands(@NonNull List<String> s) {
            List<Command> commands = new ArrayList<>();
            for (int idx = 0; idx < s.size(); idx++) {
                var line = s.get(idx);
                if (line.startsWith("$ cd ")) {
                    var args = line.substring(5);
                    commands.add(new Command(Type.CD, Optional.of(args), new ArrayList<>()));
                } else if (line.startsWith("$ ls")) {
                    List<String> result = new ArrayList<>();
                    while (idx < s.size() - 1 && !s.get(++idx).startsWith("$")) {
                        result.add(s.get(idx));
                    }
                    if (idx != s.size() - 1) {
                        idx--;
                    }
                    commands.add(new Command(Type.LS, Optional.empty(), result));
                } else {
                    throw new UnsupportedOperationException(line);
                }
            }

            return commands;
        }

        public String toString() {
            return String.format("%s: %s", type, args);
        }
    }

    @NonNull
    @AllArgsConstructor
    @Getter
    static class File {
        int size;
        String name;
    }

    @RequiredArgsConstructor
    @NonNull
    @Getter
    static class DirectoryTree {
        private final String name;
        private final Optional<DirectoryTree> parent;
        private Map<String, DirectoryTree> subdirectories = new HashMap<>();
        private List<File> files = new ArrayList<>();

        @NonNull
        public void processLs(List<String> results) {
            for (var result : results) {
                if (result.startsWith("dir")) {
                    addSubdirectory(result);
                } else {
                    addFile(result);
                }
            }
        }

        public int size(Consumer<Integer> accumulator) {
            var subdirSize = subdirectories.values().stream().reduce(0, (s, d) -> s + d.size(accumulator),
                    Integer::sum);
            var fileSize = files.stream().reduce(0, (s, f) -> s + f.getSize(), Integer::sum);

            var size = subdirSize + fileSize;
            accumulator.accept(size);
            return size;
        }

        public DirectoryTree getSubdirectory(@NonNull String name) {
            return getSubdirectories().get(name);
        }

        @NonNull
        private void addSubdirectory(String result) {
            String name = result.substring(4);
            getSubdirectories().put(name, new DirectoryTree(name, Optional.of(this)));
        }

        private void addFile(@NonNull String result) {
            var split = result.split(" ");
            getFiles().add(new File(Integer.parseInt(split[0]), split[1]));
        }
    }

    static class FileSystem {
        DirectoryTree root = new DirectoryTree("/", Optional.empty());
        DirectoryTree current = root;
        DirectoryTree previous = null;

        void process(@NonNull Command cmd) {
            switch (cmd.getType()) {
                case LS:
                    current.processLs(cmd.getResult());
                    break;
                case CD:
                    previous = current;
                    var arg = cmd.getArgs().get();
                    if (arg.equals("/")) {
                        current = root;
                    } else if (arg.equals("..")) {
                        current = current.getParent().get();
                    } else {
                        current = current.getSubdirectory(arg);
                    }
                    break;
            }
        }

        int sumDirectoriesUnder(int limit) {
            @RequiredArgsConstructor
            @Getter
            class Counter {
                int value = 0;
                final int limit;

                void increment(int amt) {
                    if (amt <= limit) {
                        value += amt;
                    }
                }
            }
            var total = new Counter(limit);
            root.size(total::increment);
            return total.getValue();
        }

        int usedSpace() {
            return root.size(i -> {
            });
        }

        int smallestDirectoryOver(int space) {
            @RequiredArgsConstructor
            @Getter
            class Deleter {
                int value = Integer.MAX_VALUE;
                final int threshold;

                public void consume(int sz) {
                    if (sz > threshold && sz < value) {
                        value = sz;
                    }
                }
            }

            var del = new Deleter(space);
            root.size(del::consume);
            return del.getValue();
        }
    }

    public static void solve() {
        List<String> commands = new ArrayList<>();
        InputUtil.getInput(Seven.class, s -> s.useDelimiter("\n"), Function.identity(), commands::add);
        FileSystem fs = new FileSystem();
        Command.parseCommands(commands).forEach(fs::process);
        part1(fs);
        part2(fs);
    }

    static void part1(@NonNull FileSystem fs) {
        System.out.printf("One: %s%n", fs.sumDirectoriesUnder(100000));
    }

    static void part2(@NonNull FileSystem fs) {
        var totalSpace = 70000000;
        var minSpaceRequired = 30000000;
        var spaceToFree = minSpaceRequired - (totalSpace - fs.usedSpace());
        System.out.printf("Two: %s%n", fs.smallestDirectoryOver(spaceToFree));
    }
}
