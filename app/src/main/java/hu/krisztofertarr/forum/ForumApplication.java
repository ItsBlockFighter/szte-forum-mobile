package hu.krisztofertarr.forum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import hu.krisztofertarr.forum.activity.LoginActivity;
import hu.krisztofertarr.forum.service.AuthService;
import lombok.Getter;

@Getter
public class ForumApplication extends AppCompatActivity {

    private AuthService authService;

    @Override
    protected void onStart() {
        super.onStart();

        authService = AuthService.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        boolean loggedIn = authService.isLoggedIn();
        if (loggedIn) {
            menu.findItem(R.id.log_out_button).setVisible(true);
            menu.findItem(R.id.settings_button).setVisible(true);
        } else {
            menu.findItem(R.id.login_button).setVisible(true);
            menu.findItem(R.id.register_button).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.login_button) {
            openLoginActivity(null);
            return true;
        } else if (id == R.id.register_button) {
            Log.d("ForumApplication", "Register clicked");
            return true;
        } else if (id == R.id.log_out_button) {
            Log.d("ForumApplication", "Log out clicked");
            return true;
        } else if (id == R.id.settings_button) {
            Log.d("ForumApplication", "Settings clicked");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void openLoginActivity(View view) {
        Log.d("ForumApplication", "openLoginActivity: Opening LoginActivity");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        /*firestore.collection("rooms").document("roomA")
                .collection("messages").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.d("ForumApplication", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d("ForumApplication", "get failed with ", task.getException());
                    }
                });*/
        // find 2fBzPmez7jcFHtEVO4nN document in 'category' collection and map it to a Category object
        /*firestore.collection("category").document("2fBzPmez7jcFHtEVO4nN").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("ForumApplication", "DocumentSnapshot data: " + document.getData());

                    // map the document to a Category object
                    Category category = document.toObject(Category.class);
                    if(category != null) {
                        List<DocumentReference> forumRefs = document.getList("forum");
                    }

                    List< DocumentReference> forumRefs = (List<DocumentReference>) document.get("forum");
                    for (DocumentReference forumRef : forumRefs) {
                        forumRef.get().addOnCompleteListener(forumTask -> {
                            if (forumTask.isSuccessful()) {
                                DocumentSnapshot forumDocument = forumTask.getResult();
                                if (forumDocument.exists()) {
                                    Forum forum = forumDocument.toObject(Forum.class);

                                    if(forum != null) {
                                        category.getForums().add(forum);
                                    }
                                    Log.d("ForumApplication", "Category: " + category);
                                } else {
                                    Log.d("ForumApplication", "No such document");
                                }
                            } else {
                                Log.d("ForumApplication", "get failed with ", forumTask.getException());
                            }
                        });
                    }
                    // document 'forum' array field values map to Forum then add to category
                    Log.d("ForumApplication", "Category: " + category);
                } else {
                    Log.d("ForumApplication", "No such document");
                }
            } else {
                Log.d("ForumApplication", "get failed with ", task.getException());
            }
        });*/

        // get User by id v3Dn9Fdr0eLh7vsWiCwn
        /*firestore.collection("user").document("v3Dn9Fdr0eLh7vsWiCwn").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);

                    Category category = new Category();
                    category.setName("General");
                    category.setDescription("General discussion");

                    Forum forum = new Forum();
                    forum.setName("General Discussion");
                    forum.setDescription("General discussion forum");

                    Thread thread = new Thread();
                    thread.setAuthor(user);
                    thread.setTitle("Welcome to the forum");

                    Post post = new Post();
                    post.setAuthor(user);
                    post.setContent("Welcome to the forum!");

                    thread.getPosts().add(post);
                    forum.getThreads().add(thread);
                    category.getForums().add(forum);

                    firestore.collection("category").add(category).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            Log.d("ForumApplication", "DocumentSnapshot added with ID: " + task2.getResult().getId());
                        } else {
                            Log.d("ForumApplication", "Error adding document", task2.getException());
                        }
                    });
                }
            }
        });*/

        /*final SerializerService serializerService = new SerializerService();
        serializerService.registerTransformer(new Room.Transformer());
        serializerService.registerTransformer(new Message.Transformer());

        // get roomA document in 'rooms' collection and map it to a Room object
        firestore.collection("rooms").document("roomA").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("ForumApplication", "DocumentSnapshot data: " + document.getData());

                    // map the document to a Room object
                    Room room = serializerService.transform(Room.class, document);
                    Log.d("ForumApplication", "Room: " + room);
                } else {
                    Log.d("ForumApplication", "No such document");
                }
            } else {
                Log.d("ForumApplication", "get failed with ", task.getException());
            }
        });*/
    }
}