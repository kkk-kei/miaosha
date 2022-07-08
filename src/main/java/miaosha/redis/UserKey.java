package miaosha.redis;

public class UserKey extends BasePrefix{
    private UserKey(String prefix){
        super(prefix);
    }
    public static UserKey getByID = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
