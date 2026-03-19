package com.revworkforce.repository;

import com.revworkforce.entity.Notification;
import com.revworkforce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Long countByUserAndIsReadFalse(User user);
}
