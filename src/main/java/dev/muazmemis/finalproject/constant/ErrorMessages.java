package dev.muazmemis.finalproject.constant;

public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String USER_NAME_EXISTS = "User name already exists: %s";
    public static final String USERNAME_NOT_FOUND = "User name not found: %s";
    public static final String USER_NOT_FOUND = "User not found with id: %d";
    public static final String DEPARTMENT_NOT_FOUND = "Department not found: %s";
    public static final String DEPARTMENT_NAME_EXISTS = "Department name already exists: %s";
    public static final String PROJECT_NOT_FOUND = "Project not found: %s";
    public static final String TASK_NOT_FOUND = "Task not found: %s";
    public static final String ACCESS_DENIED = "You do not have the necessary role to perform this action";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again later.";

}
