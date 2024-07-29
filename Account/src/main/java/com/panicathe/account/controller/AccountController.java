package com.panicathe.account.controller;

import com.panicathe.account.domain.Account;
import com.panicathe.account.dto.AccountInfo;
import com.panicathe.account.dto.CreateAccount;
import com.panicathe.account.dto.DeleteAccount;
import com.panicathe.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
    //컨트롤러에서는 서비스만 의존하도록
    private final AccountService accountService;


    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request)
    {
        return CreateAccount.Response.from(
                accountService.createAccount(
                    request.getUserId(),
                    request.getInitialBalance()
                )
        );
    }

    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request)
    {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }



    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable long id) {
        return accountService.getAccount(id);
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(
            @RequestParam("user_id") Long userId){
        return accountService.getAccountsByUserId(userId)
                .stream().map(accountDto ->
                        AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance()).build())
                .collect(Collectors.toList());
    }

}
