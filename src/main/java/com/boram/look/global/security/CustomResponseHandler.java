package com.boram.look.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * CustomResponseHandler 클래스는 클라이언트에게 반환할 JSON 형식의 응답을 생성하여 작성하는 유틸리티 컴포넌트입니다.
 * <p>
 * 성공 응답과 실패 응답에 {@link ObjectMapper}와 {@link org.springframework.http.ResponseEntity}을 활용하여 응답 객체를 JSON 문자열로 변환합니다.
 * </p>
 */
@RequiredArgsConstructor
@Component
public class CustomResponseHandler {

    private final ObjectMapper objectMapper;

    /**
     * 요청에 대한 성공 응답을 JSON 형식으로 생성하여 클라이언트에 전달합니다.
     * <p>
     * HTTP 상태 코드는 200(OK)로 설정됩니다.
     * </p>
     *
     * @param request  클라이언트의 HttpServletRequest 객체
     * @param response 클라이언트의 HttpServletResponse 객체
     * @param body     응답으로 전달할 데이터 객체
     * @throws IOException JSON 변환 또는 응답 작성 중 발생할 수 있는 I/O 예외
     */
    public void onSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Object body
    ) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ResponseEntity.ok()
                )
        );
    }

    /**
     * 요청에 대한 실패 응답을 JSON 형식으로 생성하여 클라이언트에 전달합니다.
     * <p>
     * 지정된 HTTP 상태 코드와 함께 에러 코드 및 메시지를 포함합니다.
     * </p>
     *
     * @param response   클라이언트의 HttpServletResponse 객체
     * @param httpStatus 반환할 HTTP 상태 코드
     * @param msg        에러 메시지
     * @throws IOException JSON 변환 또는 응답 작성 중 발생할 수 있는 I/O 예외
     */
    public void onFailure(
            HttpServletResponse response,
            HttpStatus httpStatus,
            String msg
    ) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(httpStatus.value());
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ResponseEntity.status(httpStatus).body(msg)
                )
        );
    }
}
