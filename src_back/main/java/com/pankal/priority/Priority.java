package com.pankal.priority;

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


@Table(name="priorities", schema="comm_refer")
@Entity
public class Priority {

	@Id
	private int priority;
	private String display_name;

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	@Override
	public String toString() {
		return "Priority{" +
				"priority=" + priority +
				", display_name='" + display_name + '\'' +
				'}';
	}
}
