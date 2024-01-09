package com.example.ebankingbackend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AccountHistoryDTO {

    //pour regler la pagination

    private String accountId;
    private double balance;
    private int currentPage;
    private  int totalPages;
    private int pagesize;
    private List<AccountOperationDTO> accountOperationDTOS;
}
