package ru.skypro.lessons.springboot.weblibrary.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.sql.SQLException;

@RestControllerAdvice
public class EmployeeException {
    @ExceptionHandler
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ade) {
        return ResponseEntity.status((HttpStatus.FORBIDDEN)).body(("Доступ запрещён, недостаточно прав: " + ade.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<String> handleIOException(IOException ioException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка ввода/вывода: " + ioException.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleSQLException(SQLException sqlException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка SQL: " + sqlException.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error: " + exception.getMessage());
    }
    @ExceptionHandler
    public ResponseEntity<String> handleRuntimeException(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid ID/Invalid position name/Employee not found: " + runtimeException.getMessage());
    }
}
