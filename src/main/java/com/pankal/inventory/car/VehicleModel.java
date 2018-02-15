package com.pankal.inventory.car;

import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by pankal on 8/2/18.
 */

@Table(name="vehicle_models", schema="inventory")
@Entity
public class VehicleModel {

	@Id

	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	private UUID id;
	private int model;
	private String name;
	private int make;
	private String make_name;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMake() {
		return make;
	}

	public void setMake(int make) {
		this.make = make;
	}

	public String getMake_name() {
		return make_name;
	}

	public void setMake_name(String make_name) {
		this.make_name = make_name;
	}
}
