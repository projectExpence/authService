package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class UserRole {

    @Id // Primary key annotation for UserRole
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated value for ID
    @Column(name="role_id") // Column name for ID in the database
    private Long roleId;
    @Column(name="role_name",length = 50,nullable = false,unique = true)
    private String roleName;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(roleId, userRole.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }
}
