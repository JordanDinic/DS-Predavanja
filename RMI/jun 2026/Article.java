import java.io.Serializable;
import java.time.LocalDateTime;


public class Article implements Serializable
{
    private String title, content;
    private LocalDateTime lastUpdated;

    public Article(String title, String content)
    {
        this.title = title;
        this.content = content;
        this.lastUpdated = LocalDateTime.now();
    }

    public String info()
    {
        String info = "Title: " + this.title + "\n Last updated: " + this.lastUpdated; 

        return info;
    }

    public void updateContent(String newContent)
    {
        this.content = newContent;
    }

    public String getTitle()
    {
        return this.title;
    }
}