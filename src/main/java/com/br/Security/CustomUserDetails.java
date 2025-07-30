// Exemplo: src/main/java/com/br/Security/CustomUserDetails.java
package com.br.Security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    private final Long id;
    private final String userType;
    private final String userName;

    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, String userType, String userName) {
        super(username, password, authorities);
        this.id = id;
        this.userType = userType;
        this.userName = userName;
    }
    public Long getId() { return id; }
    public String getUserType() { return userType; }
    public String getUserName() { return userName; }
}