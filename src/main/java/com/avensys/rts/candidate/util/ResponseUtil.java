package com.avensys.rts.candidate.util;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<Object> generateSuccessResponse(Object dataObject, HttpStatus httpStatus, String message) {
        CandidateResponseDTO.HttpResponse httpResponse = new CandidateResponseDTO.HttpResponse();
        httpResponse.setData(dataObject);
        httpResponse.setCode(httpStatus.value());
        httpResponse.setMessage(message);
        return new ResponseEntity<>(httpResponse,httpStatus);
    }

    public static ResponseEntity<Object> generateErrorResponse(HttpStatus httpStatus, String message) {
        CandidateResponseDTO.HttpResponse httpResponse = new CandidateResponseDTO.HttpResponse();
        httpResponse.setCode(httpStatus.value());
        httpResponse.setError(true);
        httpResponse.setMessage(message);
        return new ResponseEntity<>(httpResponse,httpStatus);
    }



}
