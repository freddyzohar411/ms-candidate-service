package com.avensys.rts.candidate.custombody;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.avensys.rts.candidate.payloadresponse.HttpResponse;

/***
 *
 * @author Kotaiah nalleboina
 * This class is used to intercept the response body and inject audit data into the response body.
 *
 */

@ControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomResponseBodyAdvice.class);

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply this advice to all controllers that return JSON responses
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        LOGGER.info("In CustomResponseBodyAdvice");

        try {

            // Cast body of object to Custom HttpResponse
            HttpResponse httpResponse= (HttpResponse) body;

            // Retrieve audit data from request context for injection into the response body
            Long endTime = System.currentTimeMillis();
            Long startTime = (Long) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getAttribute("startTime");
            String threadId = (String) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getAttribute("threadId");
            Long latency = endTime - startTime;

            // Put audit data into Map
            Map<String, Object> map = new HashMap<>();
            map.put("threadId", threadId);
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            map.put("latency", latency);

            // Set audit data into the response body
            httpResponse.setAudit(map);
        } catch (Exception e) {
            LOGGER.error("Error in CustomResponseBodyAdvice: " + e.getMessage());
        }

        return body; // Return the response body
    }
}