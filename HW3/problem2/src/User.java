public class User {
    public String id;
    private String password;
    User(String id, String password){
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}
