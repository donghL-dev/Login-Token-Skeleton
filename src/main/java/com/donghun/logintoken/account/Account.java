package com.donghun.logintoken.account;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) @NotNull
    private String email;

    @Column @NotNull
    @JsonIgnore
    private String password;

    @Column
    private String name;

    @Column
    private String picture;
}
