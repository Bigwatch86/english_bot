package il.nudefingers.bot.database;

/*import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WordRepository extends CrudRepository<Word, Long> {
//public interface WordRepository extends JpaRepository<Word, Long> {
    //@Transactional
    //@Query("SELECT meaning FROM vocabulary ORDER BY RANDOM() LIMIT 1")


    //Word getRandomWord();



    //List<Word> findAllByIdNotNull();
}*/

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;


public interface WordsRepository {
//public interface WordsRepository extends JpaRepository<Word, Long> {
    //List<Word>findAllById(Long id);

    void save(Word word);
}