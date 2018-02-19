package com.pankal.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface PersonRepository extends JpaRepository<Person, Long> {

	Person findByFirstname(String firstname);

	@Query("select u from Person u where LOWER(u.firstname) like lower(concat('%', :value, '%')))")
	Stream<Person> findByCriteria(@Param("value") String value);

}
