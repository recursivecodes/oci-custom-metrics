package codes.recursive;

import io.micronaut.runtime.Micronaut;

public class Application {
    public static void main(String[] args) {
        System.setProperty("oracle.jdbc.fanEnabled", "false");

        Micronaut
                .build(new String[]{})
                .mainClass(Application.class)
                .environmentPropertySource(false)
                .start();
    }
}