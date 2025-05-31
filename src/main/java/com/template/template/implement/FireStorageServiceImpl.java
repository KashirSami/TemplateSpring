package com.template.template.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.template.template.model.Product;
import com.template.template.service.FirebaseStorageService;
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
        ApiFuture<QuerySnapshot> future = firestore
                .collection("productos")
                .orderBy("nombre")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get();
        return getProducts(future);
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
        p.setId(id);
        return p;
    }
    @Override
    public List<Product> filterByCategory(String category) throws InterruptedException, ExecutionException {
        QuerySnapshot snap = firestore.collection("productos")
                .whereEqualTo("categoria", category)
                .get()
                .get();

        List<Product> productos = new ArrayList<>();
        for (QueryDocumentSnapshot doc : snap.getDocuments()) {
            Product p = doc.toObject(Product.class);
            // Firestore no rellena el ID en el POJO, así que lo asignamos manualmente:
            p.setId(doc.getId());
            productos.add(p);
        }
        return productos;
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
