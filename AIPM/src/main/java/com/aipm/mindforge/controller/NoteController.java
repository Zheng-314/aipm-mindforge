package com.aipm.mindforge.controller;

import com.aipm.mindforge.entity.Note;
import com.aipm.mindforge.entity.User;
import com.aipm.mindforge.repository.NoteRepository;
import com.aipm.mindforge.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<?> getAllNotes(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            List<Note> notes;

            if (token != null && token.startsWith("Bearer ")) {
                try {
                    User user = authService.getCurrentUser(token.substring(7));
                    notes = noteRepository.findByUserId(user.getId());
                } catch (Exception e) {
                    notes = noteRepository.findByUserIdIsNull();
                }
            } else {
                notes = noteRepository.findByUserIdIsNull();
            }

            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取笔记失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        try {
            Note note = noteRepository.findById(id).orElse(null);
            if (note == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(note);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取笔记失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Note note,
                                        @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    User user = authService.getCurrentUser(token.substring(7));
                    note.setUser(user);
                } catch (Exception e) {
                    // token无效，笔记没有用户关联
                }
            }

            if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
                String content = note.getContent();
                if (content != null && content.length() > 50) {
                    note.setTitle(content.substring(0, 50) + "...");
                } else if (content != null) {
                    note.setTitle(content);
                } else {
                    note.setTitle("未命名笔记");
                }
            }

            Note savedNote = noteRepository.save(note);
            return ResponseEntity.ok(savedNote);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建笔记失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Note note) {
        try {
            Note existingNote = noteRepository.findById(id).orElse(null);
            if (existingNote == null) {
                return ResponseEntity.notFound().build();
            }

            existingNote.setTitle(note.getTitle());
            existingNote.setContent(note.getContent());
            existingNote.setSourceUrl(note.getSourceUrl());
            existingNote.setKeywords(note.getKeywords());
            existingNote.setDifficultyLevel(note.getDifficultyLevel());
            existingNote.setLearningStatus(note.getLearningStatus());
            existingNote.setAiProcessedData(note.getAiProcessedData());

            Note updatedNote = noteRepository.save(existingNote);
            return ResponseEntity.ok(updatedNote);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新笔记失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        try {
            if (!noteRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            noteRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除笔记失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchNotes(@RequestParam String keyword) {
        try {
            List<Note> notes = noteRepository.searchByKeyword(keyword);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("搜索笔记失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getNoteStats(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Map<String, Object> stats = new HashMap<>();

            if (token != null && token.startsWith("Bearer ")) {
                try {
                    User user = authService.getCurrentUser(token.substring(7));
                    List<Note> userNotes = noteRepository.findByUserId(user.getId());

                    stats.put("total", userNotes.size());
                    stats.put("aiProcessed", userNotes.stream()
                            .filter(note -> note.getAiProcessedData() != null && !note.getAiProcessedData().isEmpty())
                            .count());

                    LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
                    stats.put("recent", userNotes.stream()
                            .filter(note -> note.getUpdatedAt() != null && note.getUpdatedAt().isAfter(sevenDaysAgo))
                            .count());

                    return ResponseEntity.ok(stats);
                } catch (Exception e) {
                    // token无效
                }
            }

            stats.put("total", 0);
            stats.put("aiProcessed", 0);
            stats.put("recent", 0);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取统计失败: " + e.getMessage());
        }
    }
}