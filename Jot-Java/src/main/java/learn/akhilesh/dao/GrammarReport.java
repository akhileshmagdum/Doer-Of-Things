package learn.akhilesh.dao;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrammarReport {
    private int lineNum;
    private String line;
    private Issue issue;
    private String issueMessage;

    @Override
    public String toString() {
        return String.format("Line No.: %s Issue: %s Issue Message: %s\nLine: %s \n-------\n",
                lineNum, issue, issueMessage, line);
    }

    public enum Issue {
        EXTRA_SPACE,
        NBSP_SPACE,
        WORD_BOLD,
        INCONSISTENT_HEADINGS
    }
}
