package sergey.shulga.uswitcherapp.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnglishWordRepository extends CrudRepository<Englishword, Long> {
    @Query("SELECT w FROM Englishword w WHERE w.word = :word")
    List<Englishword> findByWord(@Param("word") String word);
}
