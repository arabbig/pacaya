package edu.jhu.srl;

import edu.jhu.gm.feat.ObsFeatureConjoiner;
import edu.jhu.gm.model.FgModel;
import edu.jhu.srl.SrlFeatureExtractor.SrlFeatureExtractorPrm;

public class SrlFgModel extends FgModel {

    private static final long serialVersionUID = 5827437917567173421L;
    private CorpusStatistics cs;
    private SrlFeatureExtractorPrm srlFePrm;
    private ObsFeatureConjoiner ofc;
    
    public SrlFgModel(CorpusStatistics cs, ObsFeatureConjoiner ofc, SrlFeatureExtractorPrm srlFePrm) {
        super(ofc.getNumParams());
        this.cs = cs;
        this.ofc = ofc;
        this.srlFePrm = srlFePrm;
    }

    public CorpusStatistics getCs() {
        return cs;
    }

    public SrlFeatureExtractorPrm getSrlFePrm() {
        return srlFePrm;
    }
    
    public ObsFeatureConjoiner getOfc() {
        return ofc;
    }
    
}
