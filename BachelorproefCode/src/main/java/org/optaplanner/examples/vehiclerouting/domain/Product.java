package org.optaplanner.examples.vehiclerouting.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.examples.vehiclerouting.domain.solver.DepotAngleCustomerDifficultyWeightFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("VrpProduct")
public class Product{
	
    protected boolean needsCleaningIfNotSameProduct;
    protected int cleaningCost;

	public Product() {
    }

    public Product(long id, boolean needsCleaningIfNotSameProduct, int cleaningCost) {
        super();
        this.needsCleaningIfNotSameProduct = needsCleaningIfNotSameProduct;
    }
    
    public int getCleaningCost() {
		return cleaningCost;
	}

	public void setCleaningCost(Integer cleaningCost) {
		this.cleaningCost = cleaningCost;
	}


	public boolean isNeedsCleaningIfNotSameProduct() {
		return needsCleaningIfNotSameProduct;
	}

	public void setNeedsCleaningIfNotSameProduct(boolean needsCleaningIfNotSameProduct) {
		this.needsCleaningIfNotSameProduct = needsCleaningIfNotSameProduct;
	}
}
