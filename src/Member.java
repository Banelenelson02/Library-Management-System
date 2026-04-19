package src;

public class Member {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String membershipDate;
    private int borrowedCount;

    public Member(int id, String name, String email, String phone, String membershipDate, int borrowedCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membershipDate = membershipDate;
        this.borrowedCount = borrowedCount;
    }

    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.borrowedCount = 0;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMembershipDate() { return membershipDate; }
    public int getBorrowedCount() { return borrowedCount; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setMembershipDate(String membershipDate) { this.membershipDate = membershipDate; }
    public void setBorrowedCount(int borrowedCount) { this.borrowedCount = borrowedCount; }

    @Override
    public String toString() {
        return String.format("| %-4d | %-20s | %-25s | %-15s | %-12s | %-8d |",
                id, name, email, phone, membershipDate, borrowedCount);
    }
}