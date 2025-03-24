package org.example.backend.repository;

import org.example.backend.model.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
    List<ChildProfile> findByParentId(Long parentId); // pagal tevu id surasti, kad butu galima
}