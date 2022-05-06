import java.util.HashMap;
import java.util.List;

public class SearchHistory extends HashMap<User, HashMap<Movie, Integer>> {

    public void put(User user, List<Movie> matches){
        if(!containsKey(user)){
            HashMap<Movie, Integer> searchCount = new HashMap<>();
            for(Movie movie: matches){
                searchCount.put(movie, 1);
            }
            put(user, searchCount);
        }
        else{
            HashMap<Movie, Integer> searchCount = get(user);
            for(Movie movie: matches){
                if(searchCount.containsKey(movie)){
                    searchCount.put(movie, searchCount.get(movie) + 1);
                }
                else{
                    searchCount.put(movie, 1);
                }
            }
        }

    }
}
