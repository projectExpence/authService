package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserInfo {// Entity class representing user information

    @Id
    @Column(name = "user_id") // Primary key for UserInfo
    private String userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    // This is the ONLY provider field. It is the correct one.
    @Enumerated(EnumType.STRING)
    @Column(name = "provider") // The @Column annotation is now here.
    private Provider provider;




    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY) // Many-to-many relationship with UserRole
    @JoinTable( // Join table to manage the many-to-many relationship
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"), // Join column for UserInfo
        inverseJoinColumns = @JoinColumn(name = "role_id") // Join column for UserRole
    )

    @Builder.Default
    private Set<UserRole> roles=new HashSet<>(); // Set to hold UserRole entities


    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        UserInfo userInfo = (UserInfo) o;
        return userId.equals(userInfo.userId);
    }
    @Override
    public int hashCode(){
        return userId.hashCode();
    }



}
