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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("VrpVehicle")
public class Vehicle extends AbstractPersistable implements Standstill {

    //protected int capacity;
    protected Depot depot;

    protected List<Compartiment> compartimentsList = new ArrayList<>();

    // Shadow variables
    protected Customer nextCustomer;

    public Vehicle() {
    }

    public Vehicle(long id, /*int capacity,*/ Depot depot, List<Compartiment> compartimentsList) {
        super(id);
        //this.capacity = capacity;
        this.depot = depot;
        this.compartimentsList = compartimentsList;
    }
    
    public int getSpecificCompartimentCapacity(int i) {
    	return compartimentsList.get(i).getCapacity();
    }
    
    public void swapCompartiments(int cOne, int cTwo) {
    	Collections.swap(compartimentsList, cOne, cTwo);
    }
    
    public String getStringCompartimentList() {
    	StringBuilder sb = new StringBuilder();
    	for(Compartiment c : compartimentsList){
    		sb.append(c.getCapacity());
    		sb.append(", \t");
    	}
    	return sb.toString();
    }
    
    public int getAmountOfCompartiments() {
    	return compartimentsList.size();
    }

	public List<Compartiment> getCompartimentsList() {
		return compartimentsList;
	}

	public void setCompartimentsList(List<Compartiment> compartimentsList) {
		this.compartimentsList = compartimentsList;
	}

	public void addToCompartimentsList(Compartiment compartiment) {
		compartimentsList.add(compartiment);		
	}
    
//    public int getCapacity() {
//        return capacity;
//    }
//
//    public void setCapacity(int capacity) {
//        this.capacity = capacity;
//    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    @Override
    public Customer getNextCustomer() {
        return nextCustomer;
    }

    @Override
    public void setNextCustomer(Customer nextCustomer) {
        this.nextCustomer = nextCustomer;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public Vehicle getVehicle() {
        return this;
    }

    @Override
    public Location getLocation() {
        return depot.getLocation();
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceTo(Standstill standstill) {
        return depot.getDistanceTo(standstill);
    }

    @Override
    public String toString() {
        Location location = getLocation();
        if (location.getName() == null) {
            return super.toString();
        }
        return location.getName() + "/" + super.toString();
    }

}
