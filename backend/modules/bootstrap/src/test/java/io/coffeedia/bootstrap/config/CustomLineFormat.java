package io.coffeedia.bootstrap.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.util.Set;
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

    private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile("'([^']*)'");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b(\\d+)\\b");
    private static final Pattern TABLE_COLUMN_PATTERN = Pattern.compile(
        "\\b([a-zA-Z_][a-zA-Z0-9_]*\\.[a-zA-Z_][a-zA-Z0-9_]*)\\b");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\?");

    // Set.of()는 불변이므로 메모리 효율적
    private static final Set<String> NEWLINE_KEYWORDS = Set.of(
        "select", "from", "where", "join", "inner", "left", "right",
        "group", "order", "having", "union", "insert", "update",
        "delete", "values", "set"
    );

    // 빈번한 문자열 배열 재사용을 위한 캐시 - 더 많은 레벨 추가
    private static final String[] INDENTS = {
        "", " ", "  ", "   ", "    ", "     ", "      ", "       ",
        "        ", "         ", "          "
    };

    // 헤더 포맷 문자열 캐싱
    private static final String HEADER_START = "\n===========================================\n";
    private static final String CONNECTION_PREFIX = "🔗 Connection ID: ";
    private static final String TIME_PREFIX = "⏰ Execution Time: ";
    private static final String TIME_SUFFIX = " ms\n\n";
    private static final String FOOTER = "\n===========================================";

    private enum ClauseState {
        NONE, SELECT, UPDATE, WHERE
    }

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

        // 더 정확한 용량 추정으로 StringBuilder resize 방지
        final int estimatedSize = sql.length() * 2 + 100; // 하이라이팅으로 인한 크기 증가 고려
        final StringBuilder result = new StringBuilder(estimatedSize);

        // 문자열 연결 최적화
        result.append(HEADER_START)
            .append(CONNECTION_PREFIX).append(connectionId).append('\n')
            .append(TIME_PREFIX).append(elapsed).append(TIME_SUFFIX);

        formatSqlDirectly(sql.trim(), result);
        result.append(FOOTER);

        return result.toString();
    }

    // 중간 문자열 생성 없이 직접 StringBuilder에 작성
    private void formatSqlDirectly(final String sql, final StringBuilder result) {
        if (sql.isEmpty()) {
            return;
        }

        final String[] words = sql.split("\\s+");
        ClauseState currentState = ClauseState.NONE;
        boolean needsSpace = false;

        for (int i = 0; i < words.length; i++) {
            final String word = words[i];
            final String lowerWord = word.toLowerCase();
            final char lastChar = word.isEmpty() ? '\0' : word.charAt(word.length() - 1);
            final boolean hasComma = lastChar == ',';
            final boolean isAndOr = isAndOrKeyword(lowerWord);

            currentState = updateClauseState(currentState, lowerWord);

            // 키워드 앞 줄바꿈 처리
            if (i > 0 && NEWLINE_KEYWORDS.contains(lowerWord)) {
                result.append('\n');
                appendIndentationFast(result, lowerWord);
                needsSpace = false;
            } else if (needsSpace) {
                result.append(' ');
            }

            // 콤마 처리
            if (hasComma) {
                result.append(word);
                if (currentState == ClauseState.SELECT || currentState == ClauseState.UPDATE) {
                    result.append('\n').append(INDENTS[7]);
                    needsSpace = false;
                    continue;
                }
            }
            // WHERE 절의 AND, OR 처리
            else if (currentState == ClauseState.WHERE && isAndOr) {
                result.append("\n  ").append(word);
                needsSpace = true;
                continue;
            } else {
                result.append(word);
            }

            // 다음 단어를 위한 공백 필요성 결정
            needsSpace = i < words.length - 1 && !hasComma;
        }

        // 하이라이팅을 마지막에 한 번만 수행
        applySyntaxHighlightingInPlace(result);
    }

    private static boolean isAndOrKeyword(final String lowerWord) {
        return switch (lowerWord) {
            case "and", "or" -> true;
            default -> false;
        };
    }

    private ClauseState updateClauseState(final ClauseState currentState, final String lowerWord) {
        return switch (lowerWord) {
            case "select" -> ClauseState.SELECT;
            case "update" -> ClauseState.UPDATE;
            case "where" -> ClauseState.WHERE;
            case "from" -> currentState == ClauseState.SELECT ? ClauseState.NONE : currentState;
            case "group", "order", "having" -> ClauseState.NONE;
            default -> currentState;
        };
    }

    // 캐시된 문자열 사용으로 객체 생성 방지
    private void appendIndentationFast(final StringBuilder result, final String lowerWord) {
        final String indent = switch (lowerWord) {
            case "from" -> INDENTS[2];
            case "set" -> INDENTS[3];
            case "where" -> INDENTS[1];
            default -> INDENTS[0];
        };
        result.append(indent);
    }

    // StringBuilder 내용을 직접 변경하여 새로운 문자열 생성 방지
    private void applySyntaxHighlightingInPlace(final StringBuilder sql) {
        String content = sql.toString();

        // 정규식 매칭 순서를 빈도 순으로 정렬하여 성능 향상
        content = KEYWORD_PATTERN.matcher(content).replaceAll(BLUE + "$1" + RESET);
        content = TABLE_COLUMN_PATTERN.matcher(content).replaceAll(CYAN + "$1" + RESET);
        content = STRING_LITERAL_PATTERN.matcher(content).replaceAll(GREEN + "'$1'" + RESET);
        content = NUMBER_PATTERN.matcher(content).replaceAll(YELLOW + "$1" + RESET);
        content = PARAMETER_PATTERN.matcher(content).replaceAll(MAGENTA + "?" + RESET);

        // StringBuilder 내용 교체
        sql.setLength(0);
        sql.append(content);
    }
}
