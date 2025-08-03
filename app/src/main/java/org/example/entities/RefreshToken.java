package org.example.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString(exclude = "userInfo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) // JSON naming strategy
@Table(name = "tokens") // Table name for RefreshToken entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) // Unique constraint for token
    private String token; // The refresh token string
    @Column(nullable = false) // Non-nullable column for expiry time
    private Instant expiryTime; // Expiry time of the token

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id") // Join column to link to UserInfo
    private UserInfo userInfo; // UserInfo entity associated with the refresh token

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

