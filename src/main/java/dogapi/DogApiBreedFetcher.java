package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        if (breed == null || breed.trim().isEmpty()) {

            throw new BreedNotFoundException("Breed name is invalid");

        }

        String breedName = breed.trim().toLowerCase(Locale.ROOT);
        String url = "https://dog.ceo/api/breed/" + breedName + "/list";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (response.body() == null) {
                throw new BreedNotFoundException("Empty response from API");
            }

            JSONObject json = new JSONObject(response.body().string());

            if (!"success".equalsIgnoreCase(json.optString("status"))) {
                throw new BreedNotFoundException("Breed not found: " + breedName);
            }

            JSONArray array = json.optJSONArray("message");
            List<String> result = new ArrayList<>();

            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    result.add(array.getString(i));
                }
            }

            return result;

        } catch (Exception e) {
            throw new BreedNotFoundException("Error while fetching");
        }
    }
}

