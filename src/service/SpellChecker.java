package service;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SpellChecker {
    private final Set<String> dictionary;
    private final Map<String, List<String>> suggestionCache;
    private final ExecutorService executorService;
    private final Trie trie;
    private static final String DEFAULT_DICTIONARY_PATH = "./resources/du";
    private static final int MAX_SUGGESTIONS = 7;
    private static final int MAX_DISTANCE = 2;
    private static final int THREAD_POOL_SIZE = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);

    public SpellChecker() {
        this(DEFAULT_DICTIONARY_PATH);
    }

    public SpellChecker(String dictionaryPath) {
        dictionary = new ConcurrentSkipListSet<>();
        suggestionCache = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE,
                r -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                });
        trie = new Trie();
        loadDictionary(dictionaryPath);
    }

    private static class Trie {
        private final TrieNode root;
        private final Object lock = new Object();

        private static class TrieNode {
            ConcurrentHashMap<Character, TrieNode> children = new ConcurrentHashMap<>();
            volatile boolean isEndOfWord;
            volatile String word;
        }

        public Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            if (word == null) return;

            synchronized (lock) {
                TrieNode current = root;
                for (char ch : word.toLowerCase().toCharArray()) {
                    current.children.putIfAbsent(ch, new TrieNode());
                    current = current.children.get(ch);
                    if (current == null) {
                        // This shouldn't happen but let's be defensive
                        return;
                    }
                }
                current.isEndOfWord = true;
                current.word = word;
            }
        }

        public List<String> findSimilar(String word, int maxDistance) {
            if (word == null) return new ArrayList<>();

            PriorityQueue<WordMatch> matches = new PriorityQueue<>((a, b) -> {
                int similarityCompare = Integer.compare(b.similarity, a.similarity);
                if (similarityCompare != 0) return similarityCompare;
                return Integer.compare(a.editDistance, b.editDistance);
            });

            searchSimilar(root, "", word.toLowerCase(), maxDistance, matches);

            List<String> results = new ArrayList<>();
            while (!matches.isEmpty() && results.size() < MAX_SUGGESTIONS) {
                WordMatch match = matches.poll();
                if (match.similarity >= 50) {
                    results.add(match.word);
                }
            }
            return results;
        }

        private void searchSimilar(TrieNode node, String prefix, String target,
                                   int maxDistance, PriorityQueue<WordMatch> matches) {
            if (node == null) return;

            if (node.isEndOfWord) {
                int editDistance = calculateEditDistance(prefix, target);
                if (editDistance <= maxDistance) {
                    int similarity = calculateSimilarityDP(prefix, target);
                    matches.offer(new WordMatch(node.word, similarity, editDistance));
                }
            }

            if (Math.abs(prefix.length() - target.length()) > maxDistance) {
                return;
            }

            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                searchSimilar(entry.getValue(), prefix + entry.getKey(),
                        target, maxDistance, matches);
            }
        }
    }

    private static class WordMatch {
        String word;
        int similarity;
        int editDistance;

        WordMatch(String word, int similarity, int editDistance) {
            this.word = word;
            this.similarity = similarity;
            this.editDistance = editDistance;
        }
    }

    private static int calculateSimilarityDP(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Initialize base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i * -1; // Penalty for deletions
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j * -1; // Penalty for insertions
        }

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i-1) == s2.charAt(j-1)) {
                    // Match bonus
                    dp[i][j] = dp[i-1][j-1] + 2;
                } else {
                    // Consider different types of operations
                    int substitutionScore = dp[i-1][j-1] - 1;  // Substitution penalty
                    int deletionScore = dp[i-1][j] - 1;        // Deletion penalty
                    int insertionScore = dp[i][j-1] - 1;       // Insertion penalty

                    // Additional scoring for similar characters (e.g., 'a' and 'e' are both vowels)
                    if (isSimilarChar(s1.charAt(i-1), s2.charAt(j-1))) {
                        substitutionScore++;
                    }

                    dp[i][j] = Math.max(Math.max(substitutionScore, deletionScore), insertionScore);
                }
            }
        }

        // Convert the similarity score to a scale of 0-100
        int maxPossibleScore = Math.max(m, n) * 2; // Maximum possible positive score
        int minPossibleScore = -Math.max(m, n);    // Maximum possible negative score
        int score = dp[m][n];

        return (int) (((double) (score - minPossibleScore) / (maxPossibleScore - minPossibleScore)) * 100);
    }

    private static boolean isSimilarChar(char c1, char c2) {
        // Define character similarity rules
        String vowels = "aeiou";
        String consonants = "bcdfghjklmnpqrstvwxyz";

        c1 = Character.toLowerCase(c1);
        c2 = Character.toLowerCase(c2);

        // Check if both characters are vowels
        if (vowels.indexOf(c1) >= 0 && vowels.indexOf(c2) >= 0) {
            return true;
        }

        // Check if both characters are consonants and near each other in the alphabet
        if (consonants.indexOf(c1) >= 0 && consonants.indexOf(c2) >= 0) {
            return Math.abs(c1 - c2) <= 1; // Adjacent consonants
        }

        return false;
    }

    private void loadDictionary(String path) {
        try {
            Path dictionaryPath = Paths.get(path);
            if (!Files.exists(dictionaryPath)) {
                System.err.println("Dictionary file not found: " + path);
                
                return;
            }

            List<String> words = Files.readAllLines(dictionaryPath);
            words.parallelStream()
                    .filter(word -> word != null && !word.trim().isEmpty())
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .distinct()
                    .forEach(word -> {
                        try {
                            dictionary.add(word);
                            trie.insert(word);
                        } catch (Exception e) {
                            System.err.println("Error adding word to dictionary: " + word);
                        }
                    });

            if (dictionary.isEmpty()) {
                System.err.println("Dictionary is empty, loading basic word list");
                
            }

        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
            
        } catch (Exception e) {
            System.err.println("Unexpected error loading dictionary: " + e.getMessage());
            
        }
    }

    private static class DelayedExecutor {
        private static final Set<ScheduledExecutorService> ALL_EXECUTORS =
                Collections.newSetFromMap(new ConcurrentHashMap<>());

        private final ScheduledExecutorService scheduler;
        private final long delay;
        private ScheduledFuture<?> lastScheduledTask;

        DelayedExecutor(long delayMs) {
            this.delay = delayMs;
            this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });
            ALL_EXECUTORS.add(scheduler);
        }

        void execute(Runnable task) {
            if (lastScheduledTask != null) {
                lastScheduledTask.cancel(false);
            }
            lastScheduledTask = scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
        }

        static void shutdownAll() {
            for (ScheduledExecutorService executor : ALL_EXECUTORS) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            ALL_EXECUTORS.clear();
        }
    }

    public void learnWord(String word) {
        if (word == null || word.trim().isEmpty()) return;

        String lowercaseWord = word.toLowerCase();
        dictionary.add(lowercaseWord);
        trie.insert(lowercaseWord);
        suggestionCache.clear();
    }

    public List<String> getSuggestions(String word) {
        if (word == null || word.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Get only the first word if multiple words are provided
        word = word.trim().split("\\s+")[0];
        String lowercaseWord = word.toLowerCase();

        try {
            return suggestionCache.computeIfAbsent(lowercaseWord, w -> {
                if (dictionary.contains(w)) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add(w);
                    return result;
                }

                Set<String> candidateWords = Collections.newSetFromMap(new ConcurrentHashMap<>());
                candidateWords.addAll(trie.findSimilar(w, MAX_DISTANCE));
                candidateWords.addAll(findPhoneticMatches(w));

                List<WordMatch> matches = candidateWords.parallelStream()
                        .map(candidate -> new WordMatch(candidate,
                                calculateSimilarityDP(w, candidate),
                                calculateEditDistance(w, candidate)))
                        .filter(match -> match.editDistance <= MAX_DISTANCE && match.similarity >= 50)
                        .sorted((a, b) -> {
                            int similarityCompare = Integer.compare(b.similarity, a.similarity);
                            if (similarityCompare != 0) return similarityCompare;
                            return Integer.compare(a.editDistance, b.editDistance);
                        })
                        .limit(MAX_SUGGESTIONS)  // Consistently limit to 5 suggestions
                        .collect(Collectors.toList());

                // If no matches found with high similarity, try with lower threshold
                if (matches.isEmpty()) {
                    matches = candidateWords.parallelStream()
                            .map(candidate -> new WordMatch(candidate,
                                    calculateSimilarityDP(w, candidate),
                                    calculateEditDistance(w, candidate)))
                            .filter(match -> match.editDistance <= MAX_DISTANCE)
                            .sorted((a, b) -> {
                                int similarityCompare = Integer.compare(b.similarity, a.similarity);
                                if (similarityCompare != 0) return similarityCompare;
                                return Integer.compare(a.editDistance, b.editDistance);
                            })
                            .limit(MAX_SUGGESTIONS)
                            .collect(Collectors.toList());
                }

                return matches.stream()
                        .map(match -> match.word)
                        .collect(Collectors.toCollection(ArrayList::new));
            });
        } catch (Exception e) {
            System.err.println("Error getting suggestions: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<String> findPhoneticMatches(String word) {
        String soundex = calculateSoundex(word);
        return dictionary.stream()
                .filter(dictWord -> calculateSoundex(dictWord).equals(soundex))
                .limit(MAX_SUGGESTIONS)  // Consistently limit to 5 matches
                .collect(Collectors.toList());
    }

    private static String calculateSoundex(String word) {
        if (word == null || word.isEmpty()) return "";

        char[] chars = word.toUpperCase().toCharArray();
        char firstLetter = chars[0];

        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case 'B': case 'F': case 'P': case 'V': chars[i] = '1'; break;
                case 'C': case 'G': case 'J': case 'K':
                case 'Q': case 'S': case 'X': case 'Z': chars[i] = '2'; break;
                case 'D': case 'T': chars[i] = '3'; break;
                case 'L': chars[i] = '4'; break;
                case 'M': case 'N': chars[i] = '5'; break;
                case 'R': chars[i] = '6'; break;
                default: chars[i] = '0';
            }
        }

        StringBuilder result = new StringBuilder().append(firstLetter);
        for (int i = 1; i < chars.length && result.length() < 4; i++) {
            if (chars[i] != '0' && chars[i] != chars[i-1]) {
                result.append(chars[i]);
            }
        }

        while (result.length() < 4) result.append('0');
        return result.toString();
    }

    private static int calculateEditDistance(String s1, String s2) {
        int[] prev = new int[s2.length() + 1];
        int[] curr = new int[s2.length() + 1];

        for (int j = 0; j <= s2.length(); j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(Math.min(
                                curr[j - 1] + 1,     // insertion
                                prev[j] + 1),         // deletion
                        prev[j - 1] + cost);  // substitution

                if (i > 1 && j > 1 &&
                        s1.charAt(i-1) == s2.charAt(j-2) &&
                        s1.charAt(i-2) == s2.charAt(j-1)) {
                    curr[j] = Math.min(curr[j], prev[prev.length-1] + cost);
                }
            }
            System.arraycopy(curr, 0, prev, 0, curr.length);
        }
        return curr[s2.length()];
    }

    private static int calculateSimilarity(String s1, String s2) {
        int prefixLength = 0;
        int minLength = Math.min(s1.length(), s2.length());
        while (prefixLength < minLength && s1.charAt(prefixLength) == s2.charAt(prefixLength)) {
            prefixLength++;
        }

        int lengthDiff = Math.abs(s1.length() - s2.length());
        int editDistance = calculateEditDistance(s1, s2);

        int score = 100;
        score -= (editDistance * 10);
        score += (prefixLength * 5);
        score -= (lengthDiff * 5);

        return Math.max(0, score);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}