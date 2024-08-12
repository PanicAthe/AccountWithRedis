package com.panicathe.account.service;

import com.panicathe.account.domain.Account;
import com.panicathe.account.domain.AccountUser;
import com.panicathe.account.dto.AccountDto;
import com.panicathe.account.exception.AccountException;
import com.panicathe.account.repository.AccountRepository;
import com.panicathe.account.repository.AccountUserRepository;
import com.panicathe.account.type.AccountStatus;
import com.panicathe.account.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository; //생성자에 포함될 것. Req~생성자로 인해
    private final AccountUserRepository accountUserRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        //사용자 조회
        AccountUser accountUser = getAccountUser(userId);

        validateCreateAccount(accountUser);

        //계좌 생성
        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> String.valueOf(Integer.parseInt(account.getAccountNumber()) + 1))
                .orElse("1000000000"); //최초 생성

        //계좌 저장, 그 정보를 넘김

        return AccountDto.fromEntity(accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()
        ));

    }

    private AccountUser getAccountUser(Long userId) {
        return accountUserRepository.findById(userId)
                .orElseThrow(()-> new AccountException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if(accountRepository.countByAccountUser(accountUser) >= 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_ID);
        }
    }

    @Transactional
    public Account getAccount(Long id) {

        return accountRepository.findById(id).orElse(null);
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        //사용자 조회
        AccountUser accountUser = getAccountUser(userId);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        accountRepository.save(account); //없어도 됨 -> ?

        return AccountDto.fromEntity(account);

    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if(!Objects.equals(accountUser.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance()>0){
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }

    public List<AccountDto> getAccountsByUserId(Long userId) {

        AccountUser accountUser = getAccountUser(userId);

        List<Account> accounts = accountRepository.
                findByAccountUser(accountUser);

        return accounts.stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }
}
