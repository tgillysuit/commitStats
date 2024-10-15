// This class represents a single commit.
public class Commit {
  private String forkId;
  private String timestamp;
  private int linesChanged;

  public Commit(String forkId, String timestamp, int linesChanged) {
      this.forkId = forkId;
      this.timestamp = timestamp;
      this.linesChanged = linesChanged;
  }

  public String getForkId() {
      return forkId;
  }

  public String getTimestamp() {
      return timestamp;
  }

  public int getLinesChanged() {
      return linesChanged;
  }
}
