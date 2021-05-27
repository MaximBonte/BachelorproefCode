/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Product;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;

public class VehicleRoutingEasyScoreCalculator
        implements EasyScoreCalculator<VehicleRoutingSolution, HardSoftLongScore> {

    @Override
    public HardSoftLongScore calculateScore(VehicleRoutingSolution solution) {
        boolean timeWindowed = solution instanceof TimeWindowedVehicleRoutingSolution;
        List<Customer> customerList = solution.getCustomerList();
        List<Vehicle> vehicleList = solution.getVehicleList();
        
        List<Integer> vehicleCurrentAmountList = new ArrayList<>();
        
        Map<Vehicle, List<Integer>> vehicleDemandMap = new HashMap<>(vehicleList.size());
        for (Vehicle vehicle : vehicleList) {
        	List<Integer> tempList = new ArrayList<>();
        	for(int x = 0; x < vehicle.getAmountOfCompartiments(); x++) {
        		tempList.add(0);
        	}
            vehicleDemandMap.put(vehicle, tempList);
        }
        long hardScore = 0L;
        long softScore = 0L;
        for (Customer customer : customerList) {
            Standstill previousStandstill = customer.getPreviousStandstill();
            if (previousStandstill != null) {
                Vehicle vehicle = customer.getVehicle();
                vehicleCurrentAmountList = vehicleDemandMap.get(vehicle);
                for(int x = 0; x < vehicle.getAmountOfCompartiments(); x++) {
                	vehicleCurrentAmountList.set(x, vehicleDemandMap.get(vehicle).get(x) 
            				+ customer.getSpecificDemand(x));
            	}
                vehicleDemandMap.put(vehicle, vehicleCurrentAmountList);
                // Score constraint distanceToPreviousStandstill
                softScore -= customer.getDistanceFromPreviousStandstill();
                if (customer.getNextCustomer() == null) {
                    // Score constraint distanceFromLastCustomerToDepot
                    softScore -= customer.getLocation().getDistanceTo(vehicle.getLocation());
                }
                if (timeWindowed) {
                    TimeWindowedCustomer timeWindowedCustomer = (TimeWindowedCustomer) customer;
                    long dueTime = timeWindowedCustomer.getDueTime();
                    Long arrivalTime = timeWindowedCustomer.getArrivalTime();
                    if (dueTime < arrivalTime) {
                        // Score constraint arrivalAfterDueTime
                        hardScore -= (arrivalTime - dueTime);
                    }
                }
            }
        }
        for (Map.Entry<Vehicle, List<Integer>> entry : vehicleDemandMap.entrySet()) {
        	int capacity = 0;
        	int demand = 0;
        	for(int x = 0; x < entry.getValue().size(); x++) {
        		capacity = entry.getKey().getSpecificCompartimentCapacity(x);
                demand = entry.getValue().get(x);
                                
                if (demand > capacity) {
                    // Score constraint vehicleCapacity
                	if(x <  entry.getKey().getAmountOfCompartiments()) {
                    	for(int y = x+1; y < entry.getKey().getAmountOfCompartiments(); y++) {
                    		int demandOther = entry.getValue().get(y);
                    		int capacityOther = entry.getKey().getSpecificCompartimentCapacity(y);
                    		if(demand <= capacityOther && demandOther <= capacity) {
                    			if(capacityOther-demand < capacity -demandOther) {
                    				System.out.println("Switched compartiments");
                    				entry.getKey().swapCompartiments(x,y);
                    				solution.setAmountSwitched(solution.getAmountSwitched() + 1);
                    				if(vehicleCurrentAmountList.stream().mapToInt(Integer::intValue).sum() != 0) {
                    					List<Product> productList = solution.getProductList();
                    					//Score constraint cleaningCost
                    					softScore -= (productList.get(x).getCleaningCost());
                    					System.out.printf("Amount cleaned: %d%n",solution.getAmountSwitched());
                    				}
                        			break;
                    			}
                    		}
                    	}
                    }
                    if(x == entry.getKey().getAmountOfCompartiments()) {
                    	for(int y = x-1; y >= 0; y--) {
                    		int demandOther = entry.getValue().get(y);
                    		int capacityOther = entry.getKey().getSpecificCompartimentCapacity(y);
                    		if(demand <= capacityOther && demandOther <= capacity) {
                    			entry.getKey().swapCompartiments(x,y);
                    			break;
                    		}
                    	}
                    }
                    if (demand > capacity) {
                    hardScore -= (demand - capacity);
                    }
                }
        	}
        }
        
        //Hard constraint: Elk product moet worden geleverd/opgehaald.
        int totalCustomers = 0;
        for (Customer customer : customerList) {
        	totalCustomers += customer.getDemandTotal();
        }
        
        int totalVehicles = 0;
        for (Map.Entry<Vehicle, List<Integer>> entry : vehicleDemandMap.entrySet()) {
        	totalVehicles += entry.getValue().stream().mapToInt(Integer::intValue).sum();
        }
        hardScore -= (totalCustomers - totalVehicles); 

        return HardSoftLongScore.of(hardScore, softScore);
    }

}
