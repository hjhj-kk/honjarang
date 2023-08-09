package com.example.honjarang.domain.videochat.repository;

import com.example.honjarang.domain.videochat.entity.VideoChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoChatParticipantRepository extends JpaRepository <VideoChatParticipant, Long> {

    void deleteByUserId(Long userId);

    @Query(value = "SELECT COUNT(*) FROM video_chat_participant WHERE room_id = :roomId", nativeQuery = true)
    Integer countByRoomId(@Param("roomId") Long roomId);
}
