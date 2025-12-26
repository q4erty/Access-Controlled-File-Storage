package com.portfolio.acfs.repository;

import com.portfolio.acfs.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByOwnerIdAndParentIsNull(Long ownerId);
    List<Folder> findByOwnerId(Long ownerId);
    List<Folder> findByParentId(Long parentId);

    @Query("SELECT f FROM Folder f WHERE f.parent.id = :parentId AND f.owner.id = :ownerId")
    List<Folder> findByParentIdAndOwnerId(@Param("parentId") Long parentId, @Param("ownerId") Long ownerId);
}