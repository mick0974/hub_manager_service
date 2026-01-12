package com.sch.hub_manager_service.domain.model.persistency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chargers")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Charger {

    @Id
    private String id;

    //@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @ElementCollection
    private Set<String> plugs = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private ChargerOperationalState chargerOperationalState;
}
