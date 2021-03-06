package edu.jhu.pacaya.gm.decode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.pacaya.autodiff.Module;
import edu.jhu.pacaya.autodiff.Tensor;
import edu.jhu.pacaya.autodiff.TopoOrder;
import edu.jhu.pacaya.autodiff.tensor.ConvertAlgebra;
import edu.jhu.pacaya.autodiff.tensor.ElemMultiply;
import edu.jhu.pacaya.autodiff.tensor.Exp;
import edu.jhu.pacaya.autodiff.tensor.ScalarDivide;
import edu.jhu.pacaya.autodiff.tensor.Select;
import edu.jhu.pacaya.hypergraph.depparse.InsideOutsideDepParse;
import edu.jhu.pacaya.util.collections.QLists;
import edu.jhu.pacaya.util.semiring.Algebra;
import edu.jhu.pacaya.util.semiring.LogSignAlgebra;
import edu.jhu.pacaya.util.semiring.RealAlgebra;

/** 
 * Performs softmax MBR decoding for dependency parsing.
 * 
 * 1. Compute edge weights w_e = exp(p_{\theta}(y_e=1|x) / T)
 * 2. Run inside-outside on w_e to get q_{\theta}^{1/T}(e)
 * 
 * The input marginals to this module is expected to be a tensor containing the edge weights for dependency
 * parsing. The tensor is expected to be an nxn matrix, capable of being converted to EdgeScores
 * internally by EdgeScores.tensorToEdgeScores().
 * 
 * The temperature is given by the first entry of its corresponding tensor.
 * 
 * @author mgormley
 */
public class SoftmaxMbrDepParse extends TopoOrder<Tensor> implements Module<Tensor> {

    private static final Logger log = LoggerFactory.getLogger(SoftmaxMbrDepParse.class);
    
    /**
     * Constructor with a default internal semiring.
     * @param margIn The input marginals.
     * @param temperature Tensor containing the temperature in its first entry.
     */
    public SoftmaxMbrDepParse(Module<Tensor> margIn, Module<Tensor> temperature) {
        this(margIn, temperature, LogSignAlgebra.getInstance());
    }
    
    /**
     * Constructor.
     * @param margIn The input marginals.
     * @param temperature Tensor containing the temperature in its first entry.
     * @param tmpS The semiring used only internally.
     */
    public SoftmaxMbrDepParse(Module<Tensor> margIn, Module<Tensor> temperature, Algebra tmpS) {
        super();
        shallowCopy(build(margIn, temperature, tmpS));
    }
    
    private static TopoOrder<Tensor> build(Module<Tensor> pIn, Module<Tensor> tIn, Algebra tmpS) {
        Algebra outS = pIn.getAlgebra();

        // Internally we use a different algebra (tmpS) to avoid numerical precision problems.
        ConvertAlgebra<Tensor> pIn1 = new ConvertAlgebra<Tensor>(pIn, tmpS);
        ConvertAlgebra<Tensor> tIn1 = new ConvertAlgebra<Tensor>(tIn, tmpS);
        
        ScalarDivide divide = new ScalarDivide(pIn1, tIn1, 0);
        Exp exp = new Exp(divide);
        InsideOutsideDepParse io = new InsideOutsideDepParse(exp);
        
        // Compute marginals
        Select alphas = new Select(io, 0, InsideOutsideDepParse.ALPHA_IDX);
        Select betas = new Select(io, 0, InsideOutsideDepParse.BETA_IDX);
        Select root = new Select(io, 0, InsideOutsideDepParse.ROOT_IDX); // The first entry in this selection is for the root.
        ElemMultiply edgeSums = new ElemMultiply(alphas, betas);
        ScalarDivide marg = new ScalarDivide(edgeSums, root, 0);
        
        ConvertAlgebra<Tensor> conv = new ConvertAlgebra<Tensor>(marg, outS);

        return new TopoOrder<Tensor>(QLists.getList(pIn, tIn), conv);
    }

    public void report() {
        for (Module<? extends Object> mm : this.getTopoOrder()) {
            Module<Tensor> m = (Module<Tensor>) mm;
            System.out.println("Module: " + m.getClass());
            System.out.println("Algebra: " + m.getAlgebra().getClass());
            System.out.println("Output (reals): " + m.getOutput().copyAndConvertAlgebra(RealAlgebra.getInstance()));
            System.out.println("OutputAdj (reals): " + m.getOutputAdj().copyAndConvertAlgebra(RealAlgebra.getInstance()));
            System.out.println("");
        }
    }
    
}
