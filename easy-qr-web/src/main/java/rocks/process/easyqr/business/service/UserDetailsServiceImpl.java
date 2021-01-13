/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package rocks.process.easyqr.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rocks.process.easyqr.data.domain.EasyQrUser;
import rocks.process.easyqr.data.repository.EasyQrUserRepository;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private EasyQrUserRepository easyQrUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EasyQrUser easyQrUser = easyQrUserRepository.findByEmail(username);
        if (easyQrUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(easyQrUser.getEmail(), easyQrUser.getPassword(), emptyList());
    }
}
