package edu.jhu.pacaya.parse.cky.chart;

import edu.jhu.pacaya.nlp.data.Sentence;
import edu.jhu.pacaya.parse.cky.CnfGrammar;
import edu.jhu.pacaya.parse.cky.Rule;
import edu.jhu.pacaya.parse.cky.chart.Chart.BackPointer;
import edu.jhu.prim.map.IntDoubleHashMap;
import edu.jhu.prim.map.IntObjectHashMap;

/**
 * Cell that stores every entries in two hash tables.
 * 
 * @author mgormley
 * 
 */
public class DoubleHashChartCell implements ChartCell {

    private IntDoubleHashMap scores;
    private IntObjectHashMap<BackPointer> bps;
    private int[] ntsArray;    
    private boolean isClosed;

    public DoubleHashChartCell(CnfGrammar grammar) {
        scores = new IntDoubleHashMap(16, Double.NEGATIVE_INFINITY);
        bps = new IntObjectHashMap<BackPointer>();

        isClosed = false;
    }

    public void reset(Sentence sentence) {
        scores.clear();
        bps.clear();
        ntsArray = null;
        isClosed = false;
    }
    
    public final void updateCell(int nt, double score, int mid, Rule r) {
        assert(!isClosed);
        
        if (score > scores.get(nt)) {
            scores.put(nt, score);
            bps.put(nt, new BackPointer(r, mid));
        }
    }

    public final BackPointer getBp(int symbol) {
        return bps.get(symbol);
    }
    
    public final double getScore(int symbol) {
        return scores.get(symbol);
    }
    
    public final int[] getNts() {
        if (isClosed) {
            return ntsArray;
        } else {
            return bps.keys();
        }
    }

    private static class DoubleHashScores implements ScoresSnapshot {

        private IntDoubleHashMap scores;

        public DoubleHashScores(IntDoubleHashMap scores) {
            this.scores = scores;
        }

        @Override
        public double getScore(int symbol) {
            return scores.get(symbol);
        }
        
    }
    
    public ScoresSnapshot getScoresSnapshot() {
        return new DoubleHashScores(new IntDoubleHashMap(scores));
    }

    @Override
    public void close() {
        isClosed = true;
        ntsArray = bps.keys();
    }
    
}