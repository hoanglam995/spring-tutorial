package com.example.homework.entities;

import com.example.homework.constants.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity extends AbstractBaseEntity {
    @Column(name = "phone", nullable = false, unique = true, updatable = false, length = 11)
    private String phone;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "username", length = 50)
    private String username;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated;

    @Column(name = "status", nullable = false)
    private Integer status;

    @PrePersist
    protected void prePersist() {
        if (isActivated == null) {
            isActivated = false;
        }

        if (status == null) {
            status = UserStatus.NOT_ACTIVATED.getCode();
        }
    }
}
