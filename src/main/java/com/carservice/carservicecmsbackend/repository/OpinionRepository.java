package com.carservice.carservicecmsbackend.repository;

import com.carservice.carservicecmsbackend.model.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion,Long> {
}
