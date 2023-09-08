package com.avensys.rts.candidate.payloadresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author Kotaiah nalleboina
 * This class is used to create a custom response for the API calls.
 * It is used to return a custom response to the client.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponse {
    private int code;
    private boolean error;
    private String message;
    private Object data;
    private Map<?, ?> audit;
    private LocalDateTime timestamp = LocalDateTime.now();
}
