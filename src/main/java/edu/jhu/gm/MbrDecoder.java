package edu.jhu.gm;

import edu.jhu.gm.BeliefPropagation.BeliefPropagationPrm;
import edu.jhu.gm.BeliefPropagation.FgInferencerFactory;

/**
 * Minimum Bayes Risk (MBR) decoder for a CRF model.
 * 
 * @author mgormley
 */
public class MbrDecoder {

    public static class MbrDecoderPrm {
        public FgInferencerFactory infFactory = new BeliefPropagationPrm();
        public Loss loss = Loss.ACCURACY;
    }

    public enum Loss {
        // TODO: support other loss functions.
        ACCURACY
    }

    private MbrDecoderPrm prm;
    private VarConfig mbrVarConfig;
    private FeatureVector mbrFeats;

    public MbrDecoder(MbrDecoderPrm prm) {
        this.prm = prm;
    }

    /**
     * Runs inference and computes the MBR variable configuration and the
     * corresponding features that fire.
     * 
     * @param model The input model.
     * @param data The input data.
     */
    // TODO: we should really pass something other than an FgExamples object in
    // since we might not know the true values of the predicted variables.
    public void decode(FgModel model, FgExamples data) {
        if (prm.loss == Loss.ACCURACY) {
            mbrVarConfig = new VarConfig();
            mbrFeats = new FeatureVector();

            for (int i = 0; i < data.size(); i++) {
                FgExample ex = data.get(i);
                FactorGraph fgLatPred = ex.getFgLatPred(model.getParams());
                FeatureCache cacheLatPred = ex.getFeatCacheLatPred();

                // Run inference.
                FgInferencer inf = prm.infFactory.getInferencer(fgLatPred);
                inf.run();

                // Get the MBR configuration of all the latent and predicted
                // variables.
                for (int varId = 0; varId < fgLatPred.getNumVars(); varId++) {
                    Var var = fgLatPred.getVar(varId);
                    Factor marg = inf.getMarginalsForVarId(varId);
                    int argmaxState = marg.getArgmaxConfigId();
                    mbrVarConfig.put(var, argmaxState);
                }

                // Get the features that fire on the MBR variable configuration.
                for (int a = 0; a < fgLatPred.getNumFactors(); a++) {
                    Factor factor = fgLatPred.getFactor(a);
                    VarConfig factorVc = mbrVarConfig.getSubset(factor.getVars());
                    FeatureVector fv = cacheLatPred.getFeatureVector(a, factorVc.getConfigIndex());
                    // We use add here since we want the sum across all factors
                    // and all examples.
                    mbrFeats.add(fv);
                }
            }
        } else {
            throw new RuntimeException("Loss type not implemented: " + prm.loss);
        }
    }

    /** Gets the MBR variable configuration. */
    public VarConfig getMbrVarConfig() {
        return mbrVarConfig;
    }

    /** Gets the features that fire on the MBR variable configuration. */
    public FeatureVector getMbrFeats() {
        return mbrFeats;
    }

}
