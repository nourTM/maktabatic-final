package com.maktabatic.coreapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor@NoArgsConstructor
public class OperationDTO {
    private String rfidBook;
    private String rfidReader;
}