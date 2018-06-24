package com.nlc.dataimporter.repositories;

import com.nlc.dataimporter.model.CarInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by sinanaj on 19/06/2018.
 */
public interface CarInfoRepository extends JpaRepository<CarInfo, Long> {
}
