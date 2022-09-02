package com.wyz.repository;

import com.wyz.data.TaskDemo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<TaskDemo, Integer> {

    @Query(value = "SELECT * FROM BackendShoppingDB.taskDemo WHERE isValid = TRUE", nativeQuery = true)
    List<TaskDemo> findValidTask();

    @Query(value = "UPDATE BackendShoppingDB.taskDemo set isValid = FALSE WHERE id = :id", nativeQuery = true)
    @Transactional
    @Modifying
    void setTaskInvalid(@Param("id") Integer id);
}
