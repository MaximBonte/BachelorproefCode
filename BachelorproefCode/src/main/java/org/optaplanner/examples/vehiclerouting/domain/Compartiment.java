package org.optaplanner.examples.vehiclerouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("VrpCompartiment")
public class Compartiment {
	protected int capacity;
	protected Vehicle vehicle;
	protected Product product;
	
	public Compartiment() {
		
	}
	
	public Compartiment(Vehicle vehicle, int capacity, Product product) {
		super();
		this.capacity = capacity;
		this.vehicle = vehicle;
		this.product = product;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public Vehicle getVehicle(){
		return vehicle;
	}
	
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}	
}
