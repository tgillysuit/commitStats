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
            boolean first = true;

            while (fileScanner.hasNextLine()) {
                String l = fileScanner.nextLine();
                if (first) {
                    first = false;
                    continue;
                }

                String[] v = l.split(",");
                if (v.length != 3) {
                    continue;
                }

                String id = v[0];
                String time = v[1];
                String changeStr = v[2];
                int change;

                try {
                    change = Integer.parseInt(changeStr);
                } catch (NumberFormatException e) {
                    change = 0;
                }

                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("time", time);
                map.put("change", String.valueOf(change));
                data.add(map);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            s.close();
            return;
        }

        if (data.isEmpty()) {
            System.out.println("No data found in the file.");
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

        List<Map<String, String>> selected = new ArrayList<>();
        if (input.equalsIgnoreCase("all")) {
            selected = data;
        } else {
            try {
                int n = Integer.parseInt(input);
                if (n < 1 || n > count) {
                    System.out.println("Invalid number.");
                    s.close();
                    return;
                }
                String id = "fork" + n;
                List<Map<String, String>> l = map.get(id);
                if (l == null || l.isEmpty()) {
                    System.out.println("No data found for " + id + ".");
                    s.close();
                    return;
                }
                selected = l;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                s.close();
                return;
            }
        }

        int size = selected.size();

        DateTimeFormatter f1 = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime latest = null;
        for (Map<String, String> d : selected) {
            String time = d.get("time");
            LocalDateTime t;
            try {
                t = LocalDateTime.parse(time, f1);
            } catch (Exception e) {
                continue;
            }
            if (latest == null || t.isAfter(latest)) {
                latest = t;
            }
        }
        String latestTime;
        if (latest != null) {
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            latestTime = latest.format(f2);
        } else {
            latestTime = "N/A";
        }

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
