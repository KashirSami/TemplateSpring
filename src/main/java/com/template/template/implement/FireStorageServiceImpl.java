package com.template.template.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.template.template.model.Product;
import com.template.template.service.FirebaseStorageService;
import com.template.template.utilities.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FireStorageServiceImpl implements FirebaseStorageService {

    private final Firestore firestore;

    @Autowired
    public FireStorageServiceImpl(Firestore firestore) {
        this.firestore = firestore;
    }


    @Override
    public void saveProduct(Product product) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> added = firestore
                .collection("productos")
                .add(product);
        System.out.println("Documento creado con ID: " + added.get().getId());
    }

    @Override
    public List<Product> getAllProducts() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("productos").get();
        return future.get().getDocuments().stream()
                .map(d -> {
                    Product p = d.toObject(Product.class);
                    p.setId(d.getId());
                    return p;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateProduct(Product product) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore
                .collection("productos")
                .document(product.getId());
        docRef.set(product).get();
    }

    @Override
    public List<Product> searchProducts(String query) throws ExecutionException, InterruptedException {
        String queryNorm = StringUtils.normalizeNoAccents(query);
        return getAllProducts().stream()
                .filter(p -> {
                    if (p.getNombre() == null) return false;
                    String nombreNorm = StringUtils.normalizeNoAccents(p.getNombre());
                    return nombreNorm.contains(queryNorm);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Product getProductById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot =
                firestore.collection("productos")
                        .document(id)
                        .get()
                        .get();
        if (!snapshot.exists()) {
            return null;
        }
        Product p = snapshot.toObject(Product.class);
        if (p != null) {
            p.setId(id);
        }
        return p;
    }

    @Override
    public List<Product> filterByCategory(String category) throws InterruptedException, ExecutionException {
        String catNorm = StringUtils.normalizeNoAccents(category);
        return getAllProducts().stream()
                .filter(p -> {
                    if (p.getCategoria() == null) return false;
                    String catDBNorm = StringUtils.normalizeNoAccents(p.getCategoria());
                    return catDBNorm.equals(catNorm);
                })
                .collect(Collectors.toList());
    }

    public List<Product> searchByNameAndCategory(String name, String category) throws Exception {
        String nameNorm = StringUtils.normalizeNoAccents(name);
        String catNorm  = StringUtils.normalizeNoAccents(category);

        List<Product> todos = getAllProducts();
        return todos.stream()
                .filter(p -> {
                    if (p.getNombre() == null || p.getCategoria() == null) {
                        return false;
                    }
                    String nombreNorm = StringUtils.normalizeNoAccents(p.getNombre());
                    String catDBNorm  = StringUtils.normalizeNoAccents(p.getCategoria());

                    boolean mismaCat;
                    mismaCat = catDBNorm.equals(catNorm);

                    boolean contieneNombre = nombreNorm.contains(nameNorm);
                    return mismaCat && contieneNombre;
                })
                .collect(Collectors.toList());
    }


    private List<Product> getProducts(ApiFuture<QuerySnapshot> future) throws InterruptedException, ExecutionException {
        List<Product> results = new ArrayList<>();
        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Product p = doc.toObject(Product.class);
            if (p != null) {
                p.setId(doc.getId());
            }
            results.add(p);
        }
        return results;
    }

}
