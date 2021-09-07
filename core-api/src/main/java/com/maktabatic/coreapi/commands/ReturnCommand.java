package com.maktabatic.coreapi.commands;

import com.maktabatic.coreapi.model.BookState;
import com.maktabatic.coreapi.model.KeyLoanReturn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnCommand {
    @TargetAggregateIdentifier
    private KeyLoanReturn id;
    private Date dateReturnEff;
}
