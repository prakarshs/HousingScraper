package com.housing.services;

import com.housing.entities.Properties;

import java.util.List;

public interface HousingService {

    List<Properties> getProperties();
    List<Properties> searchBarProperties();




}
