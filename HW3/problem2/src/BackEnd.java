import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BackEnd extends ServerResourceAccessible {
    // Use getServerStorageDir() as a default directory
    // TODO sub-program 1 ~ 4 :

    private int largestPostId;

    public BackEnd(){
        super();
        this.largestPostId = loadLargestPostId();
    }

    public String loadPassword(String id){
        File file = new File(getServerStorageDir() + id + "/password.txt");
        try{
            Scanner input = new Scanner(file);
            return input.nextLine().toLowerCase(Locale.ROOT);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void savePost(String userId, Post post){
        try{
            FileWriter fileWriter = new FileWriter(getServerStorageDir() + userId + "/post/" + post.getId() + ".txt");
            fileWriter.write(post.getDate() + "\n");
            fileWriter.write(post.getTitle() + "\n\n");
            fileWriter.write(post.getContent());
            fileWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

    public String[] loadUserList(){
        File dir = new File(getServerStorageDir());
        return dir.list();
    }

    private int loadLargestPostId(){
        int largest = -1;
        String[] userIds = loadUserList();
        for(String userId: userIds){
            File post = new File(getServerStorageDir() + userId + "/post/");
            String[] postIds = post.list();
            for(String postId: postIds){
                int parsedPostId = Integer.parseInt(postId.replace(".txt", ""));
                if(largest < parsedPostId) largest = parsedPostId;
            }
        }
        return largest;
    }

    public int getLargestPostId() {
        return largestPostId;
    }

    public void setLargestPostId(int largestContentId) {
        this.largestPostId = largestContentId;
    }

    public List<String> loadFriends(String userId){
        File file = new File(getServerStorageDir() + userId + "/friend.txt");
        List<String> friends = new ArrayList<>();
        try{
            Scanner scanner = new Scanner(file);
            while(scanner.hasNext()){
                friends.add(scanner.nextLine());
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return friends;
    }

    public List<Post> loadPosts(String userId){
        List<Post> posts = new ArrayList<>();

        try{
            File dir = new File(getServerStorageDir() + userId + "/post/");
            File[] postFiles = dir.listFiles();
            for(File postFile: postFiles){
                int postId = Integer.parseInt(postFile.getName().replace(".txt", ""));

                Scanner scanner = new Scanner(postFile);
                LocalDateTime dateTime = Post.parseDateTimeString(scanner.nextLine(), Post.getFormatter());
                String title = scanner.nextLine();

                String content;
                String entireContent = "";
                scanner.nextLine();
                content = scanner.nextLine();
                entireContent += content + "\n";

                while(scanner.hasNextLine()){
                    content = scanner.nextLine();
                    entireContent += content + "\n";
                }

                posts.add(new Post(postId, dateTime, title, entireContent));
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }

        return posts;
    }
}
