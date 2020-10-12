package com.donghun.logintoken.account;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) @NotNull
    private String email;

    @JsonIgnore
    @Column @NotNull
    private String password;

    @Column
    private String name;

    @Column
    private String picture;
}
