
package com.br.Security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    private final Long id;
    private final String userType;
    private final String userName;
    private final String email; // <-- NOVO CAMPO ADICIONADO

    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, String userType, String userName) {
        // A superclasse User usa o 'username' como email, que é a primeira string passada
        super(username, password, authorities);
        this.id = id;
        this.userType = userType;
        this.userName = userName;
        this.email = username; // <-- ATRIBUINDO O EMAIL AQUI
    }

    public Long getId() { return id; }
    public String getUserType() { return userType; }
    public String getUserName() { return userName; }


    public String getEmail() {
        return email;
    }
}