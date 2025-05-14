package com.fisa.appcard.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;


/**
 * OpenFeign 관련 설정을 활성화하는 구성 클래스입니다.
 * <p>
 * 주요 역할:
 * - Spring Cloud OpenFeign을 사용 가능하게 만듭니다.
 * - 지정한 패키지 내의 FeignClient 인터페이스들을 스캔하여 Bean으로 등록합니다.
 *
 * @EnableFeignClients 설명:
 * - basePackages 속성을 통해 FeignClient 인터페이스들이 위치한 패키지를 지정합니다.
 * - "com.fisa.appcard.feign" 패키지에 정의된 Feign 인터페이스들이 자동으로 스캔됩니다.
 */
@Configuration
@EnableFeignClients(basePackages = "com.fisa.appcard.feign")
public class OpenFeignConfig {

}