package com.scada.ScadaCR.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagValue extends JpaRepository<TagValue, Long> {

}
