
package com.auth0.samples.authapi.user;

import javax.persistence.*;
import java.util.UUID;
import com.auth0.samples.authapi.UuidConverter;
import org.hibernate.annotations.GenericGenerator;


@Table(name="app_user", schema="public")
@Entity
public class ApplicationUser {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

    private String username;
    private String password;

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
}
