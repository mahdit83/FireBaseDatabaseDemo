package ir.mtajik.firebasedatabasedemo.models;

public class CarModel {

    private String name;
    private String id;
    private String price;

    public CarModel(){

    }

    public CarModel(String id, String name, String price) {

        this.id = id;
        this.price = price;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }


    public String getPrice() {
        return price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
