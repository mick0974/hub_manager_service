package com.sch.hub_manager_service.domain.model.persistency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "plugs")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Plug {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PlugType plugType;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charger_id")
    private Charger charger;

     */

    public Plug(String plugType) {
        this.plugType = PlugType.fromType(plugType);
    }
}

