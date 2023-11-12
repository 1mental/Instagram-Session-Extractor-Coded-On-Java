package extractor.instagram;

public final class APISession implements  Session{

    private String session;
    private final SessionType type = SessionType.API_SESSION;

    @Override
    public String getSession() {
        return session;
    }

    @Override
    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public SessionType getSessionType() {
        return this.type;
    }

}
