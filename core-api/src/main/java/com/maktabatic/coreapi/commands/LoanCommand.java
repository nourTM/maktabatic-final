package com.maktabatic.coreapi.commands;

import com.maktabatic.coreapi.model.Book;
import com.maktabatic.coreapi.model.KeyLoanReturn;
import com.maktabatic.coreapi.model.Reader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanCommand {
    @TargetAggregateIdentifier
    private KeyLoanReturn id;
    private Reader reader;
    private Book book;
    private Date dateReturn;
}
