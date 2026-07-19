import java.rmi.*;

public class Client 
{
    public static void main(String args[])
    {
        String objName = args[0];

        Enciklopedija obj;
        
        try {
            obj = (Enciklopedija) Naming.lookup("rmi://localhost:5000/"+ objName);
            String titles = obj.getArticleTitles();
            
            System.out.println(titles);

            Article a = obj.getArticleByTitle("Dva");

            System.out.println(a.info());

        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }
}