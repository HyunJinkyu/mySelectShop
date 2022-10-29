package com.myselectshop.springcore.repository;

import com.myselectshop.springcore.model.Folder;
import com.myselectshop.springcore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUser(User user);
    boolean existsByUserAndName(User user, String name);
}
