package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, String> {

    @Query("""
            select a from MessageAttachment a
            join fetch a.message m
            join fetch m.sender
            join fetch m.receiver
            where a.id = :id
            """)
    Optional<MessageAttachment> findByIdWithMessage(@Param("id") String id);
}