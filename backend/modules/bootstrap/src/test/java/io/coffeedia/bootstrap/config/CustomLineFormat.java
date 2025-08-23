package io.coffeedia.bootstrap.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.util.regex.Pattern;

public class CustomLineFormat implements MessageFormattingStrategy {

    // ANSI 색상 코드
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String MAGENTA = "\u001B[35m";

    // SQL 키워드 패턴
    private static final Pattern KEYWORD_PATTERN = Pattern.compile(
        "\\b(SELECT|FROM|WHERE|JOIN|INNER|LEFT|RIGHT|OUTER|ON|GROUP BY|ORDER BY|HAVING|" +
            "INSERT|UPDATE|DELETE|CREATE|DROP|ALTER|TABLE|INDEX|PRIMARY|KEY|FOREIGN|" +
            "CONSTRAINT|NOT NULL|DEFAULT|AUTO_INCREMENT|VALUES|SET|INTO|DISTINCT|" +
            "AS|AND|OR|IN|EXISTS|LIKE|BETWEEN|IS|NULL|LIMIT|OFFSET|UNION|ALL|" +
            "COUNT|SUM|AVG|MAX|MIN|CASE|WHEN|THEN|ELSE|END)\\b",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public String formatMessage(
        final int connectionId,
        final String now,
        final long elapsed,
        final String category,
        final String prepared,
        final String sql,
        final String url
    ) {
        if (sql == null || sql.isBlank()) {
            return "";
        }

        return String.format("""
                
                ===========================================
                🔗 Connection ID: %d
                ⏰ Execution Time: %d ms
                
                %s
                ===========================================
                """,
            connectionId,
            elapsed,
            formatSql(sql)
        );
    }

    private String formatSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }

        String formatted = sql.trim();

        // 기본적인 포맷팅
        formatted = addBasicFormatting(formatted);

        // 색상 적용
        formatted = applySyntaxHighlighting(formatted);

        return formatted;
    }

    private String addBasicFormatting(String sql) {
        StringBuilder result = new StringBuilder();

        // 소문자로 변환해서 패턴 매칭 (원본은 유지)
        String lowerSql = sql.toLowerCase();
        String[] words = sql.split("\\s+");
        String[] lowerWords = lowerSql.split("\\s+");

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String lowerWord = lowerWords[i];

            // 주요 키워드 앞에 줄바꿈 추가
            if (shouldAddNewlineBefore(lowerWord) && i > 0) {
                result.append("\n");
                if (lowerWord.equals("from")) {
                    result.append("  ");
                }
                if (lowerWord.equals("set")) {
                    result.append("   ");
                }
                if (lowerWord.equals("where")) {
                    result.append(" ");
                }
            }

            // SELECT 절에서 콤마 후 줄바꿈
            if (word.endsWith(",") && isInSelectClause(lowerWords, i)) {
                result.append(word).append("\n       ");
                continue;
            }

            // UPDATE 절에서 콤마 후 줄바꿈
            if (word.endsWith(",") && isInUpdateClause(lowerWords, i)) {
                result.append(word).append("\n       ");
                continue;
            }

            // WHERE 절에서 AND, OR 앞에 줄바꿈과 들여쓰기
            if ((lowerWord.equals("and") || lowerWord.equals("or")) &&
                isInWhereClause(lowerWords, i)) {
                result.append("\n  ").append(word);
                continue;
            }

            result.append(word);

            // 단어 사이에 공백 추가 (마지막 단어가 아닌 경우)
            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    private boolean shouldAddNewlineBefore(String lowerWord) {
        return lowerWord.equals("select") ||
            lowerWord.equals("from") ||
            lowerWord.equals("where") ||
            lowerWord.equals("join") ||
            lowerWord.equals("inner") ||
            lowerWord.equals("left") ||
            lowerWord.equals("right") ||
            lowerWord.equals("group") ||
            lowerWord.equals("order") ||
            lowerWord.equals("having") ||
            lowerWord.equals("union") ||
            lowerWord.equals("insert") ||
            lowerWord.equals("update") ||
            lowerWord.equals("delete") ||
            lowerWord.equals("values") ||
            lowerWord.equals("set");
    }

    private boolean isInSelectClause(String[] lowerWords, int currentIndex) {
        // SELECT 이후이고 FROM 이전인지 확인
        boolean afterSelect = false;
        for (int i = 0; i < currentIndex; i++) {
            if (lowerWords[i].equals("select")) {
                afterSelect = true;
            } else if (lowerWords[i].equals("from")) {
                afterSelect = false;
            }
        }
        return afterSelect;
    }

    private boolean isInUpdateClause(String[] lowerWords, int currentIndex) {
        // UPDATE 이후이고 WHERE 이전인지 확인
        boolean afterSelect = false;
        for (int i = 0; i < currentIndex; i++) {
            if (lowerWords[i].equals("update")) {
                afterSelect = true;
            } else if (lowerWords[i].equals("where")) {
                afterSelect = false;
            }
        }
        return afterSelect;
    }

    private boolean isInWhereClause(String[] lowerWords, int currentIndex) {
        // WHERE 이후이고 GROUP BY, ORDER BY, HAVING 등 이전인지 확인
        boolean afterWhere = false;
        for (int i = 0; i < currentIndex; i++) {
            if (lowerWords[i].equals("where")) {
                afterWhere = true;
            } else if (lowerWords[i].equals("group") ||
                lowerWords[i].equals("order") ||
                lowerWords[i].equals("having")) {
                afterWhere = false;
            }
        }
        return afterWhere;
    }

    private String applySyntaxHighlighting(String sql) {
        // SQL 키워드에 색상 적용
        sql = KEYWORD_PATTERN.matcher(sql).replaceAll(BLUE + "$1" + RESET);

        // 문자열 리터럴에 색상 적용
        sql = sql.replaceAll("'([^']*)'", GREEN + "'$1'" + RESET);

        // 숫자에 색상 적용
        sql = sql.replaceAll("\\b(\\d+)\\b", YELLOW + "$1" + RESET);

        // 테이블명/컬럼명 (점으로 구분된 것들)에 색상 적용
        sql = sql.replaceAll("\\b([a-zA-Z_][a-zA-Z0-9_]*\\.[a-zA-Z_][a-zA-Z0-9_]*)\\b",
            CYAN + "$1" + RESET);

        // 매개변수 플레이스홀더에 색상 적용
        sql = sql.replaceAll("\\?", MAGENTA + "?" + RESET);

        return sql;
    }
}
