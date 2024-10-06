package com.nextPick.synonyms.repository;

import com.nextPick.synonyms.entity.Synonyms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SynonymsRepository extends JpaRepository<Synonyms, Long> {
    Optional<Synonyms> findByWord(String word);
}
