package hu.krisztofertarr.forum.serializer.transformer;

import com.google.firebase.firestore.DocumentSnapshot;

import hu.krisztofertarr.forum.serializer.SerializerService;

public interface ObjectTransformer<T> {

    T transform(SerializerService context, DocumentSnapshot value);

    Class<?>[] types();
}
