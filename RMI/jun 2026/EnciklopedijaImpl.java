import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;

public class EnciklopedijaImpl extends UnicastRemoteObject implements Enciklopedija
{
    public Map<String,Article> db;

    public EnciklopedijaImpl() throws RemoteException
    {
        super();

        db = new HashMap<>();

        Article a1 = new Article("Jedan","JedanJedan");
        Article a2 = new Article("Dva","DvaDva");

        db.put(a1.getTitle(),a1);
        db.put(a2.getTitle(),a2);
    }

    @Override
    public String getArticleTitles() throws RemoteException
    {
        String result = "";

        for(String title: db.keySet())
        {
            result = result + title + " ";
        }

        return result;
    }

    @Override
    public Article getArticleByTitle(String title) throws RemoteException
    {
        return db.get(title);
    }
}