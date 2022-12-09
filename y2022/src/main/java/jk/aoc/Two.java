package jk.aoc;

import java.util.ArrayList;
import java.util.List;

import jk.aoc.util.InputUtil;
import lombok.NonNull;

public class Two {
    enum Outcome {
        Win, Loss, Draw;

        int score() {
            switch (this) {
                case Win:
                    return 6;
                case Loss:
                    return 0;
                default:
                    return 3;
            }
        }

        static Outcome parseOutcome(@NonNull String s) {
            switch (s) {
                case "X":
                    return Loss;
                case "Y":
                    return Draw;
                default:
                    return Win;
            }
        }
    }

    enum Hand {
        Rock, Paper, Scissors;

        Outcome play(@NonNull Hand other) {
            if (this == other) {
                return Outcome.Draw;
            }
            switch (this) {
                case Rock:
                    return other == Scissors ? Outcome.Win : Outcome.Loss;
                case Paper:
                    return other == Rock ? Outcome.Win : Outcome.Loss;
                default:
                    return other == Paper ? Outcome.Win : Outcome.Loss;
            }
        }

        Hand satisfy(@NonNull Outcome outcome) {
            if (outcome == Outcome.Draw) {
                return this;
            }
            switch (this) {
                case Rock:
                    return outcome == Outcome.Loss ? Scissors : Paper;
                case Paper:
                    return outcome == Outcome.Loss ? Rock : Scissors;
                default:
                    return outcome == Outcome.Loss ? Paper : Rock;
            }
        }

        int score() {
            switch (this) {
                case Rock:
                    return 1;
                case Paper:
                    return 2;
                default:
                    return 3;
            }
        }

        static Hand parseHand(@NonNull String s) {
            switch (s) {
                case "A":
                case "X":
                    return Rock;
                case "B":
                case "Y":
                    return Paper;
                default:
                    return Scissors;
            }
        }
    }

    record Round1(@NonNull Hand them, @NonNull Hand me) {
        int score() {
            return me.play(them).score() + me.score();
        }

        static Round1 parseRound(@NonNull String s) {
            return new Round1(Hand.parseHand(s.substring(0, 1)), Hand.parseHand(s.substring(2)));
        }
    }

    record Round2(@NonNull Hand them, @NonNull Outcome outcome) {
        int score() {
            return outcome.score() + them.satisfy(outcome).score();
        }

        static Round2 parseRound(@NonNull String s) {
            return new Round2(Hand.parseHand(s.substring(0, 1)), Outcome.parseOutcome(s.substring(2)));
        }
    }

    public static void solve() {
        List<Round1> rounds = new ArrayList<>();
        InputUtil.getInput(Two.class, sc -> sc.useDelimiter("\n"), s -> Round1.parseRound(s), rounds::add);
        part1(rounds);
        List<Round2> rounds2 = new ArrayList<>();
        InputUtil.getInput(Two.class, sc -> sc.useDelimiter("\n"), s -> Round2.parseRound(s), rounds2::add);
        part2(rounds2);
    }

    static void part1(List<Round1> rounds) {
        System.out.println(String.format("One: %s", rounds.stream().mapToInt(Round1::score).sum()));
    }

    static void part2(List<Round2> rounds) {
        System.out.println(String.format("Two: %s", rounds.stream().mapToInt(Round2::score).sum()));
    }
}
