package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException("Breed name is invalid");}

        String Given_breed = breed.trim().toLowerCase(Locale.ROOT);

        if (cache.containsKey(Given_breed)) {
            return cache.get(Given_breed);
        }

        try {callsMade++;
            List<String> result = fetcher.getSubBreeds(Given_breed);
            cache.put(Given_breed, result);
            return result;

        } catch (BreedNotFoundException e) {
            throw e;
        }
    }
    public int getCallsMade() {return callsMade;}
}

