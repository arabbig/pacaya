package edu.jhu.pacaya.autodiff.tensor;

import java.util.List;

import edu.jhu.pacaya.autodiff.AbstractModule;
import edu.jhu.pacaya.autodiff.Module;
import edu.jhu.pacaya.autodiff.Tensor;
import edu.jhu.pacaya.util.collections.QLists;

/**
 * Sum of all the entries in the tensor.
 * @author mgormley
 */
public class Select extends AbstractModule<Tensor> implements Module<Tensor> {

    private Module<Tensor> modIn;
    private int dim; // In comments below, d.
    private int idx; // In comments below, k.
    
    public Select(Module<Tensor> modIn, int dim, int idx) {
        super(modIn.getAlgebra());
        this.modIn = modIn;
        this.dim = dim;
        this.idx = idx;
    }
    
    /** Foward pass: y[i] = x[j], where j = (i1, i2, ..., i(d-1), k, i(d+1), ..., i(n)) */
    @Override
    public Tensor forward() {
        Tensor x = modIn.getOutput();
        y = x.select(dim, idx);
        return y;
    }

    /** Backward pass: dG/dx_i = dG/dy dy/dx_i = dG/dy */
    @Override
    public void backward() {
        Tensor xAdj = modIn.getOutputAdj();
        xAdj.addTensor(yAdj, dim, idx);
    }

    @Override
    public List<Module<Tensor>> getInputs() {
        return QLists.getList(modIn);
    }

}
