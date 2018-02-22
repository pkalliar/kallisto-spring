
package com.pankal.user;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.pankal.utilities.LocalDateTimeConverter;
import org.hibernate.annotations.GenericGenerator;


@Table(name="users", schema="security")
@Entity
public class User {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

    private String username;
    private String password;
    private String apikey;

	@Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime apikey_expires;

	public User() {}

	public User(UUID id) {
		this.id = id;
	}

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public LocalDateTime getApikey_expires() {
		return apikey_expires;
	}

	public void setApikey_expires(LocalDateTime apikey_expires) {
		this.apikey_expires = apikey_expires;
	}
}
