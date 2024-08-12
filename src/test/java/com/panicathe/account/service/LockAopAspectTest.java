package com.panicathe.account.service;

import com.panicathe.account.dto.UseBalance;
import com.panicathe.account.exception.AccountException;
import com.panicathe.account.type.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {


    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    void loackAndUnlock() throws Throwable {
        //given
        ArgumentCaptor<String> lockArgumentCaptor
                = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<String> unlockArgumentCaptor
                = ArgumentCaptor.forClass(String.class);

        UseBalance.Request request = new UseBalance.Request(123L,
                "1234", 1000L);

        //when
        lockAopAspect.aroundMethod(proceedingJoinPoint, request);
        //then
        verify(lockService, times(1))
                .lock(lockArgumentCaptor.capture());
        verify(lockService, times(1))
                .unlock(unlockArgumentCaptor.capture());

        assertEquals("1234", lockArgumentCaptor.getValue());
        assertEquals("1234", unlockArgumentCaptor.getValue());

    }

    @Test
    void loackAndUnlock_evenIfThrow() throws Throwable {
        //given
        ArgumentCaptor<String> lockArgumentCaptor
                = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<String> unlockArgumentCaptor
                = ArgumentCaptor.forClass(String.class);

        UseBalance.Request request = new UseBalance.Request(123L,
                "1234", 1000L);

        given(proceedingJoinPoint.proceed())
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        //when
        assertThrows(AccountException.class, ()->
                lockAopAspect.aroundMethod(proceedingJoinPoint, request));

        //then
        verify(lockService, times(1))
                .lock(lockArgumentCaptor.capture());
        verify(lockService, times(1))
                .unlock(unlockArgumentCaptor.capture());

        assertEquals("1234", lockArgumentCaptor.getValue());
        assertEquals("1234", unlockArgumentCaptor.getValue());

    }

}