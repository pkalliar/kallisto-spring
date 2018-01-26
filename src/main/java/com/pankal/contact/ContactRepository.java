package com.pankal.contact;

import com.pankal.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface ContactRepository extends JpaRepository<Contact, Long> {

	@Query("select u from contacts u where LOWER(u.legal_name) like lower(concat('%', :value, '%')) or" +
			" lower(u.code) like lower(concat('%', :value, '%'))")
	List<Contact> findByNameOrCode(@Param("value") String value);

	@Query("select u from contacts u where LOWER(u.legal_name) like lower(concat('%', :value, '%')) or" +
			" lower(u.code) like lower(concat('%', :value, '%'))")
	Stream<Contact> findByCriteria(@Param("value") String value);

}
