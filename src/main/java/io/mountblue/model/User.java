package io.mountblue.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String role;
    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy = "user")
//    @JsonManagedReference
    List<Post> post;
}
