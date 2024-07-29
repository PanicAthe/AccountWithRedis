package com.panicathe.account.domain;

import com.panicathe.account.type.AccountStatus;
import com.panicathe.account.type.TransactionResultType;
import com.panicathe.account.type.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;

    @ManyToOne
    private Account account;
    private long amount;
    private Long balanceSnapshot;

    private String transactionId;
    private LocalDateTime transactedAt;

}
