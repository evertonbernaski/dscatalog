package com.dscatalog.dscatalog.repositories;

import com.dscatalog.dscatalog.entities.Category;
import com.dscatalog.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
