import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// This class manages forks and their associated commits.
public class ForkManager {
    private Map<String, List<Commit>> forkCommitsMap;

    public ForkManager(List<Commit> commits) {
        forkCommitsMap = commits.stream()
                .collect(Collectors.groupingBy(Commit::getForkId));
    }

    public List<Commit> getCommitsByFork(String forkId) {
        return forkCommitsMap.getOrDefault(forkId, new ArrayList<>());
    }

    public int getTotalForks() {
        return forkCommitsMap.size();
    }
}
