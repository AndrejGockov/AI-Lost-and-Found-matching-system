package mk.finki.ukim.lost_and_found.repository;

import mk.finki.ukim.lost_and_found.model.domain.Message;
import mk.finki.ukim.lost_and_found.repository.specifications.JpaSpecificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaSpecificationRepository<Message, String> {

    @Query(value = """
            select m from Message m
            join fetch m.sender
            join fetch m.receiver
            where (m.sender.id = :a and m.receiver.id = :b)
               or (m.sender.id = :b and m.receiver.id = :a)
            """,
            countQuery = """
            select count(m) from Message m
            where (m.sender.id = :a and m.receiver.id = :b)
               or (m.sender.id = :b and m.receiver.id = :a)
            """)
    Page<Message> findConversation(@Param("a") String userA,
                                   @Param("b") String userB,
                                   Pageable pageable);

    /** Latest message of every conversation the user takes part in. */
    @Query("""
            select m from Message m
            join fetch m.sender
            join fetch m.receiver
            where (m.sender.id = :userId or m.receiver.id = :userId)
              and m.dateSent = (
                  select max(m2.dateSent) from Message m2
                  where (m2.sender = m.sender and m2.receiver = m.receiver)
                     or (m2.sender = m.receiver and m2.receiver = m.sender))
            order by m.dateSent desc
            """)
    List<Message> findLatestMessagePerConversation(@Param("userId") String userId);

    @Query("""
            select m from Message m
            join fetch m.sender
            join fetch m.receiver
            where m.id = :id
            """)
    Optional<Message> findByIdWithUsers(@Param("id") String id);
}