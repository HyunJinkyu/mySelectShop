package com.myselectshop.springcore.repository;

import com.myselectshop.springcore.model.ApiUseTime;
import com.myselectshop.springcore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {

    Optional<ApiUseTime> findByUser(User user);
}
