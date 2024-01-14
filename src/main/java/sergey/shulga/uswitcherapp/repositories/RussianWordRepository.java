package sergey.shulga.uswitcherapp.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RussianWordRepository extends CrudRepository<Russianword, Long> {
    @Query("SELECT w FROM Russianword w WHERE w.word = :word")
    List<Russianword> findByWord(@Param("word") String word);
}