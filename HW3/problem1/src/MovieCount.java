import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MovieCount implements Comparable<MovieCount>{
    Movie movie;
    int count;
    Double medianRating;

    MovieCount(Movie movie, int count, HashMap<UserMoviePair, Integer> ratings){
        this.movie = movie;
        this.count = count;

        List<Integer> rating = new ArrayList<>();

        for(UserMoviePair userMoviePair: ratings.keySet()){
            if(userMoviePair.getMovie().equals(movie)){
                rating.add(ratings.get(userMoviePair));
            }
        }
        if(rating.size() % 2 == 0){
            this.medianRating = ((double)(rating.get(rating.size()/2) + rating.get(rating.size()/2 - 1))) / 2;
        } else {
            this.medianRating = (double)rating.get(rating.size()/2);
        }
    }

    @Override
    public int compareTo(MovieCount other){
        if(count != other.count){
            return count - other.count;
        }
        else if(!Objects.equals(medianRating, other.medianRating)){
            return medianRating.compareTo(other.medianRating);
        }
        else{
            return -movie.getTitle().compareTo(other.movie.getTitle());
        }
    }

}
