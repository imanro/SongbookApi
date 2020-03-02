package songbook.rest.util;

public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;

    public SearchCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public SearchCriteria setKey(String key) {
        this.key = key;
        return this;
    }

    public String getOperation() {
        return operation;
    }

    public SearchCriteria setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public SearchCriteria setValue(Object value) {
        this.value = value;
        return this;
    }
}
