package service;

import model.Director;

import java.util.List;

/**
 * Formats director details into a single string for storage.
 */
public class DirectorFormatter {

    private DirectorFormatter() {
    }

    public static String format(List<Director> directors) {
        StringBuilder sb = new StringBuilder();
        int index = 1;

        for (Director d : directors) {
            if (d.getFullName().trim().isEmpty()) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(" | ");
            }

            sb.append("Director ").append(index).append(": ")
                    .append(d.getFullName()).append(", ")
                    .append(d.getPhone());

            index++;
        }

        return sb.toString();
    }
}