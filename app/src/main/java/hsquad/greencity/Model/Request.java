package hsquad.greencity.Model;

import java.util.List;

public class Request {

    private String phone, name, address, total, status, comment;
    private List<Order> plants_services; // List of plant or service order

    public Request() {
    }

    public Request(String phone, String name, String address, String total, String status, String comment, List<Order> plants_services) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.plants_services = plants_services;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getPlants_services() {
        return plants_services;
    }

    public void setPlants_services(List<Order> plants_services) {
        this.plants_services = plants_services;
    }
}
