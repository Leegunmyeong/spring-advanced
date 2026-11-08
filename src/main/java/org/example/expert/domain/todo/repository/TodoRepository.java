package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // Lv 3: N+1 문제
    // Todo와 연관된 User 엔티티도 함께 조회
    // Todo 엔티티를 수정일 기준 내림차순으로 조회
    // 페이징은 아직 잘 모르겠습니다 사용해보질 못했습니다.
    // @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user ORDER BY t.modifiedAt DESC")
    // 기존 fetch join을 사용한 N+1 문제 해결 방식에서 EntityGraph방식으로 변환
    // 아직 이해 필요합니다
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT t FROM Todo t ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    int countById(Long todoId);
}
