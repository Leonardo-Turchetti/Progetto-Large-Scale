package it.unipi.dii.lsmd.socialrestaurant.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.unipi.dii.lsmd.socialrestaurant.utils.Utils;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Properties;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Singleton class used to connect to MongoDB
 */
public class MongoDriver {
    private static MongoDriver instance;

    private MongoClient client = null;
    private CodecRegistry pojoCodecRegistry;
    public String mongoUsername;
    public String mongoPassword;
    public String mongoFirstIp;
    public int mongoFirstPort;
    public String mongoSecondIp;
    public int mongoSecondPort;
    public String mongoThirdIp;
    public int mongoThirdPort;
    public String mongodbName;

    private MongoDriver(Properties configurationParameters){
        this.mongoUsername = configurationParameters.getProperty("mongoUsername");
        this.mongoPassword = configurationParameters.getProperty("mongoPassword");
        this.mongoFirstIp = configurationParameters.getProperty("mongoFirstIp");
        this.mongoFirstPort = Integer.parseInt(configurationParameters.getProperty("mongoFirstPort"));
        this.mongoSecondIp = configurationParameters.getProperty("mongoSecondIp");
        this.mongoSecondPort = Integer.parseInt(configurationParameters.getProperty("mongoSecondPort"));
        this.mongoThirdIp = configurationParameters.getProperty("mongoThirdIp");
        this.mongoThirdPort = Integer.parseInt(configurationParameters.getProperty("mongoThirdPort"));
        this.mongodbName = configurationParameters.getProperty("mongoDbName");
    };


    public static MongoDriver getInstance() {
        if (instance == null)
            instance = new MongoDriver(Utils.readConfigurationParameters());
        return instance;
    }

    /**
     * Method that connects to mongoDB and returns the MongoClient instance
     */
    public MongoClient openConnection() {
        if (client != null)
            return client;

        try
        {
            //String string = "mongodb://localhost:27017";
            String string = "mongodb://"  + mongoFirstIp + ":" + mongoFirstPort + "," + mongoSecondIp + ":" + mongoSecondPort + "," + mongoThirdIp + ":" + mongoThirdPort;
            ConnectionString connectionString = new ConnectionString(string);

            pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));

            MongoClientSettings clientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .readPreference(ReadPreference.primaryPreferred())
                    .retryWrites(true)
                    .writeConcern(WriteConcern.W1)
                    .codecRegistry(pojoCodecRegistry)
                    .build();

            client = MongoClients.create(clientSettings);

            System.out.println("Connected to MongoDB ...");
            return client;
        }
        catch (Exception ex)
        {
            System.out.println("MongoDB is not available");
            return null;
        }
    }

    /**
     * Method used to close the connection
     */
    public void closeConnection() {
        if (client != null)
            System.out.println("Connection closed ...");
        client.close();
    }
}
