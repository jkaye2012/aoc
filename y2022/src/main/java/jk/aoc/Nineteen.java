package jk.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jk.aoc.util.InputUtil;
import jk.aoc.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class Nineteen {

    @Getter
    @ToString
    @AllArgsConstructor
    static class Blueprint {
        int id;
        int oreRobotRequirement;
        int clayRobotRequirement;
        // ore, clay
        @NonNull
        Pair<Integer> obsidianRobotRequirement;
        // ore, obsidian
        @NonNull
        Pair<Integer> geodeRobotRequirement;

        public static Blueprint parseBlueprint(@NonNull String s) {
            var split = s.split(" ");
            var id = Integer.parseInt(split[1].substring(0, split[1].length() - 1));
            var o = Integer.parseInt(split[6]);
            var c = Integer.parseInt(split[12]);
            var oo = Integer.parseInt(split[18]);
            var oc = Integer.parseInt(split[21]);
            var go = Integer.parseInt(split[27]);
            var gob = Integer.parseInt(split[30]);

            return new Blueprint(id, o, c, new Pair<>(oo, oc), new Pair<>(go, gob));
        }

        public int maxOre() {
            return Arrays
                    .asList(oreRobotRequirement, clayRobotRequirement, obsidianRobotRequirement.getFirst(),
                            geodeRobotRequirement.getFirst())
                    .stream().max(Integer::compare).get();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    static class Robots {
        private int ore = 1;
        private int clay = 0;
        private int obsidian = 0;
        private int geode = 0;

        public Robots snapshot() {
            return new Robots(ore, clay, obsidian, geode);
        }

        public Robots addOre() {
            this.ore++;
            return this;
        }

        public Robots addClay() {
            this.clay++;
            return this;
        }

        public Robots addObsidian() {
            this.obsidian++;
            return this;
        }

        public Robots addGeode() {
            this.geode++;
            return this;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    static class Materials {
        private int ore = 0;
        private int clay = 0;
        private int obsidian = 0;
        private int geode = 0;

        public Materials increment(@NonNull Robots robots) {
            this.ore += robots.getOre();
            this.clay += robots.getClay();
            this.obsidian += robots.getObsidian();
            this.geode += robots.getGeode();
            return this;
        }

        public Materials snapshot() {
            return new Materials(ore, clay, obsidian, geode);
        }

        public boolean tryCreateGeodeRobot(@NonNull Blueprint bp) {
            var req = bp.getGeodeRobotRequirement();
            if (getOre() >= req.getFirst() && getObsidian() >= req.getSecond()) {
                this.ore -= req.getFirst();
                this.obsidian -= req.getSecond();
                return true;
            }

            return false;
        }

        public Materials tryCreateObsidianRobot(@NonNull Blueprint bp) {
            var req = bp.getObsidianRobotRequirement();
            if (getOre() >= req.getFirst() && getClay() >= req.getSecond()) {
                this.ore -= req.getFirst();
                this.clay -= req.getSecond();
                return this;
            }

            return null;
        }

        public Materials tryCreateClayRobot(@NonNull Blueprint bp) {
            var req = bp.getClayRobotRequirement();
            if (getOre() >= req) {
                this.ore -= req;
                return this;
            }

            return null;
        }

        public Materials tryCreateOreRobot(@NonNull Blueprint bp) {
            if (getOre() >= bp.getOreRobotRequirement()) {
                this.ore -= bp.getOreRobotRequirement();
                return this;
            }

            return null;

        }
    }

    static int turnsToFabGeode(@NonNull Blueprint bp, @NonNull Materials mats, @NonNull Robots bots) {
        if (bots.getObsidian() == 0) {
            return Integer.MAX_VALUE;
        }

        var req = bp.getGeodeRobotRequirement();
        var oreTime = Math.ceil((double) (req.getFirst() - mats.getOre()) / bots.getOre());
        var obsTime = Math.ceil((double) (req.getSecond() - mats.getObsidian()) / bots.getObsidian());
        return (int) Math.max(oreTime, obsTime);
    }

    static int turnsToFabObsidian(@NonNull Blueprint bp, @NonNull Materials mats, @NonNull Robots bots) {
        if (bots.getClay() == 0) {
            return Integer.MAX_VALUE;
        }

        var req = bp.getObsidianRobotRequirement();
        var oreTime = Math.ceil((double) (req.getFirst() - mats.getOre()) / bots.getOre());
        var clayTime = Math.ceil((double) (req.getSecond() - mats.getClay()) / bots.getClay());
        return (int) Math.max(oreTime, clayTime);

    }

    static int turnsToFabClay(@NonNull Blueprint bp, @NonNull Materials mats, @NonNull Robots bots) {
        return (int) Math.ceil((double) (bp.getClayRobotRequirement() - mats.getOre()) / bots.getOre());
    }

    @RequiredArgsConstructor
    static class Factory {
        private final @NonNull Blueprint blueprint;

        private int fabricate(int timeRemaining, Materials mats, Robots bots) {
            if (timeRemaining == 0) {
                return mats.getGeode();
            }

            var snapshot = bots.snapshot();
            if (mats.tryCreateGeodeRobot(blueprint)) {
                return fabricate(timeRemaining - 1, mats.snapshot().increment(bots), bots.snapshot().addGeode());
            } else {
                var tryOre = mats.snapshot().tryCreateOreRobot(blueprint);
                var tryClay = mats.snapshot().tryCreateClayRobot(blueprint);
                var tryObs = mats.snapshot().tryCreateObsidianRobot(blueprint);
                var best = Integer.MIN_VALUE;
                if (bots.getOre() < blueprint.maxOre() && tryOre != null) {
                    best = Math.max(best,
                            fabricate(timeRemaining - 1, tryOre.increment(snapshot), bots.snapshot().addOre()));
                }
                if (bots.getClay() < blueprint.getObsidianRobotRequirement().getSecond() && tryClay != null) {
                    best = Math.max(best,
                            fabricate(timeRemaining - 1, tryClay.increment(snapshot), bots.snapshot().addClay()));
                }
                if (bots.getObsidian() < blueprint.getGeodeRobotRequirement().getSecond() && tryObs != null) {
                    best = Math.max(best,
                            fabricate(timeRemaining - 1, tryObs.increment(snapshot), bots.snapshot().addObsidian()));
                }
                if (tryOre == null || tryClay == null || tryObs == null) {
                    best = Math.max(best, fabricate(timeRemaining - 1, mats.snapshot().increment(bots), bots));
                }

                return best;
            }

        }

        public int fabricate(int untilTime) {
            return fabricate(untilTime, new Materials(), new Robots());
        }
    }

    public static void solve() {
        List<Blueprint> blueprints = new ArrayList<>();
        InputUtil.getInput(Nineteen.class, sc -> sc.useDelimiter("\n"), Blueprint::parseBlueprint, blueprints::add);
        part1(blueprints);
        part2(blueprints);
    }

    static void part1(@NonNull List<Blueprint> blueprints) {
        int sum = 0;
        for (var bp : blueprints) {
            var factory = new Factory(bp);
            sum += bp.getId() * factory.fabricate(24);
        }
        System.out.printf("One: %s%n", sum);
    }

    static void part2(@NonNull List<Blueprint> blueprints) {
        int result = 1;
        for (var bp : blueprints.subList(0, 3)) {
            var factory = new Factory(bp);
            result *= factory.fabricate(32);
        }
        System.out.printf("Two: %s%n", result);
    }
}
