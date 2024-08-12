package com.panicathe.account.controller;

import com.panicathe.account.aop.AccountLock;
import com.panicathe.account.dto.CancelBalance;
import com.panicathe.account.dto.QueryTransactionResponse;
import com.panicathe.account.dto.UseBalance;
import com.panicathe.account.exception.AccountException;
import com.panicathe.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    @AccountLock
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) throws InterruptedException {

       try {
           Thread.sleep(3000L);
           return UseBalance.Response.from(
                   transactionService.useBalance(
                           request.getUserId(), request.getAccountNumber(), request.getAmount()));
       }catch (AccountException e){
           log.error("Failed to use balance. " + e.getMessage());
           transactionService.saveFailedUseTransaction(
                   request.getAccountNumber(),
                   request.getAmount()
           );

           throw e;
       }
    }

    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request){

        try {
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(
                            request.getTransactionId(), request.getAccountNumber(), request.getAmount()));
        }catch (AccountException e){
            log.error("Failed to cancel balance. " + e.getMessage());
            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId){
        return QueryTransactionResponse.from(
                transactionService.queryTransaction(transactionId)
        );
    }
}
