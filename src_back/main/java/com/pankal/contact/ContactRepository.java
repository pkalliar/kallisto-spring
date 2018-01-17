package com.pankal.contact;

import com.pankal.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

	@Query("select u from Contact u where LOWER(u.legal_name) like lower(concat('%', :value, '%')) or" +
			" lower(u.code) like lower(concat('%', :value, '%'))")
	List<Contact> findByNameOrCode(@Param("value") String value);

}
