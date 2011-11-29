package br.edu.ufcg.aweseries.thetvdb;

public enum Language {
    DA,
    FI,
    NL,
    DE,
    IT,
    ES,
    FR,
    PL,
    HU,
    EL,
    TR,
    RU,
    HE,
    JA,
    PT,
    ZH,
    CS,
    SL,
    HR,
    KO,
    EN,
    SV,
    NO;

    public static Language from(String abbreviation) {
        if (abbreviation == null) {
            throw new IllegalArgumentException("abbreviation should not be null");
        }

        return valueOf(abbreviation.toUpperCase());
    }

    public String abbreviation() {
        return this.toString().toLowerCase();
    }
}

