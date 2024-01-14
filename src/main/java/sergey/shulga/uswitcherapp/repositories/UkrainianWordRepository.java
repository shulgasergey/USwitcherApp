package sergey.shulga.uswitcherapp.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UkrainianWordRepository extends CrudRepository<Ukrainianword, Long> {
    @Query("SELECT w FROM Ukrainianword w WHERE w.word = :word")
    List<Ukrainianword> findByWord(@Param("word") String word);
}
