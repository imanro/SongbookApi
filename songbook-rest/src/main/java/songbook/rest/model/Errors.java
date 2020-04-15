package songbook.rest.model;

import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Errors {

    private String message;

    private Map<String, String> fields;

    public Errors(String message, Map<String, String> fields) {
        this.message = message;
        this.fields = fields;
    }

    public Errors(String message) {
        this.message = message;
        this.fields = new HashMap<>();;
    }

    public Errors() {
        this.message = "";
        this.fields = new HashMap<>();;
    }

    public Errors convertFieldErrors(List<FieldError> fieldErrors) {
        fields = new HashMap<>();

        for(FieldError fieldError : fieldErrors) {
            fields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return this;
    }

    public String getMessage() {
        return message;
    }

    public Errors setMessage(String message) {
        this.message = message;
        return this;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public Errors setFields(Map<String, String> fields) {
        this.fields = fields;
        return this;
    }
}
