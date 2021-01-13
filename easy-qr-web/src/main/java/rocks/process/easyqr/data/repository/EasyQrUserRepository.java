/*
 * Copyright (c) 2020. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package rocks.process.easyqr.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rocks.process.easyqr.data.domain.EasyQrUser;

@Repository
public interface EasyQrUserRepository extends JpaRepository<EasyQrUser, Long> {
	EasyQrUser findByEmail(String email);
	EasyQrUser findByEmailAndIdNot(String email, Long agentId);
}
