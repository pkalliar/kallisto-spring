package com.pankal.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

	@Query("select u from User u where LOWER(u.username) like lower(concat('%', :value, '%')))")
	Stream<User> findByCriteria(@Param("value") String value);

}
