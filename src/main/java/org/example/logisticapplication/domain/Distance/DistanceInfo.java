package org.example.logisticapplication.domain.Distance;

public interface DistanceInfo {
    static boolean isRecommendDistance(
            Double distance,
            Double recommendedDistance
    ) {
        return distance > recommendedDistance;
    }
}
