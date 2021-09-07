package com.maktabatic.mscmdloanreturn.dao;

import com.maktabatic.coreapi.model.BookState;
import com.maktabatic.mscmdloanreturn.aggregates.LoanRetur;
import com.maktabatic.mscmdloanreturn.entities.KeyLoanReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface LoanReturnRepository extends JpaRepository<LoanRetur, KeyLoanReturn> {
    List<LoanRetur> findLoanReturnById_RrOrderById_DateLoanDesc(String rr);
    List<LoanRetur> findLoanReturnsById_RbOrderById_DateLoanDesc(String rb);
    List<LoanRetur> findLoanReturnsById_State(BookState state);
    List<LoanRetur> findLoanReturnsById_RrAndId_StateOrderById_DateLoanDesc(String rr, BookState state);
}
