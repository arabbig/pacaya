package edu.jhu.hltcoe.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.jhu.hltcoe.data.Label;
import edu.jhu.hltcoe.util.Quadruple;
import edu.jhu.hltcoe.util.Triple;

public class FixedIntervalDeltaGenerator extends IdentityDeltaGenerator implements DeltaGenerator {

    private double interval;
    private int numPerSide;
    
    public FixedIntervalDeltaGenerator(double interval, int numPerSide) {
        this.interval = interval;
        this.numPerSide = numPerSide;
    }

    @Override
    public Map<Quadruple<Label, String, Label, String>, Double> getCWDeltas(
            Map<Triple<Label, String, Label>, Double> chooseWeights) {
        
        Map<Quadruple<Label, String, Label, String>, Double> cwDeltas = super.getCWDeltas(chooseWeights); 

        for (Entry<Triple<Label,String,Label>,Double> entry : chooseWeights.entrySet()) {
            double weight = entry.getValue();
            for (int i = -numPerSide; i<=numPerSide; i++) {
                if (i == 0) {
                    // Don't generate the identity delta
                    continue;
                }
                String deltaId = "add" + interval * i;
                Quadruple<Label, String, Label, String> key = getDeltaKey(entry.getKey(), deltaId);
                double newWeight = weight + interval * i;
                // Only keep valid probabilities
                if (newWeight <= 1.0 && newWeight >= 0.0) {
                    cwDeltas.put(key, newWeight);
                }
            }            
        }        
        return cwDeltas;
    }

}
