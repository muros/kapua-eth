package org.eclipse.kapua.message;

public class KapuaInvalidTopicException extends Exception {

    private static final long serialVersionUID = 2841854292521738239L;

    private String m_account;

    public KapuaInvalidTopicException(String account, String topic) {
        super(topic);
        m_account = account;
    }

    public String getAccount() {
        return m_account;
    }
}
