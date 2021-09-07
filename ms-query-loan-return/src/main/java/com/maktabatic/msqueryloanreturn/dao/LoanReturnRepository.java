package com.maktabatic.msqueryloanreturn.dao;

import com.maktabatic.coreapi.model.BookState;
import com.maktabatic.msqueryloanreturn.entities.KeyLoanReturn;
import com.maktabatic.msqueryloanreturn.entities.LoanReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

@RepositoryRestResource
public interface LoanReturnRepository extends JpaRepository <LoanReturn, KeyLoanReturn> {
    Long countLoanReturnsByIdNoticeAndState(Long id, BookState state);
    List<LoanReturn> findLoanReturnsById_RrOrderById_DateLoanDesc(String rr);
    List<LoanReturn> findLoanReturnsByStateAndDateReturnBefore(BookState state,Date date);
}
