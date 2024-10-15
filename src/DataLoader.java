import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// This class is responsible for loading and parsing the CSV data.
public class DataLoader {

    public List<Commit> loadCommits(String filename) {
        List<Commit> commits = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] values = line.split(",");
                if (values.length != 3) {
                    continue; // Skip malformed lines
                }

                String forkId = values[0];
                String timestamp = values[1];
                int linesChanged;

                try {
                    linesChanged = Integer.parseInt(values[2]);
                } catch (NumberFormatException e) {
                    linesChanged = 0;
                }

                commits.add(new Commit(forkId, timestamp, linesChanged));
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }

        return commits;
    }
}
