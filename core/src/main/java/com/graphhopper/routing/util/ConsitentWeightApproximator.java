package com.graphhopper.routing.util;

import com.graphhopper.storage.NodeAccess;

/**
 * Turns an unidirectional weight Approximation into a bidirectional consistent one.
 * <p/>
 * Ikeda, T., Hsu, M.-Y., Imai, H., Nishimura, S., Shimoura, H., Hashimoto, T., Tenmoku, K., and
 * Mitoh, K. (1994). A fast algorithm for finding better routes by ai search techniques. In VNIS,
 * pages 291–296.
 * <p/>
 *
 * @author Jan Soe
 */
public class ConsitentWeightApproximator {

    private NodeAccess nodeAccess;
    private Weighting weighting;
    private WeightApproximator uniDirectionalApproximator;
    int goalNode, sourceNode;

    public ConsitentWeightApproximator(WeightApproximator weightApprox){
        this.uniDirectionalApproximator = weightApprox;
    }

    public void setSourceNode(int sourceNode){
        this.sourceNode = sourceNode;
    }

    public void setGoalNode(int goalNode){
        this.goalNode = goalNode;
    }

    public double approximate(int fromNode, boolean reverse)    {
        double weightApproximation = 0.5*(uniDirectionalApproximator.approximate(fromNode, goalNode)
                                          - uniDirectionalApproximator.approximate(fromNode, sourceNode));
        if (reverse) {
            weightApproximation *= -1;
        }

        return weightApproximation;
    }
}
