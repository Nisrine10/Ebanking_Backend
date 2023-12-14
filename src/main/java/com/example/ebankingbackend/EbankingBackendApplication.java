package com.example.ebankingbackend;

import com.example.ebankingbackend.entities.*;
import com.example.ebankingbackend.enums.AccountStatus;
import com.example.ebankingbackend.enums.OperationType;
import com.example.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;
import com.example.ebankingbackend.repositories.AccountOperationRepository;
import com.example.ebankingbackend.repositories.BankAccountRepository;
import com.example.ebankingbackend.repositories.CustomerRepository;
import com.example.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(EbankingBackendApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {

        return args -> {
            Stream.of("Nisrine","Nour","Amal").forEach(name->{
                Customer customer=new Customer();

                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
               bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomer().forEach(customer -> {
                //pour chaque customer on creer un compte courant et un compte epargne
                try {
                    bankAccountService.saveCurrentBankAccount(90000,90000, customer.getId());
                    bankAccountService.saveSavingBankAccount(120000,5.5, customer.getId());
                    List<BankAccount> bankAccounts = bankAccountService.bankAccountList();
                    for(BankAccount bankAccount:bankAccounts){
                            for(int i=0; i<10 ;i++) {
                                bankAccountService.credit(bankAccount.getId(), 10000 + Math.random() * 120000, "Credit");
                                bankAccountService.debit(bankAccount.getId(),10000+Math.random() * 90000,"Debit");
                            }
                    }
                }catch(CustomerNotFoundException e){
                   e.printStackTrace();
                } catch (BankAccountNotFoundException|BalanceNotSufficientException e) {
                    throw new RuntimeException(e);
                }

            });
            /*
            BankAccount bankAccount=bankAccountRepository.findById("0a66c50e-9cd8-4997-882e-9a399a715263").orElse(null);
            if(bankAccount!=null) {
                System.out.println(bankAccount.getId());
                System.out.println(bankAccount.getBalance());
                System.out.println(bankAccount.getStatus());
                System.out.println(bankAccount.getCreatedAt());
                System.out.println(bankAccount.getCustomer().getName());
                // afficher le nom du compte
                System.out.println(bankAccount.getClass().getSimpleName());

                if (bankAccount instanceof CurrentAccount) {

                    System.out.println("OverDraft => " + ((CurrentAccount) bankAccount).getOverDraft());

                } else if (bankAccount instanceof SavingAccount) {

                    System.out.println("InterestRate => " + ((SavingAccount) bankAccount).getInterestRate());
                }
                //afficher les operations
                bankAccount.getAccountOperations().forEach(op -> {
                    System.out.println("-----------les operations du compte------------");
                    System.out.println(op.getType() + "\t" + op.getAmount() + "\t" + op.getOperationDate());
                });
            }*/
        };
    }


   // @Bean
    //inserer les clients dans la base de donnees_1ere partie du test
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {
        return args -> {
            Stream.of("Nisrine","Nour","Amal").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            //--------------------------Pour chaque client creer Current Account-----------------------------------
            customerRepository.findAll().forEach(cust->{
                CurrentAccount currentAccount=new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*9000);
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

            //--------------------------Pour chaque client creer Saving Account-----------------------------------
                SavingAccount savingAccount=new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());//generer dun id unique
                savingAccount.setBalance(Math.random()*9000);
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(currentAccount);
            });

            //_________________Pour chaque compte on cree des operations---------------------

            bankAccountRepository.findAll().forEach(acc->{
                for(int i=0;i<5;i++){
                    AccountOperation accountOperation=new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT:OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }


            });
        };
    }


}
