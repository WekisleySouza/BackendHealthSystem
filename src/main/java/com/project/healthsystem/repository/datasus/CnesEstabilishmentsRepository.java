package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.cnes.Estabilishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CnesEstabilishmentsRepository extends JpaRepository<Estabilishment, Long>, JpaSpecificationExecutor<Estabilishment> {

}
