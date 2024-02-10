package ru.practicum.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    Page<Area> findAllByOrderById(PageRequest pageRequest);

    Page<Area> findAllByOrderByRadius(PageRequest pageRequest);
}
