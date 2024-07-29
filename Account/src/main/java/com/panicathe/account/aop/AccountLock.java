package com.panicathe.account.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AccountLock {
    // AccountLock라는 이름의 어노테이션은 메서드에 적용되어 해당 메서드 실행 시 계정을 잠그는 기능을 구현할 때 사용될 수 있다.

    long tryLockTime() default 5000L;
//    tryLockTime 속성을 통해 잠금을 시도하는 최대 시간을 설정할 수 있다.
//    기본적으로 5000밀리초 동안 잠금을 시도
}
