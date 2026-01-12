package com.sch.hub_manager_service.domain.repository;

import com.sch.hub_manager_service.domain.model.persistency.Charger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, String> {

}
