package com.panicathe.account.domain;

import com.panicathe.account.exception.AccountException;
import com.panicathe.account.type.AccountStatus;
import com.panicathe.account.type.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity{

    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;


    public void useBalance(Long amount){
        if(amount>balance){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        balance = balance - amount;
    }

    public void cancelBalance(Long amount){
        if(amount<0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
        balance = balance + amount;
    }
}
