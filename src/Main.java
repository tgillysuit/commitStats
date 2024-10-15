import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt for filename
        System.out.print("Enter the CSV filename: ");
        String filename = scanner.nextLine();

        // Load data
        DataLoader dataLoader = new DataLoader();
        List<Commit> commits = dataLoader.loadCommits(filename);

        // Group commits by fork
        ForkManager forkManager = new ForkManager(commits);
        int totalForks = forkManager.getTotalForks();

        // Prompt for fork selection
        System.out.println("There are " + totalForks + " forks available (fork1 to fork" + totalForks + ").");
        System.out.print("Enter the fork number to analyze (or 'all' for all forks): ");
        String input = scanner.nextLine();

        List<Commit> selectedCommits;
        if (input.equalsIgnoreCase("all")) {
            selectedCommits = commits;
        } else {
            try {
                int forkNumber = Integer.parseInt(input);
                if (forkNumber < 1 || forkNumber > totalForks) {
                    System.out.println("Invalid fork number.");
                    return;
                }
                String forkId = "fork" + forkNumber;
                selectedCommits = forkManager.getCommitsByFork(forkId);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                return;
            }
        }

        // Compute statistics
        StatsCalculator statsCalculator = new StatsCalculator(selectedCommits);
        int commitCount = statsCalculator.getCommitCount();
        String latestCommitTimestamp = statsCalculator.getLatestCommitTimestamp();
        double averageLinesChanged = statsCalculator.getAverageLinesChanged();

        // Display results
        System.out.println("\nStatistics:");
        System.out.println("Number of commits: " + commitCount);
        System.out.println("Most recent commit timestamp: " + latestCommitTimestamp);
        System.out.printf("Average lines changed per commit: %.2f\n", averageLinesChanged);

        scanner.close();
    }
}
