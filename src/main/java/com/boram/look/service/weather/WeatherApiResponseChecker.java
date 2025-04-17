package com.boram.look.service.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

@Slf4j
public class WeatherApiResponseChecker {

    public static boolean isErrorResponse(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String body = response.getBody();

            // XML로 에러메시지인지 확인
            if (body.contains("<OpenAPI_ServiceResponse>")) {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new ByteArrayInputStream(body.getBytes()));

                    NodeList errMsgNodes = doc.getElementsByTagName("errMsg");
                    NodeList reasonNodes = doc.getElementsByTagName("returnReasonCode");

                    if (errMsgNodes.getLength() > 0 && reasonNodes.getLength() > 0) {
                        String errMsg = errMsgNodes.item(0).getTextContent();
                        String reason = reasonNodes.item(0).getTextContent();

                        log.warn("기상청 API 오류 발생: [{}] {}", reason, errMsg);
                        return true;
                    }
                } catch (Exception e) {
                    log.error("기상청 응답 XML 파싱 중 오류", e);
                }
            }
        }
        return false;
    }
}
