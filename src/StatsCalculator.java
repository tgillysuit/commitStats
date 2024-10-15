import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

// This class computes statistics from a list of commits.
public class StatsCalculator {
    private List<Commit> commits;

    public StatsCalculator(List<Commit> commits) {
        this.commits = commits;
    }

    public int getCommitCount() {
        return commits.size();
    }

    public String getLatestCommitTimestamp() {
        if (commits.isEmpty()) {
            return "N/A";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime latest = commits.stream()
                .map(commit -> LocalDateTime.parse(commit.getTimestamp(), formatter))
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (latest != null) {
            DateTimeFormatter userFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return latest.format(userFormatter);
        } else {
            return "N/A";
        }
    }

    public double getAverageLinesChanged() {
        if (commits.isEmpty()) {
            return 0.0;
        }

        double totalLines = commits.stream()
                .mapToInt(Commit::getLinesChanged)
                .sum();

        return totalLines / commits.size();
    }
}
