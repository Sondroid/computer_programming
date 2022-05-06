

import java.util.*;

public class User {
    private String username;
    public User(String username) { this.username = username; }
    @Override
    public String toString() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o){
        if(! (o instanceof User)) return false;
        return username.equals(((User) o).username);
    }
    @Override
    public int hashCode(){
        return username.hashCode();
    }
}
