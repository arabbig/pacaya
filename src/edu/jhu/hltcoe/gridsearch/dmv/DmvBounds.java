package edu.jhu.hltcoe.gridsearch.dmv;

import java.util.Arrays;

import edu.jhu.hltcoe.util.Utilities;

public class DmvBounds {
    
    private double[][] lbs;
    private double[][] ubs;

    public DmvBounds(IndexedDmvModel idm) {
        lbs = new double[idm.getNumConds()][];
        ubs = new double[idm.getNumConds()][];
        for (int c=0; c<lbs.length; c++) {
            lbs[c] = new double[idm.getNumParams(c)];
            ubs[c] = new double[lbs[c].length];
            // Lower bound by log(1 / (one trillion)) ~= -27 
            Arrays.fill(lbs[c], Utilities.log(Math.pow(10,-12)));
            Arrays.fill(ubs[c], 0.0);
        }
    }

    public double getLb(int c, int m) {
        return lbs[c][m];
    }

    public double getUb(int c, int m) {
        return ubs[c][m];
    }

    public void set(int c, int m, double newLb, double newUb) {
        lbs[c][m] = newLb;
        ubs[c][m] = newUb;
    }

}
