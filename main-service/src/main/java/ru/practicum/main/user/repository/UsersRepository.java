package ru.practicum.main.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.user.model.User;

import java.util.Collection;

public interface UsersRepository extends JpaRepository<User, Long> {
    @Query("select us " +
            "from User as us " +
            "where (:ids is null or us.id in :ids)")
    Page<User> getUsers(Collection<Long> ids, Pageable pageable);
}