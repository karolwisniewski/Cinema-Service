package com.app.persistence.repositories.repository;

import com.app.persistence.model.Seance;
import com.app.persistence.repositories.repository.criteria.SeanceCriteria;
import com.app.persistence.repositories.repository.criteria.SearchCriteria;
import com.app.persistence.repositories.repository.generic.CrudRepository;
import com.app.persistence.views.SeanceView;

import java.util.List;
import java.util.Optional;

public interface SeanceRepository extends CrudRepository<Seance, Integer> {
    List<Seance> findByMovieNameAndCity(String city, String name);
    List<SeanceView> findSeancesMatchingToCriteria(SeanceCriteria criteria);
    List<SeanceView> findSeancesByCriteria(SearchCriteria criteria);
    Optional<Seance> doesExist(Seance seance);

}
