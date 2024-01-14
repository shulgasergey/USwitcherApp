package sergey.shulga.uswitcherapp.repositories;

import jakarta.persistence.*;

@Entity
@Table(indexes = {@Index(name = "idx_word", columnList = "word")})
public class Russianword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;

    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
