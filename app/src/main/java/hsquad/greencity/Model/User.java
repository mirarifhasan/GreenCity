package hsquad.greencity.Model;

public class User {
    private String phone, email, name, password, isStaff;

    public User(String phone, String email, String name, String password) {
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.password = password;
        this.isStaff = "false";
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.isStaff = "false";
    }

    public User() {
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getpassword() {
        return password;
    }

    public void setpassword(String password) {
        this.password = password;
    }
}
