package extractor.instagram;
public interface Session
{
    public abstract String getSession();
    public abstract void setSession(String session);

    public abstract SessionType getSessionType();


}