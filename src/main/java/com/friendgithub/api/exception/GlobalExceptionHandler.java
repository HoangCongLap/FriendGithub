package com.friendgithub.api.exception;

import com.friendgithub.api.dto.request.ApiResponse;
import com.friendgithub.api.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNKNOWN_ERROR.getCode());
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        Response response = Response.builder()
                .message("File to large!")
                .build();
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
    }


    //    @ExceptionHandler(value = AppException.class)
    //    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
    //        ErrorCode errorCode = exception.getErrorCode();
    //        ApiResponse apiResponse = new ApiResponse();
    //
    //        apiResponse.setCode(errorCode.getCode());
    //        apiResponse.setMessage(errorCode.getMessage());
    //
    //        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    //    }
}
