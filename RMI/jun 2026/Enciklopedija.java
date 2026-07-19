import java.rmi.*;

public interface Enciklopedija extends Remote
{
    public String getArticleTitles() throws RemoteException;

    public Article getArticleByTitle(String title) throws RemoteException;

}