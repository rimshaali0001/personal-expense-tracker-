import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBConnection() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("expense_tracker_db");
        collection = database.getCollection("transactions");
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
