package com.k2.testapp.k2javavulnerableperf.repository;

import com.k2.testapp.k2javavulnerableperf.model.Billionaires;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

@Repository
public interface BillionairesRepository extends CrudRepository<Billionaires, Long> {

    @Query(value = "SELECT * FROM billionaires WHERE first_name = ?1", nativeQuery = true)
    Billionaires getBillionaireByName(@PathVariable String condition);
}