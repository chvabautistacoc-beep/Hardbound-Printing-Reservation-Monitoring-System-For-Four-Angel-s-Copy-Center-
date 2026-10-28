package printing_reservation_system;

public class UserSession {
    private static int user_id;
    private static String first_name;

    public static void setUser(int id, String f_name) {
        user_id = id;
        first_name = f_name;
    }

    public static int getUserId() {
        return user_id;
    }

    public static String getFirstName() {
        return first_name;
    }

    public static void clearSession() {
        user_id = 0;
        first_name = null;
    }
    
}