import java.io.File;
import java.util.*;
import java.time.LocalDateTime;

public class FrontEnd {
    private UserInterface ui;
    private BackEnd backend;
    private User user;

    public FrontEnd(UserInterface ui, BackEnd backend) {
        this.ui = ui;
        this.backend = backend;
    }

    User getUser(){
        return user;
    }
    public boolean auth(String authInfo){
        // TODO sub-problem 1
        String[] authInfoSplit = authInfo.split("\n");
        user = new User(authInfoSplit[0], authInfoSplit[1].toLowerCase(Locale.ROOT));
        return user.getPassword().equals(backend.loadPassword(user.getId()));
    }

    public void post(Pair<String, String> titleContentPair) {
        // TODO sub-problem 2
        Post post =new Post(titleContentPair.key, titleContentPair.value);
        post.setId(backend.getLargestPostId() + 1);
        backend.savePost(user.getId(), post);
        backend.setLargestPostId(backend.getLargestPostId() + 1);
    }
    
    public void recommend(int N){
        // TODO sub-problem 3
        List<Post> postsOfFriends = new ArrayList<>();

        List<String> friends = backend.loadFriends(user.getId());
        for(String friend: friends){
            postsOfFriends.addAll(backend.loadPosts(friend));
        }
        Collections.sort(postsOfFriends);
        for(int i=0; i<N && i<postsOfFriends.size(); i++){
            ui.println(postsOfFriends.get(i));
        }
    }

    public void search(String command) {
        // TODO sub-problem 4
        String[] commandSlices = command.split(" ");
        HashSet<String> keywordSet = new HashSet<>();
        for(int i=1; i<commandSlices.length; i++){
            keywordSet.add(commandSlices[i]);
        }

        HashMap<Post, Integer> matchCounts = new HashMap<>();

        for(String userId: backend.loadUserList()){
            for(Post post: backend.loadPosts(userId)){
                int matchCount = 0;
                for(String word: postWordList(post)){
                    for(String keyword: keywordSet){
                        if(word.equals(keyword)){
                            matchCount++;
                        }
                    }
                }
                if(matchCount > 0){
                    matchCounts.put(post, matchCount);
                }
            }
        }

        List<Post> candidates = new ArrayList<>(matchCounts.keySet());
        Collections.sort(candidates, new ContentLengthComp());
        Collections.sort(candidates, new OccurenceComp(matchCounts));

        for(int i=0; i<10 && i<candidates.size(); i++){
            ui.println(candidates.get(i).getSummary());
        }
    }

    private List<String> postWordList(Post post){
        ArrayList<String> wordList = new ArrayList<>();
        wordList.addAll(Arrays.asList(post.getTitle().split(" ")));
        wordList.addAll(Arrays.asList(post.getContent().split("\\s")));
        return wordList;
    }

}

class OccurenceComp implements Comparator<Post>{
    HashMap<Post, Integer> matchCounts;
    OccurenceComp(HashMap<Post, Integer> matchCounts){
        this.matchCounts = matchCounts;
    }

    @Override
    public int compare(Post p1, Post p2){
        return matchCounts.get(p2) - matchCounts.get(p1);
    }
}

class ContentLengthComp implements Comparator<Post>{
    @Override
    public int compare(Post p1, Post p2){
        return p2.getContent().split(" ").length - p1.getContent().split(" ").length;
    }
}