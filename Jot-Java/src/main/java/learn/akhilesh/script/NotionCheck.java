package learn.akhilesh.script;

import learn.akhilesh.dao.GrammarReport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks Notion notes for any syntactical errors
 */
public class NotionCheck {

    public static final String TRAILING_SPACE = "  ";
    public static final String NBSP_CHAR = "\\u00A0";
    public static final String BOLD = "**";
    public static final char HEADING = '#';
    public static final String CODE_BLOCK = "```";
    public static final String TABLE = "|";
    private int lastHeadingLevel = 0;
    private transient boolean[] isInCodeBlockOrTableState;  // [inCodeBlock, inTable]

    /**
     * Method reads the Notion markdown file and generates the report
     */
    public List<GrammarReport> generateGrammarReport(String filePath) {
        int lineNum = 1;
        String currentLine;
        List<GrammarReport> reportList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((currentLine = br.readLine()) != null) {
                if (!isInCodeBlockOrTable(currentLine)) {
                    checkExtraSpaces(lineNum, currentLine, reportList);
                }
                checkNBSPCharacter(lineNum, currentLine, reportList);
                checkInconsistentBoldingOfLines(lineNum, currentLine, reportList);
                checkHeadingHierarchy(lineNum, currentLine, reportList);
                lineNum++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found for path: \nException: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: \nException: " + filePath, e);
        }
        return reportList;
    }

    private void checkExtraSpaces(int lineNum, String line, List<GrammarReport> reportList) {
        line = line.trim();
        if (line.contains(TRAILING_SPACE)) {
            reportList.add(GrammarReport.builder()
                    .lineNum(lineNum)
                    .line(formatLineForLogs(line))
                    .issue(GrammarReport.Issue.EXTRA_SPACE)
                    .issueMessage("Extra space at: " + line.indexOf(TRAILING_SPACE))
                    .build());
        }
    }

    private void checkNBSPCharacter(int lineNum, String line, List<GrammarReport> reportList) {
        if (line.contains(NBSP_CHAR)) {
            reportList.add(GrammarReport.builder()
                    .lineNum(lineNum)
                    .line(formatLineForLogs(line))
                    .issue(GrammarReport.Issue.NBSP_SPACE)
                    .issueMessage("NBSP character found at position: " + line.indexOf(NBSP_CHAR))
                    .build());
        }
    }

    private void checkInconsistentBoldingOfLines(int lineNum, String line, List<GrammarReport> reportList) {
        int index = 0;
        while (index < line.length() - 1) {
            int open = line.indexOf(BOLD, index);

            //No bold characters present
            if (open == -1) {
                break;
            }

            // After **, the next character should not be a space
            if (open + 2 >= line.length() || line.charAt(open + 2) == ' ') {
                reportList.add(GrammarReport.builder()
                        .lineNum(lineNum)
                        .line(formatLineForLogs(line))
                        .issue(GrammarReport.Issue.WORD_BOLD)
                        .issueMessage("Space after bold block opening")
                        .build());
                break;
            }

            // Find closing **
            int close = line.indexOf(BOLD, open + 2);

            if (close == -1) {
                reportList.add(GrammarReport.builder()
                        .lineNum(lineNum)
                        .line(formatLineForLogs(line))
                        .issue(GrammarReport.Issue.WORD_BOLD)
                        .issueMessage("Bold block not closed")
                        .build());
                break;
            }

            // Space before bold closing
            if (close > 0 && line.charAt(close - 1) == ' ') {
                reportList.add(GrammarReport.builder()
                        .lineNum(lineNum)
                        .line(formatLineForLogs(line))
                        .issue(GrammarReport.Issue.WORD_BOLD)
                        .issueMessage("Space before bold block closing")
                        .build());
                break;
            }

            // Only now it is safe to substring
            String inside = line.substring(open + 2, close);
            if (inside.trim().isEmpty()) {
                reportList.add(GrammarReport.builder()
                        .lineNum(lineNum)
                        .line(formatLineForLogs(line))
                        .issue(GrammarReport.Issue.WORD_BOLD)
                        .issueMessage("No content inside bold block")
                        .build());
                break;
            }
            index = close + 2;
        }
    }

    private void checkHeadingHierarchy(int lineNum, String line, List<GrammarReport> reports) {
        if (line.startsWith(String.valueOf(HEADING))) {
            int currentHeadingLevel = countHeadingLevel(line);

            if (!isValidHeadingTransition(lastHeadingLevel, currentHeadingLevel)) {
                reports.add(GrammarReport.builder()
                        .lineNum(lineNum)
                        .line(formatLineForLogs(line))
                        .issue(GrammarReport.Issue.INCONSISTENT_HEADINGS)
                        .issueMessage("Invalid heading transition from level " + lastHeadingLevel + " to " + currentHeadingLevel)
                        .build());
            }
            lastHeadingLevel = currentHeadingLevel;
        }
    }

    /**
     * Counts how many '#' are at the beginning
     */
    private int countHeadingLevel(String line) {
        int level = 0;
        while (level < line.length() && line.charAt(level) == HEADING) {
            level++;
        }
        return level;
    }

    /**
     * Supports till heading level 3 as of now
     */
    private boolean isValidHeadingTransition(int lastLevel, int currentLevel) {
        if (lastLevel == 0) {
            // No previous heading, always valid
            return true;
        }
        if (lastLevel == 1) {
            // After heading 1, only heading 1 or heading 2 allowed
            return currentLevel == 1 || currentLevel == 2;
        }
        if (lastLevel == 2 || lastLevel == 3) {
            // After heading 2 or 3, heading 1, 2, or 3 allowed
            return currentLevel == 1 || currentLevel == 2 || currentLevel == 3;
        }
        return true;
    }

    private String formatLineForLogs(String wholeLine) {
        String line;
        line = wholeLine.trim();
        if (line.length() >= 30) {
            line = line.substring(0, 30);
        }
        return line;
    }

    private boolean isInCodeBlockOrTable(String line) {
        if (isInCodeBlockOrTableState == null) {
            isInCodeBlockOrTableState = new boolean[]{false, false};
        }
        String trimmed = line.trim();
        // Toggle code block state
        if (trimmed.startsWith(CODE_BLOCK)) {
            isInCodeBlockOrTableState[0] = !isInCodeBlockOrTableState[0];
        }
        // Table state: if line starts with | and not in code block
        if (!isInCodeBlockOrTableState[0] && trimmed.startsWith(TABLE)) {
            isInCodeBlockOrTableState[1] = true;
        } else if (!trimmed.startsWith(TABLE)) {
            isInCodeBlockOrTableState[1] = false;
        }
        return isInCodeBlockOrTableState[0] || isInCodeBlockOrTableState[1];
    }
}
