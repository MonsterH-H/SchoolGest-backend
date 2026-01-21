package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Message;
import com.example.schoolgestapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMessage extends JpaRepository<Message, Long> {
    List<Message> findByReceiverOrderBySentAtDesc(User receiver);
    List<Message> findBySenderOrderBySentAtDesc(User sender);
    long countByReceiverAndIsReadFalse(User receiver);
}
