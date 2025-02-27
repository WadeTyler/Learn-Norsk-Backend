package net.tylerwade.learnnorsk.model.question;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.tylerwade.learnnorsk.model.word.Word;

import java.util.List;

@Entity @Getter @Setter @NoArgsConstructor @ToString
public class Question {

    @Id
    @GeneratedValue
    private int id;
    private String type;
    // TODO: Convert question title List Of words
    private String title;

    @ManyToMany
    @JoinTable(
            name = "question_option",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    @OrderColumn(name = "list_index")
    private List<Word> options;

    @ManyToMany
    @JoinTable(
            name = "question_answer",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    @OrderColumn(name = "list_index")
    private List<Word> answer;

    public Question(String type, String title) {
        this.type = type;
        this.title = title;
    }

    public Question(int id, String type, String title, List<Word> options, List<Word> answer) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.options = options;
        this.answer = answer;
    }
}
