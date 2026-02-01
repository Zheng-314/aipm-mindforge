package com.aipm.mindforge.repository;

import com.aipm.mindforge.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    List<Note> findByUserIdIsNull();

    @Query("SELECT n FROM Note n WHERE n.title LIKE %:keyword% OR n.content LIKE %:keyword%")
    List<Note> searchByKeyword(@Param("keyword") String keyword);
}