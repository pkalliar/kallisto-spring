package com.pankal.inventory;

import com.pankal.inventory.car.VehicleModel;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by pankal on 8/2/18.
 */

public interface ClassifiedAdsRepository extends JpaRepository<ClassifiedAds, Long> {

}
