package com.finalshield.Auditoria;

public enum AuditoriaEventoTipo {
    LOGIN_SUCCESS,
    LOGIN_FAILED,

    FILE_ENCRYPTED,
    FILE_DECRYPTED,

    LINK_CREATED,
    LINK_ACCESSED,
    LINK_EXPIRED,

    PASSWORD_CHANGED,

    USER_REGISTERED,

    SECURITY_ALERT
}
