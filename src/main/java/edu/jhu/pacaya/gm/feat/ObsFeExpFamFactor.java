package edu.jhu.pacaya.gm.feat;

import edu.jhu.pacaya.gm.model.ObsFeatureCarrier;
import edu.jhu.pacaya.gm.model.TemplateFactor;
import edu.jhu.pacaya.gm.model.VarSet;

public class ObsFeExpFamFactor extends ObsCjExpFamFactor implements ObsFeatureCarrier, TemplateFactor {
    
    private static final long serialVersionUID = 1L;
    private ObsFeatureExtractor obsFe;
    // Features cache.
    private FeatureVector feats;
    
    public ObsFeExpFamFactor(VarSet vars, Object templateKey, ObsFeatureConjoiner ofc, ObsFeatureExtractor obsFe) {
        super(vars, templateKey, ofc);
        this.obsFe = obsFe;
    }
    
    @Override
    public FeatureVector getObsFeatures() {
        if (feats == null) {
            feats = this.obsFe.calcObsFeatureVector(this);
        }
        return feats;
    }
    
}