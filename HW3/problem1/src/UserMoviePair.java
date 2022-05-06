public class UserMoviePair {
    private Movie movie;
    private User user;

    UserMoviePair(User user, Movie movie){
        this.movie = movie;
        this.user = user;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof UserMoviePair)) return false;
        return movie.equals(((UserMoviePair) o).getMovie()) && user.equals(((UserMoviePair) o).getUser());
    }

    @Override
    public int hashCode(){
        return movie.getTitle().hashCode() * 17 + user.getUsername().hashCode() * 31;
    }

    public Movie getMovie() {
        return movie;
    }
    public User getUser() {
        return user;
    }
}
