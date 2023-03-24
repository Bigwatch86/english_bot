package il.nudefingers.bot.database;

import jakarta.persistence.*;
import lombok.*;


import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "word", nullable = false)
    private String meaning;
    @Column
    private String translation;
    private int total;
    private int correct;
}



