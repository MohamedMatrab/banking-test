package com.exam_jee.ds.services;

import com.exam_jee.ds.repositories.RoleRepo;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }
}
