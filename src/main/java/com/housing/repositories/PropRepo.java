package com.housing.repositories;

import com.housing.entities.Properties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PropRepo extends JpaRepository<Properties,Integer> {

    Optional<Properties> findByPropertyName(String propertyName);

}
