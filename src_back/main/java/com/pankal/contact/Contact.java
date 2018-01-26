package com.pankal.contact;

import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Created by pankal on 12/11/17.
 */


@Table(name="contacts", schema="comm")
@Entity
public class Contact {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	private String code;
	private String legal_name;

	public Contact() {}

	public Contact(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLegal_name() {
		return legal_name;
	}

	public void setLegal_name(String legal_name) {
		this.legal_name = legal_name;
	}


	@Override
	public String toString() {
		return "Item{" +
				"id=" + id +
				", code='" + code + '\'' +
				", legal_name='" + legal_name + '\'' +
				'}';
	}
}
