package com.housing.repositories;

import com.housing.entities.Properties;
import com.housing.entities.SearchProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchPropRepo extends JpaRepository<SearchProperty,Integer> {


}
