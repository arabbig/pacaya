package edu.jhu.pacaya.gm.feat;

import edu.jhu.pacaya.gm.data.UFgExample;
import edu.jhu.pacaya.gm.model.FeExpFamFactor;
import edu.jhu.pacaya.gm.model.VarConfig;
import edu.jhu.pacaya.gm.model.VarSet;

/**
 * For testing only.
 * 
 * Gives (convenient) access to the VarConfig of the factor.
 * 
 * @author mgormley
 */
public abstract class SlowFeatureExtractor implements FeatureExtractor {

    public SlowFeatureExtractor() {
    }

    @Override
    public FeatureVector calcFeatureVector(FeExpFamFactor factor, int configId) {
        VarSet vars = factor.getVars();
        VarConfig varConfig = vars.getVarConfig(configId);
        return calcFeatureVector(factor, varConfig);
    }
    
    public abstract FeatureVector calcFeatureVector(FeExpFamFactor factor, VarConfig varConfig);
}
