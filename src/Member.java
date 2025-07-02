public class Member {
    private int id;
    private String name;
    private String email;
    private String phone;

    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    
    // Setters
    public void setId(int id) { this.id = id; }
}