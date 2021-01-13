/*
 * Copyright (c) 2020. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package rocks.process.easyqr.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rocks.process.easyqr.data.domain.EasyQrCode;
import rocks.process.easyqr.data.domain.EasyQrUser;

import java.util.List;

@Repository
public interface EasyQrCodeRepository extends JpaRepository<EasyQrCode, Long> {
	EasyQrCode findByQrURLContent(String qrURLContent);
}