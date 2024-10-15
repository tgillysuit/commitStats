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

        // Reading in the .csv file and dividing up the file that has a comma in it.
        List<Map<String, String>> data = new ArrayList<>();
        try (Scanner fs = new Scanner(new File(f))) {
            fs.nextLine();

            // Looking for the next line in the csv file
            while (fs.hasNextLine()) {
                // Seperates each column with the approriate column.
                String[] v = fs.nextLine().split(",");

                int chg = Integer.parseInt(v[2]); 
                // Creates a new map for that push 
                Map<String, String> pushes = new HashMap<>();
                pushes.put("id", v[0]);  // Fork Number
                pushes.put("tm", v[1]);  // Timestamp
                pushes.put("chg", String.valueOf(chg)); // The commits made after that push
                data.add(pushes);
            }
        // catching a nonexistent file and throwing an error.
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            s.close();
            return;
        }
        // Searching the total commits that have been pushed with the appropriate fork id.
        Map<String, List<Map<String, String>>> mp2 = new HashMap<>();
        for (Map<String, String> d : data) {
            String id = d.get("id");
            List<Map<String, String>> lst = mp2.get(id);
            if (lst == null) {
                lst = new ArrayList<>();
                mp2.put(id, lst);
            }
            lst.add(d);
        }
        int cnt = mp2.size();
        // Prints out the total commits.
        System.out.println("There are " + cnt + " forks available (fork1 to fork" + cnt + ").");
        System.out.print("Enter the fork number to analyze (or 'all' for all forks): ");
        String inp = s.nextLine();
        // When prompted the user will select a number and it will gather the data to the appropriate fork id.
        List<Map<String, String>> sel;
        if (inp.equalsIgnoreCase("all")) {
            sel = data;
        } else {
            String id = "fork" + inp; 
            sel = mp2.get(id);
        }
        // returns the size of the id that's selected
        int sz = sel.size();

        // Properly formats the time to be readable
        DateTimeFormatter f1 = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime lat = null;
        for (Map<String, String> d : sel) {
            LocalDateTime t = LocalDateTime.parse(d.get("tm"), f1); 
            if (lat == null || t.isAfter(lat)) {
                lat = t;
            }
        }
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String latT = lat.format(f2);

        double tot = 0.0;
        int tlc = 0;
        for (Map<String, String> d : sel) {
            int lc = Integer.parseInt(d.get("chg"));
            tot += lc;
            tlc += lc;
        }
        double avg = tot / sz;

        int mx = Integer.MIN_VALUE;
        int mn = Integer.MAX_VALUE;
        for (Map<String, String> d : sel) {
            int chg = Integer.parseInt(d.get("chg"));
            if (chg > mx) {
                mx = chg;
            }
            if (chg < mn) {
                mn = chg;
            }
        }

        System.out.println("\nStatistics:");
        System.out.println("Number of commits: " + sz);
        System.out.println("Most recent commit timestamp: " + latT);
        System.out.printf("Average lines changed per commit: %.2f\n", avg);
        System.out.println("Total lines changed across all commits: " + tlc);
        System.out.println("Max lines changed in a commit: " + mx);
        System.out.println("Min lines changed in a commit: " + mn);

        s.close();
    }
}
