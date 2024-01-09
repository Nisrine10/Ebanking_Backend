package com.example.ebankingbackend.dtos;

import lombok.Data;
@Data
public class TransferRequestDTO {

    private String accountDestination;
    private String accountSource;
    private double amount;
    private  String description;

}
