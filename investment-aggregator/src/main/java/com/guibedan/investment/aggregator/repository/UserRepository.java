package com.guibedan.investment.aggregator.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.guibedan.investment.aggregator.entities.User;

public interface UserRepository extends CrudRepository<User, UUID> {
}
