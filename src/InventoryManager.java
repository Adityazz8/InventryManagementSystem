import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {
    private static InventoryManager instance;
    private List<Product> products;
    private Map<String, Product> productMap;

    private InventoryManager() {
        products = new ArrayList<>();
        productMap = new HashMap<>();
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public void addOrUpdateProduct(Product product) {
        if (productMap.containsKey(product.getBarcode())) {
            // Update existing
            Product existing = productMap.get(product.getBarcode());
            existing.setName(product.getName());
            existing.setPrice(product.getPrice());
            existing.setQuantity(product.getQuantity());
        } else {
            // Add new
            products.add(product);
            productMap.put(product.getBarcode(), product);
        }
    }

    public void deleteProduct(String barcode) {
        Product toRemove = productMap.remove(barcode);
        if (toRemove != null) {
            products.remove(toRemove);
        }
    }

    public Product getProductByBarcode(String barcode) {
        return productMap.get(barcode);
    }

    public List<Product> getProducts() {
        return products;
    }

    public double calculateTotalInventoryValue() {
        return products.stream().mapToDouble(Product::getTotalValue).sum();
    }
}
