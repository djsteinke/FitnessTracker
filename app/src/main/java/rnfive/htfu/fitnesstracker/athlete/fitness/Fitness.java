package rnfive.htfu.fitnesstracker.athlete.fitness;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Fitness {
    private Integer max20Power;
    private List<StressScore> stressScores = new ArrayList<>();
    private Double fitness;
    private Double fatigue;
    private Double form;
    private long id;

    public Fitness() {}

    public Fitness(List<StressScore> stressScores, Double fitness, Double fatigue, Double form, long id) {

        this.stressScores = stressScores;
        this.fitness = fitness;
        this.fatigue = fatigue;
        this.form = form;
        this.id = id;
    }

    public Fitness withMax20Power(Integer max20Power) {
        this.max20Power = max20Power;
        return this;
    }

    public void addStressScore(StressScore stressScore) {
        if (stressScores == null) {
            stressScores = new ArrayList<>();
        }

        int i = 0;
        for (StressScore ss : stressScores) {
            if (ss.equals(stressScore)) {
                break;
            }
            i++;
        }

        if (i == stressScores.size())
            stressScores.add(stressScore);
        else
            stressScores.set(i, stressScore);
    }

    @Getter
    @Setter
    @ToString
    public static class StressScore {
        private long id;
        private Integer pwrStressScore;
        private Integer hrStressScore;

        public StressScore() {}

        public StressScore(long id, Integer pwrStressScore, Integer hrStressScore) {
            this.id = id;
            this.pwrStressScore = pwrStressScore;
            this.hrStressScore = hrStressScore;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof StressScore)) {
                return false;
            }
            StressScore c = (StressScore) o;
            return id == c.id;
        }

        @Override
        public int hashCode() {
            return 5*7 + (int) (id ^ (id >>> 32));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Fitness)) {
            return false;
        }
        Fitness c = (Fitness) o;
        return id == c.id;
    }

    @Override
    public int hashCode() {
        return 3*5 + (int) (id ^ (id >>> 32));
    }

}
