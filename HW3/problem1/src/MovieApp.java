import java.util.*;

public class MovieApp {
    List<Movie> movies = new LinkedList<>();
    List<User> users = new LinkedList<>();
    HashMap<UserMoviePair, Integer> ratings = new HashMap<>();

    public boolean addMovie(String title, String[] tags) {
        int idx = idxToRegisterMovie(title);
        if(idx != -1 && tags.length != 0){
            movies.add(idx, new Movie(title, tags));
            return true;
        }
        return false;
    }

    private int idxToRegisterMovie(String title){
        for(int i=0; i < movies.size(); i++){
            int compare = title.compareTo(movies.get(i).getTitle());
            if(compare == 0) return -1;
            if(compare < 0) return i;
        }
        return movies.size();
    }

    private int idxToRegisterUser(String name){
        for(int i=0; i < users.size(); i++){
            int compare = name.compareTo(users.get(i).getUsername());
            if(compare == 0) return -1;
            if(compare < 0) return i;
        }
        return users.size();
    }

    public boolean addUser(String name) {
        int idx = idxToRegisterUser(name);
        if(idx != -1){
            users.add(idx, new User(name));
            return true;
        }
        return false;
    }

    public Movie findMovie(String title) {
        if(title == null) return null;

        ListIterator<Movie> iterator = movies.listIterator();
        while(iterator.hasNext()){
            Movie curr = iterator.next();
            int compare = title.compareTo(curr.getTitle());
            if(compare == 0) return curr;
            if(compare < 0) return null;
        }
        return null;
    }

    public User findUser(String username) {
        if(username == null) return null;

        ListIterator<User> iterator = users.listIterator();
        while(iterator.hasNext()){
            User curr = iterator.next();
            int compare = username.compareTo(curr.getUsername());
            if(compare == 0) return curr;
            if(compare < 0) return null;
        }
        return null;
    }

    public List<Movie> findMoviesWithTags(String[] tags) {
        List<Movie> matches = new LinkedList<>();

        ListIterator<Movie> iterator = movies.listIterator();
        while(iterator.hasNext()){
            Movie curr = iterator.next();
            if(includeTags(curr.getTags(), tags)){
                matches.add(0, curr);
            }
        }
        return matches;
    }

    private boolean includeTags(String[] movieTags, String[] queryTags){
        for(String queryTag: queryTags){
            if(!includeTag(movieTags, queryTag)){
                return false;
            }
        }
        return true;
    }
    private boolean includeTag(String[] movieTags, String queryTag){
        for(String movieTag: movieTags){
            if(movieTag.equals(queryTag)){
                return true;
            }
        }
        return false;
    }

    public boolean rateMovie(User user, String title, int rating) {
        if(title == null) return false;
        Movie movie = findMovie(title);
        if(movie == null) return false;
        if(user == null) return false;
        if(findUser(user.getUsername()) == null) return false;
        if(rating < 1 || rating > 5) return false;

        ratings.put(new UserMoviePair(user, movie), rating);

        return false;
    }

    public int getUserRating(User user, String title) {
        if(user == null) return -1;
        if(findUser(user.getUsername()) == null) return -1;
        if(title == null) return -1;
        Movie movie = findMovie(title);
        if(movie == null) return -1;

        Integer rating = ratings.get(new UserMoviePair(user, movie));
        if(rating == null){
            return 0;
        } else {
            return rating;
        }
    }

    SearchHistory searchHistory = new SearchHistory();

    public List<Movie> findUserMoviesWithTags(User user, String[] tags) {
        // TODO sub-problem 4
        if(user == null || findUser(user.getUsername()) == null) return new LinkedList<>();

        List<Movie> match = findMoviesWithTags(tags);
        searchHistory.put(user, match);
        return match;
    }

    public List<Movie> recommend(User user) {
        // TODO sub-problem 4
        if(user == null || findUser(user.getUsername()) == null) return new LinkedList<>();
        HashMap<Movie, Integer> searchCount = searchHistory.get(user);
        List<MovieCount> searchCountList = new LinkedList<>();

        for(Movie movie: searchCount.keySet()){
            searchCountList.add(new MovieCount(movie, searchCount.get(movie), ratings));
        }
        Collections.sort(searchCountList);
        List<Movie> result =  new LinkedList<>();
        for(int i=0; i < 3; i++){
            result.add(i, searchCountList.get(searchCountList.size()-i-1).movie);
        }
        return result;
    }
}
