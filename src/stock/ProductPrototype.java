package stock;

public class ProductPrototype {
    private Product prototype;

    public ProductPrototype(Product prototype) {
        this.prototype = prototype;
    }

    public Product cloneProduct() {
        return prototype.clone();
    }
}
