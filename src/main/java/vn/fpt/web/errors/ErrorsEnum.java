package vn.fpt.web.errors;

import lombok.Getter;
import vn.fpt.constant.EntitiesConstant;
import vn.fpt.constant.ErrorsKeyConstant;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
public enum ErrorsEnum {

    // System Errors

    // Auth Errors
    AUTH_FAILED(EntitiesConstant.AUTH, ErrorsKeyConstant.UNAUTHORIZED, getResourceBundle("i18n/error_messages", Locale.getDefault()).getString("auth.unauthorized")),
    AUTH_NO_ACCESS(EntitiesConstant.AUTH, ErrorsKeyConstant.PERMISSION_DENIED, getResourceBundle("i18n/error_messages", Locale.getDefault()).getString("auth.permission_denied")),

    // User Errors
    USERS_HAD_BEEN_INACTIVE(EntitiesConstant.USERS, ErrorsKeyConstant.HAD_BEEN_INACTIVE, getResourceBundle("i18n/error_messages", Locale.getDefault()).getString("users.had_been_inactive")),

    // ... add more cases here ...

    ;

    private static final Map<String, ErrorsEnum> ENUM_MAP = new HashMap<>();
    private final String entityName;
    private final String errorKey;
    private String message;

    public static ResourceBundle getResourceBundle(String bundleName, Locale locale) {

        if(locale == null)
            return ResourceBundle.getBundle(bundleName, Locale.getDefault());
        else
            return ResourceBundle.getBundle(bundleName, locale);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(String bundleName, Locale locale) {

        String messageContent = getResourceBundle(bundleName, locale).getString(getFullKey());
        this.message = new String(messageContent.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public String getFullKey() {
        return this.entityName + "." + this.errorKey;
    }

    ErrorsEnum(String entityName, String errorKey, String message) {
        this.entityName = entityName;
        this.errorKey = errorKey;
        this.message = message;
    }

    // Static block to initialize the enum map
    static {
        for (ErrorsEnum errorsEnum : ErrorsEnum.values()) {
            ENUM_MAP.put(errorsEnum.name(), errorsEnum);
        }
    }

    // Static method to get ErrorsEnum instance by name
    public static ErrorsEnum getByName(String name) {
        return ENUM_MAP.get(name);
    }
}