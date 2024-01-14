package sergey.shulga.uswitcherapp.repositories;

import jakarta.persistence.*;

@Entity
@Table(indexes = {@Index(name = "idx_word", columnList = "word")})
public class Englishword {

    @Id
    private Long id;
    private String word;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
