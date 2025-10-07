package hexagonal_architecture.application;

import hexagonal_architecture.App;
import hexagonal_architecture.domain.User;
import hexagonal_architecture.infrastructure.UserRegistry;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class VertxFileUserRegistry implements UserRegistry {

    private final File userRegistryFile;
    private final Map<String, User> users;

    public VertxFileUserRegistry(final String filePath) {
        this.userRegistryFile = new File(filePath);
        this.users = new HashMap<>();
        this.initUsers();
    }

    @Override
    public void saveUser(final User user) {
        this.users.put(user.id(), user);
        this.saveOnDB();
    }

    @Override
    public Optional<User> findUserById(final String userId) {
        return Optional.ofNullable(this.users.get(userId));
    }

    @Override
    public Iterable<User> findAllUsers() {
        return this.users.values();
    }

    @Override
    public void initUsers() {
        try {
            final BufferedReader usersDB = new BufferedReader(new FileReader(this.userRegistryFile));
            final StringBuilder stringBuilder = new StringBuilder();
            while (usersDB.ready()) {
                stringBuilder.append(usersDB.readLine()).append("\n");
            }
            usersDB.close();
            final JsonArray array = new JsonArray(stringBuilder.toString());
            for (int i = 0; i < array.size(); i++) {
                final JsonObject user = array.getJsonObject(i);
                final String key = user.getString("userId");
                this.users.put(key, new User(key, user.getString("userName")));
            }

        } catch (Exception ex) {
            App.getLogger().info("No dbase, creating a new one");
            this.saveOnDB();
        }
    }

    private void saveOnDB() {
        try {
            final JsonArray list = new JsonArray();
            for (final User user: this.users.values()) {
                final JsonObject obj = new JsonObject();
                obj.put("userId", user.id());
                obj.put("userName", user.name());
                list.add(obj);
            }
            final FileWriter usersDB = new FileWriter(this.userRegistryFile);
            usersDB.append(list.encodePrettily());
            usersDB.flush();
            usersDB.close();
        } catch (Exception ex) {
            App.getLogger().log(Level.INFO, "Error saving on the Database");
        }
    }
}
