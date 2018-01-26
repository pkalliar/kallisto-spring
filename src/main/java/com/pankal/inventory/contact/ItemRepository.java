package com.pankal.inventory.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface ItemRepository extends JpaRepository<Item, Long> {

//	@Query("select u from items u where LOWER(u.name) like lower(concat('%', :value, '%'))")
//	List<Item> findByNameOrCode(@Param("value") String value);
//
//	@Query("select u from items u where LOWER(u.name) like lower(concat('%', :value, '%'))")
//	Stream<Item> findByCriteria(@Param("value") String value);

}
