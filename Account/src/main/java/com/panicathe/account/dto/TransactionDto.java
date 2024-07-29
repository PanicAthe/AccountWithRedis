package com.panicathe.account.dto;

import com.panicathe.account.domain.Account;
import com.panicathe.account.domain.Transaction;
import com.panicathe.account.type.TransactionResultType;
import com.panicathe.account.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

    private TransactionType transactionType;
    private TransactionResultType transactionResultType;
    private String accountNumber;
    private long amount;
    private Long balanceSnapshot;
    private String transactionId;
    private LocalDateTime transactedAt;

    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultType(transaction.getTransactionResultType())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .transactionId(transaction.getTransactionId())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }

}
