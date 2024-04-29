package hu.krisztofertarr.forum.serializer;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import hu.krisztofertarr.forum.serializer.transformer.ObjectTransformer;

public class SerializerService {

    private final Map<Class<?>, ObjectTransformer<?>> transformers;

    public SerializerService() {
        this.transformers = new HashMap<>();
    }

    public void registerTransformer(ObjectTransformer<?> transformer) {
        for (Class<?> type : transformer.types()) {
            transformers.put(type, transformer);
        }
    }

    public <T> ObjectTransformer<T> findTransformer(Class<T> type) {
        return (ObjectTransformer<T>) transformers.get(type);
    }

    public <T> T transform(Class<T> type, DocumentSnapshot value) {
        ObjectTransformer<T> transformer = findTransformer(type);
        if (transformer == null) {
            throw new IllegalArgumentException("No transformer found for type: " + type);
        }
        return transformer.transform(this, value);
    }
}
