package me.thesquadmc.utils.random;

import org.apache.commons.text.RandomStringGenerator;

public class RandomUtils {

    private static final RandomStringGenerator GENERATOR = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(Character::isLetterOrDigit)
            .build();


    public static String generateAlphaNumeric(int length) {
        return GENERATOR.generate(length);
    }
}
