package com.pankal.inventory.contact;

import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by pankal on 12/11/17.
 */


@Table(name="items", schema="inventory")
@Entity
public class Item {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	private String name;

	public Item() {}

	public Item(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String code) {
		this.name = code;
	}



	@Override
	public String toString() {
		return "Item{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
