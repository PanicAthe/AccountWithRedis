package com.panicathe.account.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panicathe.account.domain.Account;
import com.panicathe.account.dto.AccountDto;
import com.panicathe.account.dto.CreateAccount;
import com.panicathe.account.dto.DeleteAccount;
import com.panicathe.account.exception.AccountException;
import com.panicathe.account.type.AccountStatus;
import com.panicathe.account.service.AccountService;
import com.panicathe.account.type.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired //? Object Json 변환
    private ObjectMapper objectMapper;

    @Test
    void successCreateAccount() throws Exception { //throws Exception ?
        //given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        //when

        //then
        //MockMvc를 사용하여 POST 요청을 /account 경로로 보냄
        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateAccount.Request(1L, 100L)
                        // 객체를 JSON 문자열로 변환하여 Request Body에 추가
                )))
                //응답 상태 코드가 200 OK인지 검증
                .andExpect(status().isOk())
                ////응답 JSON의 필드들 검증
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }

    @Test
    void successGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());

        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value("3456"))
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }

    @Test
    void successGetAccountsByUserId() throws Exception {
        //given
        List<AccountDto> accountDtos =
                Arrays.asList(
                        AccountDto.builder()
                            .accountNumber("1234567890")
                            .balance(100L).build(),
                        AccountDto.builder()
                                .accountNumber("1234567891")
                                .balance(1000L).build(),
                        AccountDto.builder()
                                .accountNumber("1234567892")
                                .balance(2000L).build()
                );

        given(accountService.getAccountsByUserId(anyLong()))
                .willReturn(accountDtos);

        //when

        //then
        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value(100))
                .andExpect(jsonPath("$[1].accountNumber").value("1234567891"))
                .andExpect(jsonPath("$[1].balance").value(1000))
                .andExpect(jsonPath("$[2].accountNumber").value("1234567892"))
                .andExpect(jsonPath("$[2].balance").value(2000))
                .andExpect(status().isOk());
    }

    @Test
    void successDeleteAccount() throws Exception { //throws Exception ?
        //given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        //when

        //then
        //MockMvc를 사용하여 DELETE 요청을 /account 경로로 보냄
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(1L, "1234567890")
                                // 객체를 JSON 문자열로 변환하여 Request Body에 추가
                        )))
                //응답 상태 코드가 200 OK인지 검증
                .andExpect(status().isOk())
                ////응답 JSON의 필드들 검증
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }

    @Test
    void failGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("계좌가 없습니다."))
                .andExpect(status().isOk());
    }

}