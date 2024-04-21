package com.example.bankservice.service;

import com.example.bankservice.client.EmailServiceClient;
import com.example.bankservice.client.UserServiceClient;
import com.example.bankservice.domain.dto.transaction.ConfirmPaymentTransactionDto;
import com.example.bankservice.domain.dto.transaction.PaymentTransactionActivationDto;
import com.example.bankservice.domain.dto.transaction.PaymentTransactionDto;
import com.example.bankservice.domain.mapper.TransactionMapper;
import com.example.bankservice.domain.model.Transaction;
import com.example.bankservice.domain.model.accounts.Account;
import com.example.bankservice.domain.model.accounts.CompanyAccount;
import com.example.bankservice.domain.model.accounts.UserAccount;
import com.example.bankservice.domain.model.enums.TransactionStatus;
import com.example.bankservice.domain.model.enums.TransactionType;
import com.example.bankservice.repository.AccountRepository;
import com.example.bankservice.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {

    private final UserServiceClient userServiceClient;
    private final EmailServiceClient emailServiceClient;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private TransactionRepository transactionRepository;
    private AccountService accountService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void startPaymentTransaction(PaymentTransactionDto paymentTransactionDto) {
        Account accountFrom = accountRepository.findByAccountNumber(paymentTransactionDto.getAccountFrom())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Account accountTo = accountRepository.findByAccountNumber(paymentTransactionDto.getAccountTo())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!accountService.checkBalance(paymentTransactionDto.getAccountFrom(), paymentTransactionDto.getAmount())) {
            throw new RuntimeException("Insufficient funds");
        }

        if (accountFrom.getCurrency().getMark().equals(accountTo.getCurrency().getMark())) {
            startSameCurrencyPaymentTransaction(paymentTransactionDto, accountFrom, accountTo);
        } else {
            throw new RuntimeException("Different currency transactions are not supported");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void confirmPaymentTransaction(ConfirmPaymentTransactionDto confirmPaymentTransactionDto) {
        Transaction transaction = transactionRepository.findById(confirmPaymentTransactionDto.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (transaction.getTransactionStatus().equals(TransactionStatus.PENDING)) {
            acceptTransaction(transaction);
            Account accountFrom = accountRepository.findByAccountNumber(transaction.getAccountFrom())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            accountService.reserveFunds(accountFrom, BigDecimal.valueOf(transaction.getAmount()));
        } else {
            throw new RuntimeException("Transaction already completed");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void startCurrencyExchangeTransaction() {

    }

    private void startSameCurrencyPaymentTransaction(PaymentTransactionDto paymentTransactionDto,
                                                     Account accountFrom,
                                                     Account accountTo) {
        Transaction transaction = transactionMapper.paymentTransactionDtoToTransaction(paymentTransactionDto);
        transaction.setDate(System.currentTimeMillis());
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setType(TransactionType.PAYMENT_TRANSACTION);
        transaction = transactionRepository.save(transaction);

        String email = (accountFrom instanceof UserAccount) ?
                userServiceClient.getEmailByUserId(String.valueOf(((UserAccount) accountFrom).getUserId())).getEmail() :
                userServiceClient.getEmailByCompanyId(String.valueOf(((CompanyAccount) accountFrom).getCompanyId()));

        emailServiceClient.sendTransactionActivationEmailToEmailService(new PaymentTransactionActivationDto(email,
                transaction.getTransactionId()));
    }

    @Scheduled(fixedRate = 30000) // Postavljanje cron izraza da se metoda izvrsava svakih 5 minuta
    public void processTransactions() {

        Optional<List<Transaction>> optionalTransactions = transactionRepository.findAllByTransactionStatus(TransactionStatus.ACCEPTED);
        List<Transaction> transactions;
        if (!optionalTransactions.isPresent()) return;
        transactions = optionalTransactions.get();
        for (Transaction transaction : transactions) {
            finishTransaction(transaction);
        }
    }

    private void finishTransaction(Transaction transaction) {
        Account accountFrom = accountRepository.findByAccountNumber(transaction.getAccountFrom())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Account accountTo = accountRepository.findByAccountNumber(transaction.getAccountTo())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        accountService.transferFunds(accountFrom, accountTo, BigDecimal.valueOf(transaction.getAmount()));
        transaction.setTransactionStatus(TransactionStatus.FINISHED);
        transactionRepository.save(transaction);
    }

    private void acceptTransaction(Transaction transaction) {
        transaction.setTransactionStatus(TransactionStatus.ACCEPTED);
        transactionRepository.save(transaction);
    }
}
