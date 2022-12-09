package jk.aoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import jk.aoc.util.InputUtil;
import jk.aoc.util.Matrix;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class Eight {
    @RequiredArgsConstructor
    @Getter
    static class Tree {
        private final int height;
        private boolean visible = false;
        private int score = 1;

        public int checkVisibility(int currentMax) {
            if (getHeight() > currentMax) {
                this.visible = true;
                return getHeight();
            }

            return currentMax;
        }

        public void updateScore(@NonNull List<Integer> others) {
            int scoreUpdate = 0;
            for (int idx = others.size() - 1; idx >= 0; idx--) {
                var otherHeight = others.get(idx);
                scoreUpdate++;
                if (otherHeight >= getHeight()) {
                    break;
                }
            }
            score *= scoreUpdate;
        }
    }

    public static void solve() {
        List<String> rows = new ArrayList<>();
        InputUtil.getInput(Eight.class, s -> s.useDelimiter("\n"), Function.identity(), rows::add);
        Function<String, List<Tree>> parser = s -> {
            List<Tree> trees = new ArrayList<>();
            for (var c : s.toCharArray()) {
                trees.add(new Tree(Character.getNumericValue(c)));
            }

            return trees;
        };

        var grove = Matrix.parseMatrix(rows, parser);
        part1(grove);
        part2(grove);
    }

    static void checkVisibility(@NonNull Iterator<Tree> it) {
        int currentMax = -1;
        while (it.hasNext()) {
            currentMax = it.next().checkVisibility(currentMax);
        }
    }

    static void updateScores(@NonNull Iterator<Tree> it) {
        List<Integer> heights = new ArrayList<>();
        while (it.hasNext()) {
            var tree = it.next();
            tree.updateScore(heights);
            heights.add(tree.getHeight());
        }
    }

    static void part1(@NonNull Matrix<Tree> grove) {
        for (int row = 0; row < grove.getNumRows(); row++) {
            checkVisibility(grove.getForwardRowIterator(row));
            checkVisibility(grove.getReverseRowIterator(row));
        }
        for (int col = 0; col < grove.getNumCols(); col++) {
            checkVisibility(grove.getForwardColumnIterator(col));
            checkVisibility(grove.getReverseColumnIterator(col));
        }

        System.out.printf("One: %s%n", grove.stream().filter(t -> t.visible).count());
    }

    static void part2(@NonNull Matrix<Tree> grove) {
        for (int row = 0; row < grove.getNumRows(); row++) {
            updateScores(grove.getForwardRowIterator(row));
            updateScores(grove.getReverseRowIterator(row));
        }
        for (int col = 0; col < grove.getNumCols(); col++) {
            updateScores(grove.getForwardColumnIterator(col));
            updateScores(grove.getReverseColumnIterator(col));
        }

        System.out.printf("Two: %s%n", grove.stream().map(Tree::getScore).max(Integer::compare).get());
    }
}
