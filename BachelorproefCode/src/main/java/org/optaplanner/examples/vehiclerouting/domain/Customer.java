/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.vehiclerouting.domain;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.solver.DepotAngleCustomerDifficultyWeightFactory;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;

@PlanningEntity(difficultyWeightFactoryClass = DepotAngleCustomerDifficultyWeightFactory.class)
@XStreamAlias("VrpCustomer")
@XStreamInclude({
        TimeWindowedCustomer.class
})
public class Customer extends AbstractPersistable implements Standstill {

    protected Location location;
    protected int demand;
    
    protected List<Integer> demandArray = new ArrayList<>();
    protected boolean needsCleaning;
    

    // Planning variables: changes during planning, between score calculations.
    protected Standstill previousStandstill;

    // Shadow variables
    protected Customer nextCustomer;
    protected Vehicle vehicle;

    public Customer() {
    }

    public Customer(long id, Location location, List<Integer> demand) {
        super(id);
        this.location = location;
        this.demandArray = demand;
    }
    
    //change this to something like the price for cleaning
    public long getAmountCleaning() {
		return 10;
	}

    @Override
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Integer> getDemandArray() {
        return demandArray;
    }
    
    public int getSpecificDemand(int i) {
    	return demandArray.get(i);
    }
    
    public int getDemand() {
    	return getDemandTotal();
    }
    
    //om de drl te omzeilen, kijk na of score klopt
    public void setDemand(int demand) {
    	List<Integer> list = new ArrayList<>();
    	list.add(demand);
    	setDemandArray(list);
    	this.demand = demand;
    }
    
    
    
    public int getDemandTotal() {
    	int demandTotal = getDemandArray().stream().mapToInt(Integer::intValue).sum();
    	return demandTotal;
    }

    public void setDemandArray(List<Integer> randomList) {
        this.demandArray = randomList;
    }

    @PlanningVariable(valueRangeProviderRefs = { "vehicleRange",
            "customerRange" }, graphType = PlanningVariableGraphType.CHAINED)
    public Standstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(Standstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    @Override
    public Customer getNextCustomer() {
        return nextCustomer;
    }

    @Override
    public void setNextCustomer(Customer nextCustomer) {
        this.nextCustomer = nextCustomer;
    }

    @Override
    @AnchorShadowVariable(sourceVariableName = "previousStandstill")
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceFromPreviousStandstill() {
        if (previousStandstill == null) {
            throw new IllegalStateException("This method must not be called when the previousStandstill ("
                    + previousStandstill + ") is not initialized yet.");
        }
        return getDistanceFrom(previousStandstill);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceFrom(Standstill standstill) {
        return standstill.getLocation().getDistanceTo(location);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceTo(Standstill standstill) {
        return location.getDistanceTo(standstill.getLocation());
    }

    @Override
    public String toString() {
        if (location.getName() == null) {
            return super.toString();
        }
        return location.getName();
    }

}
