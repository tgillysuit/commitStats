import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        System.out.print("Enter the CSV filename: ");
        String f = s.nextLine();

        List<Map<String, String>> data = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(f))) {
            fileScanner.nextLine();

            while (fileScanner.hasNextLine()) {
                String[] v = fileScanner.nextLine().split(",");

                int change = Integer.parseInt(v[2]);  

                Map<String, String> map = new HashMap<>();
                map.put("id", v[0]);  
                map.put("time", v[1]);  
                map.put("change", String.valueOf(change));
                data.add(map);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            s.close();
            return;
        }

        Map<String, List<Map<String, String>>> map = new HashMap<>();
        for (Map<String, String> d : data) {
            String id = d.get("id");
            List<Map<String, String>> list = map.get(id);
            if (list == null) {
                list = new ArrayList<>();
                map.put(id, list);
            }
            list.add(d);
        }
        int count = map.size();

        System.out.println("There are " + count + " forks available (fork1 to fork" + count + ").");
        System.out.print("Enter the fork number to analyze (or 'all' for all forks): ");
        String input = s.nextLine();

        List<Map<String, String>> selected;
        if (input.equalsIgnoreCase("all")) {
            selected = data;
        } else {
            String id = "fork" + input; 
            selected = map.get(id);
        }

        int size = selected.size();

        DateTimeFormatter f1 = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime latest = null;
        for (Map<String, String> d : selected) {
            LocalDateTime t = LocalDateTime.parse(d.get("time"), f1); 
            if (latest == null || t.isAfter(latest)) {
                latest = t;
            }
        }
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String latestTime = latest.format(f2);

        double total = 0.0;
        int totalLinesChanged = 0;
        for (Map<String, String> d : selected) {
            int linesChanged = Integer.parseInt(d.get("change"));
            total += linesChanged;
            totalLinesChanged += linesChanged;
        }
        double avg = total / size;

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (Map<String, String> d : selected) {
            int change = Integer.parseInt(d.get("change"));
            if (change > max) {
                max = change;
            }
            if (change < min) {
                min = change;
            }
        }

        System.out.println("\nStatistics:");
        System.out.println("Number of commits: " + size);
        System.out.println("Most recent commit timestamp: " + latestTime);
        System.out.printf("Average lines changed per commit: %.2f\n", avg);
        System.out.println("Total lines changed across all commits: " + totalLinesChanged);
        System.out.println("Max lines changed in a commit: " + max);
        System.out.println("Min lines changed in a commit: " + min);

        s.close();
    }
}
