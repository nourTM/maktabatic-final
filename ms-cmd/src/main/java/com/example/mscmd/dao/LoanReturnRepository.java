package com.example.mscmd.dao;

import com.example.mscmd.aggregates.LoanReturn;
import com.maktabatic.coreapi.model.BookState;
import com.maktabatic.coreapi.model.KeyLoanReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface LoanReturnRepository extends JpaRepository<LoanReturn, KeyLoanReturn> {
    List<LoanReturn> findLoanReturnById_RrOrderById_DateLoanDesc(String rr);
    List<LoanReturn> findLoanReturnsById_RbOrderById_DateLoanDesc(String rb);
    List<LoanReturn> findLoanReturnsById_State(BookState state);
    List<LoanReturn> findLoanReturnsById_RrAndId_StateOrderById_DateLoanDesc(String rr, BookState state);
}
